package com.zeeshan.coinbudget.model;

import java.util.List;

public class Budget {

    List<String> expenseName;
    List<String> dueDate;
    List<String> expense;
    List<String> income;

    public Budget() {
    }

    public Budget(List<String> expenseName, List<String> dueDate, List<String> expense, List<String> income) {
        this.expenseName = expenseName;
        this.dueDate = dueDate;
        this.expense = expense;
        this.income = income;
    }

    public List<String> getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(List<String> expenseName) {
        this.expenseName = expenseName;
    }

    public List<String> getDueDate() {
        return dueDate;
    }

    public void setDueDate(List<String> dueDate) {
        this.dueDate = dueDate;
    }

    public List<String> getExpense() {
        return expense;
    }

    public void setExpense(List<String> expense) {
        this.expense = expense;
    }

    public List<String> getIncome() {
        return income;
    }

    public void setIncome(List<String> income) {
        this.income = income;
    }

}
