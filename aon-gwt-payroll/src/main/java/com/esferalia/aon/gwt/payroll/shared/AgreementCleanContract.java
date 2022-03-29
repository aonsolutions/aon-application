package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class AgreementCleanContract implements Serializable {
	
	private String enterprise;
	private String workplace;
	private String employee;
	
	public AgreementCleanContract() {
		super();
	}
	
	public AgreementCleanContract(String enterprise, String workplace, String employee) {
		super();
		this.enterprise = enterprise;
		this.workplace = workplace;
		this.employee = employee;
	}

	public String getEnterprise() {
		return this.enterprise;
	}
	
	public void setEnterprise(String enterprise) {
		this.enterprise = enterprise;
	}
	
	public String getWorkplace() {
		return this.workplace;
	}
	
	public void setWorkplace(String workplace) {
		this.workplace = workplace;
	}
	
	public String getEmployee() {
		return this.employee;
	}
	
	public void setEmployee(String employee) {
		this.employee = employee;
	}
	
}