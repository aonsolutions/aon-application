package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

import com.google.gwt.view.client.ProvidesKey;

public class ContractClause implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer contract;
	private Short lineNumber;
	private String name;
	private String description;
	private Byte general;
	
	/**
     * The key provider that provides the unique ID of a contract clause.
     */
    public static final ProvidesKey<ContractClause> KEY_PROVIDER = item -> item == null ? null : item.getId();
	
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
		return null == name ? "" : name;
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
