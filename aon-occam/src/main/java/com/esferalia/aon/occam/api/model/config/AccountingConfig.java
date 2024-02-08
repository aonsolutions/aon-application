package com.esferalia.aon.occam.api.model.config;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountPeriod;

public class AccountingConfig implements Serializable {

	private static final long serialVersionUID = -4608981705550453419L;
	
	private LinkedList<AccountPeriod> periods;
	private Account defaultSalesAccount;
	private Account defaultPurchaseAccount;
	private Account defaultChargedVatAccount;
	private Account defaultPaidVatAccount;
	private Account defaultChargedRetAccount;
	private Account defaultPaidRetAccount;
	private Account defaultCashAccount;
	private Account vatNegativeAdjustAccount;
	private Account directTaxAdjustAccount;
	private Account defaultDUAVatAccount;
	private Account defaultDUADutyAccount;
	private Account defaultSalary;
	private Account defaultSalaryInKind;
	private Account defaultAllowance;
	private Account defaultCompensation;
	private Account defaultCompanySocIns;
	private Account salaryChargedRet;
	private Account salaryChargedRetInKind;
	private Account defaultSocialInsurance;
	private Account defaultPendingSalary;
	private Account salaryOtherDeductions;
	private Account salaryDedAdvPayment;
	private Account salaryDedSeize;
	private Account defaultPrepayment;
	private LinkedList<String> autoConcepts;
	private LinkedList<String> costCenters;

	public LinkedList<AccountPeriod> getPeriods() {
		return periods;
	}
	public AccountingConfig setPeriods(LinkedList<AccountPeriod> periods) {
		this.periods = periods;
		return this;
	}
	public AccountPeriod getDefaultAccountPeriod() {
		for (AccountPeriod period : periods) {
			if (period.isDefaultPeriod()) return period; 
		}
		return null;
	}
	
	public LinkedList<String> getAutoConcepts() {
		return autoConcepts;
	}
	public AccountingConfig setAutoConcepts(LinkedList<String> autoConcepts) {
		this.autoConcepts = autoConcepts;
		return this;
	}
	
	public LinkedList<String> getCostCenters() {
		return costCenters;
	}
	public AccountingConfig setCostCenters(LinkedList<String> costCenters) {
		this.costCenters = costCenters;
		return this;
	}
	
	public boolean hasCostCenters() {
		return this.costCenters != null && this.costCenters.size() > 0;
	}
	
	public Account getDefaultSalesAccount() {
		return defaultSalesAccount;
	}
	public AccountingConfig setDefaultSalesAccount(Account defaultSalesAccount) {
		this.defaultSalesAccount = defaultSalesAccount;
		return this;
	}

	public Account getDefaultPurchaseAccount() {
		return defaultPurchaseAccount;
	}
	public AccountingConfig setDefaultPurchaseAccount(Account defaultPurchaseAccount) {
		this.defaultPurchaseAccount = defaultPurchaseAccount;
		return this;
	}

	public Account getDefaultChargedVatAccount() {
		return defaultChargedVatAccount;
	}
	public AccountingConfig setDefaultChargedVatAccount(Account defaultChargedVatAccount) {
		this.defaultChargedVatAccount = defaultChargedVatAccount;
		return this;
	}

	public Account getDefaultPaidVatAccount() {
		return defaultPaidVatAccount;
	}
	public AccountingConfig setDefaultPaidVatAccount(Account defaultPaidVatAccount) {
		this.defaultPaidVatAccount = defaultPaidVatAccount;
		return this;
	}

	public Account getDefaultChargedRetAccount() {
		return defaultChargedRetAccount;
	}
	public AccountingConfig setDefaultChargedRetAccount(Account defaultChargedRetAccount) {
		this.defaultChargedRetAccount = defaultChargedRetAccount;
		return this;
	}

	public Account getDefaultPaidRetAccount() {
		return defaultPaidRetAccount;
	}
	public AccountingConfig setDefaultPaidRetAccount(Account defaultPaidRetAccount) {
		this.defaultPaidRetAccount = defaultPaidRetAccount;
		return this;
	}

	public Account getDefaultCashAccount() {
		return  this.defaultCashAccount;
	}
	public AccountingConfig setDefaultCashAccount(Account defaultCashAccount) {
		this.defaultCashAccount = defaultCashAccount;
		return this;
	}

	public Account getVatNegativeAdjustAccount() {
		return vatNegativeAdjustAccount;
	}
	public AccountingConfig setVatNegativeAdjustAccount(Account vatNegativeAdjustAccount) {
		this.vatNegativeAdjustAccount = vatNegativeAdjustAccount;
		return this;
	}
	public Account getDirectTaxAdjustAccount() {
		return directTaxAdjustAccount;
	}
	public AccountingConfig setDirectTaxAdjustAccount(Account directTaxAdjustAccount) {
		this.directTaxAdjustAccount = directTaxAdjustAccount;
		return this;
	}
	public Account getDefaultDUAVatAccount() {
		return defaultDUAVatAccount;
	}
	public AccountingConfig setDefaultDUAVatAccount(Account defaultDUAVatAccount) {
		this.defaultDUAVatAccount = defaultDUAVatAccount;
		return this;
	}
	
	public Account getDefaultDUADutyAccount() {
		return defaultDUADutyAccount;
	}
	public AccountingConfig setDefaultDUADutyAccount(Account defaultDUADutyAccount) {
		this.defaultDUADutyAccount = defaultDUADutyAccount;
		return this;
	}

	public Account getDefaultSalary() {
		return defaultSalary;
	}
	public AccountingConfig setDefaultSalary(Account defaultSalary) {
		this.defaultSalary = defaultSalary;
		return this;
	}
	
	public Account getDefaultPrepayment() {
		return defaultPrepayment;
	}
	public AccountingConfig setDefaultPrepayment(Account defaultPrepayment) {
		this.defaultPrepayment = defaultPrepayment;
		return this;
	}

	public Account getDefaultSalaryInKind() {
		return defaultSalaryInKind;
	}
	public AccountingConfig setDefaultSalaryInKind(Account defaultSalaryInKind) {
		this.defaultSalaryInKind = defaultSalaryInKind;
		return this;
	}

	public Account getDefaultAllowance() {
		return defaultAllowance;
	}
	public AccountingConfig setDefaultAllowance(Account defaultAllowance) {
		this.defaultAllowance = defaultAllowance;
		return this;
	}

	public Account getDefaultCompensation() {
		return defaultCompensation;
	}
	public AccountingConfig setDefaultCompensation(Account defaultCompensation) {
		this.defaultCompensation = defaultCompensation;
		return this;
	}

	public Account getDefaultCompanySocIns() {
		return defaultCompanySocIns;
	}
	public AccountingConfig setDefaultCompanySocIns(Account defaultCompanySocIns) {
		this.defaultCompanySocIns = defaultCompanySocIns;
		return this;
	}

	public Account getSalaryChargedRet() {
		return salaryChargedRet;
	}
	public AccountingConfig setSalaryChargedRet(Account salaryChargedRet) {
		this.salaryChargedRet = salaryChargedRet;
		return this;
	}

	public Account getSalaryChargedRetInKind() {
		return salaryChargedRetInKind;
	}
	public AccountingConfig setSalaryChargedRetInKind(Account salaryChargedRetInKind) {
		this.salaryChargedRetInKind = salaryChargedRetInKind;
		return this;
	}

	public Account getDefaultSocialInsurance() {
		return defaultSocialInsurance;
	}
	public AccountingConfig setDefaultSocialInsurance(Account defaultSocialInsurance) {
		this.defaultSocialInsurance = defaultSocialInsurance;
		return this;
	}

	public Account getDefaultPendingSalary() {
		return defaultPendingSalary;
	}
	public AccountingConfig setDefaultPendingSalary(Account defaultPendingSalary) {
		this.defaultPendingSalary = defaultPendingSalary;
		return this;
	}

	public Account getSalaryDedAdvPayment() {
		return salaryDedAdvPayment;
	}
	public AccountingConfig setSalaryDedAdvPayment(Account salaryDedAdvPayment) {
		this.salaryDedAdvPayment = salaryDedAdvPayment;
		return this;
	}
	
	public Account getSalaryOtherDeductions() {
		return salaryOtherDeductions;
	}
	public AccountingConfig setSalaryOtherDeductions(Account salaryOtherDeductions) {
		this.salaryOtherDeductions = salaryOtherDeductions;
		return this;
	}

	public Account getSalaryDedSeize() {
		return salaryDedSeize;
	}
	public AccountingConfig setSalaryDedSeize(Account salaryDedSeize) {
		this.salaryDedSeize = salaryDedSeize;
		return this;
	}
}
