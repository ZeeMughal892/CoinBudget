package com.zeeshan.coinbudget.model;

public class Savings {
    private String savingId;
    private String userID;
    private String savingGoalTitle;
    private String amountToSave;
    private String goalDate;

    public Savings() {
    }

    public Savings(String savingId, String userID, String savingGoalTitle, String amountToSave, String goalDate) {
        this.savingId = savingId;
        this.userID = userID;
        this.savingGoalTitle = savingGoalTitle;
        this.amountToSave = amountToSave;
        this.goalDate = goalDate;
    }

    public String getSavingId() {
        return savingId;
    }

    public void setSavingId(String savingId) {
        this.savingId = savingId;
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

}
