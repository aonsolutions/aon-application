package com.esferalia.aon.gwt.payroll.shared;

public class DeductionEvent extends Event implements HasDeduction {
	Deduction deduction;

	@Override
	public Deduction getDeduction() {
		return deduction;
	}

	public DeductionEvent setDeduction(Deduction deduction) {
		this.deduction = deduction;
		return this;
	}
}