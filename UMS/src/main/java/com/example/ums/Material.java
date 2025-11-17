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
    @PropertyName("materialId")
    public String getMaterialId() {
        return materialId;
    }
    @PropertyName("materialId")
    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }
    @PropertyName("materialName")
    public String getMaterialName() {
        return materialName;
    }
    @PropertyName("materialName")
    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }
    @PropertyName("url")
    public String geturl() {
        return url;
    }
    @PropertyName("url")
    public void seturl(String url) {
        this.url = url;
    }
}
