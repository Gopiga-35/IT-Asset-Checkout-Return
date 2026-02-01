package com.asset.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.asset.bean.Asset;
import com.asset.util.DBUtil;

public class AssetDAO {

    public static Asset findAsset(String asset_tag) {
        Asset asset = null;
        try {
            Connection con = DBUtil.getDBConnection();
            String sql = "SELECT * FROM ASSET_TBL WHERE ASSET_TAG=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, asset_tag);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                asset = new Asset();
                asset.setAssetTag(rs.getString("ASSET_TAG"));
                asset.setCategory(rs.getString("CATEGORY"));
                asset.setModel(rs.getString("MODEL"));
                asset.setTotalQuantity(rs.getInt("TOTAL_QUANTITY"));
                asset.setAvailableQuantity(rs.getInt("AVAILABLE_QUANTITY"));
                asset.setStatus(rs.getString("STATUS"));
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return asset;
    }

    public static List<Asset> viewAllAssets() {
        List<Asset> assetList = new ArrayList<>();
        try {
            Connection con = DBUtil.getDBConnection();
            String sql = "SELECT * FROM ASSET_TBL";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Asset asset = new Asset();
                asset.setAssetTag(rs.getString("ASSET_TAG"));
                asset.setCategory(rs.getString("CATEGORY"));
                asset.setModel(rs.getString("MODEL"));
                asset.setTotalQuantity(rs.getInt("TOTAL_QUANTITY"));
                asset.setAvailableQuantity(rs.getInt("AVAILABLE_QUANTITY"));
                asset.setStatus(rs.getString("STATUS"));
                assetList.add(asset);
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return assetList;
    }

    public boolean insertAsset(Asset asset) {
        boolean result = false;
        try {
            Connection con = DBUtil.getDBConnection();
            String checkSql = "SELECT COUNT(*) FROM ASSET_TBL WHERE ASSET_TAG=?";
            PreparedStatement checkPs = con.prepareStatement(checkSql);
            checkPs.setString(1, asset.getAssetTag());
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                con.close();
                return false;
            }
            String sql = "INSERT INTO ASSET_TBL (ASSET_TAG,CATEGORY,MODEL,TOTAL_QUANTITY,AVAILABLE_QUANTITY,STATUS) VALUES(?,?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, asset.getAssetTag());
            ps.setString(2, asset.getCategory());
            ps.setString(3, asset.getModel());
            ps.setInt(4, asset.getTotalQuantity());
            ps.setInt(5, asset.getAvailableQuantity());
            ps.setString(6, asset.getStatus());
            int rows = ps.executeUpdate();
            if (rows > 0) result = true;
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean updateAssetQuantitiesAndStatus(String assetTag, int newAvailableQuantity, String newStatus) {
        boolean result = false;
        try {
            Connection con = DBUtil.getDBConnection();
            String sql = "UPDATE ASSET_TBL SET AVAILABLE_QUANTITY=?, STATUS=? WHERE ASSET_TAG=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, newAvailableQuantity);
            ps.setString(2, newStatus);
            ps.setString(3, assetTag);
            int rows = ps.executeUpdate();
            if (rows > 0) result = true;
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean deleteAsset(String assetTag) {
        boolean result = false;
        try {
            Connection con = DBUtil.getDBConnection();
            String sql = "DELETE FROM ASSET_TBL WHERE ASSET_TAG=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, assetTag);
            int rows = ps.executeUpdate();
            if (rows > 0) result = true;
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}

