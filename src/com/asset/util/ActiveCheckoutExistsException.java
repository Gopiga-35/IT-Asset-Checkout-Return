package com.asset.util;

public class ActiveCheckoutExistsException extends Exception {

    @Override
    public String toString() {
        return "Asset removal blocked: Active checkout exists (OUT / LOST / DAMAGED)";
    }
}
