package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.gwt.payroll.shared.Shared.format;
import static com.esferalia.aon.gwt.payroll.shared.Shared.parse;

import java.io.Serializable;
import java.util.Date;

public class Period implements Serializable {
	
	private String start, end;
	
	
	public Period() {
	}

	public Period(Date start, Date end ) {
		this.start = format(start);
		this.end = format(end);
	}
	
	public Date getStart() {
		return parse(start);
	}
	
	public Date getEnd() {
		return parse(end);
	}
	
	public void setStart(Date start) {
		this.start = format(start);
	}
	
	public void setEnd(Date end) {
		this.end = format(end);
	}

}
