package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

public class Period implements Serializable {
	
	private Date start, end;
	
	
	public Period() {
	}

	public Period(Date start, Date end ) {
		this.start = start;
		this.end = end;
	}
	
	public Date getStart() {
		return start;
	}
	
	public Date getEnd() {
		return end;
	}
	
	public void setStart(Date start) {
		this.start = start;
	}
	
	public void setEnd(Date end) {
		this.end = end;
	}

}
