package com.app.twiglydb.models;

/**
 * Created by naresh on 30/04/17.
 */

public class LocationParamas {
    long updateInterval;
    long offJobInterval;
    String checklisturl;

    public long getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(long updateInterval) {
        this.updateInterval = updateInterval;
    }

    public long getOffJobInterval() {
        return offJobInterval;
    }

    public void setOffJobInterval(long offJobInterval) {
        this.offJobInterval = offJobInterval;
    }

    public String getChecklisturl() {
        return checklisturl;
    }

    public void setChecklisturl(String checklisturl) {
        this.checklisturl = checklisturl;
    }
}
