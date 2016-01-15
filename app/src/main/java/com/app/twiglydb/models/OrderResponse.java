
package com.app.twiglydb.models;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class OrderResponse {

    @SerializedName("orders")
    @Expose
    private List<Order> orders = new ArrayList<Order>();
    @SerializedName("items")
    @Expose
    private List<Item> items = new ArrayList<Item>();
    @SerializedName("verifier")
    @Expose
    private String verifier;

    /**
     * 
     * @return
     *     The orders
     */
    public List<Order> getOrders() {
        return orders;
    }

    /**
     * 
     * @param orders
     *     The orders
     */
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    /**
     * 
     * @return
     *     The items
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * 
     * @param items
     *     The items
     */
    public void setItems(List<Item> items) {
        this.items = items;
    }

    /**
     * 
     * @return
     *     The verifier
     */
    public String getVerifier() {
        return verifier;
    }

    /**
     * 
     * @param verifier
     *     The verifier
     */
    public void setVerifier(String verifier) {
        this.verifier = verifier;
    }

}
