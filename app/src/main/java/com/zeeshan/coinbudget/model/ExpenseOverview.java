package com.zeeshan.coinbudget.model;

public class ExpenseOverview {
    private String userID;
    private String remainingAmount;
    private String remainingDays;
    private String averageExpenseAmount;

    public ExpenseOverview(){}

    public ExpenseOverview(String userID, String remainingAmount, String remainingDays, String averageExpenseAmount) {
        this.userID = userID;
        this.remainingAmount = remainingAmount;
        this.remainingDays = remainingDays;
        this.averageExpenseAmount = averageExpenseAmount;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(String remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public String getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(String remainingDays) {
        this.remainingDays = remainingDays;
    }

    public String getAverageExpenseAmount() {
        return averageExpenseAmount;
    }

    public void setAverageExpenseAmount(String averageExpenseAmount) {
        this.averageExpenseAmount = averageExpenseAmount;
    }
}
