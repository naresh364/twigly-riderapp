package com.app.twiglydb.models;

/**
 * Created by twigly on 23/7/15.
 */
public enum DiscountType {
    ABSOLUTE("Rs."),
    PERCENTAGE("%");

    String name;
    DiscountType(String name) {
        this.name = name;
    }
}
