package com.example.ums;

public class Material {
    private int materialId;
    private String materialName;
    private String url;

    public Material(int materialId, String materialName, String url) {
        this.materialId = materialId;
        this.materialName = materialName;
        this.url = url;
    }

    public Material(String materialName, String url) {
        this.materialName = materialName;
        this.url = url;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
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
