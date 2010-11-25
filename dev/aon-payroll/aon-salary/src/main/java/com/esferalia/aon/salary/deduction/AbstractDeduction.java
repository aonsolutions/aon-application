package com.esferalia.aon.salary.deduction;


public abstract class AbstractDeduction implements IDeduction {

	private double amount;
	private String function;


	protected void setFunction(String function) {
		this.function = function;
	}

	protected void setAmount(double amount) {
		this.amount = amount;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getFunction() {
		return function;
	}

	@Override
	public double getAmount() {
		return amount;
	}
	
}
