package com.esferalia.aon.payroll.agreement;

import java.io.Serializable;

public class AgreementPayment implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;
	private String conceptCode;
	private String expression;
	private byte type;
	private String periodicity;
	
	public AgreementPayment() {
		super();
	}

	public String getName() {
		return name;
	}

	public AgreementPayment setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public AgreementPayment setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getConceptCode() {
		return conceptCode;
	}

	public AgreementPayment setConceptCode(String conceptCode) {
		this.conceptCode = conceptCode;
		return this;
	}

	public String getExpression() {
		return expression;
	}

	public AgreementPayment setExpression(String expression) {
		this.expression = expression;
		return this;
	}

	public byte getType() {
		return type;
	}

	public AgreementPayment setType(byte type) {
		this.type = type;
		return this;
	}

	public String getPeriodicity() {
		return periodicity;
	}

	public AgreementPayment setPeriodicity(String periodicity) {
		this.periodicity = periodicity;
		return this;
	}
	
}
