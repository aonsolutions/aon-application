package com.esferalia.aon.gwt.payroll.shared;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CompositeDeduction extends Deduction  implements ICompositeItem<Deduction>  {
	
	private static final String UNSET_DATE = new String(); 
	private static final String UNSET_STRING = new String(); 
	private static final Integer UNSET_INTEGER = new Integer(1); 
	
	private LinkedList<Deduction> childs;

	public CompositeDeduction() {
		endDate = UNSET_DATE;
		startDate = UNSET_DATE;
		conceptId = UNSET_INTEGER;
		expression = UNSET_STRING;
		description = UNSET_STRING;
		descriptionTemplate = UNSET_STRING;
		
		childs = new LinkedList<Deduction>();
	}

	public void addChild(Deduction child) {
		childs.add(child);
	}

	// ------------------------------------------------------------------------

	@Override
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
			if (child.dbAmount != null && Item.isNotEmptyAmount(child.dbAmount))
				totalDbAmount = totalDbAmount == null ? child.dbAmount
						: totalDbAmount + child.dbAmount;
		return totalDbAmount != null ? totalDbAmount : super.getDbAmount();
	}


	@Override
	public Date getEndDate() {
		Date end = new Date(0); // January 1, 1970, 00:00:00
		for (Deduction child : childs)
			end = DateUtils.after(end, child.getEndDate());
		return end;
	}

	@Override
	public Date getStartDate() {
		Date start = null;
		for (Deduction child : childs)
			start = DateUtils.before(start, child.getStartDate());
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
		if ( description != UNSET_STRING )
			return description;
		return 
		childs.stream()
		.map(p-> p.getDescription())
		.filter(AonStringUtils::isNotBlank)
		.reduce(AonStringUtils::longestCommonSubstring)
		.orElseGet(() -> childs.isEmpty() ? null : childs.peek().description )
		;
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
