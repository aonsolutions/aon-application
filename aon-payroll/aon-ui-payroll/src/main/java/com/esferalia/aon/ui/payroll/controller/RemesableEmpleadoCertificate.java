package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;

import com.code.aon.employee.Contract;
import com.esferalia.aon.payroll.enumeration.CausaSuspension;

public class RemesableEmpleadoCertificate implements Serializable {
	
	private static final long serialVersionUID = -8577568511485133638L;

	private boolean selected;
	private Contract contract;
	private CausaSuspension suspensionCause;
	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	public CausaSuspension getSuspensionCause() {
		return suspensionCause;
	}
	public void setSuspensionCause(CausaSuspension suspensionCause) {
		this.suspensionCause = suspensionCause;
	}
	
	
}
