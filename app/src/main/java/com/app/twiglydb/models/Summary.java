package com.app.twiglydb.models;

/**
 * Created by abhishek on 29-07-2016.
 */

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Summary {

    @SerializedName("amountCash")
    @Expose
    private Double amountCash;
    @SerializedName("countCash")
    @Expose
    private Integer countCash;
    @SerializedName("amountCard")
    @Expose
    private Double amountCard;
    @SerializedName("countCard")
    @Expose
    private Integer countCard;
    @SerializedName("amountOnline")
    @Expose
    private Double amountOnline;
    @SerializedName("countOnline")
    @Expose
    private Integer countOnline;
    @SerializedName("amountTotal")
    @Expose
    private Double amountTotal;
    @SerializedName("countTotal")
    @Expose
    private Integer countTotal;
    @SerializedName("amountFree")
    @Expose
    private Double amountFree;
    @SerializedName("countFree")
    @Expose
    private Integer countFree;
    @SerializedName("amountPending")
    @Expose
    private Double amountPending;
    @SerializedName("countPending")
    @Expose
    private Integer countPending;
    @SerializedName("amountPendingCollected")
    @Expose
    private Double amountPendingCollected;
    @SerializedName("countPendingCollected")
    @Expose
    private Integer countPendingCollected;
    @SerializedName("amountSettlement")
    @Expose
    private Double amountSettlement;

    /**
     *
     * @return
     * The amountCash
     */
    public Double getAmountCash() {
        return amountCash;
    }

    /**
     *
     * @param amountCash
     * The amountCash
     */
    public void setAmountCash(Double amountCash) {
        this.amountCash = amountCash;
    }

    /**
     *
     * @return
     * The countCash
     */
    public Integer getCountCash() {
        return countCash;
    }

    /**
     *
     * @param countCash
     * The countCash
     */
    public void setCountCash(Integer countCash) {
        this.countCash = countCash;
    }

    /**
     *
     * @return
     * The amountCard
     */
    public Double getAmountCard() {
        return amountCard;
    }

    /**
     *
     * @param amountCard
     * The amountCard
     */
    public void setAmountCard(Double amountCard) {
        this.amountCard = amountCard;
    }

    /**
     *
     * @return
     * The countCard
     */
    public Integer getCountCard() {
        return countCard;
    }

    /**
     *
     * @param countCard
     * The countCard
     */
    public void setCountCard(Integer countCard) {
        this.countCard = countCard;
    }

    /**
     *
     * @return
     * The amountOnline
     */
    public Double getAmountOnline() {
        return amountOnline;
    }

    /**
     *
     * @param amountOnline
     * The amountOnline
     */
    public void setAmountOnline(Double amountOnline) {
        this.amountOnline = amountOnline;
    }

    /**
     *
     * @return
     * The countOnline
     */
    public Integer getCountOnline() {
        return countOnline;
    }

    /**
     *
     * @param countOnline
     * The countOnline
     */
    public void setCountOnline(Integer countOnline) {
        this.countOnline = countOnline;
    }

    /**
     *
     * @return
     * The amountTotal
     */
    public Double getAmountTotal() {
        return amountTotal;
    }

    /**
     *
     * @param amountTotal
     * The amountTotal
     */
    public void setAmountTotal(Double amountTotal) {
        this.amountTotal = amountTotal;
    }

    /**
     *
     * @return
     * The countTotal
     */
    public Integer getCountTotal() {
        return countTotal;
    }

    /**
     *
     * @param countTotal
     * The countTotal
     */
    public void setCountTotal(Integer countTotal) {
        this.countTotal = countTotal;
    }

    /**
     *
     * @return
     * The amountFree
     */
    public Double getAmountFree() {
        return amountFree;
    }

    /**
     *
     * @param amountFree
     * The amountFree
     */
    public void setAmountFree(Double amountFree) {
        this.amountFree = amountFree;
    }

    /**
     *
     * @return
     * The countFree
     */
    public Integer getCountFree() {
        return countFree;
    }

    /**
     *
     * @param countFree
     * The countFree
     */
    public void setCountFree(Integer countFree) {
        this.countFree = countFree;
    }

    /**
     *
     * @return
     * The amountPending
     */
    public Double getAmountPending() {
        return amountPending;
    }

    /**
     *
     * @param amountPending
     * The amountPending
     */
    public void setAmountPending(Double amountPending) {
        this.amountPending = amountPending;
    }

    /**
     *
     * @return
     * The countPending
     */
    public Integer getCountPending() {
        return countPending;
    }

    /**
     *
     * @param countPending
     * The countPending
     */
    public void setCountPending(Integer countPending) {
        this.countPending = countPending;
    }

    /**
     *
     * @return
     * The amountPendingCollected
     */
    public Double getAmountPendingCollected() {
        return amountPendingCollected;
    }

    /**
     *
     * @param amountPendingCollected
     * The amountPendingCollected
     */
    public void setAmountPendingCollected(Double amountPendingCollected) {
        this.amountPendingCollected = amountPendingCollected;
    }

    /**
     *
     * @return
     * The countPendingCollected
     */
    public Integer getCountPendingCollected() {
        return countPendingCollected;
    }

    /**
     *
     * @param countPendingCollected
     * The countPendingCollected
     */
    public void setCountPendingCollected(Integer countPendingCollected) {
        this.countPendingCollected = countPendingCollected;
    }

    /**
     *
     * @return
     * The amountSettlement
     */
    public Double getAmountSettlement() {
        return amountSettlement;
    }

    /**
     *
     * @param amountSettlement
     * The amountSettlement
     */
    public void setAmountSettlement(Double amountSettlement) {
        this.amountSettlement = amountSettlement;
    }

}