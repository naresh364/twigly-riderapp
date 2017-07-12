
package com.app.twiglydb.models;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class OrderDetail implements Parcelable {

    @SerializedName("quantity")
    @Expose
    private int quantity;
    @SerializedName("menuItemName")
    @Expose
    private String menuItemName;

    boolean verified;

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

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    protected OrderDetail(Parcel in) {
        quantity = in.readInt();
        menuItemName = in.readString();
        verified = in.readByte() == 1 ? true : false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(quantity);
        dest.writeString(menuItemName);
        dest.writeByte((byte)(verified?1:0));
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
