package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.gwt.payroll.shared.Shared.format;
import static com.esferalia.aon.gwt.payroll.shared.Shared.parse;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.gwt.common.shared.HasDomain;
import com.esferalia.aon.gwt.common.shared.HasId;
import com.esferalia.aon.gwt.common.shared.HasStartAndEndDate;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;

public abstract class Item<T extends Enum<?>> implements HasStartAndEndDate,
		HasId<Integer>, HasDomain<Integer>, Serializable {

	private static final Double EMPTY_AMOUNT = .000123456789;

	T type;
	Integer id;
	Short month;
	Scope scope;
	String startDate;
	String endDate;
	Double amount;
	String name;
	String expression;
	String description;
	Salary.Type salaryType;
	Double dbAmount;
	Double ssAmount;
	Integer conceptId;
	String descriptionTemplate;

	Integer domain;
	
	boolean[] defined;
	
	// For payment_concept.expression when agreement_payment.expression = DISABLE();
	String hiddenExpression;
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public T getType() {
		return type;
	}

	public void setType(T type) {
		this.type = type;
	}

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
	
	public Short getMonth() {
		return month;
	}

	public void setMonth(Short month) {
		this.month = month;
	}

	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}
	
	public Long getEndDateTime() {
		return null == endDate ? null : getEndDate().getTime();
	}

	public Date getEndDate() {
		return parse(endDate);
	}

	public void setEndDate(Date endDate) {
		this.endDate = format(endDate);
	}
	
	public Long getStartDateTime() {
		return null == startDate ? null : getStartDate().getTime();
	}

	public Date getStartDate() {
		return parse(startDate);
	}

	public void setStartDate(Date startDate) {
		this.startDate = format(startDate);
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public Salary.Type getSalaryType() {
		return salaryType;
	}

	public void setSalaryType(Salary.Type salaryType) {
		this.salaryType = salaryType;
	}

	public Double getDbAmount() {
		return dbAmount;
	}

	public void setDbAmount(Double dbAmount) {
		this.dbAmount = dbAmount;
	}

	public Double getSsAmount() {
		return ssAmount;
	}

	public void setSsAmount(Double ssAmount) {
		this.ssAmount = ssAmount;
	}


	public Integer getConceptId() {
		return conceptId;
	}

	public void setConceptId(Integer conceptId) {
		this.conceptId = conceptId;
	}
	
	public boolean isDefinedAt(Scope scope) {
		return defined != null ? defined[scope.ordinal()] : false;
	}

	public void setDefined(boolean defined[]) {
		this.defined = defined;
	}
	
	public String getDescriptionTemplate() {
		return descriptionTemplate;
	}

	public void setDescriptionTemplate(String descriptionTemplate) {
		this.descriptionTemplate = descriptionTemplate;
	}
	
	public String getHiddenExpression() {
		return hiddenExpression;
	}

	public void setHiddenExpression(String hiddenExpression) {
		this.hiddenExpression = hiddenExpression;
	}

	@Override
	public int hashCode() {
		return id != null ? id : 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Item))
			return false;
		Item item = (Item) obj;
		return ((id == item.id) || ((id != null) && id.equals(item.id)));
	}
	
	public void empytDbAmount() {
		setDbAmount(EMPTY_AMOUNT);
	}
	
	
	public static boolean isEmptyAmount(Double amount) {
		return EMPTY_AMOUNT.equals(amount);
	}	
	
	public static boolean isNotEmptyAmount(Double amount) {
		return !isEmptyAmount(amount);
	}

}