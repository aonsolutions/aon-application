package com.esferalia.aon.gwt.payroll.shared;

import java.util.Date;

import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;

public class DelegateVariable extends Variable {
	
	
	public static Variable getVariable(Variable variable, Scope ascope) {
		return new DelegateVariable(variable) {
			@Override
			public Scope getScope() {
				return ascope;
			}
		};
	}
	
	private Variable variable;
	
	
	public DelegateVariable(Variable variable) {
		this.variable = variable;
	}
	
	@Override
	public Integer getId() {
		return variable.getId();
	}

	@Override
	public void setId(Integer id) {
		variable.setId(id);
	}

	@Override
	public String getName() {
		return variable.getName();
	}

	@Override
	public void setName(String name) {
		variable.setName(name);
	}

	@Override
	public Integer getDomain() {
		return variable.getDomain();
	}

	@Override
	public void setDomain(Integer domain) {
		variable.setDomain(domain);
	}

	@Override
	public Date getStartDate() {
		return variable.getStartDate();
	}

	@Override
	public void setStartDate(Date startDate) {
		variable.setStartDate(startDate);
	}

	@Override
	public Date getEndDate() {
		return variable.getEndDate();
	}

	@Override
	public void setEndDate(Date endDate) {
		variable.setEndDate(endDate);
	}

	@Override
	public boolean isImpicit() {
		return variable.isImpicit();
	}

	@Override
	public void setImplicit(boolean implicit) {
		variable.setImplicit(implicit);
	}

	@Override
	public Scope getScope() {
		return variable.getScope();
	}

	@Override
	public void setScope(Scope scope) {
		variable.setScope(scope);
	}

	@Override
	public String getExpression() {
		return variable.getExpression();
	}

	@Override
	public void setExpression(String expression) {
		variable.setExpression(expression);
	}

	@Override
	public boolean isDefinedAt(Scope scope) {
		return variable.isDefinedAt(scope);
	}

	@Override
	public void setDefined(boolean[] defined) {
		variable.setDefined(defined);
	}

	@Override
	public boolean[] getDefined() {
		return variable.getDefined();
	}

	@Override
	public boolean equals(Object obj) {
		return variable.equals(obj);
	}

	@Override
	public int hashCode() {
		return variable.hashCode();
	}

	@Override
	public String toString() {
		return variable.toString();
	}

	@Override
	public Object getValue() {
		return variable.getValue();
	}

	@Override
	public void setValue(Object value) {
		variable.setValue(value);
	}
	
	
	
}
