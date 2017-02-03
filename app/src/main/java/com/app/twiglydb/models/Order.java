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
    @SerializedName("walletTransaction")
    @Expose
    private Object walletTransaction;
    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lng")
    @Expose
    private double lng;
    @SerializedName("serviceTax")
    @Expose
    private double serviceTax;
    @SerializedName("vat")
    @Expose
    private double vat;
    @SerializedName("subTotal")
    @Expose
    private double subTotal;
    @SerializedName("deliveryCharges")
    @Expose
    private double deliveryCharges;
    @SerializedName("total")
    @Expose
    private double total;
    @SerializedName("isForLater")
    @Expose
    private boolean isForLater;
    @SerializedName("cashbackDone")
    @Expose
    private boolean cashbackDone;
    @SerializedName("flag")
    @Expose
    private int flat;
    @SerializedName("expectedDeliveryTime")
    @Expose
    private String expectedDeliveryTime;
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
    @SerializedName("orderTime")
    @Expose
    private String orderTime;
    @SerializedName("orderDate")
    @Expose
    private String orderDate;
    @SerializedName("deliveryRating")
    @Expose
    private int deliveryRating;
    @SerializedName("deliveryBoyNameId")
    @Expose
    private String deliveryBoyNameId;
    @SerializedName("orderId")
    @Expose
    private String orderId;
    @SerializedName("coupon_discount")
    @Expose
    private double couponDiscount;
    @SerializedName("paymentStatus")
    @Expose
    private String paymentStatus;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("coupon_code")
    @Expose
    private String couponCode;
    @SerializedName("wallet_amount")
    @Expose
    private double walletAmount;
    @SerializedName("deliveryTime")
    @Expose
    private String deliveryTime;
    @SerializedName("is_first_order")
    @Expose
    private String isFirstOrder;
    @SerializedName("addFreeItem")
    @Expose
    private boolean addFreeItem;
    @SerializedName("userPendingBalance")
    @Expose
    private double userPendingBalance;

    @SerializedName("isCheckedIn")
    @Expose
    public boolean isCheckedIn;

    @SerializedName("qrCode")
    @Expose
    private String qrCode;
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
     *     The walletTransaction
     */
    public Object getWalletTransaction() {
        return walletTransaction;
    }

    /**
     *
     * @param walletTransaction
     *     The walletTransaction
     */
    public void setWalletTransaction(Object walletTransaction) {
        this.walletTransaction = walletTransaction;
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
     *     The serviceTax
     */
    public double getServiceTax() {
        return serviceTax;
    }

    /**
     *
     * @param serviceTax
     *     The serviceTax
     */
    public void setServiceTax(double serviceTax) {
        this.serviceTax = serviceTax;
    }

    /**
     *
     * @return
     *     The vat
     */
    public double getVat() {
        return vat;
    }

    /**
     *
     * @param vat
     *     The vat
     */
    public void setVat(double vat) {
        this.vat = vat;
    }

    /**
     *
     * @return
     *     The subTotal
     */
    public double getSubTotal() {
        return subTotal;
    }

    /**
     *
     * @param subTotal
     *     The subTotal
     */
    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    /**
     *
     * @return
     *     The deliveryCharges
     */
    public double getDeliveryCharges() {
        return deliveryCharges;
    }

    /**
     *
     * @param deliveryCharges
     *     The deliveryCharges
     */
    public void setDeliveryCharges(double deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
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
     *     The expectedDeliveryTime
     */
    public String getExpectedDeliveryTime() {
        return expectedDeliveryTime;
    }

    /**
     *
     * @param expectedDeliveryTime
     *     The expectedDeliveryTime
     */
    public void setExpectedDeliveryTime(String expectedDeliveryTime) {
        this.expectedDeliveryTime = expectedDeliveryTime;
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
     *     The orderTime
     */
    public String getOrderTime() {
        return orderTime;
    }

    /**
     *
     * @param orderTime
     *     The orderTime
     */
    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    /**
     *
     * @return
     *     The orderDate
     */
    public String getOrderDate() {
        return orderDate;
    }

    /**
     *
     * @param orderDate
     *     The orderDate
     */
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
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
     *     The couponDiscount
     */
    public double getCouponDiscount() {
        return couponDiscount;
    }

    /**
     *
     * @param couponDiscount
     *     The coupon_discount
     */
    public void setCouponDiscount(double couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    /**
     *
     * @return
     *     The paymentStatus
     */
    public String getPaymentStatus() {
        return paymentStatus;
    }

    /**
     *
     * @param paymentStatus
     *     The paymentStatus
     */
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    /**
     *
     * @return
     *     The source
     */
    public String getSource() {
        return source;
    }

    /**
     *
     * @param source
     *     The source
     */
    public void setSource(String source) {
        this.source = source;
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
     *     The couponCode
     */
    public String getCouponCode() {
        return couponCode;
    }

    /**
     *
     * @param couponCode
     *     The coupon_code
     */
    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    /**
     *
     * @return
     *     The walletAmount
     */
    public double getWalletAmount() {
        return walletAmount;
    }

    /**
     *
     * @param walletAmount
     *     The wallet_amount
     */
    public void setWalletAmount(double walletAmount) {
        this.walletAmount = walletAmount;
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
     *     The isFirstOrder
     */
    public String getIsFirstOrder() {
        return isFirstOrder;
    }

    /**
     *
     * @param isFirstOrder
     *     The is_first_order
     */
    public void setIsFirstOrder(String isFirstOrder) {
        this.isFirstOrder = isFirstOrder;
    }

    /**
     *
     * @return
     *     The addFreeItem
     */
    public boolean isAddFreeItem() {
        return addFreeItem;
    }

    /**
     *
     * @param addFreeItem
     *     The addFreeItem
     */
    public void setAddFreeItem(boolean addFreeItem) {
        this.addFreeItem = addFreeItem;
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
        walletTransaction = (Object) in.readValue(Object.class.getClassLoader());
        mobileNumber = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        serviceTax = in.readDouble();
        vat = in.readDouble();
        subTotal = in.readDouble();
        deliveryCharges = in.readDouble();
        total = in.readDouble();
        isForLater = in.readByte() != 0x00;
        cashbackDone = in.readByte() != 0x00;
        flat = in.readInt();
        expectedDeliveryTime = in.readString();
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
        orderTime = in.readString();
        orderDate = in.readString();
        deliveryRating = in.readInt();
        deliveryBoyNameId = in.readString();
        orderId = in.readString();
        couponDiscount = in.readDouble();
        paymentStatus = in.readString();
        source = in.readString();
        name = in.readString();
        couponCode = in.readString();
        walletAmount = in.readDouble();
        deliveryTime = in.readString();
        isFirstOrder = in.readString();
        addFreeItem = in.readByte() != 0x00;
        userPendingBalance = in.readDouble();
        isCheckedIn = in.readByte() != 0x00;
        qrCode = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeValue(walletTransaction);
        dest.writeString(mobileNumber);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeDouble(serviceTax);
        dest.writeDouble(vat);
        dest.writeDouble(subTotal);
        dest.writeDouble(deliveryCharges);
        dest.writeDouble(total);
        dest.writeByte((byte) (isForLater ? 0x01 : 0x00));
        dest.writeByte((byte) (cashbackDone ? 0x01 : 0x00));
        dest.writeInt(flat);
        dest.writeString(expectedDeliveryTime);
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
        dest.writeString(orderTime);
        dest.writeString(orderDate);
        dest.writeInt(deliveryRating);
        dest.writeString(deliveryBoyNameId);
        dest.writeString(orderId);
        dest.writeDouble(couponDiscount);
        dest.writeString(paymentStatus);
        dest.writeString(source);
        dest.writeString(name);
        dest.writeString(couponCode);
        dest.writeDouble(walletAmount);
        dest.writeString(deliveryTime);
        dest.writeString(isFirstOrder);
        dest.writeByte((byte) (addFreeItem ? 0x01 : 0x00));
        dest.writeDouble(userPendingBalance);
        dest.writeByte((byte) (isCheckedIn ? 0x01 : 0x00));
        dest.writeString(qrCode);
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