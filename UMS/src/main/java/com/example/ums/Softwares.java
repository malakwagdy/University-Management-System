package com.example.ums;

import java.sql.Timestamp;

public class Softwares {
    private String softwareName;
    private String description;
    private boolean active;
    private Timestamp expiryDate;

    public Softwares(String softwareName, String description, boolean active, Timestamp expiryDate) {
        this.softwareName = softwareName;
        this.description = description;
        this.active = active;
        this.expiryDate = expiryDate;
    }

    public String getSoftwareName() {
        return softwareName;
    }

    public void setSoftwareName(String softwareName) {
        this.softwareName = softwareName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Timestamp expiryDate) {
        this.expiryDate = expiryDate;
    }
}
