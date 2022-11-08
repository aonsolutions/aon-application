package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class ContractConcept implements Serializable{

	private static final long serialVersionUID = 2928882144134159456L;
	private Integer id;
	private String code;
	private Byte type;
	private String description;
	private String expression;
	
	public Integer getId() {
		return id;
	}

	public ContractConcept setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getCode() {
		return code;
	}

	public ContractConcept setCode(String code) {
		this.code = code;
		return this;
	}
	
	public Byte getType() {
		return type;
	}

	public ContractConcept setType(Byte type) {
		this.type = type;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public ContractConcept setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getExpression() {
		return expression;
	}

	public ContractConcept setExpression(String expression) {
		this.expression = expression;
		return this;
	}

	@Override
	public String toString() {
		return "ContractConcept [id=" + id + ", code=" + code + ", type=" + type + ", description=" + description
				+ ", expression=" + expression + "]";
	}
	
}