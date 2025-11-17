package com.example.ums;

import java.sql.Timestamp;

import com.google.firebase.database.PropertyName;



public class LeaveRequest {
    private String id;
    private String issuer;
    private String handler;
    private Timestamp date;
    private String status;
    private String reason;

    public LeaveRequest(String id, String issuer, String handler, Timestamp date, String status, String reason) {
        this.id = id;
        this.issuer = issuer;
        this.handler = handler;
        this.date = date;
        this.status = status;
        this.reason = reason;
    }
    @PropertyName("id")
    public String getId() {
        return id;
    }
    @PropertyName("id")
    public void setId(String id) {
        this.id = id;
    }
    @PropertyName("issuer")
    public String getIssuer() {
        return issuer;
    }
    @PropertyName("issuer")
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
    @PropertyName("handler")
    public String getHandler() {
        return handler;
    }
    @PropertyName("handler")
    public void setHandler(String handler) {
        this.handler = handler;
    }
    @PropertyName("status")
    public String getStatus() {
        return status;
    }
    @PropertyName("status")
    public void setStatus(String status) {
        this.status = status;
    }
    @PropertyName("date")
    public Timestamp getDate() {
        return date;
    }
    @PropertyName("date")
    public void setDate(Timestamp date) {
        this.date = date;
    }
    @PropertyName("reason")
    public String getReason() {
        return reason;
    }
    @PropertyName("reason")
    public void setReason(String reason) {
        this.reason = reason;
    }
}
