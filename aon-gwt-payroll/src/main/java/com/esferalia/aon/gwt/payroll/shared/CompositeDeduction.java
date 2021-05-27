package com.esferalia.aon.gwt.payroll.shared;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;

public class CompositeDeduction extends Deduction {

	private LinkedList<Deduction> childs;

	public CompositeDeduction() {
		childs = new LinkedList<Deduction>();
	}

	public void addChild(Deduction child) {
		childs.add(child);
	}

	public Collection<Deduction> getChilds() {
		return childs;
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public Double getAmount() {
		Double totalAmount = null;
		for (Deduction child : childs)
			if (child.amount != null)
				totalAmount = totalAmount == null ? child.amount : totalAmount
						+ child.amount;
		return totalAmount;
	}

	@Override
	public Double getDbAmount() {
		Double totalDbAmount = null;
		for (Deduction child : childs)
			if (child.dbAmount != null)
				totalDbAmount = totalDbAmount == null ? child.dbAmount
						: totalDbAmount + child.dbAmount;
		return totalDbAmount != null ? totalDbAmount : super.getDbAmount();
	}


	@Override
	public Date getEndDate() {
		Date end = new Date(0); // January 1, 1970, 00:00:00
		for (Deduction child : childs)
			end = DateUtils.after(end, child.endDate);
		return end;
	}

	@Override
	public Date getStartDate() {
		Date start = null;
		for (Deduction child : childs)
			start = DateUtils.before(start, child.startDate);
		return start;
	}

	@Override
	public Integer getId() {
		return childs.isEmpty() ? null : childs.peek().id;
	}

	@Override
	public String getName() {
		return childs.isEmpty() ? null : childs.peek().name;
	}

	@Override
	public Scope getScope() {
		return childs.isEmpty() ? null : childs.peek().scope;
	}

	@Override
	public Type getType() {
		return childs.isEmpty() ? null : childs.peek().type;
	}

	@Override
	public Short getMonth() {
		return childs.isEmpty() ? null : childs.peek().month;
	}

	@Override
	public Integer getDomain() {
		return childs.isEmpty() ? null : childs.peek().domain;
	}

	@Override
	public Integer getConceptId() {
		return childs.isEmpty() ? null : childs.peek().conceptId;
	}

	@Override
	public String getDescription() {
		return childs.isEmpty() ? null : childs.peek().description;
	}
	
	@Override
	public String getDescriptionTemplate() {
		return childs.isEmpty() ? null : childs.peek().descriptionTemplate;
	}

	@Override
	public String getExpression() {
		return childs.isEmpty() ? null : childs.peek().expression;
	}

	@Override
	public com.esferalia.aon.gwt.payroll.shared.Salary.Type getSalaryType() {
		return childs.isEmpty() ? null : childs.peek().salaryType;

	}
	


}
