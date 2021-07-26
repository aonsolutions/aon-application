package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.gwt.payroll.shared.Shared.format;
import static com.esferalia.aon.gwt.payroll.shared.Shared.parse;

import java.io.Serializable;
import java.util.Date;

public class JourneyDuration implements Serializable {

	private String name;
	private String expression;
	private String startDate;
	private String endDate;
	
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
		return parse(startDate);
	}

	public void setStartDate(Date startDate) {
		this.startDate = format(startDate);
	}

	public Date getEndDate() {
		return parse(endDate);
	}

	public void setEndDate(Date endDate) {
		this.endDate = format(endDate);
	}
	
}
