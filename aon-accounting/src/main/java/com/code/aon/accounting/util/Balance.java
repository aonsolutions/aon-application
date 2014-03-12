package com.code.aon.accounting.util;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.AonVersion;
import com.code.aon.common.util.CommonUtil;

public class Balance implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Integer accountEntry;
	private String account;
	private String description;
	private String concept;
	private String documentNumber;
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
	
	public void dragBalance(Balance balance) {
		double d = CommonUtil.round(balance.getUnpaidBalance() + getDebit());
		double c = CommonUtil.round(balance.getCreditBalance() + getCredit());
		double b = CommonUtil.round(d - c);
		if (b > 0) {
			setUnpaidBalance(b);
			setCreditBalance(0);
		} else {
			setCreditBalance(CommonUtil.round(b * (-1)));
			setUnpaidBalance(0);
		}
	}

	public void set(double debit, double credit) {
		setDebit( CommonUtil.round(debit + getDebit()));
		setCredit( CommonUtil.round(credit + getCredit()));
		changeBalance();
	}

	public void addBalance(Balance balance) {
		setDebit( CommonUtil.round(balance.getUnpaidBalance() + getDebit()));
		setCredit( CommonUtil.round(balance.getCreditBalance() + getCredit()));
		changeBalance();
	}

	public void substractBalance(Balance balance) {
		setDebit( CommonUtil.round(getDebit() - balance.getDebit()) );
		setCredit( CommonUtil.round(getCredit() - balance.getCredit()) );
		changeBalance();
	}

	public void addBalance( AccountEntryDetail detail) {
		setDebit( CommonUtil.round(detail.getDebit() + getDebit()) );
		setCredit( CommonUtil.round(detail.getCredit() + getCredit()) );
		changeBalance();
	}
	
	public void substractBalance( AccountEntryDetail detail) {
		setDebit( CommonUtil.round(getDebit() - detail.getDebit()) );
		setCredit( CommonUtil.round(getCredit() - detail.getCredit()) );
		changeBalance();
	}

	private void changeBalance() {
		double b = CommonUtil.round(getDebit() - getCredit());
		if (b > 0) {
			setUnpaidBalance(b);
			setCreditBalance(0);
		} else {
			setCreditBalance(CommonUtil.round(b * (-1)));
			setUnpaidBalance(0);
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

	public String getDocumentNumber() {
		return documentNumber;
	}
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

}