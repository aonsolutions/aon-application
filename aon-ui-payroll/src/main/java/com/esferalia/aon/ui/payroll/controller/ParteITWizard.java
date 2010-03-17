package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;

import javax.faces.event.ActionEvent;

import com.esferalia.aon.payroll.core.empleado.EmpleadoParams;

public class ParteITWizard implements Serializable {
	
	private static final long serialVersionUID = -6091663393601321263L;
	
	private EmpleadoParams params;
	
	public EmpleadoParams getParams() {
		return params;
	}
	public void setParams(EmpleadoParams params) {
		this.params = params;
	}
	

	// Action Listeners
	public void onStart(ActionEvent event) {
		params = new EmpleadoParams();
	}
	public void onSearch(ActionEvent event) {
		
	}
	public void onSelect(ActionEvent event) {
		
	}
	
}
