package com.zeeshan.coinbudget.model;

public class Transactions {
    private String userID;
    private String transactionID;
    private String transactionName;
    private String transactionAmount;
    private String notes;
    private Boolean isPremium;
    private String transactionDay;

    public Transactions(){}

    public Transactions(String transactionID,String userID, String transactionName, String transactionAmount, String notes, Boolean isPremium, String transactionDay) {
        this.transactionID = transactionID;
        this.userID = userID;
        this.transactionName = transactionName;
        this.transactionAmount = transactionAmount;
        this.notes = notes;
        this.isPremium = isPremium;
        this.transactionDay = transactionDay;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
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

    public String getTransactionDay() {
        return transactionDay;
    }

    public void setTransactionDay(String transactionDay) {
        this.transactionDay = transactionDay;
    }
}
