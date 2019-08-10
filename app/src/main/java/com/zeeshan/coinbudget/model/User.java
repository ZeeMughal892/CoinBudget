package com.zeeshan.coinbudget.model;

public class User {

    public String userID;
    public String fullName;
    public String email;
    public String currency;
    public String frequency;
    public String pin;
    public Boolean isPremium;


    public User() {
    }

    public User(String userID, String fullName, String email, String currency, String frequency, String pin, Boolean isPremium) {
        this.userID = userID;
        this.fullName = fullName;
        this.email = email;
        this.currency = currency;
        this.frequency = frequency;
        this.pin = pin;
        this.isPremium = isPremium;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Boolean getPremium() {
        return isPremium;
    }

    public void setPremium(Boolean premium) {
        isPremium = premium;
    }
}
