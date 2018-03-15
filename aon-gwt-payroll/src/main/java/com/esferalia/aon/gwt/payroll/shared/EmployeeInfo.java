package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class EmployeeInfo implements Serializable{
	
	private Integer employeeId;
	private String name;
	private String surName;
	
	public EmployeeInfo(){
		super();
	}
	
	public EmployeeInfo(Integer employeeId, String name, String surName) {
		super();
		this.employeeId = employeeId;
		this.name = name;
		this.surName = surName;
		 
	}

	// ------------- GETTERS / SETTERS -------------
	
	public Integer getEmployeeId() {
		return employeeId;
	}

	public String getName() {
		return name;
	}

	public String getSurName() {
		return surName;
	}

}