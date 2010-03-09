package com.esferalia.aon.payroll.impl.calc;

import java.util.Date;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.enumeration.TipoNomina;

public class NominaParams {
	private IEmpleado empleado;
	private Month mes;
	private Integer year;
	private TipoNomina tipo;
	private Date fechaTope;
	

	public IEmpleado getEmpleado() {
		return empleado;
	}
	public void setEmpleado(IEmpleado empleado) {
		this.empleado = empleado;
	}
	public Month getMes() {
		return mes;
	}
	public void setMes(Month mes) {
		this.mes = mes;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public TipoNomina getTipo() {
		return tipo;
	}
	public void setTipo(TipoNomina tipo) {
		this.tipo = tipo;
	}
	public Date getFechaTope() {
		return fechaTope;
	}
	public void setFechaTope(Date fechaTope) {
		this.fechaTope = fechaTope;
	}
}
