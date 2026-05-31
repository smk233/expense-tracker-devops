package com.expensetracker.enums;

public enum PaymentMethod {
    CASH("Cash"),
    CREDIT_CARD("Credit Card"),
    ONLINE_TRANSFER("Online Transfer"),
    DEBIT_CARD("Debit Card"),
    UPI("UPI"),
    OTHER("Other");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}