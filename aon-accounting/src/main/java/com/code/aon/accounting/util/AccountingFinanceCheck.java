package com.code.aon.accounting.util;

import com.code.aon.common.util.CommonUtil;

public class AccountingFinanceCheck {
	
	private int accountId;
	private String accountCode;
	private String accountDescription;
	private int registryId;
	private double debit;
	private double credit;
	private double finBalance;
	
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	public String getAccountCode() {
		return accountCode;
	}
	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}
	public String getAccountDescription() {
		return accountDescription;
	}
	public void setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
	}
	public int getRegistryId() {
		return registryId;
	}
	public void setRegistryId(int registryId) {
		this.registryId = registryId;
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
	public double getFinBalance() {
		return finBalance;
	}
	public void setFinBalance(double finBalance) {
		this.finBalance = finBalance;
	}
	public double getDebitBalance() {
		double d = CommonUtil.round(getDebit() - getCredit()); 
		return d >0?d:0;
	}
	public double getCreditBalance() {
		double d = CommonUtil.round(getCredit() - getDebit()); 
		return d >0?d:0;
	}
	public double getDifference() {
		return CommonUtil.round((getDebitBalance()>0?getDebitBalance():getCreditBalance()) - getFinBalance());
	}
	
}
