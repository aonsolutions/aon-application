package com.esferalia.aon.payroll.core.nomina;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.enumeration.TipoNomina;

public class NominaParams {
	private IEmpleado empleado;
	private Integer mes;
	private Integer year;
	private TipoNomina tipo;
	
	private int offset;
	private int count;

	public Month getMonth() {
		return Month.values()[ getMes() - 1];
	}
	public void setMonth(Month month) {
		this.mes = month.ordinal() + 1;
	}

	public IEmpleado getEmpleado() {
		return empleado;
	}
	public void setEmpleado(IEmpleado empleado) {
		this.empleado = empleado;
	}
	public Integer getMes() {
		return mes;
	}
	public void setMes(Integer mes) {
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
	
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	
}
