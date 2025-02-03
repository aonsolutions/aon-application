package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.gwt.payroll.client.Variables.Variable;
import com.google.gwt.view.client.ProvidesKey;

public class ContractVariable implements Serializable , Variable<ContractVariableType> {
	
	private static final long serialVersionUID = 1L;
	
	public static final ProvidesKey<ContractVariable> KEY_PROVIDER = item -> item == null ? null : item.getId();
		
	private Integer id;
	private ContractVariableType contractVariableType;
	private String description;
	private String expression;
	private Date startDate;
	private Date endDate;
	private boolean hasChange;
	
	public ContractVariable() {
		super();
	}
	
	@Override
	public Integer getId() {
		return id;
	}

	public ContractVariable setId(Integer id) {
		this.id = id;
		return this;
	}
	

	@Override
	public ContractVariableType getVariableType() {
		return contractVariableType;
	}

	@Override
	public ContractVariable setVariableType(ContractVariableType contractVariableType) {
		this.contractVariableType = contractVariableType;
		return this;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public ContractVariable setDescription(String description) {
		this.description = description;
		return this;
	}

	@Override
	public String getExpression() {
		return expression;
	}

	@Override
	public ContractVariable setExpression(String expression) {
		this.expression = expression;
		return this;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public ContractVariable setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	@Override
	public ContractVariable setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	@Override
	public boolean getHasChange() {
		return hasChange;
	}

	@Override
	public ContractVariable setHasChange(boolean hasChange) {
		this.hasChange = hasChange;
		return this;
	}

}
