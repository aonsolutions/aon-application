package com.esferalia.aon.ui.payroll.controller;

import java.util.Date;

import com.esferalia.aon.payroll.core.IEmpleado;

public class ContratoEmpleado {
	
	private IEmpleado empleado;
	private Date fechaBaja;
//	private boolean selected;

	public IEmpleado getEmpleado() {
		return empleado;
	}
	public void setEmpleado(IEmpleado empleado) {
		this.empleado = empleado;
	}
	public Date getFechaBaja() {
		return fechaBaja;
	}
	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}
//	public boolean isSelected() {
//		return selected;
//	}
//	public void setSelected(boolean selected) {
//		this.selected = selected;
//	}
	

}
