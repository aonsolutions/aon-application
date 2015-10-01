package com.code.aon.accounting.util;

import java.io.Serializable;
import java.math.BigDecimal;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountingFinanceCheck implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private int accountId;
	private String accountCode;
	private String accountDescription;
	private int registryId;
	private String registryName;
	private double debit;
	private double credit;
	private double finBalance;
	private String documentNumber;
	private byte financeStatus;
	
	public int getAccountId() {
		return accountId;
	}
	public AccountingFinanceCheck setAccountId(int accountId) {
		this.accountId = accountId;
		return this;
	}
	public String getAccountCode() {
		return accountCode;
	}
	public AccountingFinanceCheck setAccountCode(String accountCode) {
		this.accountCode = accountCode;
		return this;
	}
	public String getAccountDescription() {
		return accountDescription;
	}
	public AccountingFinanceCheck setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
		return this;
	}
	public String getFullAccountDescription() {
		return AonStringUtils.abbreviate(AonStringUtils.join(accountCode," ",accountDescription),40);
	}
	
	public int getRegistryId() {
		return registryId;
	}
	public AccountingFinanceCheck setRegistryId(int registryId) {
		this.registryId = registryId;
		return this;
	}
	public String getRegistryName() {
		return registryName;
	}
	public String getAbbRegistryName() {
		return AonStringUtils.abbreviate(registryName,40);
	}
	public AccountingFinanceCheck setRegistryName(String registryName) {
		this.registryName = registryName;
		return this;
	}
	public double getDebit() {
		return debit;
	}
	public AccountingFinanceCheck setDebit(double debit) {
		this.debit = debit;
		return this;
	}
	public double getCredit() {
		return credit;
	}
	public AccountingFinanceCheck setCredit(double credit) {
		this.credit = credit;
		return this;
	}
	public double getFinBalance() {
		return finBalance;
	}
	public AccountingFinanceCheck setFinBalance(double finBalance) {
		this.finBalance = finBalance;
		return this;
	}
	public AccountingFinanceCheck setFinBalance(BigDecimal finBalance) {
		return  setFinBalance( finBalance == null? 0.0 : finBalance.doubleValue());
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
		return CommonUtil.round((getDebitBalance()>0?getDebitBalance():getCreditBalance()) - AonMathUtils.absRounded(getFinBalance()));
	}
	public String getDocumentNumber() {
		return documentNumber;
	}
	public AccountingFinanceCheck setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
		return this;
	}
	public byte getFinanceStatus() {
		return financeStatus;
	}
	public FinanceStatus getFinanceStatusEnum() {
		return FinanceStatus.values()[financeStatus];
	}
	public AccountingFinanceCheck setFinanceStatus(byte financeStatus) {
		this.financeStatus = financeStatus;
		return this;
	}
	
}
