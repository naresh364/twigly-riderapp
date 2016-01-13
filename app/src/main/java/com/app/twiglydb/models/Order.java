package com.app.twiglydb.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by twigly on 23/7/15.
 */
public class Order {
    int orderId;
    String name;
    String address;
    WalletTransaction walletTransaction;
    String mobile_number;
    float serviceTax;
    float vat;
    float subTotal;
    float deliveryCharges;
    float total;
    float coupon_discount;
    List<OrderItem> orderDetails;

    String orderStatus;
    transient OrderStatus orderStatusEnum;

    String paymentOption;
    transient  PaymentOption paymentOptionEnum;

    String date_add;

    boolean isForLater;
    String deliveryTime;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public WalletTransaction getWalletTransaction() {
        return walletTransaction;
    }

    public void setWalletTransaction(WalletTransaction walletTransaction) {
        this.walletTransaction = walletTransaction;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public float getServiceTax() {
        return serviceTax;
    }

    public void setServiceTax(float serviceTax) {
        this.serviceTax = serviceTax;
    }

    public float getVat() {
        return vat;
    }

    public void setVat(float vat) {
        this.vat = vat;
    }

    public float getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(float subTotal) {
        this.subTotal = subTotal;
    }

    public float getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(float deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public List<OrderItem> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderItem> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public String getOrderStatus() {
        if (orderStatusEnum != null) {
            orderStatus = orderStatusEnum.name();
        }
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
        this.orderStatusEnum = OrderStatus.valueOf(orderStatus);
    }

    public OrderStatus getOrderStatusEnum() {
        if (orderStatusEnum == null) this.orderStatusEnum = OrderStatus.valueOf(this.orderStatus);
        return orderStatusEnum;
    }

    public void setOrderStatusEnum(OrderStatus orderStatusEnum) {
        this.orderStatusEnum = (orderStatusEnum==null)?OrderStatus.RECEIVED:orderStatusEnum;
        this.orderStatus = this.orderStatusEnum.name();
    }

    public String getPaymentOption() {
        if (paymentOptionEnum!=null) {
            paymentOption = paymentOptionEnum.name();
        }
        return paymentOption;
    }

    public void setPaymentOption(String paymentOption) {
        this.paymentOption = paymentOption;
        this.paymentOptionEnum = PaymentOption.valueOf(paymentOption);
    }

    public PaymentOption getPaymentOptionEnum() {
        if (paymentOptionEnum == null) paymentOptionEnum = PaymentOption.valueOf(paymentOption);
        return paymentOptionEnum;
    }

    public void setPaymentOptionEnum(PaymentOption paymentOptionEnum) {
        this.paymentOptionEnum = (paymentOptionEnum==null)?PaymentOption.COD:paymentOptionEnum;
        this.paymentOption = this.paymentOptionEnum.name();
    }

    public String getDate_add() {
        return date_add;
    }

    public void setDate_add(String date_add) {
        this.date_add = date_add;
    }

    public String getOrderDate() {
        String newDate = "";
        if (date_add != null || !date_add.trim().equals("")) {
            try {
                DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy  hh:mm aaa");
                newDate = dateFormat.format(new Date(Long.parseLong(date_add)));
            }catch (Exception ex){
                Timber.e("Unable to pass order date");
            }
        }
        return newDate;
    }

    public float getCoupon_discount() {
        return coupon_discount;
    }

    public void setCoupon_discount(float coupon_discount) {
        this.coupon_discount = coupon_discount;
    }

    public boolean isForLater() {
        return isForLater;
    }

    public void setIsForLater(boolean isForLater) {
        this.isForLater = isForLater;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String expectedDeliveryTime) {
        this.deliveryTime = expectedDeliveryTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
