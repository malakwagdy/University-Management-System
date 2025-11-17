package com.example.ums;

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

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }
}
