package com.zeeshan.coinbudget.model;

public class BankAccount {
    private String bankAccountId;
    private String userId;
    private String amount;
    private String date;

    public BankAccount(){}

    public BankAccount(String bankAccountId, String userId, String amount, String date) {
        this.bankAccountId = bankAccountId;
        this.userId = userId;
        this.amount = amount;
        this.date = date;
    }

    public String getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(String bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
