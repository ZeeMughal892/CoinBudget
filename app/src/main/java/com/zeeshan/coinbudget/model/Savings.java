package com.zeeshan.coinbudget.model;

public class Savings {
    private String userID;
    private String savingGoalTitle;
    private String amountToSave;
    private String goalDate;
    private Boolean isPremium;

    public Savings(){}

    public Savings(String userID, String savingGoalTitle, String amountToSave, String goalDate, Boolean isPremium) {
        this.userID = userID;
        this.savingGoalTitle = savingGoalTitle;
        this.amountToSave = amountToSave;
        this.goalDate = goalDate;
        this.isPremium = isPremium;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getSavingGoalTitle() {
        return savingGoalTitle;
    }

    public void setSavingGoalTitle(String savingGoalTitle) {
        this.savingGoalTitle = savingGoalTitle;
    }

    public String getAmountToSave() {
        return amountToSave;
    }

    public void setAmountToSave(String amountToSave) {
        this.amountToSave = amountToSave;
    }

    public String getGoalDate() {
        return goalDate;
    }

    public void setGoalDate(String goalDate) {
        this.goalDate = goalDate;
    }

    public Boolean getPremium() {
        return isPremium;
    }

    public void setPremium(Boolean premium) {
        isPremium = premium;
    }
}
