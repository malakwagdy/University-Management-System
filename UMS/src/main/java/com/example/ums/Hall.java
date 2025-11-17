package com.example.ums;

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

    public String getHallId() {
        return hallId;
    }

    public void setHallId(String hallId) {
        this.hallId = hallId;
    }

    public String getHallCapacity() {
        return hallCapacity;
    }

    public void setHallCapacity(String hallCapacity) {
        this.hallCapacity = hallCapacity;
    }

    public Map<Timestamp, Boolean> getSlots() {
        return slots;
    }

    public void setSlots(Map<Timestamp, Boolean> slots) {
        this.slots = slots;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }
}
