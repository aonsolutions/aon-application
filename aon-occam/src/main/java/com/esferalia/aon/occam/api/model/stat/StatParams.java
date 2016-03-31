package com.esferalia.aon.occam.api.model.stat;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

public class StatParams implements Serializable, Cloneable {

	private static final long serialVersionUID = 8321751053437854437L;

	private Date from;
	private Date to;
	private StatChartType chartType;
	
	private LinkedList<StatFilterItem> filterItems; 
	
	public Date getFrom() {
		return from;
	}

	public StatParams setFrom(Date from) {
		this.from = from;
		return this;
	}

	public Date getTo() {
		return to;
	}

	public StatParams setTo(Date to) {
		this.to = to;
		return this;
	}

	public StatChartType getChartType() {
		return chartType;
	}

	public StatParams setChartType(StatChartType chartType) {
		this.chartType = chartType;
		return this;
	}

	public LinkedList<StatFilterItem> getFilterItems() {
		if (filterItems == null) {
			setFilterItems( new LinkedList<StatFilterItem>() );
		}
		return filterItems;
	}

	public StatParams setFilterItems(LinkedList<StatFilterItem> map) {
		this.filterItems = map;
		return this;
	}
	
	public StatParams clone(){
		return null;
	}
	
	
}
