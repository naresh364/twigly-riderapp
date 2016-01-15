
package com.app.twiglydb.models;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Item {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("imageURL")
    @Expose
    private String imageURL;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("calories")
    @Expose
    private int calories;
    @SerializedName("isActive")
    @Expose
    private boolean isActive;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("isCombo")
    @Expose
    private boolean isCombo;
    @SerializedName("menuItemTags")
    @Expose
    private List<MenuItemTag> menuItemTags = new ArrayList<MenuItemTag>();
    @SerializedName("itemSidesAsList")
    @Expose
    private List<String> itemSidesAsList = new ArrayList<String>();
    @SerializedName("menuItemId")
    @Expose
    private int menuItemId;

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
     *     The imageURL
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * 
     * @param imageURL
     *     The imageURL
     */
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * 
     * @return
     *     The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     *     The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 
     * @return
     *     The calories
     */
    public int getCalories() {
        return calories;
    }

    /**
     * 
     * @param calories
     *     The calories
     */
    public void setCalories(int calories) {
        this.calories = calories;
    }

    /**
     * 
     * @return
     *     The isActive
     */
    public boolean isIsActive() {
        return isActive;
    }

    /**
     * 
     * @param isActive
     *     The isActive
     */
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * 
     * @return
     *     The category
     */
    public String getCategory() {
        return category;
    }

    /**
     * 
     * @param category
     *     The category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * 
     * @return
     *     The isCombo
     */
    public boolean isIsCombo() {
        return isCombo;
    }

    /**
     * 
     * @param isCombo
     *     The isCombo
     */
    public void setIsCombo(boolean isCombo) {
        this.isCombo = isCombo;
    }

    /**
     * 
     * @return
     *     The menuItemTags
     */
    public List<MenuItemTag> getMenuItemTags() {
        return menuItemTags;
    }

    /**
     * 
     * @param menuItemTags
     *     The menuItemTags
     */
    public void setMenuItemTags(List<MenuItemTag> menuItemTags) {
        this.menuItemTags = menuItemTags;
    }

    /**
     * 
     * @return
     *     The itemSidesAsList
     */
    public List<String> getItemSidesAsList() {
        return itemSidesAsList;
    }

    /**
     * 
     * @param itemSidesAsList
     *     The itemSidesAsList
     */
    public void setItemSidesAsList(List<String> itemSidesAsList) {
        this.itemSidesAsList = itemSidesAsList;
    }

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

}
