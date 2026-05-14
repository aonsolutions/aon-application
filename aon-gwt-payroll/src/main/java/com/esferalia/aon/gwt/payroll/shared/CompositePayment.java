package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.gwt.payroll.shared.Shared.parse;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CompositePayment extends Payment implements ICompositeItem<Payment>  {

	private static final String UNSET_DATE = new String(); 
	private static final String UNSET_STRING = new String(); 
	private static final Integer UNSET_INTEGER = new Integer(1); 
	
	private LinkedList<Payment> childs;

	public CompositePayment() {
		
		endDate = UNSET_DATE;
		startDate = UNSET_DATE;
		conceptId = UNSET_INTEGER;
		expression = UNSET_STRING;
		description = UNSET_STRING;
		irpfExpression = UNSET_STRING;
		quoteExpression = UNSET_STRING;
		descriptionTemplate = UNSET_STRING;
		
		childs = new LinkedList<Payment>();
	}

	public void addChild(Payment child) {
		childs.add(child);
		//System.out.println(child.getDescription() + " / " + child.getType());
	}

	@Override
	public Collection<Payment> getChilds() {
		Collections.sort(childs, (p1,p2) -> p1.getStartDate().compareTo(p2.getStartDate()));
		return childs;
	}

	// ------------------------------------------------------------------------
	
	
	@Override
	public Double getAmount() {		
		Double totalAmount = null;
		for (Payment child : childs)
			if (child.amount != null)
				totalAmount = totalAmount == null ? child.amount : totalAmount
						+ child.amount;
		return totalAmount;
	}

	@Override
	public Double getDbAmount() {		
		Double totalDbAmount = null;
		for (Payment child : childs)
			if (child.dbAmount != null && Item.isNotEmptyAmount(child.dbAmount))
				totalDbAmount = totalDbAmount == null ? child.dbAmount
						: totalDbAmount + child.dbAmount;
		return totalDbAmount != null ? totalDbAmount : super.getDbAmount();
	}

	@Override
	public Double getIrpf() {		
		Double totalIrpf = null;
		for (Payment child : childs)
			if (child.irpf != null)
				totalIrpf = totalIrpf == null ? child.irpf : totalIrpf
						+ child.irpf;
		return totalIrpf;
	}

	@Override
	public Double getQuote() {
		Double totalQuote = null;
		for (Payment child : childs)
			if (child.quote != null)
				totalQuote = totalQuote == null ? child.quote : totalQuote
						+ child.quote;
		return totalQuote;
	}

	@Override
	public Date getEndDate() {
		
		if ( endDate != UNSET_DATE )
			return parse(endDate);
		
		Date end = new Date(0); // January 1, 1970, 00:00:00
		for (Payment child : childs)
			end = DateUtils.after(end, child.getEndDate());
		return end;
	}

	@Override
	public Date getStartDate() {
		if ( startDate != UNSET_DATE )
			return parse(startDate);

		Date start = null;
		for (Payment child : childs)
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
		if ( conceptId != UNSET_INTEGER )
			return conceptId;

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
		if ( descriptionTemplate != UNSET_STRING )
			return descriptionTemplate;
		
		return childs.isEmpty() ? null : childs.peek().descriptionTemplate;
	}
	
	@Override
	public String getExpression() {
		if ( expression != UNSET_STRING )
			return expression;
		return childs.isEmpty() ? null : childs.peek().expression;
	}

	@Override
	public String getIrpfExpression() {
		if ( irpfExpression != UNSET_STRING )
			return irpfExpression;
		
		return childs.isEmpty() ? null : childs.peek().irpfExpression;
	}

	@Override
	public String getQuoteExpression() {
		if ( quoteExpression != UNSET_STRING )
			return quoteExpression;

		return childs.isEmpty() ? null : childs.peek().quoteExpression;
	}

	@Override
	public com.esferalia.aon.gwt.payroll.shared.Salary.Type getSalaryType() {
		if ( salaryType != null )
			return salaryType;

		return childs.isEmpty() ? null : childs.peek().salaryType;

	}

}
