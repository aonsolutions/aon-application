package com.esferalia.aon.omega;

import java.util.Date;

public class ContractInfo {
	
	/*
	 * Ejemplos de variables, si tienes cualquier duda consultar mas variables
	 * name = expression
	 * 
	 * RETA = xxx (xxx = true o false)
	 * 
	 * **/

	String name;			// Nombre variable
	String expression;		// Expression variable
	Date startDate;			// Fecha inicio variable (si no se define se coge la fecha inicio de contrato)
	Date endDate;			// Fecha inicio variable (si no se define se coge la fecha fin de contrato)
	
	protected ContractInfo() {
		super();
	}

	public String getName() {
		return name;
	}

	public ContractInfo setName(String name) {
		this.name = name;
		return this;
	}

	public String getExpression() {
		return expression;
	}

	public ContractInfo setExpression(String expression) {
		this.expression = expression;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public ContractInfo setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public ContractInfo setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
}
