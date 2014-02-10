package com.code.aon.accounting.util;

import com.code.aon.common.util.CommonUtil;

public class AccountingFinanceCheck {
	
	private int accountId;
	private String accountCode;
	private String accountDescription;
	private int registryId;
	private double accBalance;
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
	public double getAccBalance() {
		return accBalance;
	}
	public void setAccBalance(double accBalance) {
		this.accBalance = accBalance;
	}
	public double getFinBalance() {
		return finBalance;
	}
	public void setFinBalance(double finBalance) {
		this.finBalance = finBalance;
	}
	public double getDifference() {
		return CommonUtil.round(getAccBalance() - getFinBalance());
	}
	
}
