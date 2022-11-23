package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.sql.Date;

public class EnterpriseData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer enterprise;
	private String name;
	private String expression;
	private Date startDate;
	private Date endDate;
	
	private boolean isRemoved;
	
	public EnterpriseData() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public EnterpriseData setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public EnterpriseData setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getEnterprise() {
		return enterprise;
	}

	public EnterpriseData setEnterprise(Integer enterprise) {
		this.enterprise = enterprise;
		return this;
	}

	public String getName() {
		return name;
	}

	public EnterpriseData setName(String name) {
		this.name = name;
		return this;
	}

	public String getExpression() {
		return expression;
	}

	public EnterpriseData setExpression(String expression) {
		this.expression = expression;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public EnterpriseData setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public EnterpriseData setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
	public boolean isRemoved() {
		return isRemoved;
	}

	public EnterpriseData setIsRemoved(boolean isRemoved) {
		this.isRemoved = isRemoved;
		return this;
	}

}
