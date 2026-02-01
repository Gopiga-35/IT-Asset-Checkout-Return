package com.asset.service;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import com.asset.bean.Asset;
import com.asset.bean.Checkout;
import com.asset.dao.AssetDAO;
import com.asset.dao.CheckoutDAO;
import com.asset.util.ActiveCheckoutExistsException;
import com.asset.util.AssetNotAvailableException;
import com.asset.util.DBUtil;
import com.asset.util.ValidationException;

public class AssetService {

    private AssetDAO assetDAO = new AssetDAO();
    private CheckoutDAO checkoutDAO = new CheckoutDAO();
    public Asset viewAssetDetails(String assetTag) {

        if (assetTag == null || assetTag.trim().isEmpty()) {
            return null;
        }
        return assetDAO.findAsset(assetTag);
    }
    public List<Asset> viewAllAssets() {
        return assetDAO.viewAllAssets();
    }

    public boolean addNewAsset(Asset asset) throws ValidationException {

        if (asset == null)
            throw new ValidationException();

        if (asset.getAssetTag() == null || asset.getAssetTag().trim().isEmpty())
            throw new ValidationException();

        if (asset.getCategory() == null || asset.getCategory().trim().isEmpty())
            throw new ValidationException();

        if (asset.getModel() == null || asset.getModel().trim().isEmpty())
            throw new ValidationException();

        if (asset.getTotalQuantity() < 0)
            throw new ValidationException();

        if (asset.getAvailableQuantity() < 0 ||
            asset.getAvailableQuantity() > asset.getTotalQuantity())
            throw new ValidationException();
        if (asset.getAvailableQuantity() == 0) {
            asset.setAvailableQuantity(asset.getTotalQuantity());
        }
        if (assetDAO.findAsset(asset.getAssetTag()) != null) {
            throw new ValidationException();
        }

        asset.setStatus("ACTIVE");
        return assetDAO.insertAsset(asset);
    }
    public boolean removeAsset(String assetTag)
            throws ValidationException, ActiveCheckoutExistsException {

        if (assetTag == null || assetTag.trim().isEmpty())
            throw new ValidationException();

        List<Checkout> active =
            checkoutDAO.findActiveCheckoutByAsset(assetTag);

        if (!active.isEmpty()) {
            throw new ActiveCheckoutExistsException(
            );
        }

        if (assetDAO.findAsset(assetTag) == null) {
            return false;
        }

        return assetDAO.deleteAsset(assetTag);
    }
    public boolean checkoutAsset(
            String assetTag,
            String employeeID,
            Date checkoutDate,
            Date dueDate)
            throws ValidationException, AssetNotAvailableException {

        if (assetTag == null || assetTag.trim().isEmpty()
                || employeeID == null || employeeID.trim().isEmpty()
                || checkoutDate == null || dueDate == null
                || dueDate.before(checkoutDate)) {
            throw new ValidationException();
        }

        Asset asset = assetDAO.findAsset(assetTag);
        if (asset == null)
            return false;

        if (!"ACTIVE".equals(asset.getStatus()) ||
            asset.getAvailableQuantity() < 1) {
            throw new AssetNotAvailableException();
        }

        Connection con = null;

        try {
            con = DBUtil.getDBConnection();
            con.setAutoCommit(false);

            int checkoutID = checkoutDAO.generateCheckoutID();

            Checkout checkout = new Checkout();
            checkout.setCheckoutID(checkoutID);
            checkout.setAssetTag(assetTag);
            checkout.setEmployeeID(employeeID);
            checkout.setCheckoutDate(checkoutDate);
            checkout.setDueDate(dueDate);
            checkout.setStatus("OUT");

            boolean checkoutDone = checkoutDAO.recordCheckout(checkout);

            int newQty = asset.getAvailableQuantity() - 1;
            boolean assetUpdated =
                assetDAO.updateAssetQuantitiesAndStatus(
                    assetTag, newQty, asset.getStatus());

            if (checkoutDone && assetUpdated) {
                con.commit();
                return true;
            } else {
                con.rollback();
                return false;
            }

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
            return false;
        } finally {
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }
    public boolean returnAsset(int checkoutID, Date actualReturnDate)
            throws ValidationException {

        if (checkoutID <= 0 || actualReturnDate == null)
            throw new ValidationException();

        Checkout checkout = checkoutDAO.findCheckoutByID(checkoutID);
        if (checkout == null || !"OUT".equals(checkout.getStatus()))
            return false;

        Asset asset = assetDAO.findAsset(checkout.getAssetTag());
        if (asset == null)
            return false;

        Connection con = null;

        try {
            con = DBUtil.getDBConnection();
            con.setAutoCommit(false);

            boolean checkoutUpdated =
                checkoutDAO.updateCheckoutStatusAndReturnDate(
                    checkoutID, "RETURNED", actualReturnDate);

            int newQty = asset.getAvailableQuantity() + 1;
            boolean assetUpdated =
                assetDAO.updateAssetQuantitiesAndStatus(
                    asset.getAssetTag(), newQty, asset.getStatus());

            if (checkoutUpdated && assetUpdated) {
                con.commit();
                return true;
            } else {
                con.rollback();
                return false;
            }

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
            return false;
        } finally {
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }

    public boolean markAssetLostOrDamaged(
            int checkoutID,
            String newStatus,
            Date reportedDate)
            throws ValidationException {

        if (checkoutID <= 0 ||
            (!"LOST".equals(newStatus) && !"DAMAGED".equals(newStatus)) ||
            reportedDate == null) {
            throw new ValidationException();
        }

        Checkout checkout = checkoutDAO.findCheckoutByID(checkoutID);
        if (checkout == null ||
            !"OUT".equals(checkout.getStatus()))
            return false;

        Asset asset = assetDAO.findAsset(checkout.getAssetTag());
        if (asset == null)
            return false;

        Connection con = null;

        try {
            con = DBUtil.getDBConnection();
            con.setAutoCommit(false);

            boolean checkoutUpdated =
                checkoutDAO.updateCheckoutStatusAndReturnDate(
                    checkoutID, newStatus, reportedDate);
            if (asset.getTotalQuantity() == 1) {
                assetDAO.updateAssetQuantitiesAndStatus(
                    asset.getAssetTag(),
                    asset.getAvailableQuantity(),
                    "RETIRED");
            }

            if (checkoutUpdated) {
                con.commit();
                return true;
            } else {
                con.rollback();
                return false;
            }

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
            return false;
        } finally {
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }
}