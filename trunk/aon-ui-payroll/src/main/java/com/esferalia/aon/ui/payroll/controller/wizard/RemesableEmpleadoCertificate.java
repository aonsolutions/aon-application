package com.esferalia.aon.ui.payroll.controller.wizard;

import java.io.Serializable;

import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.SuspensionCause;

public class RemesableEmpleadoCertificate implements Serializable {
	
	private static final long serialVersionUID = 4515899308824088082L;

	private boolean selected;
	private Contract contract;
	private SuspensionCause suspensionCause;
	
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
	public SuspensionCause getSuspensionCause() {
		return suspensionCause;
	}
	public void setSuspensionCause(SuspensionCause suspensionCause) {
		this.suspensionCause = suspensionCause;
	}
	
}
