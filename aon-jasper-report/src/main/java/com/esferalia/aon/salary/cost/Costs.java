package com.esferalia.aon.salary.cost;

import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.salary.deduction.IDeduction;

public class Costs {
	
	
	private Salary salary;
	
	public Costs(Salary salary) {
		this.salary = salary;
	}
	
	public IDeduction getAtepIt() {
		return salary.getAtepIt();
	}

	public IDeduction getAtepIms() {
		return salary.getAtepIms();
	}

	public IDeduction getFogasa() {
		return salary.getFogasa();
	}

	public IDeduction getJobTraining() {
		return salary.getJobTrainningCost();
	}

	public IDeduction getStructuralOvertime() {
		return salary.getStructuralOvertimeCost();
	}

	public IDeduction getNonStructuralOvertime() {
		return salary.getNonStructuralOvertimeCost();
	}

	public IDeduction getUnemployment() {
		return salary.getUnemploymentCost();
	}

	public IDeduction getCommonContingency() {
		return salary.getCommonContingencyCost();
	}

}
