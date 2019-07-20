package com.zeeshan.coinbudget.model;

public class Income {
    private String incomeID;
    private String userID;
    private String incomeAmount;
    private String frequency;
    private String dateOfIncome;
    private String description;

    public Income(){}

    public Income(String incomeID, String userID, String incomeAmount, String frequency, String dateOfIncome, String description) {
        this.incomeID = incomeID;
        this.userID = userID;
        this.incomeAmount = incomeAmount;
        this.frequency = frequency;
        this.dateOfIncome = dateOfIncome;
        this.description = description;
    }

    public String getIncomeID() {
        return incomeID;
    }

    public void setIncomeID(String incomeID) {
        this.incomeID = incomeID;
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

    public String getDateOfIncome() {
        return dateOfIncome;
    }

    public void setDateOfIncome(String dateOfIncome) {
        this.dateOfIncome = dateOfIncome;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
