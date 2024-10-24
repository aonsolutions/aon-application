package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.TreeMap;

public class FinanceEntry implements Serializable, IAccountEntryWrapper {
	
	private static final long serialVersionUID = -6427093207922812377L;

	private AccountEntry accountEntry;
	
	private boolean multipleGeneration;
	private Integer financeBatch;
	
	private Account bankAccount;
	private double expenses;
	private Account expensesAccount;
	private String manualConcept;
	
	private TreeMap<Integer,FinanceTracking> trackings;
	
	@Override
	public AccountEntry getAccountEntry() {
		return accountEntry;
	}
	@Override
	public void setAccountEntry(AccountEntry accountEntry) {
		this.accountEntry = accountEntry;
	}
	
	@Override
	public LinkedList<AccountEntry> getAccountEntries() {
		LinkedList<AccountEntry> list = new LinkedList<>();
		list.add(getAccountEntry());
		return list;
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
	public boolean isFromFinanceBatch() {
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
	public String getManualConcept() {
		return manualConcept;
	}
	public FinanceEntry setManualConcept(String manualConcept) {
		this.manualConcept = manualConcept;
		return this;
	}
	public TreeMap<Integer,FinanceTracking> getTrackings() {
		if (trackings == null) {
			trackings = new TreeMap<>();
		}
		return trackings;
	}
	public FinanceEntry setTrackings(TreeMap<Integer,FinanceTracking> trackings) {
		this.trackings = trackings;
		return this;
	}
	public void add(Finance finance) {
		getTrackings().put(finance.getId(), 
			new FinanceTracking()
			.setFinance(finance)
			.setChecked(true)
			.setLastTracking(true))
		;
	}
	public void add(FinanceTracking tracking) {
		getTrackings().put(tracking.getFinance().getId(), 
			tracking
			.setChecked(true)
			.setLastTracking(true))
		;
	}
	public void remove(Finance finance) {
		getTrackings().remove(finance.getId());
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
