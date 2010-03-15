package com.code.aon.accounting;

import java.util.Date;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.registry.RegistryBank;

public class SalaryEntryHeader implements ITransferObject {
	
	private static final long serialVersionUID = -3872966060253678894L;

	private Period period;
	private Date date;
	private RegistryBank registryBank;
	private String concept;
	private SecurityLevel securityLevel;
	private double grossSalary;
	private double allowance;
	private double compensation;
	private double retention;
	private double employeeSocialInsurance1;
	private double employeeSocialInsurance2;
	private double employeeSocialInsurance3;
	private double employeeSocialInsurance4;

	private double companySocialInsurance;
	
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

	public double getGrossSalary() {
		return grossSalary;
	}
	public void setGrossSalary(double grossSalary) {
		this.grossSalary = grossSalary;
	}

	public double getAllowance() {
		return allowance;
	}
	public void setAllowance(double allowance) {
		this.allowance = allowance;
	}
	
	public double getCompensation() {
		return compensation;
	}
	public void setCompensation(double compensation) {
		this.compensation = compensation;
	}

	public double getRetention() {
		return retention;
	}
	public void setRetention(double retention) {
		this.retention = retention;
	}

	public double getEmployeeSocialInsurance1() {
		return employeeSocialInsurance1;
	}
	public void setEmployeeSocialInsurance1(double employeeSocialInsurance1) {
		this.employeeSocialInsurance1 = employeeSocialInsurance1;
	}
	
	public double getEmployeeSocialInsurance2() {
		return employeeSocialInsurance2;
	}
	public void setEmployeeSocialInsurance2(double employeeSocialInsurance2) {
		this.employeeSocialInsurance2 = employeeSocialInsurance2;
	}
	
	public double getEmployeeSocialInsurance3() {
		return employeeSocialInsurance3;
	}
	public void setEmployeeSocialInsurance3(double employeeSocialInsurance3) {
		this.employeeSocialInsurance3 = employeeSocialInsurance3;
	}
	
	public double getEmployeeSocialInsurance4() {
		return employeeSocialInsurance4;
	}
	public void setEmployeeSocialInsurance4(double employeeSocialInsurance4) {
		this.employeeSocialInsurance4 = employeeSocialInsurance4;
	}

	public double getEmployeeSocialInsurance() {
		return CommonUtil.round(employeeSocialInsurance1 + employeeSocialInsurance2 + employeeSocialInsurance3 + employeeSocialInsurance4);
	}
	public void setEmployeeSocialInsurance(double a) {
	}

	public double getCompanySocialInsurance() {
		return companySocialInsurance;
	}
	public void setCompanySocialInsurance(double companySocialIsurance) {
		this.companySocialInsurance = companySocialIsurance;
	}

	public double getAccruedTotal(){
		return CommonUtil.round(grossSalary + allowance + compensation);
	}
	public void setAccruedTotal(double a) {
	}

	public double getNetSalary(){
		return CommonUtil.round(getAccruedTotal() - retention - getEmployeeSocialInsurance());
	}
	public void setNetSalary(double a) {
	}

	public double getTotalSocialInsurance(){
		return CommonUtil.round(getEmployeeSocialInsurance() + companySocialInsurance);
	}

}