package com.esferalia.aon.salary.deduction;

import com.esferalia.aon.payroll.Salary;

public class Deductions {
	
	private Salary salary;
	
	public Deductions(Salary salary) {
		this.salary = salary;
	}
	
	public IDeduction getIrpf() {
		return salary.getIrpf();
	}
	
	public IDeduction getOther() {
		return salary.getOther();
	}

	public IDeduction getAdvancePayment() {
		return salary.getAdvancePayment();
	}

	public IDeduction getUnemployment() {
		return salary.getUnemployment();
	}
	
	public IDeduction getJobTraining() {
		return salary.getJobTrainning();
	}

	public IDeduction getCommonContingency() {
		return salary.getCommonContingency();
	}

	public IDeduction getStructuralOvertime() {
		return salary.getStructuralOvertime();
	}

	public IDeduction getNonStructuralOvertime() {
		return salary.getNonStructuralOvertime();
	}
	

	public Double getSocialSecurityContributions() {
		return salary.getSocialSecurityContributions();
	}
	

}
