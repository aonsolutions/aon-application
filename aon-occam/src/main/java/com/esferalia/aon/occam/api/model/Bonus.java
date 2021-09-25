package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.BonusType;

public class Bonus implements Serializable, HasId, HasDomain  , HasStartDate, HasEndDate {

	private static final long serialVersionUID = 3824440651805682374L;
	
	private Integer id;
	private Integer domain;
	private Date startDate;
	private Date endDate;
	private String description;
	private String expression;
	private BonusType type ;
	
	
	@Override
	public Integer getId() {
		return id;
	}

	public Bonus setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public BonusType getType() {
		return type;
	}
	
	public Bonus setType(BonusType type) {
		this.type = type;
		return this;
	}
	
	@Override
	public Integer getDomain() {
		return domain;
	}

	public Bonus setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	public Bonus setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	public Bonus setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Bonus setDescription(String name) {
		this.description = name;
		return this;
	}

	public String getExpression() {
		return expression;
	}

	public Bonus setExpression(String expression) {
		this.expression = expression;
		return this;
	}


}
