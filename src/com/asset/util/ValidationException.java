package com.asset.util;

public class ValidationException extends Exception {

    @Override
    public String toString() {
        return "Validation failed: Invalid or missing input values";
    }
}
