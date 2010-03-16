package com.code.aon.ui.payroll.controller;

import javax.faces.event.ActionEvent;


public class PayrollConfigurationController {
	
	private String selectedId;
	
	public void onMenuChanged(ActionEvent event) {
		selectedId =  event.getComponent().getAttributes().get("id").toString();
	}
	
	public String getSelectedId(){
		return selectedId;
	}

	public void setSelectedId(String selectedId) {
		this.selectedId = selectedId;
	}
	
}
