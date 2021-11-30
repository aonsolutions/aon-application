package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.view.client.ProvidesKey;

public class ContractVariable implements Serializable {
	
	public enum VariableType {
		CONTRACT_DATA,
		CONTRACT_INFO
	}

	private static final long serialVersionUID = 1L;
	
	public static final ProvidesKey<ContractVariable> KEY_PROVIDER = item -> item == null ? null : item.getId();
		
	private Integer id;
	private VariableType variableType;
	private String description;
	private String expression;
	private Date startDate;
	private Date endDate;
	private boolean hasChange;
	
	public ContractVariable() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public ContractVariable setId(Integer id) {
		this.id = id;
		return this;
	}

	public VariableType getVariableType() {
		return variableType;
	}

	public ContractVariable setVariableType(VariableType variableType) {
		this.variableType = variableType;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public ContractVariable setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getExpression() {
		return expression;
	}

	public ContractVariable setExpression(String expression) {
		this.expression = expression;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public ContractVariable setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public ContractVariable setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public boolean getHasChange() {
		return hasChange;
	}

	public ContractVariable setHasChange(boolean hasChange) {
		this.hasChange = hasChange;
		return this;
	}
	
}
