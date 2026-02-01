package com.asset.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.asset.bean.Checkout;
import com.asset.util.DBUtil;

public class CheckoutDAO {

    public int generateCheckoutID() {
        int checkoutId = 0;
        try {
            Connection con = DBUtil.getDBConnection();
            String sql = "SELECT CHECKOUT_SEQ.NEXTVAL FROM DUAL";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) checkoutId = rs.getInt(1);
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return checkoutId;
    }

    public boolean recordCheckout(Checkout checkout) {
        String sql = "INSERT INTO CHECKOUT_TBL (checkout_id,asset_tag,employee_id,checkout_date,due_date,status) VALUES(?,?,?,?,?,?)";
        try {
            Connection con = DBUtil.getDBConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, checkout.getCheckoutID());
            ps.setString(2, checkout.getAssetTag());
            ps.setString(3, checkout.getEmployeeID());
            ps.setDate(4, new java.sql.Date(checkout.getCheckoutDate().getTime()));
            ps.setDate(5, new java.sql.Date(checkout.getDueDate().getTime()));
            ps.setString(6, "OUT");
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Checkout> findActiveCheckoutByAsset(String assetTag) {
        List<Checkout> list = new ArrayList<>();
        String sql = "SELECT * FROM CHECKOUT_TBL WHERE asset_tag=? AND status IN ('OUT','LOST','DAMAGED')";
        try {
            Connection con = DBUtil.getDBConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, assetTag);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Checkout c = new Checkout();
                c.setCheckoutID(rs.getInt("checkout_id"));
                c.setAssetTag(rs.getString("asset_tag"));
                c.setEmployeeID(rs.getString("employee_id"));
                c.setCheckoutDate(rs.getDate("checkout_date"));
                c.setDueDate(rs.getDate("due_date"));
                c.setReturnDate(rs.getDate("return_date"));
                c.setStatus(rs.getString("status"));
                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Checkout findCheckoutByID(int checkoutID) {
        Checkout checkout = null;
        String sql = "SELECT * FROM CHECKOUT_TBL WHERE checkout_id=?";
        try {
            Connection con = DBUtil.getDBConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, checkoutID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                checkout = new Checkout();
                checkout.setCheckoutID(rs.getInt("checkout_id"));
                checkout.setAssetTag(rs.getString("asset_tag"));
                checkout.setEmployeeID(rs.getString("employee_id"));
                checkout.setCheckoutDate(rs.getDate("checkout_date"));
                checkout.setDueDate(rs.getDate("due_date"));
                checkout.setReturnDate(rs.getDate("return_date"));
                checkout.setStatus(rs.getString("status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return checkout;
    }

    public boolean updateCheckoutStatusAndReturnDate(int checkoutID, String status, Date returnDate) {
        boolean updated = false;
        try {
            Connection con = DBUtil.getDBConnection();
            String sql = "UPDATE CHECKOUT_TBL SET STATUS=?, RETURN_DATE=? WHERE CHECKOUT_ID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, status);
            ps.setDate(2, new java.sql.Date(returnDate.getTime()));
            ps.setInt(3, checkoutID);
            if (ps.executeUpdate() > 0) updated = true;
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updated;
    }
}