package com.esferalia.aon.gwt.payroll.shared;

public class UndefinedDeductionVariable extends UndefinedVariable implements HasDeduction{

	private Deduction deduction;
	
	@Override
	public Deduction getDeduction() {
		return deduction;
	}
	
	public void setDeduction(Deduction deduction) {
		this.deduction = deduction;
	}
}