package com.code.aon.accounting;

import java.util.Date;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.util.CommonUtil;

public class LoanFeeEntryHeader implements ITransferObject {

	private static final long serialVersionUID = -8273893100974866857L;

	private Period feePeriod;
	private Date feeDate;
	private Loan loan;
	private String description;
	private double amortization;
	private double interest;
	private Account interestAccount;
	private double expenses;
	private Account expensesAccount;

	public Period getFeePeriod() {
		return feePeriod;
	}
	public void setFeePeriod(Period feePeriod) {
		this.feePeriod = feePeriod;
	}

	public Date getFeeDate() {
		return feeDate;
	}
	public void setFeeDate(Date feeDate) {
		this.feeDate = feeDate;
	}

	public Loan getLoan() {
		return loan;
	}
	public void setLoan(Loan loan) {
		this.loan = loan;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public double getInterest() {
		return interest;
	}
	public void setInterest(double interest) {
		this.interest = interest;
	}

	public Account getInterestAccount() {
		return interestAccount;
	}
	public void setInterestAccount(Account interestAccount) {
		this.interestAccount = interestAccount;
	}

	public double getAmortization() {
		return amortization;
	}
	public void setAmortization(double amortization) {
		this.amortization = amortization;
	}

	public double getExpenses() {
		return expenses;
	}
	public void setExpenses(double expenses) {
		this.expenses = expenses;
	}
	public Account getExpensesAccount() {
		return expensesAccount;
	}
	public void setExpensesAccount(Account expensesAccount) {
		this.expensesAccount = expensesAccount;
	}

	public double getFee(){
		return CommonUtil.round( getAmortization() + getInterest() + getExpenses());
	}

	public boolean isSameAmount(){
		if (getLoan() == null || getLoan().getId() == null) {
			return false;
		}
		double a = getFee();
		double b = getLoan().getFeeAmount();
		return ( CommonUtil.round(a) == CommonUtil.round(b) );
	}

}