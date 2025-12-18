package com.example.ums;
public class Classroom {

    private int hallId;
    private String hallCapacity;
    private String hallType;
    private boolean hallMaintenance;
    private boolean availability;

    public Classroom(int hallId, String hallCapacity, String hallType,
                     boolean hallMaintenance, boolean availability) {
        this.hallId = hallId;
        this.hallCapacity = hallCapacity;
        this.hallType = hallType;
        this.hallMaintenance = hallMaintenance;
        this.availability = availability;
    }

    public int getHallId() { return hallId; }
    public String getHallCapacity() { return hallCapacity; }
    public String getHallType() { return hallType; }
    public boolean isHallMaintenance() { return hallMaintenance; }
    public boolean isAvailability() { return availability; }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }
}
