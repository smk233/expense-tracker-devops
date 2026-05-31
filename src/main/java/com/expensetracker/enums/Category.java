package com.expensetracker.enums;

public enum Category {
    FOOD("Food & Dining"),
    TRANSPORT("Transport"),
    UTILITIES("Utilities"),
    ENTERTAINMENT("Entertainment"),
    HEALTH("Health & Medical"),
    EDUCATION("Education"),
    SHOPPING("Shopping"),
    TRAVEL("Travel"),
    RENT("Rent & Housing"),
    PERSONAL("Personal Care"),
    INVESTMENT("Investment"),
    OTHER("Other");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}