package com.esferalia.aon.omega;

import java.util.Date;

public class ContractData {
	
	/*
	 * Ejemplos de variables, si tienes cualquier duda consultar mas variables
	 * name = expression
	 * 
	 * TC2 = "xxx" (tiene que llevar las comillas dobles en el valor, y xxx = tipo de contrato (100, 200, ...))
	 * GRUPO_COTIZACION = "xx" (tiene que llevar las comillas dobles en el valor, y xx = tipo de contrato (01, 02, ...))
	 * OCUPACION = "x" (tiene que llevar las comillas dobles en el valor, y x = tipo de contrato (a, b, ...))
	 * 
	 * TIEMPO_COMPLETO = x (x = true o false)
	 * RLCE = "xxxx" (tiene que llevar las comillas dobles en el valor, y x = codigo RLCE)
	 * 
	 * COEFICIENTE_PARCIALIDAD = xxxx (xxxx = coeficiente parcialidad (0.50, 0.70, ...))
	 * 
	 * Para los contratos parciales en los que hay que indicar las horas por dias:
	 * 
	 * HORAS_LUNES = xxxx (xxx =  horas (4.00, 6.50, ...), null es dia NO LABORABLE)
	 * HORAS_MARTES = xxxx (xxx =  horas (4.00, 6.50, ...), null es dia NO LABORABLE)
	 * HORAS_MIERCOLES = xxxx (xxx =  horas (4.00, 6.50, ...), null es dia NO LABORABLE)
	 * HORAS_JUEVES = xxxx (xxx =  horas (4.00, 6.50, ...), null es dia NO LABORABLE)
	 * HORAS_VIERNES = xxxx (xxx =  horas (4.00, 6.50, ...), null es dia NO LABORABLE)
	 * HORAS_SABADO = xxxx (xxx =  horas (4.00, 6.50, ...), null es dia NO LABORABLE)
	 * HORAS_DOMINGO = xxxx (xxx =  horas (4.00, 6.50, ...), null es dia NO LABORABLE)
	 * 
	 * **/

	String name;			// Nombre variable
	String expression;		// Expression variable
	Date startDate;			// Fecha inicio variable (si no se define se coge la fecha inicio de contrato)
	Date endDate;			// Fecha inicio variable (si no se define se coge la fecha fin de contrato)
	
	protected ContractData() {
		super();
	}

	public String getName() {
		return name;
	}

	public ContractData setName(String name) {
		this.name = name;
		return this;
	}

	public String getExpression() {
		return expression;
	}

	public ContractData setExpression(String expression) {
		this.expression = expression;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public ContractData setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public ContractData setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
}
