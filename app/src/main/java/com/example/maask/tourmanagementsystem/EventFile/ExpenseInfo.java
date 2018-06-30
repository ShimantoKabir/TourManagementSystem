package com.example.maask.tourmanagementsystem.EventFile;

/**
 * Created by Maask on 2/7/2018.
 */

public class ExpenseInfo {

    String expenseTitle;
    String expenseAmount;
    String expenseParentName;
    String createdDate;

    public ExpenseInfo() {}

    public ExpenseInfo(String expenseTitle, String expenseAmount, String expenseParentName, String createdDate) {
        this.expenseTitle = expenseTitle;
        this.expenseAmount = expenseAmount;
        this.expenseParentName = expenseParentName;
        this.createdDate = createdDate;
    }

    public String getExpenseTitle() {
        return expenseTitle;
    }

    public void setExpenseTitle(String expenseTitle) {
        this.expenseTitle = expenseTitle;
    }

    public String getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(String expenseAmount) {
        this.expenseAmount = expenseAmount;
    }

    public String getExpenseParentName() {
        return expenseParentName;
    }

    public void setExpenseParentName(String expenseParentName) {
        this.expenseParentName = expenseParentName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
