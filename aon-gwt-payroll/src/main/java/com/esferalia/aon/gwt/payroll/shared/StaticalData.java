package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class StaticalData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1235943557908188383L;

	private int month;
	private String monthName;
	private double liquid; // Salario neto del trabajador
	private double irpf;	
	private double ss_enterprise; //Contribución a la SS de la empresa
	private double ss_employee; //Contribución a la SS del trabajador
	private double totalPayment; //Total
	private double otros; //Otros conceptos

		
	public StaticalData() {
		this.month = 0;
		this.monthName = "";
		this.liquid = 0;
		this.irpf = 0;
		this.ss_employee = 0;
		this.totalPayment = 0;
		this.otros = 0;
		this.ss_enterprise=0;
	}

	public int getMonth() {
		return month;
	}
	public void setMonthName(String pName) {
		this.monthName = pName;
	}
	
	public String getMonthName() {
		return monthName;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public double getLiquid() {
		return liquid;
	}

	public void setLiquid(double liquid) {
		this.liquid = liquid;
	}

	public double getIrpf() {
		return irpf;
	}

	public void setIrpf(double irpf) {
		this.irpf = irpf;
	}
	
	public double getSSEnterprise() {
		return ss_enterprise;
	}

	public void setSSEnterprise(double pSSEnterprise) {
		this.ss_enterprise = pSSEnterprise;
	}
	
	public double getSSEmployee() {
		return ss_employee;
	}
	
	public void setSSEmployee(double pSSEmployee) {
		this.ss_employee = pSSEmployee;
	}

	public double getTotalPayment() {
		return totalPayment;
	}

	public void setTotalPayment(double totalPayment) {
		this.totalPayment = totalPayment;
	}
	public double getOtros() {
		return otros;
	}

	public void setOtros(double otros) {
		this.otros = otros;
	}
	public void setTotalEnterprise(double total_enterprise) {
		this.ss_enterprise = total_enterprise;
	}
	public double getTotalEnterprise() {
		return ss_enterprise;
	}
	
	/*
	 * Funcion que utilizo para obtener el LineChart
	 */
	/*public Integer getGastoTotal() {
		return (int) Math.floor((totalEnterprise+totalPayment));
	}*/
	
	public double getGastoTotal() {
		return ss_enterprise+totalPayment;
	}
	
	
	
	
	
	
	

}
