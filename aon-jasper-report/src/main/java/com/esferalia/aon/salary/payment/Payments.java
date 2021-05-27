package com.esferalia.aon.salary.payment;

import java.util.Collection;

import com.esferalia.aon.payroll.Salary;

import net.aonsolutions.payroll.report.Payment;
import net.aonsolutions.payroll.report.Values;

public class Payments {
	
	private Salary salary;
	
	public Payments(Salary salary) {
		this.salary = salary;
	}

	public BaseSalary getBaseSalary() {
		return salary.getBaseSalary();
	}

	public SalaryInKind getSalaryInKind() {
		return salary.getSalaryInKind();
	}

	public OtherNonWages getOtherNonWages() {
		return salary.getOtherNonWages();
	}

	public OvertimeHours getOvertimeHours() {
		return salary.getOvertimeHours();
	}

	public NoEstructuralOvertimeHours getNoEstructuralOvertimeHours() {
		return salary.getNoEstructuralOvertimeHours();
	}

	public SpecialBonuses getSpecialBonuses() {
		return salary.getSpecialBonuses();
	}

	public SalarySupplements getSalarySupplements() {
		return salary.getSalarySupplements();
	}

	public SpecialSecurityBenefits getSpecialSecurityBenefits() {
		return salary.getSpecialSecurityBenefits();
	}

	public CompensationOrPrepaidExpenses getCompensationOrPrepaidExpenses() {
		return salary.getCompensationOrPrepaidExpenses();
	}

	public Collection<Payment> getOrderedPayment() {
		return salary.getOrderedPayment();
	}
	
	
	
}
