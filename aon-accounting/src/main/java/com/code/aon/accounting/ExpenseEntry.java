package com.code.aon.accounting;

import java.util.Date;

import com.code.aon.account.Account;
import com.code.aon.common.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.PayMethodTypeDetail;
import com.code.aon.registry.RegistryBank;

public class ExpenseEntry implements ITransferObject {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Period period;
	private Date date;
	private RegistryBank registryBank;
	private PayMethodTypeDetail payMethodTypeDetail;
	private String concept;
	private Account account;
	private double amount;
	private SecurityLevel securityLevel;
	private int deposit;

	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public RegistryBank getRegistryBank() {
		return registryBank;
	}
	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

	public PayMethodTypeDetail getPayMethodTypeDetail() {
		return payMethodTypeDetail;
	}
	public void setPayMethodTypeDetail(PayMethodTypeDetail payMethodTypeDetail) {
		this.payMethodTypeDetail = payMethodTypeDetail;
	}

	public String getConcept() {
		return concept;
	}
	public void setConcept(String concept) {
		this.concept = concept;
	}

	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}

	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
	public boolean isConfidential() {
		return getSecurityLevel() == SecurityLevel.CONFIDENTIAL;
	}
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential?SecurityLevel.CONFIDENTIAL:SecurityLevel.OFFICIAL );
	}

	public int getDeposit() {
		return deposit;
	}
	public void setDeposit(int deposit) {
		this.deposit = deposit;
	}
	
}