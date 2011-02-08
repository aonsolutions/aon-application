package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;

import com.esferalia.aon.payroll.EnterpriseCertificate;

public class RemesableCertificate implements Serializable {
	
	private static final long serialVersionUID = 516016098978209602L;

	private boolean selected;
	private EnterpriseCertificate batch;
	private boolean showEmployees;
	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public EnterpriseCertificate getBatch() {
		return batch;
	}
	public void setBatch(EnterpriseCertificate batch) {
		this.batch = batch;
	}
	public boolean isShowEmployees() {
		return showEmployees;
	}
	public void setShowEmployees(boolean showEmployees) {
		this.showEmployees = showEmployees;
	}
		
}
