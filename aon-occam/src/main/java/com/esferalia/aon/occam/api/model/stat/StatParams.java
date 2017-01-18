package com.esferalia.aon.occam.api.model.stat;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.stat.StatFilterItem.StatFilterType;
import com.esferalia.aon.occam.api.model.task.IssueFilter;
import com.esferalia.aon.occam.api.model.type.InvoiceType;

public class StatParams implements Serializable, Cloneable {

	private static final long serialVersionUID = 8321751053437854437L;

	private Date from;
	private Date to;
	private boolean viewAmounts;
	private IssueFilter issueFilter;

	private StatType statType;
	private Byte chartType;
	
	private LinkedList<StatFilterItem> filterItems; 
	private HashMap<String, String[]> filterMap;
	
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

	public boolean mustViewAmounts() {
		return viewAmounts;
	}
	public StatParams setViewAmounts(boolean viewAmounts) {
		this.viewAmounts = viewAmounts;
		return this;
	}

	public IssueFilter getIssueFilter() {
		return issueFilter;
	}

	public StatParams setIssueFilter(IssueFilter issueFilter) {
		this.issueFilter = issueFilter;
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

	public StatType getStatType() {
		return statType;
	}

	public StatParams setStatType(StatType statType) {
		this.statType = statType;
		return this;
	}

	public Byte getChartType() {
		return chartType;
	}

	public StatParams setChartType(Byte chartType) {
		this.chartType = chartType;
		return this;
	}
	
	public HashMap<String, String[]> getFilterMap() {
		return filterMap;
	}

	public StatParams setFilterMap(HashMap<String, String[]> filterMap) {
		this.filterMap = filterMap;
		return this;
	}

	public StatParams clone(){
		return null;
	}
	
	public boolean isResultVisible() {
		if (getFilterItems().size() > 0) {
			boolean sales = false;
			boolean other = false;
			for (StatFilterItem item : getFilterItems()) {
				if (item.getType() == StatFilterType.INVOICE_TYPE && item.isSelected()) {
					if (InvoiceType.valueOf( item.getId()) == InvoiceType.SALES) {
						sales = true;	
					} else {
						other = true;
					}
				}
			}
			return (sales && other) || (!sales && !other);
		}
		return true;
	}
	
}
