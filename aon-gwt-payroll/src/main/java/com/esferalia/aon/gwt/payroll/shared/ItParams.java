package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

public class ItParams implements Serializable {

	private static final long serialVersionUID = 6015720258657135544L;

	private String description;
	private Date start;
	private Date end;
	private Integer workplace;
	
	private int limit;
	private int offset;
	
	private String orderBy;
	private boolean asc = true;
	
	public ItParams() {
		super();
	}

	public String getDescription() {
		return description;
	}

	public ItParams setDescription(String description) {
		this.description = description;
		return this;
	}

	public Date getStart() {
		return start;
	}

	public ItParams setStart(Date start) {
		this.start = start;
		return this;
	}

	public Date getEnd() {
		return end;
	}

	public ItParams setEnd(Date end) {
		this.end = end;
		return this;
	}
	
	public Integer getWorkplace() {
		return workplace;
	}

	public ItParams setWorkplace(Integer workplace) {
		this.workplace = workplace;
		return this;
	}

	public int getLimit() {
		return limit;
	}
	
	public ItParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public ItParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	
	public String getOrderBy() {
		return orderBy;
	}
	
	public ItParams setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	
	public boolean isAsc() {
		return asc;
	}
	
	public ItParams setAsc(boolean asc) {
		this.asc = asc;
		return this;
	}
}
