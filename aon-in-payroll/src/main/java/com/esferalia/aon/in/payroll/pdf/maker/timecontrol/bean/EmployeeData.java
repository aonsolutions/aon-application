package com.esferalia.aon.in.payroll.pdf.maker.timecontrol.bean;

public class EmployeeData {
	private String employeeName;
	private String contract;
	private String naf;
	private String dni;
	public EmployeeData() {
		
	}
	public EmployeeData(String employeeName, String contract, String naf, String dni) {
		super();
		this.employeeName = employeeName;
		this.contract = contract;
		this.naf = naf;
		this.dni = dni;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getContract() {
		return contract;
	}
	public void setContract(String contract) {
		this.contract = contract;
	}
	public String getNaf() {
		return naf;
	}
	public void setNaf(String naf) {
		this.naf = naf;
	}
	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		this.dni = dni;
	}
	
	
}
