package com.zeeshan.coinbudget.model;

public class EstimatedExpenses {

    private String estimateId;
    private String userID;
    private String expenseName;
    private String expenseAmount;
    private String description;

    public EstimatedExpenses(){}

    public EstimatedExpenses(String estimateId,String userID, String expenseName, String expenseAmount, String description) {
        this.estimateId = estimateId;
        this.userID = userID;
        this.expenseName = expenseName;
        this.expenseAmount = expenseAmount;
        this.description = description;
    }

    public String getEstimateId() {
        return estimateId;
    }

    public void setEstimateId(String estimateId) {
        this.estimateId = estimateId;
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


}
