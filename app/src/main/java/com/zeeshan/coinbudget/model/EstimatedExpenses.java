package com.zeeshan.coinbudget.model;

public class EstimatedExpenses {
    private String userID;
    private String expenseName;
    private String expenseAmount;
    private String description;
    private Boolean isPremium;
    private String totalEstimatedExpenseAmount;

    public EstimatedExpenses(){}

    public EstimatedExpenses(String userID, String expenseName, String expenseAmount, String description, Boolean isPremium, String totalEstimatedExpenseAmount) {
        this.userID = userID;
        this.expenseName = expenseName;
        this.expenseAmount = expenseAmount;
        this.description = description;
        this.isPremium = isPremium;
        this.totalEstimatedExpenseAmount = totalEstimatedExpenseAmount;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public String getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(String expenseAmount) {
        this.expenseAmount = expenseAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getPremium() {
        return isPremium;
    }

    public void setPremium(Boolean premium) {
        isPremium = premium;
    }

    public String getTotalEstimatedExpenseAmount() {
        return totalEstimatedExpenseAmount;
    }

    public void setTotalEstimatedExpenseAmount(String totalEstimatedExpenseAmount) {
        this.totalEstimatedExpenseAmount = totalEstimatedExpenseAmount;
    }
}
