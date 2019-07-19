package com.zeeshan.coinbudget.model;

public class Lookup {
    private String lookUpId;
    private String lookUpName;
    private String lookUpItemName;
    private String itemImage;
    private Boolean isPremium;

    public Lookup() {
    }

    public Lookup(String lookUpId, String lookUpName, String lookUpItemName, String itemImage, Boolean isPremium) {
        this.lookUpId = lookUpId;
        this.lookUpName = lookUpName;
        this.lookUpItemName = lookUpItemName;
        this.itemImage = itemImage;
        this.isPremium = isPremium;
    }

    public String getLookUpId() {
        return lookUpId;
    }

    public void setLookUpId(String lookUpId) {
        this.lookUpId = lookUpId;
    }

    public String getLookUpName() {
        return lookUpName;
    }

    public void setLookUpName(String lookUpName) {
        this.lookUpName = lookUpName;
    }

    public String getLookUpItemName() {
        return lookUpItemName;
    }

    public void setLookUpItemName(String lookUpItemName) {
        this.lookUpItemName = lookUpItemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public Boolean getPremium() {
        return isPremium;
    }

    public void setPremium(Boolean premium) {
        isPremium = premium;
    }
}
