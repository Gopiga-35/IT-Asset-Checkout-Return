package com.asset.util;

public class AssetNotAvailableException extends Exception {

    @Override
    public String toString() {
        return "Asset not available: Either no stock or asset is retired";
    }
}
