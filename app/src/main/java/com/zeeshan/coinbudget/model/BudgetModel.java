package com.zeeshan.coinbudget.model;

import java.util.List;

public class BudgetModel {

   List<Income> incomeList;
   List<ExtraIncome> extraIncomeList;
   List<Transactions> transactionsList;

    public BudgetModel() {
    }

    public BudgetModel(List<Income> incomeList, List<ExtraIncome> extraIncomeList, List<Transactions> transactionsList) {
        this.incomeList = incomeList;
        this.extraIncomeList = extraIncomeList;
        this.transactionsList = transactionsList;
    }

    public List<Income> getIncomeList() {
        return incomeList;
    }

    public void setIncomeList(List<Income> incomeList) {
        this.incomeList = incomeList;
    }

    public List<ExtraIncome> getExtraIncomeList() {
        return extraIncomeList;
    }

    public void setExtraIncomeList(List<ExtraIncome> extraIncomeList) {
        this.extraIncomeList = extraIncomeList;
    }

    public List<Transactions> getTransactionsList() {
        return transactionsList;
    }

    public void setTransactionsList(List<Transactions> transactionsList) {
        this.transactionsList = transactionsList;
    }
}
