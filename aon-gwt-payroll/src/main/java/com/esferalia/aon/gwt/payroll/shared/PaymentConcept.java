package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class PaymentConcept implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Integer conceptId;
	Integer domain;
	String code;
	String description;
	String type;
	String description_decorable;
	String expression;
	String irpf_expression;
	String quote_expression;
	
	public PaymentConcept(){
		super();
	}
	
	public PaymentConcept(Integer conceptId, Integer domain, String code, String description, String type,
			String description_decorable, String expression, String irpf_expression, String quote_expression) {
		this.conceptId = conceptId;
		this.domain = domain;
		this.code = code;
		this.description = description;
		this.type = type;
		this.description_decorable = description_decorable;
		this.expression = expression;
		this.irpf_expression = irpf_expression;
		this.quote_expression = quote_expression;
	}

	public Integer getConceptId() {
		return conceptId;
	}

	public void setConceptId(Integer conceptId) {
		this.conceptId = conceptId;
	}

	public Integer getDomain() {
		return domain;
	}

	public void setDomain(Integer domain) {
		this.domain = domain;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription_decorable() {
		return description_decorable;
	}

	public void setDescription_decorable(String description_decorable) {
		this.description_decorable = description_decorable;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getIrpf_expression() {
		return irpf_expression;
	}

	public void setIrpf_expression(String irpf_expression) {
		this.irpf_expression = irpf_expression;
	}

	public String getQuote_expression() {
		return quote_expression;
	}

	public void setQuote_expression(String quote_expression) {
		this.quote_expression = quote_expression;
	}

}
