package com.zeeshan.coinbudget.model;

public class Income {
    private String userID;
    private String incomeAmount;
    private String frequency;
    private String description;


    public Income(){}

    public Income(String userID, String incomeAmount, String frequency, String description) {
        this.userID = userID;
        this.incomeAmount = incomeAmount;
        this.frequency = frequency;
        this.description = description;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getIncomeAmount() {
        return incomeAmount;
    }

    public void setIncomeAmount(String incomeAmount) {
        this.incomeAmount = incomeAmount;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
