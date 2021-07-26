package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.gwt.payroll.shared.Shared.format;
import static com.esferalia.aon.gwt.payroll.shared.Shared.parse;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;

public class VariableDescriptor implements Serializable  {
	
	
	protected static final Class<?> TYPES [] = 
		{ Number.class, Date.class, Boolean.class , String.class };
	
	Integer type;
	String value;
	String description;
	String expression;
	Scope scope;
	String startDate;
	String endDate;
	
	
	
	public Date getStartDate() {
		return parse(startDate);
	}

	public void setStartDate(Date startDate) {
		this.startDate = format(startDate);
	}

	public Date getEndDate() {
		return parse(endDate);
	}

	public void setEndDate(Date endDate) {
		this.endDate = format(endDate);
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public String getSyntax() {
		return "";
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Class getType() {
		return getClass(type);
	}
	
	public void setType(Class clazz) {
		this.type = findClazz(clazz);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	protected Integer findClazz(Class clazz ) {
		do {
			for ( int i = 0; i < TYPES.length; i++)
				if ( clazz == TYPES[i])
					return i;
			clazz = clazz.getSuperclass();
		} while ( clazz != null );
		
		return null;
	}
	
	protected Class getClass(Integer type) {
		return type != null ? TYPES[type] : Object.class;
	}
	
}