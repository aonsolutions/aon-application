package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.time.Month;
import java.util.Date;

import com.esferalia.aon.occam.api.GwtIncompatible;
import com.esferalia.aon.occam.api.model.type.PaymentType;

public class Payment implements Serializable, HasId, HasDomain {

	private Integer id;
	private Integer domain;
	private String code;
	private Date startDate;
	private Date endDate;
	private String description;
	private String expression;
	private String irpfExpression;
	private String quoteExpression;
	private PaymentType type = PaymentType.CRA_0001;
	
	@GwtIncompatible
	private Month	month;
	
	
	@Override
	public Integer getId() {
		return id;
	}

	public Payment setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public String getCode() {
		return code;
	}

	public Payment setCode(String code) {
		this.code = code;
		return this;
	}

	public PaymentType getType() {
		return type;
	}
	
	public Payment setType(PaymentType type) {
		this.type = type;
		return this;
	}
	
	@GwtIncompatible
	public Month getMonth() {
		return month;
	}
	
	@GwtIncompatible
	public Payment setMonth(Month month) {
		this.month = month;
		return this;
	}

	@Override
	public Integer getDomain() {
		return domain;
	}

	public Payment setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Payment setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Payment setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String name) {
		this.description = name;
	}

	public String getExpression() {
		return expression;
	}

	public Payment setExpression(String expression) {
		this.expression = expression;
		return this;
	}

	public String getIrpfExpression() {
		return irpfExpression;
	}

	public Payment setIrpfExpression(String irpfExpression) {
		this.irpfExpression = irpfExpression;
		return this;
	}

	public String getQuoteExpression() {
		return quoteExpression;
	}

	public Payment setQuoteExpression(String quoteExpression) {
		this.quoteExpression = quoteExpression;
		return this;
	}

}
