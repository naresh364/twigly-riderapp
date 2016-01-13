package com.app.twiglydb.models;

/**
 * Created by twigly on 23/7/15.
 */
public class OrderItem {
    String menuItemName;
    int menuItemId;
    int quantity;
    float price;
    float discount;

    String discountType;
    transient DiscountType discountTypeEnum;

    public int getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(int menuItemId) {
        this.menuItemId = menuItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDiscountType() {
        if (discountTypeEnum != null){
            discountType = discountTypeEnum.name();
        }
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
        this.discountTypeEnum = DiscountType.valueOf(discountType);
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getDiscountedPrice(){
        float discount;
        DiscountType type = DiscountType.valueOf(discountType);
        if (type == null) type = DiscountType.ABSOLUTE;
        discount = (type==DiscountType.ABSOLUTE)?this.discount:(price*this.discount/100);
        return this.price-discount;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }
}
