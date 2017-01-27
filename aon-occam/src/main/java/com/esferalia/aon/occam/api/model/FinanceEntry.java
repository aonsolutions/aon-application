package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.TreeMap;

import com.esferalia.aon.occam.api.model.finance.Finance;

public class FinanceEntry implements Serializable, IAccountEntryWrapper {
	
	private static final long serialVersionUID = -6427093207922812377L;

	private AccountEntry accountEntry;
	
	private boolean multipleGeneration;
	private Integer financeBatch;
	
	private Account bankAccount;
	private double expenses;
	private Account expensesAccount;
	
	private TreeMap<Integer,Finance> finances;
	
	@Override
	public AccountEntry getAccountEntry() {
		return accountEntry;
	}
	@Override
	public void setAccountEntry(AccountEntry accountEntry) {
		this.accountEntry = accountEntry;
	}
	
	public boolean isMultipleGeneration() {
		return multipleGeneration;
	}
	public FinanceEntry setMultipleGeneration(boolean multipleGeneration) {
		this.multipleGeneration = multipleGeneration;
		return this;
	}
	public Integer getFinanceBatch() {
		return financeBatch;
	}
	public FinanceEntry setFinanceBatch(Integer financeBatch) {
		this.financeBatch = financeBatch;
		return this;
	}
	public boolean isFromfinanceBatch() {
		return getFinanceBatch() != null;
	}
	public Account getBankAccount() {
		return bankAccount;
	}
	public FinanceEntry setBankAccount(Account bankAccount) {
		this.bankAccount = bankAccount;
		return this;
	}
	public double getExpenses() {
		return expenses;
	}
	public FinanceEntry setExpenses(double expenses) {
		this.expenses = expenses;
		return this;
	}
	public Account getExpensesAccount() {
		return expensesAccount;
	}
	public FinanceEntry setExpensesAccount(Account expensesAccount) {
		this.expensesAccount = expensesAccount;
		return this;
	}
	public TreeMap<Integer,Finance> getFinances() {
		if (finances == null) {
			finances = new TreeMap<Integer,Finance>();
		}
		return finances;
	}
	public FinanceEntry setFinances(TreeMap<Integer,Finance> finances) {
		this.finances = finances;
		return this;
	}
	public void add(Finance finance) {
		getFinances().put(finance.getId(), finance);
	}
	public void remove(Finance finance) {
		getFinances().remove(finance.getId());
	}

	public static FinanceEntry clone(FinanceEntry ori) {
		FinanceEntry entry = new FinanceEntry();
		entry.setAccountEntry( AccountEntry.clone(entry.getAccountEntry()) );
		return entry.setMultipleGeneration(ori.isMultipleGeneration())
			.setBankAccount(ori.getBankAccount())
			.setExpenses(ori.getExpenses())
			.setExpensesAccount(ori.getExpensesAccount());
	}
}
