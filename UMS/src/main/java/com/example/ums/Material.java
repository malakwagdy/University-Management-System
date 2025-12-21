package com.example.ums;

import com.google.firebase.database.PropertyName;

public class Material {
    private String materialId;
    private String materialName;
    private String url;

    public Material(String materialId, String materialName, String url) {
        this.materialId = materialId;
        this.materialName = materialName;
        this.url = url;
    }
    public String getMaterialId() {
        return materialId;
    }
    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }
    public String getMaterialName() {
        return materialName;
    }
    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }
    public String geturl() {
        return url;
    }
    public void seturl(String url) {
        this.url = url;
    }
}
