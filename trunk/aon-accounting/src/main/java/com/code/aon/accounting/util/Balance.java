package com.code.aon.accounting.util;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.util.CommonUtil;

public class Balance implements Serializable {

	private static final long serialVersionUID = -1663183754727817121L;

	private Integer accountEntry;
	private String account;
	private String description;
	private String concept;
	private Date fromDate;
	private Date toDate;
	private double debit;
	private double credit;
	private String balancingAccount;
	private String balancingAccountDescription;

	private double unpaidBalance;
	private double creditBalance;

	public Integer getAccountEntry() {
		return accountEntry;
	}

	public void setAccountEntry(Integer accountEntry) {
		this.accountEntry = accountEntry;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public String getBalancingAccountDescription() {
		return balancingAccountDescription;
	}

	public void setBalancingAccountDescription(String balancingAccountDescription) {
		this.balancingAccountDescription = balancingAccountDescription;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public double getDebit() {
		return debit;
	}

	public void setDebit(double debit) {
		this.debit = debit;
	}

	public double getCredit() {
		return credit;
	}

	public void setCredit(double credit) {
		this.credit = credit;
	}

	public double getUnpaidBalance() {
		return unpaidBalance;
	}

	public void setUnpaidBalance(double unpaidBalance) {
		this.unpaidBalance = unpaidBalance;
	}

	public double getCreditBalance() {
		return creditBalance;
	}

	public void setCreditBalance(double creditBalance) {
		this.creditBalance = creditBalance;
	}
	
	public void addBalance( Balance balance) {
		double d =  CommonUtil.round(balance.getUnpaidBalance() + getDebit());
		double c =  CommonUtil.round(balance.getCreditBalance() + getCredit());
		double b = CommonUtil.round(d - c);
		if ( b > 0 ) {
			setUnpaidBalance(b);
		} else {
			setCreditBalance(CommonUtil.round(b*(-1)));
		}
	}

	public void addBalance( AccountEntryDetail detail) {
		setDebit( CommonUtil.round(detail.getDebit() + getDebit()) );
		setCredit( CommonUtil.round(detail.getCredit() + getCredit()) );
		double b = CommonUtil.round(getDebit() - getCredit());
		if ( b > 0 ) {
			setUnpaidBalance(b);
			setCreditBalance(0);
		} else {
			setUnpaidBalance(0);
			setCreditBalance(CommonUtil.round(b*(-1)));
		}
	}

	public String getBalancingAccount() {
		return balancingAccount;
	}

	public void setBalancingAccount(String balancingAccount) {
		this.balancingAccount = balancingAccount;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}
	
}