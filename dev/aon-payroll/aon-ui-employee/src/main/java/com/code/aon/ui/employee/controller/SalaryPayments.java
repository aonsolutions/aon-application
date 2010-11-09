package com.code.aon.ui.employee.controller;

import java.util.List;

import com.code.aon.employee.SalaryPayment;

public class SalaryPayments {

	private SalaryPayment baseSalary;
	private List<SalaryPayment> salarySupplements;
	private SalaryPayment overtimeHours;
	private SalaryPayment specialBonuses;
	private SalaryPayment salaryInKid;
	private List<SalaryPayment> complementarySuply;
	private SalaryPayment specialSecurityBenefits;
	private SalaryPayment movingCompensation;
	private SalaryPayment otherNonWage;

	
	public SalaryPayment getSalaryInKid() {
		return salaryInKid;
	}
	public void setSalaryInKid(SalaryPayment salaryInKid) {
		this.salaryInKid = salaryInKid;
	}
	public SalaryPayment getBaseSalary() {
		return baseSalary;
	}
	public void setBaseSalary(SalaryPayment baseSalary) {
		this.baseSalary = baseSalary;
	}
	public List<SalaryPayment> getSalarySupplements() {
		return salarySupplements;
	}
	public void setSalarySupplements(List<SalaryPayment> salarySupplements) {
		this.salarySupplements = salarySupplements;
	}
	public SalaryPayment getOvertimeHours() {
		return overtimeHours;
	}
	public void setOvertimeHours(SalaryPayment overtimeHours) {
		this.overtimeHours = overtimeHours;
	}
	public SalaryPayment getSpecialBonuses() {
		return specialBonuses;
	}
	public void setSpecialBonuses(SalaryPayment specialBonuses) {
		this.specialBonuses = specialBonuses;
	}
	public List<SalaryPayment> getComplementarySuply() {
		return complementarySuply;
	}
	public void setComplementarySuply(List<SalaryPayment> complementarySuply) {
		this.complementarySuply = complementarySuply;
	}
	public SalaryPayment getSpecialSecurityBenefits() {
		return specialSecurityBenefits;
	}
	public void setSpecialSecurityBenefits(SalaryPayment specialSecurityBenefits) {
		this.specialSecurityBenefits = specialSecurityBenefits;
	}
	public SalaryPayment getMovingCompensation() {
		return movingCompensation;
	}
	public void setMovingCompensation(SalaryPayment movingCompensation) {
		this.movingCompensation = movingCompensation;
	}
	public SalaryPayment getOtherNonWage() {
		return otherNonWage;
	}
	public void setOtherNonWage(SalaryPayment otherNonWage) {
		this.otherNonWage = otherNonWage;
	}
	

}
