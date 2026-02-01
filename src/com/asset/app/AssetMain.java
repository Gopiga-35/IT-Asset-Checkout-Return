package com.asset.app;

import java.util.Date;
import java.util.Scanner;

import com.asset.bean.Asset;
import com.asset.service.AssetService;
import com.asset.util.ActiveCheckoutExistsException;
import com.asset.util.AssetNotAvailableException;
import com.asset.util.ValidationException;

public class AssetMain {

    private static AssetService assetService;

    public static void main(String[] args) {

        assetService = new AssetService();
        Scanner sc = new Scanner(System.in);

        System.out.println("--- IT Asset Checkout & ReturnConsole ---");

        
        try {
            Asset a = new Asset();
            a.setAssetTag("LAPTOP-389");
            a.setCategory("LAPTOP");
            a.setModel("HP ProBook 440 G9");
            a.setSerialNo("HP-PR-010");
            a.setTotalQuantity(5);
            a.setAvailableQuantity(5);
            a.setStatus("ACTIVE");

            boolean r = assetService.addNewAsset(a);
            System.out.println(r ? "ASSET ADDED" : "ASSET ADD FAILED");

        } catch (ValidationException e) {
            System.out.println("Validation Error: " + e.toString());
        } catch (Exception e) {
            System.out.println("System Error: " + e.getMessage());
        }

        
        
        try {
            boolean r = assetService.checkoutAsset(
                    "LAPTOP-765",
                    "EMP2001",
                    new Date(),
                    new Date()
            );
            System.out.println(r ? "CHECKOUT SUCCESS" : "CHECKOUT FAILED");

        } catch (AssetNotAvailableException e) {
            System.out.println("Availability Error: " + e.toString());
        } catch (ValidationException e) {
            System.out.println("Validation Error: " + e.toString());
        } catch (Exception e) {
            System.out.println("System Error: " + e.getMessage());
        }

        
        
        try {
            boolean r = assetService.removeAsset("LAPTOP-900");
            System.out.println(r ? "ASSET REMOVED" : "ASSET REMOVAL FAILED");

        } catch (ActiveCheckoutExistsException e) {
            System.out.println("Removal Blocked: " + e.toString());
        } catch (ValidationException e) {
            System.out.println("Validation Error: " + e.toString());
        } catch (Exception e) {
            System.out.println("System Error: " + e.getMessage());
        }

        sc.close();
    }
}
