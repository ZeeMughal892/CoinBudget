package com.zeeshan.coinbudget.model;


public class ExtraIncome {
    private String userID;
    private String incomeID;
    private String incomeSource;
    private String extraAmount;
    private String notes;
    private Boolean isPremium;
    private String incomeDay;

    public ExtraIncome() {
    }

    public ExtraIncome(String incomeID, String userID, String incomeSource, String extraAmount, String notes, Boolean isPremium, String incomeDay) {
        this.userID = userID;
        this.incomeSource = incomeSource;
        this.extraAmount = extraAmount;
        this.notes = notes;
        this.isPremium = isPremium;
        this.incomeDay = incomeDay;
        this.incomeID = incomeID;
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

    public String getIncomeSource() {
        return incomeSource;
    }

    public void setIncomeSource(String incomeSource) {
        this.incomeSource = incomeSource;
    }

    public String getExtraAmount() {
        return extraAmount;
    }

    public void setExtraAmount(String extraAmount) {
        this.extraAmount = extraAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getPremium() {
        return isPremium;
    }

    public void setPremium(Boolean premium) {
        isPremium = premium;
    }

    public String getIncomeDay() {
        return incomeDay;
    }

    public void setIncomeDay(String incomeDay) {
        this.incomeDay = incomeDay;
    }
}
