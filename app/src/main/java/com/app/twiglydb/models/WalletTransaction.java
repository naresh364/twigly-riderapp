package com.app.twiglydb.models;

/**
 * Created by twigly on 23/7/15.
 */
public class WalletTransaction {
    String type;
    float amount;
    transient TransactionType transactionType;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        this.transactionType = TransactionType.valueOf(type);
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public enum TransactionType {
        DEBIT,
        CREDIT
    }
}
