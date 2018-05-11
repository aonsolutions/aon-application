package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

public class DateInterval implements Serializable, Comparable<DateInterval> {

	private static final long serialVersionUID = 6645776588511049453L;

	private Date start;
	private Date end;
	private String name;

	public Date getStart() {
		return start;
	}
	public DateInterval setStart(Date start) {
		this.start = start;
		return this;
	}

	public Date getEnd() {
		return end;
	}
	public DateInterval setEnd(Date end) {
		this.end = end;
		return this;
	}

	public String getName() {
		return name;
	}
	public DateInterval setName(String name) {
		this.name = name;
		return this;
	}
	
	@Override
	public int compareTo(DateInterval other) {
		if (getStart() == null && (other == null || other.getStart() == null)) return 0;
		if (other == null || other.getStart() == null) return -1;
		if (getStart() == null) return 1;
		return getStart().compareTo(other.getStart());
	}

}
