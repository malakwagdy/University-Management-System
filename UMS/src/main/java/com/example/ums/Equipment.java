package com.example.ums;

import com.google.firebase.database.PropertyName;

public class Equipment {
    private String equipmentId;
    private String equipmentType;
    private String description;
    private boolean assigned;
    private String hall;
    private boolean maintenance;

    public Equipment(String equipmentId, String equipmentType, String description, boolean assigned, String hall, boolean maintenance) {
        this.equipmentId = equipmentId;
        this.equipmentType = equipmentType;
        this.description = description;
        this.assigned = assigned;
        this.hall = hall;
        this.maintenance = maintenance;
    }
    @PropertyName("equipmentId")
    public String getEquipmentId() {
        return equipmentId;
    }
    @PropertyName("equipmentId")
    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }
    @PropertyName("equipmentType")
    public String getEquipmentType() {
        return equipmentType;
    }
    @PropertyName("equipmentType")
    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }
    @PropertyName("description")
    public String getDescription() {
        return description;
    }
    @PropertyName("description")
    public void setDescription(String description) {
        this.description = description;
    }
    @PropertyName("assigned")
    public boolean isAssigned() {
        return assigned;
    }
    @PropertyName("assigned")
    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }
    @PropertyName("hall")
    public String getHall() {
        return hall;
    }
    @PropertyName("hall")
    public void setHall(String hall) {
        this.hall = hall;
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
