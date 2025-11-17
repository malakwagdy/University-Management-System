package com.example.ums;

import com.google.firebase.database.PropertyName;

import java.sql.Timestamp;

public class Software {
    private String softwareName;
    private String description;
    private boolean active;
    private Timestamp expiryDate;

    public Software(String softwareName, String description, boolean active, Timestamp expiryDate) {
        this.softwareName = softwareName;
        this.description = description;
        this.active = active;
        this.expiryDate = expiryDate;
    }
    @PropertyName("softwareName")
    public String getSoftwareName() {
        return softwareName;
    }
    @PropertyName("softwareName")
    public void setSoftwareName(String softwareName) {
        this.softwareName = softwareName;
    }
    @PropertyName("description")
    public String getDescription() {
        return description;
    }
    @PropertyName("description")
    public void setDescription(String description) {
        this.description = description;
    }
    @PropertyName("active")
    public boolean isActive() {
        return active;
    }
    @PropertyName("active")
    public void setActive(boolean active) {
        this.active = active;
    }
    @PropertyName("expiryDate")
    public Timestamp getExpiryDate() {
        return expiryDate;
    }
    @PropertyName("expiryDate")
    public void setExpiryDate(Timestamp expiryDate) {
        this.expiryDate = expiryDate;
    }
}
