package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class ContractClause implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer contract;
	private Short lineNumber;
	private String name;
	private String description;
	private Byte general;
	
	public ContractClause() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public ContractClause setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public ContractClause setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getContract() {
		return contract;
	}

	public ContractClause setContract(Integer contract) {
		this.contract = contract;
		return this;
	}

	public Short getLineNumber() {
		return lineNumber;
	}

	public ContractClause setLineNumber(Short lineNumber) {
		this.lineNumber = lineNumber;
		return this;
	}

	public String getName() {
		return null == name ? "" : name;
	}

	public ContractClause setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public ContractClause setDescription(String description) {
		this.description = description;
		return this;
	}

	public Byte getGeneral() {
		return general;
	}

	public ContractClause setGeneral(Byte general) {
		this.general = general;
		return this;
	}
	
}
