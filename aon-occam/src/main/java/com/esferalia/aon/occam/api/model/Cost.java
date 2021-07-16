package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.DeductionType;

public class Cost implements Serializable, HasId, HasDomain, HasStartDate, HasEndDate {

	private Integer id;
	private Integer domain;
	private Date startDate;
	private Date endDate;
	private String name;
	private String description;
	private String expression;
	private DeductionType type ;
	
	
	@Override
	public Integer getId() {
		return id;
	}

	public Cost setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public DeductionType getType() {
		return type;
	}
	
	public Cost setType(DeductionType type) {
		this.type = type;
		return this;
	}
	
	@Override
	public Integer getDomain() {
		return domain;
	}

	public Cost setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	public Cost setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	public Cost setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public Cost setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Cost setDescription(String name) {
		this.description = name;
		return this;
	}

	public String getExpression() {
		return expression;
	}

	public Cost setExpression(String expression) {
		this.expression = expression;
		return this;
	}
	
	


}
