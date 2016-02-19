package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.gwt.common.shared.HasDomain;
import com.esferalia.aon.gwt.common.shared.HasStartAndEndDate;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.watson.util.AonStringUtils;

public abstract class Variable implements HasStartAndEndDate, HasDomain<Integer>, Serializable {

	String name;
	Date startDate;
	Date endDate;
	boolean implicit;
	Scope scope;
	String expression;
	Integer domain;

	boolean[] defined;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public Integer getDomain() {
		return domain;
	}
	
	public void setDomain(Integer domain) {
		this.domain = domain;
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isImpicit() {
		return implicit;
	}

	public void setImplicit(boolean implicit) {
		this.implicit = implicit;
	}

	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public boolean isDefinedAt(Scope scope) {
		return defined != null ? defined[scope.ordinal()] : false;
	}

	public void setDefined(boolean defined[]) {
		this.defined = defined;
	}

	public abstract Object getValue();

	public abstract void setValue(Object value);
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Variable))
			return false;
		Variable var = (Variable) obj;
		
		return ((name == var.name) || ((name != null) && name.equals(var.name)))
				&& ((startDate == var.startDate) || ((startDate != null) && startDate.equals(var.startDate)))
				&& ((endDate == var.endDate) || ((endDate != null) && endDate.equals(var.endDate)))
				;
//		return ((name == var.name) || ((name != null) && name.equals(var.name)));
	}

	public static boolean isAgreementVariable(Variable var) {
		if (var.getScope() == Scope.AGREEMENT)
			return true;
	
		String expression = var.getExpression();
	
		if (StringUtils.isBlank(expression))
			return false;
	
		String name = var.getName();
	
		return expression.matches("\\s*CONVENIO\\s*\\(\\s*('" + name + "'|\""
				+ name + "\")\\s*\\)\\s*");
	
	}
	

}