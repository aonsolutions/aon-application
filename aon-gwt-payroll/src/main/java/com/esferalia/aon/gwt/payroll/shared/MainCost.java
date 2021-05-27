package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class MainCost implements Serializable {

	private static final long serialVersionUID = 1L;

	//Variables
	private String employeeName;
	private String workplaceName;
	private String salaryType;
	private Double totalPayment;
	private Double employeeSS;
	private Double totalIRPF;
	private Double totalDeductions;
	private Double totalLiquid;
	private Double enterpriseSS;
	private Double totalCost;
	private Double totalSS;
	
	public MainCost() {
		super();
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getWorkplaceName() {
		return workplaceName;
	}

	public void setWorkplaceName(String workplaceName) {
		this.workplaceName = workplaceName;
	}

	public String getSalaryType() {
		return salaryType;
	}

	public void setSalaryType(String salaryType) {
		this.salaryType = salaryType;
	}

	public Double getTotalPayment() {
		return totalPayment;
	}

	public void setTotalPayment(Double totalPayment) {
		this.totalPayment = totalPayment;
	}

	public Double getEmployeeSS() {
		return employeeSS;
	}

	public void setEmployeeSS(Double employeeSS) {
		this.employeeSS = employeeSS;
	}

	public Double getTotalIRPF() {
		return totalIRPF;
	}

	public void setTotalIRPF(Double totalIRPF) {
		this.totalIRPF = totalIRPF;
	}

	public Double getTotalDeductions() {
		return totalDeductions;
	}

	public void setTotalDeductions(Double totalDeductions) {
		this.totalDeductions = totalDeductions;
	}

	public Double getTotalLiquid() {
		return totalLiquid;
	}

	public void setTotalLiquid(Double totalLiquid) {
		this.totalLiquid = totalLiquid;
	}

	public Double getEnterpriseSS() {
		return enterpriseSS;
	}

	public void setEnterpriseSS(Double enterpriseSS) {
		this.enterpriseSS = enterpriseSS;
	}

	public Double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}

	public Double getTotalSS() {
		return totalSS;
	}

	public void setTotalSS(Double totalSS) {
		this.totalSS = totalSS;
	}
}
