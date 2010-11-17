package com.code.aon.employee;

import java.util.List;

public class Payments {

	private IPayment baseSalary;
	private List<IPayment> salarySupplements;
	private IPayment overtimeHours;
	private IPayment specialBonuses;
	private IPayment salaryInKind;
	private List<IPayment> complementarySuply;
	private IPayment specialSecurityBenefits;
	private IPayment movingCompensation;
	private IPayment otherNonWage;

	
	public IPayment getSalaryInKind() {
		return salaryInKind;
	}
	public void setSalaryInKind(IPayment salaryInKind) {
		this.salaryInKind = salaryInKind;
	}
	public IPayment getBaseSalary() {
		return baseSalary;
	}
	public void setBaseSalary(IPayment baseSalary) {
		this.baseSalary = baseSalary;
	}
	public List<IPayment> getSalarySupplements() {
		return salarySupplements;
	}
	public void setSalarySupplements(List<IPayment> salarySupplements) {
		this.salarySupplements = salarySupplements;
	}
	public IPayment getOvertimeHours() {
		return overtimeHours;
	}
	public void setOvertimeHours(IPayment overtimeHours) {
		this.overtimeHours = overtimeHours;
	}
	public IPayment getSpecialBonuses() {
		return specialBonuses;
	}
	public void setSpecialBonuses(IPayment specialBonuses) {
		this.specialBonuses = specialBonuses;
	}
	public List<IPayment> getComplementarySuply() {
		return complementarySuply;
	}
	public void setComplementarySuply(List<IPayment> complementarySuply) {
		this.complementarySuply = complementarySuply;
	}
	public IPayment getSpecialSecurityBenefits() {
		return specialSecurityBenefits;
	}
	public void setSpecialSecurityBenefits(IPayment specialSecurityBenefits) {
		this.specialSecurityBenefits = specialSecurityBenefits;
	}
	public IPayment getMovingCompensation() {
		return movingCompensation;
	}
	public void setMovingCompensation(IPayment movingCompensation) {
		this.movingCompensation = movingCompensation;
	}
	public IPayment getOtherNonWage() {
		return otherNonWage;
	}
	public void setOtherNonWage(IPayment otherNonWage) {
		this.otherNonWage = otherNonWage;
	}
	

}
