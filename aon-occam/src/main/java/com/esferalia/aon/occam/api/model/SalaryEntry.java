package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonMathUtils;

public class SalaryEntry implements Serializable, IAccountEntryWrapper {
	
	private static final long serialVersionUID = -4435280253306756102L;
	
	private AccountEntry accountEntry;
	
	private int salaryCount;
	private String salaryDescription;
	
	private String concept;
	
	private double moneySalary;
	private Account moneySalaryAccount;
	
	private double inKindSalary;
	private Account inKindSalaryAccount;
	
	private double allowance;
	private Account allowanceAccount;
	
	private double salaryCompensation;
	private Account salaryCompensationAccount;
	
	private double salaryDedAdvPayment;
	private Account salaryDedAdvPaymentAccount;

	private double salaryDedSeize;
	private Account salaryDedSeizeAccount;
	
	private double salaryOtherDeductions;
	private Account salaryOtherDeductionsAccount;

	private double irpf;
	private Account irpfAccount;
	
	private double inKindIrpf;
	private Account inKindIrpfAccount;
	
	private double employeeSocialInsurance;
	private Account employeeSocialInsuranceAccount;
	
	private double companySocialInsurance;
	private Account companySocialInsuranceAccount;
	
	private Account netSalaryAccount;
	
	@Override
	public AccountEntry getAccountEntry() {
		return accountEntry;
	}
	@Override
	public void setAccountEntry(AccountEntry accountEntry) {
		this.accountEntry = accountEntry;
	}
	public int getSalaryCount() {
		return salaryCount;
	}
	public SalaryEntry setSalaryCount(int salaryCount) {
		this.salaryCount = salaryCount;
		return this;
	}
	public String getSalaryDescription() {
		return salaryDescription;
	}
	public SalaryEntry setSalaryDescription(String salaryDescription) {
		this.salaryDescription = salaryDescription;
		return this;
	}
	public String getConcept() {
		return concept;
	}
	public SalaryEntry setConcept(String concept) {
		this.concept = concept;
		return this;
	}
	public double getMoneySalary() {
		return moneySalary;
	}
	public SalaryEntry setMoneySalary(double moneySalary) {
		this.moneySalary = moneySalary;
		return this;
	}
	public Account getMoneySalaryAccount() {
		return moneySalaryAccount;
	}
	public SalaryEntry setMoneySalaryAccount(Account moneySalaryAccount) {
		this.moneySalaryAccount = moneySalaryAccount;
		return this;
	}
	public double getInKindSalary() {
		return inKindSalary;
	}
	public SalaryEntry setInKindSalary(double inKindSalary) {
		this.inKindSalary = inKindSalary;
		return this;
	}
	public Account getInKindSalaryAccount() {
		return inKindSalaryAccount;
	}
	public SalaryEntry setInKindSalaryAccount(Account inKindSalaryAccount) {
		this.inKindSalaryAccount = inKindSalaryAccount;
		return this;
	}
	public double getAllowance() {
		return allowance;
	}
	public SalaryEntry setAllowance(double allowance) {
		this.allowance = allowance;
		return this;
	}
	public Account getAllowanceAccount() {
		return allowanceAccount;
	}
	public SalaryEntry setAllowanceAccount(Account allowanceAccount) {
		this.allowanceAccount = allowanceAccount;
		return this;
	}
	public double getSalaryCompensation() {
		return salaryCompensation;
	}
	public SalaryEntry setSalaryCompensation(double salaryCompensation) {
		this.salaryCompensation = salaryCompensation;
		return this;
	}
	public Account getSalaryCompensationAccount() {
		return salaryCompensationAccount;
	}
	public SalaryEntry setSalaryCompensationAccount(Account salaryCompensationAccount) {
		this.salaryCompensationAccount = salaryCompensationAccount;
		return this;
	}
	public double getSalaryDedAdvPayment() {
		return salaryDedAdvPayment;
	}
	public SalaryEntry setSalaryDedAdvPayment(double salaryDedAdvPayment) {
		this.salaryDedAdvPayment = salaryDedAdvPayment;
		return this;
	}
	public Account getSalaryDedAdvPaymentAccount() {
		return salaryDedAdvPaymentAccount;
	}
	public SalaryEntry setSalaryDedAdvPaymentAccount(Account salaryDedAdvPaymentAccount) {
		this.salaryDedAdvPaymentAccount = salaryDedAdvPaymentAccount;
		return this;
	}
	public double getSalaryDedSeize() {
		return salaryDedSeize;
	}
	public SalaryEntry setSalaryDedSeize(double salaryDedSeize) {
		this.salaryDedSeize = salaryDedSeize;
		return this;
	}
	public Account getSalaryDedSeizeAccount() {
		return salaryDedSeizeAccount;
	}
	public SalaryEntry setSalaryDedSeizeAccount(Account salaryDedSeizeAccount) {
		this.salaryDedSeizeAccount = salaryDedSeizeAccount;
		return this;
	}
	
	public double getSalaryOtherDeductions() {
		return salaryOtherDeductions;
	}
	public SalaryEntry setSalaryOtherDeductions(double salaryOtherDeductions) {
		this.salaryOtherDeductions = salaryOtherDeductions;
		return this;
	}
	public Account getSalaryOtherDeductionsAccount() {
		return salaryOtherDeductionsAccount;
	}
	public SalaryEntry setSalaryOtherDeductionsAccount(Account salaryOtherDeductionsAccount) {
		this.salaryOtherDeductionsAccount = salaryOtherDeductionsAccount;
		return this;
	}
		
	public double getIrpf() {
		return irpf;
	}
	public SalaryEntry setIrpf(double irpf) {
		this.irpf = irpf;
		return this;
	}
	public Account getIrpfAccount() {
		return irpfAccount;
	}
	public SalaryEntry setIrpfAccount(Account irpfAccount) {
		this.irpfAccount = irpfAccount;
		return this;
	}
	public double getInKindIrpf() {
		return inKindIrpf;
	}
	public SalaryEntry setInKindIrpf(double inKindIrpf) {
		this.inKindIrpf = inKindIrpf;
		return this;
	}
	public Account getInKindIrpfAccount() {
		return inKindIrpfAccount;
	}
	public SalaryEntry setInKindIrpfAccount(Account inKindIrpfAccount) {
		this.inKindIrpfAccount = inKindIrpfAccount;
		return this;
	}
	public double getEmployeeSocialInsurance() {
		return employeeSocialInsurance;
	}
	public SalaryEntry setEmployeeSocialInsurance(double employeeSocialInsurance) {
		this.employeeSocialInsurance = employeeSocialInsurance;
		return this;
	}
	public Account getEmployeeSocialInsuranceAccount() {
		return employeeSocialInsuranceAccount;
	}
	public SalaryEntry setEmployeeSocialInsuranceAccount(Account employeeSocialInsuranceAccount) {
		this.employeeSocialInsuranceAccount = employeeSocialInsuranceAccount;
		return this;
	}
	public double getCompanySocialInsurance() {
		return companySocialInsurance;
	}
	public SalaryEntry setCompanySocialInsurance(double companySocialInsurance) {
		this.companySocialInsurance = companySocialInsurance;
		return this;
	}
	public Account getCompanySocialInsuranceAccount() {
		return companySocialInsuranceAccount;
	}
	public SalaryEntry setCompanySocialInsuranceAccount(Account companySocialInsuranceAccount) {
		this.companySocialInsuranceAccount = companySocialInsuranceAccount;
		return this;
	}
	public Account getNetSalaryAccount() {
		return netSalaryAccount;
	}
	public SalaryEntry setNetSalaryAccount(Account netSalaryAccount) {
		this.netSalaryAccount = netSalaryAccount;
		return this;
	}
	public double getNetSalary(){
		return AonMathUtils.round(
				getMoneySalary() 
				+ getInKindSalary() 
				+ getAllowance() 
				+ getSalaryCompensation() 
				- getIrpf() 
				- getInKindIrpf() 
				- getEmployeeSocialInsurance()
				- getSalaryDedAdvPayment()
				- getSalaryOtherDeductions()
				- getSalaryDedSeize()
				);
	}
	public double getTotalSocialInsurance(){
		return AonMathUtils.round(getEmployeeSocialInsurance() + getCompanySocialInsurance());
	}
//	public double getTotalAccrued() {
//		return AonMathUtils.round(getMoneySalary() + getInKindSalary() + getAllowance() + getSalaryCompensation());
//	}
//	
}
