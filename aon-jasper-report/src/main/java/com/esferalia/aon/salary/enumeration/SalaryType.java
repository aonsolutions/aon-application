package com.esferalia.aon.salary.enumeration;

public enum SalaryType { 
	SALARY,
	EXTRA,
	SETTLE("PAY_REPORT_settlement_PAY", "settlement"),
	DELAY,
	NOT_ENJOYED_VACATIONS
	;
	private String reportName ;
	private String defaultReport;

	private SalaryType() {
		this("PAY_REPORT_salary_PAY", "salary");
	}

	private SalaryType(String reportName, String defaultReport) {
		this.reportName = reportName;
		this.defaultReport = defaultReport;
	}
	
	public String getReportName() {
		return reportName;
	}
	
	public String getDefaultReport() {
		return defaultReport;
	}
}