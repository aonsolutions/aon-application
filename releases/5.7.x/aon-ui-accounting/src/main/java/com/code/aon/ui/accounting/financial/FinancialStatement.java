package com.code.aon.ui.accounting.financial;
public class FinancialStatement {

	private String code;
	private String description;
	private double amount;
	private boolean addition;
	private boolean disabled = false;

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	public boolean isAddition() {
		return addition;
	}
	public void setAddition(boolean addition) {
		this.addition = addition;
	}
}
