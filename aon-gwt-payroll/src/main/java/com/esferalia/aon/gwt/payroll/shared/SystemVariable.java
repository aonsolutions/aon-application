package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.gwt.payroll.client.Variables.Variable;
import com.google.gwt.view.client.ProvidesKey;

public class SystemVariable implements Serializable , Variable<SystemVariableType> {
	
	private static final long serialVersionUID = 1L;
	
	public static final ProvidesKey<SystemVariable> KEY_PROVIDER = item -> item == null ? null : item.getId();
		
	private Integer id;
	private Date startDate;
	private Date endDate;
	private boolean hasChange;
	private String expression;
	private String description;
	private SystemVariableType systemVariableType;
	
	public SystemVariable() {
		super();
	}
	
	@Override
	public Integer getId() {
		return id;
	}

	public SystemVariable setId(Integer id) {
		this.id = id;
		return this;
	}
	

	@Override
	public SystemVariableType getVariableType() {
		return systemVariableType;
	}

	@Override
	public SystemVariable setVariableType(SystemVariableType systemVariableType) {
		this.systemVariableType = systemVariableType;
		return this;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public SystemVariable setDescription(String description) {
		this.description = description;
		return this;
	}

	@Override
	public String getExpression() {
		return expression;
	}

	@Override
	public SystemVariable setExpression(String expression) {
		this.expression = expression;
		return this;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public SystemVariable setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	@Override
	public SystemVariable setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	@Override
	public boolean getHasChange() {
		return hasChange;
	}

	@Override
	public SystemVariable setHasChange(boolean hasChange) {
		this.hasChange = hasChange;
		return this;
	}

}
