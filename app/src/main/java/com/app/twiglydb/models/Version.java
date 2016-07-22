package com.app.twiglydb.models;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by abhishek on 21-07-2016.
 */




@Generated("org.jsonschema2pojo")
public class Version {

    @SerializedName("version")
    @Expose
    private Integer version;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("password")
    @Expose
    private Integer password;

    /**
     *
     * @return
     * The version
     */
    public Integer getVersion() {
        return version;
    }

    /**
     *
     * @param version
     * The version
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *
     * @return
     * The password
     */
    public Integer getPassword() {
        return password;
    }

    /**
     *
     * @param password
     * The password
     */
    public void setPassword(Integer password) {
        this.password = password;
    }

}