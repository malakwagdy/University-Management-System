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
    @PropertyName("issueId")
    public String getIssueId() {
        return issueId;
    }
    @PropertyName("issueId")
    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }
    @PropertyName("description")
    public String getDescription() {
        return description;
    }
    @PropertyName("description")
    public void setDescription(String description) {
        this.description = description;
    }
    @PropertyName("hallId")
    public String getHallId() {
        return hallId;
    }
    @PropertyName("hallId")
    public void setHallId(String hallId) {
        this.hallId = hallId;
    }
    @PropertyName("issuer")
    public String getIssuer() {
        return issuer;
    }
    @PropertyName("issuer")
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
