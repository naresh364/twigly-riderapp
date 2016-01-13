package com.app.twiglydb.models;

/**
 * Created by twigly on 23/7/15.
 */
public enum OrderStatus {
    RECEIVED,
    COOKING,
    DISPATCHED,
    DELIVERED,
    RETURNED,
    CANCELLED_USER,
    CANCELLED_US,
    NOT_DELIVERED,
    DISPATCHING,
    UNKNOWN,
    DELIVERED_FREE_MISSED_GURANTEE,
    DELIVERED_FREE_FOOD_QUALITY,
    FOOD_TRIAL,
    PG_FAILED,
    PG_PENDING
}
