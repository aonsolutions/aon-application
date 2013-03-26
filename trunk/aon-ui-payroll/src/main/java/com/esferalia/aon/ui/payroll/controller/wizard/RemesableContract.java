package com.esferalia.aon.ui.payroll.controller.wizard;

import java.io.Serializable;

import com.esferalia.aon.payroll.Contract;

public class RemesableContract implements Serializable {
	
	private static final long serialVersionUID = 8301784022578472390L;

	private boolean selected;
	private Contract contract;
	
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
}
