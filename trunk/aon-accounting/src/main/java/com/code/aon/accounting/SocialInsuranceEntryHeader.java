package com.code.aon.accounting;

import java.util.Date;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.registry.RegistryBank;

public class SocialInsuranceEntryHeader implements ITransferObject{

	private static final long serialVersionUID = 9083596439112054168L;

	private Period period;
	private Date date;
	private double amount;
	private double recharge;
	private Account rechargeAccount;
	private RegistryBank registryBank;
	private String concept;
	private SecurityLevel securityLevel;
	private boolean paymentAdjustable;
	private Month month;
	private AccountEntryLink adjustEntryLink;

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

	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getRecharge() {
		return recharge;
	}
	public void setRecharge(double recharge) {
		this.recharge = recharge;
	}

	public double getTotal() {
		return CommonUtil.round( getAmount() + getRecharge());
	}
	public void setTotal(double total) {
	}

	public Account getRechargeAccount() {
		return rechargeAccount;
	}
	public void setRechargeAccount(Account rechargeAccount) {
		this.rechargeAccount = rechargeAccount;
	}

	public RegistryBank getRegistryBank() {
		return registryBank;
	}
	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

	public String getConcept() {
		return concept;
	}
	public void setConcept(String concept) {
		this.concept = concept;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public boolean isPaymentAdjustable() {
		return paymentAdjustable;
	}
	public void setPaymentAdjustable(boolean paymentAdjustable) {
		this.paymentAdjustable = paymentAdjustable;
	}
	
	public Month getMonth() {
		return month;
	}
	public void setMonth(Month month) {
		this.month = month;
	}
	
	public AccountEntryLink getAdjustEntryLink() {
		return adjustEntryLink;
	}
	public void setAdjustEntryLink(AccountEntryLink adjustEntryLink) {
		this.adjustEntryLink = adjustEntryLink;
	}
}