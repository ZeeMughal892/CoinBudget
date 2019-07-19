package com.zeeshan.coinbudget.model;

public class RecurringExpenses {
    private String expenseID;
    private String userID;
    private String expenseName;
    private String expenseAmount;
    private String frequency;
    private String dueDate;
    private String description;

    public RecurringExpenses() {
    }

    public RecurringExpenses(String expenseID,String userID, String expenseName, String expenseAmount, String frequency, String dueDate, String description) {
        this.expenseID = expenseID;
        this.userID = userID;
        this.expenseName = expenseName;
        this.expenseAmount = expenseAmount;
        this.frequency = frequency;
        this.dueDate = dueDate;
        this.description = description;
    }

    public String getExpenseID() {
        return expenseID;
    }

    public void setExpenseID(String expenseID) {
        this.expenseID = expenseID;
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

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
