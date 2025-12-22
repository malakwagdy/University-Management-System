package com.example.ums;

import java.sql.Timestamp;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
