package com.example.ums;

import com.google.firebase.database.PropertyName;

import java.sql.Timestamp;
import java.util.Map;

public class Hall {
    private String hallId;
    private String hallCapacity;
    private Map<Timestamp, Boolean> slots;
    private String type;
    private boolean maintenance;

    public Hall(String hallId, String hallCapacity, Map<Timestamp, Boolean> slots, String type, boolean maintenance) {
        this.hallId = hallId;
        this.hallCapacity = hallCapacity;
        this.slots = slots;
        this.type = type;
        this.maintenance = maintenance;
    }
    @PropertyName("hallId")
    public String getHallId() {
        return hallId;
    }
    @PropertyName("hallId")
    public void setHallId(String hallId) {
        this.hallId = hallId;
    }
    @PropertyName("hallCapacity")
    public String getHallCapacity() {
        return hallCapacity;
    }
    @PropertyName("hallCapacity")
    public void setHallCapacity(String hallCapacity) {
        this.hallCapacity = hallCapacity;
    }
    @PropertyName("slots")
    public Map<Timestamp, Boolean> getSlots() {
        return slots;
    }
    @PropertyName("slots")
    public void setSlots(Map<Timestamp, Boolean> slots) {
        this.slots = slots;
    }
    @PropertyName("type")
    public String getType() {
        return type;
    }
    @PropertyName("type")
    public void setType(String type) {
        this.type = type;
    }
    @PropertyName("maintenance")
    public boolean isMaintenance() {
        return maintenance;
    }
    @PropertyName("maintenance")
    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }
}
