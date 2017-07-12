package com.app.twiglydb.models;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Order implements Parcelable {

    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lng")
    @Expose
    private double lng;
    @SerializedName("total")
    @Expose
    private double total;
    @SerializedName("isForLater")
    @Expose
    private boolean isForLater;
    @SerializedName("orderStatus")
    @Expose
    private String orderStatus;
    @SerializedName("paymentOption")
    @Expose
    private String paymentOption;
    @SerializedName("orderDetails")
    @Expose
    private List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
    @SerializedName("date_add")
    @Expose
    private String dateAdd;
    @SerializedName("numItems")
    @Expose
    private int numItems;
    @SerializedName("deliveryRating")
    @Expose
    private int deliveryRating;
    @SerializedName("deliveryBoyNameId")
    @Expose
    private String deliveryBoyNameId;
    @SerializedName("orderId")
    @Expose
    private String orderId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("deliveryTime")
    @Expose
    private String deliveryTime;
    @SerializedName("userPendingBalance")
    @Expose
    private double userPendingBalance;

    @SerializedName("isCheckedIn")
    @Expose
    public boolean isCheckedIn;

    @SerializedName("qrCode")
    @Expose
    private String qrCode;

    @SerializedName("qrCodeWithPending")
    @Expose
    private String qrCodeWithPending;
    /**
     *
     * @return
     *     The address
     */
    public String getAddress() {
        return address;
    }

    /**
     *
     * @param address
     *     The address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * @return
     *     The mobileNumber
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     *
     * @param mobileNumber
     *     The mobile_number
     */
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    /**
     *
     * @return
     *     The total
     */
    public double getTotal() {
        return total;
    }

    /**
     *
     * @param total
     *     The total
     */
    public void setTotal(double total) {
        this.total = total;
    }

    /**
     *
     * @return
     *     The isForLater
     */
    public boolean isIsForLater() {
        return isForLater;
    }

    /**
     *
     * @param isForLater
     *     The isForLater
     */
    public void setIsForLater(boolean isForLater) {
        this.isForLater = isForLater;
    }

    /**
     *
     * @return
     *     The orderStatus
     */
    public String getOrderStatus() {
        return orderStatus;
    }

    /**
     *
     * @param orderStatus
     *     The orderStatus
     */
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    /**
     *
     * @return
     *     The paymentOption
     */
    public String getPaymentOption() {
        return paymentOption;
    }

    /**
     *
     * @param paymentOption
     *     The paymentOption
     */
    public void setPaymentOption(String paymentOption) {
        this.paymentOption = paymentOption;
    }

    /**
     *
     * @return
     *     The orderDetails
     */
    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    /**
     *
     * @param orderDetails
     *     The orderDetails
     */
    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    /**
     *
     * @return
     *     The dateAdd
     */
    public String getDateAdd() {
        return dateAdd;
    }

    /**
     *
     * @param dateAdd
     *     The date_add
     */
    public void setDateAdd(String dateAdd) {
        this.dateAdd = dateAdd;
    }

    /**
     *
     * @return
     *     The numItems
     */
    public int getNumItems() {
        return numItems;
    }

    /**
     *
     * @param numItems
     *     The numItems
     */
    public void setNumItems(int numItems) {
        this.numItems = numItems;
    }

    /**
     *
     * @return
     *     The deliveryRating
     */
    public int getDeliveryRating() {
        return deliveryRating;
    }

    /**
     *
     * @param deliveryRating
     *     The deliveryRating
     */
    public void setDeliveryRating(int deliveryRating) {
        this.deliveryRating = deliveryRating;
    }

    /**
     *
     * @return
     *     The deliveryBoyNameId
     */
    public String getDeliveryBoyNameId() {
        return deliveryBoyNameId;
    }

    /**
     *
     * @param deliveryBoyNameId
     *     The deliveryBoyNameId
     */
    public void setDeliveryBoyNameId(String deliveryBoyNameId) {
        this.deliveryBoyNameId = deliveryBoyNameId;
    }

    /**
     *
     * @return
     *     The orderId
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     *
     * @param orderId
     *     The orderId
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     *
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     *     The deliveryTime
     */
    public String getDeliveryTime() {
        return deliveryTime;
    }

    /**
     *
     * @param deliveryTime
     *     The deliveryTime
     */
    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    /**
     *
     * @return
     *     The userPendingBalance
     */
    public double getUserPendingBalance() {
        return userPendingBalance;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getQrCodeWithPending() {
        return qrCodeWithPending;
    }

    public void setQrCodeWithPending(String qrCodeWithPending) {
        this.qrCodeWithPending = qrCodeWithPending;
    }

    /**
     *
     * @param userPendingBalance
     *     The userPendingBalance
     */
    public void setUserPendingBalance(double userPendingBalance) {
        this.userPendingBalance = userPendingBalance;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    protected Order(Parcel in) {
        address = in.readString();
        mobileNumber = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        total = in.readDouble();
        isForLater = in.readByte() != 0x00;
        orderStatus = in.readString();
        paymentOption = in.readString();
        if (in.readByte() == 0x01) {
            orderDetails = new ArrayList<OrderDetail>();
            in.readList(orderDetails, OrderDetail.class.getClassLoader());
        } else {
            orderDetails = null;
        }
        dateAdd = in.readString();
        numItems = in.readInt();
        deliveryRating = in.readInt();
        deliveryBoyNameId = in.readString();
        orderId = in.readString();
        name = in.readString();
        deliveryTime = in.readString();
        userPendingBalance = in.readDouble();
        isCheckedIn = in.readByte() != 0x00;
        qrCode = in.readString();
        qrCodeWithPending = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(mobileNumber);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeDouble(total);
        dest.writeByte((byte) (isForLater ? 0x01 : 0x00));
        dest.writeString(orderStatus);
        dest.writeString(paymentOption);
        if (orderDetails == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(orderDetails);
        }
        dest.writeString(dateAdd);
        dest.writeInt(numItems);
        dest.writeInt(deliveryRating);
        dest.writeString(deliveryBoyNameId);
        dest.writeString(orderId);
        dest.writeString(name);
        dest.writeString(deliveryTime);
        dest.writeDouble(userPendingBalance);
        dest.writeByte((byte) (isCheckedIn ? 0x01 : 0x00));
        dest.writeString(qrCode);
        dest.writeString(qrCodeWithPending);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public boolean isPaid() {
        return !this.paymentOption.equalsIgnoreCase("COD") && !this.paymentOption.equalsIgnoreCase("CardOD");
    }

    boolean collectPending = false;

    public boolean shouldCollectPending() {
        return collectPending;
    }

    public void setCollectPending(boolean collectPending) {
        this.collectPending = collectPending;
    }
}