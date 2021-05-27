package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

public class JourneyDuration implements Serializable {

	private String name;
	private String expression;
	private Date startDate;
	private Date endDate;
	
	public JourneyDuration(){
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExpression() {
		return null == expression ? "NL" : expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
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
	
}
