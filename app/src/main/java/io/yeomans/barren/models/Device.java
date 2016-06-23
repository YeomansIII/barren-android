package io.yeomans.barren.models;

/**
 * Created by jason on 6/20/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Device {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("fcmid")
    @Expose
    private String fcmid;

    public Device(String fcmid) {
        this.fcmid = fcmid;
    }

    public Device(String name, String owner) {
        this.name = name;
        this.owner = owner;
    }

    public Device(String name, String owner, String fcmid) {
        this.name = name;
        this.owner = owner;
        this.fcmid = fcmid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner The owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @return The fcmid
     */
    public String getFcmid() {
        return fcmid;
    }

    /**
     * @param fcmid The fcmid
     */
    public void setFcmid(String fcmid) {
        this.fcmid = fcmid;
    }

}