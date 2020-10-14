package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class ContractClause implements Serializable {

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

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDomain() {
		return domain;
	}

	public void setDomain(Integer domain) {
		this.domain = domain;
	}

	public Integer getContract() {
		return contract;
	}

	public void setContract(Integer contract) {
		this.contract = contract;
	}

	public Short getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Short lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Byte getGeneral() {
		return general;
	}

	public void setGeneral(Byte general) {
		this.general = general;
	}
	
}
