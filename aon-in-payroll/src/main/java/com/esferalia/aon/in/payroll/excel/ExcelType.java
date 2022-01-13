package com.esferalia.aon.in.payroll.excel;

public enum ExcelType {
	COMPLETE(true, true),
	SUMMARY(false, true),
	EMPLOYEE_COMPLETE(true, false),
	EMPLOYEE_SUMMARY(false, false),
	PERIOD_COMPLETE(true, false),
	PERIOD_SUMMARY(false, false);
	
	private boolean complete;
	private boolean withType;
	
	private ExcelType(boolean complete, boolean withType) {
		this.complete = complete;
		this.withType = withType;
	}

	public boolean isComplete() {
		return complete;
	}

	public boolean isWithType() {
		return withType;
	}
	
}