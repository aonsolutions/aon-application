package com.esferalia.aon.gwt.payroll.shared;


public class Payment extends Item {

	String irpfExpression;
	String quoteExpression;

	public static enum Type {
		BASE_SALARY, SALARY_SUPPLEMENTS, STRUCTURAL_HOURS, NON_STRUCTURAL_HOURS, SPECIAL_BONUSES, SALARY_IN_KIND, COMPENSATION_OR_PREPAID_EXPENSES, SOCIAL_SECURITY_BENEFITS, MOVING_COMPENSATION, OTHER_NON_WAGE;

	}

	Payment.Type type;

	public Payment.Type getType() {
		return type;
	}

	public void setType(Payment.Type type) {
		this.type = type;
	}
	

	public String getIrpfExpression() {
		return irpfExpression;
	}
	
	public void setIrpfExpression(String irpfExpression) {
		this.irpfExpression = irpfExpression;
	}

	public String getQuoteExpression() {
		return quoteExpression;
	}
	
	public void setQuoteExpression(String quoteExpression) {
		this.quoteExpression = quoteExpression;
	}

}