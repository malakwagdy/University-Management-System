package com.example.ums;

import com.google.firebase.database.PropertyName;

public class Issue {
    private String issueId;
    private String hallId;
    private String description;
    private String issuer;

    public Issue(String issueId, String description, String hallId, String issuer) {
        this.issueId = issueId;
        this.description = description;
        this.hallId = hallId;
        this.issuer = issuer;
    }
    public String getIssueId() {
        return issueId;
    }
    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getHallId() {
        return hallId;
    }
    public void setHallId(String hallId) {
        this.hallId = hallId;
    }
    public String getIssuer() {
        return issuer;
    }
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
