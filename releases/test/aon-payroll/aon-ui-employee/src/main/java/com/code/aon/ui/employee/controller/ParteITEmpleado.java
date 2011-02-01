package com.code.aon.ui.employee.controller;

import java.util.Date;

import com.code.aon.employee.Contract;

public class ParteITEmpleado {
	
	private Contract empleado;
	private Date fechaBaja;
	private Integer numeroRenovaciones;
	private boolean selected;

	public Contract getEmpleado() {
		return empleado;
	}
	public void setEmpleado(Contract empleado) {
		this.empleado = empleado;
	}
	public Date getFechaBaja() {
		return fechaBaja;
	}
	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}
	public Integer getNumeroRenovaciones() {
		return numeroRenovaciones;
	}
	public void setNumeroRenovaciones(Integer numeroRenovaciones) {
		this.numeroRenovaciones = numeroRenovaciones;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	

}
