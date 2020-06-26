package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.accounting.analytical.AnalyticalAccountLevel;
import com.esferalia.aon.watson.util.AonMathUtils;

public class AccountingAnalyticalStatement implements Serializable {

	private static final long serialVersionUID = -1037494768508954210L;
	
	private AccountOperatingAccount account; 
	private double debit;
	private double credit;
	private double percent;
	private AnalyticalAccountLevel level;
	private AnalyticalAccountLevel percentSource; 
	private String sourceCode; 
	
	public AccountingAnalyticalStatement duplicate( ) {
		return new AccountingAnalyticalStatement()
				.setAccount( getAccount() == null? null : getAccount().clone())
				.setDebit(getDebit())
				.setCredit(getCredit())
				.setLevel(getLevel())
				.setPercent(getPercent())
				.setPercentSource(getPercentSource() )
				.setSourceCode(getSourceCode())
				;
	}
	public boolean isCalculated() {
		return getAccount() != null && getAccount().getType() != null && getAccount().getType().isCalculated();
	}
	public boolean modifies() {
		return getAccount() != null && getAccount().getType() != null && getAccount().getType().modifies() != null;
	}
	
	public AccountOperatingAccount getAccount() {
		return account;
	}
	public AccountingAnalyticalStatement setAccount(AccountOperatingAccount account) {
		this.account = account;
		return this;
	}
	
	public double getDebit() {
		return debit;
	}
	public AccountingAnalyticalStatement setDebit(double debit) {
		this.debit = debit;
		return this;
	}

	public double getCredit() {
		return credit;
	}
	public AccountingAnalyticalStatement setCredit(double credit) {
		this.credit = credit;
		return this;
	}
	
	public double getPercent() {
		return percent;
	}
	public AccountingAnalyticalStatement setPercent(double percent) {
		this.percent = percent;
		return this;
	}

	public AnalyticalAccountLevel getLevel() {
		return level;
	}
	public AccountingAnalyticalStatement setLevel(AnalyticalAccountLevel level) {
		this.level = level;
		return this;
	}
	
	public AnalyticalAccountLevel getPercentSource() {
		return percentSource;
	}
	public AccountingAnalyticalStatement setPercentSource(AnalyticalAccountLevel percentSource) {
		this.percentSource = percentSource;
		return this;
	}

	public boolean isInherited() {
		return level != percentSource;
	}
	
	public String getSourceCode() {
		return sourceCode;
	}
	public AccountingAnalyticalStatement setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
		return this;
	}

	public double getBalance() {
		return AonMathUtils.round( credit - debit);
	}
	public double getAmount() {
		return AonMathUtils.round( (credit - debit) * percent / 100);
	}
	public double getPercentDebit() {
		return AonMathUtils.round( debit * percent / 100);
	}
	public double getPercentCredit() {
		return AonMathUtils.round( credit * percent / 100);
	}

	public double getDebitBalance() {
		double d = AonMathUtils.round( debit - credit);
		return d>0?d:0.0;
	}
	
	public double getUnpaidBalance() {
		double d = AonMathUtils.round( credit - debit);
		return d>0?d:0.0;
	}
}
