package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;

import com.esferalia.aon.payroll.core.IEmpleado;

public class RemesableEmpleadoCertificate implements Serializable {
	
	private static final long serialVersionUID = 4515899308824088082L;

	private boolean selected;
	private IEmpleado empleado;
	private String causaSuspension;
	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public IEmpleado getEmpleado() {
		return empleado;
	}
	public void setEmpleado(IEmpleado empleado) {
		this.empleado = empleado;
	}
	public String getCausaSuspension() {
		return causaSuspension;
	}
	public void setCausaSuspension(String causaSuspension) {
		this.causaSuspension = causaSuspension;
	}
	
	
}
