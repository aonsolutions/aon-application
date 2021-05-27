package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.DeductionType;

public class Deduction implements Serializable, HasId, HasDomain, HasStartDate, HasEndDate {

	private Integer id;
	private Integer domain;
	private Date startDate;
	private Date endDate;
	private String description;
	private String expression;
	private DeductionType type ;
	
	
	@Override
	public Integer getId() {
		return id;
	}

	public Deduction setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public DeductionType getType() {
		return type;
	}
	
	public Deduction setType(DeductionType type) {
		this.type = type;
		return this;
	}
	
	@Override
	public Integer getDomain() {
		return domain;
	}

	public Deduction setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	public Deduction setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	public Deduction setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Deduction setDescription(String name) {
		this.description = name;
		return this;
	}

	public String getExpression() {
		return expression;
	}

	public Deduction setExpression(String expression) {
		this.expression = expression;
		return this;
	}


}
