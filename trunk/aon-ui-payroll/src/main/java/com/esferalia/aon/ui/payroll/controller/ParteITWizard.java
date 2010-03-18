package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import com.esferalia.aon.payroll.core.empleado.EmpleadoParams;

public class ParteITWizard implements Serializable {
	
	private static final long serialVersionUID = -6091663393601321263L;
	
	private EmpleadoParams params;
	private DataModel empleadoModel;
	
	public EmpleadoParams getParams() {
		return params;
	}
	public void setParams(EmpleadoParams params) {
		this.params = params;
	}
	
	public DataModel getEmpleadoModel() {
		return empleadoModel;
	}
	public void setEmpleadoModel(DataModel empleadoModel) {
		this.empleadoModel = empleadoModel;
	}

	// Action Listeners
	public void onStart(ActionEvent event) {
		params = new EmpleadoParams();
		setEmpleadoModel(null);
	}
	public void onSearch(ActionEvent event) {
		
	}
	public void onSelect(ActionEvent event) {
		
	}
	
}
