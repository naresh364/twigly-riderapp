
package com.app.twiglydb.models;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class OrderDetail implements Parcelable {

    @SerializedName("menuItemId")
    @Expose
    private int menuItemId;
    @SerializedName("quantity")
    @Expose
    private int quantity;
    @SerializedName("price")
    @Expose
    private double price;
    @SerializedName("discountType")
    @Expose
    private String discountType;
    @SerializedName("discount")
    @Expose
    private double discount;
    @SerializedName("orderDetailId")
    @Expose
    private int orderDetailId;
    @SerializedName("menuItemName")
    @Expose
    private String menuItemName;

    /**
     * 
     * @return
     *     The menuItemId
     */
    public int getMenuItemId() {
        return menuItemId;
    }

    /**
     * 
     * @param menuItemId
     *     The menuItemId
     */
    public void setMenuItemId(int menuItemId) {
        this.menuItemId = menuItemId;
    }

    /**
     * 
     * @return
     *     The quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * 
     * @param quantity
     *     The quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * 
     * @return
     *     The price
     */
    public double getPrice() {
        return price;
    }

    /**
     * 
     * @param price
     *     The price
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * 
     * @return
     *     The discountType
     */
    public String getDiscountType() {
        return discountType;
    }

    /**
     * 
     * @param discountType
     *     The discountType
     */
    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    /**
     * 
     * @return
     *     The discount
     */
    public double getDiscount() {
        return discount;
    }

    /**
     * 
     * @param discount
     *     The discount
     */
    public void setDiscount(double discount) {
        this.discount = discount;
    }

    /**
     * 
     * @return
     *     The orderDetailId
     */
    public int getOrderDetailId() {
        return orderDetailId;
    }

    /**
     * 
     * @param orderDetailId
     *     The orderDetailId
     */
    public void setOrderDetailId(int orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    /**
     * 
     * @return
     *     The menuItemName
     */
    public String getMenuItemName() {
        return menuItemName;
    }

    /**
     * 
     * @param menuItemName
     *     The menuItemName
     */
    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    protected OrderDetail(Parcel in) {
        menuItemId = in.readInt();
        quantity = in.readInt();
        price = in.readDouble();
        discountType = in.readString();
        discount = in.readDouble();
        orderDetailId = in.readInt();
        menuItemName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(menuItemId);
        dest.writeInt(quantity);
        dest.writeDouble(price);
        dest.writeString(discountType);
        dest.writeDouble(discount);
        dest.writeInt(orderDetailId);
        dest.writeString(menuItemName);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<OrderDetail> CREATOR = new Parcelable.Creator<OrderDetail>() {
        @Override
        public OrderDetail createFromParcel(Parcel in) {
            return new OrderDetail(in);
        }

        @Override
        public OrderDetail[] newArray(int size) {
            return new OrderDetail[size];
        }
    };

}
