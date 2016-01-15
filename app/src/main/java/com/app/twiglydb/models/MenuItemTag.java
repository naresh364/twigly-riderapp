
package com.app.twiglydb.models;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class MenuItemTag {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("priority")
    @Expose
    private int priority;
    @SerializedName("isVisible")
    @Expose
    private boolean isVisible;
    @SerializedName("mandatoryForOrder")
    @Expose
    private boolean mandatoryForOrder;
    @SerializedName("tagId")
    @Expose
    private int tagId;

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
     *     The priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * 
     * @param priority
     *     The priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * 
     * @return
     *     The isVisible
     */
    public boolean isIsVisible() {
        return isVisible;
    }

    /**
     * 
     * @param isVisible
     *     The isVisible
     */
    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    /**
     * 
     * @return
     *     The mandatoryForOrder
     */
    public boolean isMandatoryForOrder() {
        return mandatoryForOrder;
    }

    /**
     * 
     * @param mandatoryForOrder
     *     The mandatoryForOrder
     */
    public void setMandatoryForOrder(boolean mandatoryForOrder) {
        this.mandatoryForOrder = mandatoryForOrder;
    }

    /**
     * 
     * @return
     *     The tagId
     */
    public int getTagId() {
        return tagId;
    }

    /**
     * 
     * @param tagId
     *     The tagId
     */
    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

}
