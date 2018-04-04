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

	private int domain;
	private Date from;
	private Date to;
	private boolean viewAmounts;
	private boolean viewPreviousPeriod;
	private IssueFilter issueFilter;

	private StatType statType;
	private Byte chartType;
	
	private Integer registry;
	private Integer product;
	
	private LinkedList<StatFilterItem> filterItems; 
	private HashMap<String, String[]> filterMap;
	

	
	public int getDomain() {
		return domain;
	}

	public StatParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}

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

	public boolean isViewAmounts() {
		return viewAmounts;
	}
	public StatParams setViewAmounts(boolean viewAmounts) {
		this.viewAmounts = viewAmounts;
		return this;
	}
	public boolean isViewPreviousPeriodAvailable() {
		return isViewPreviousPeriod() && getFrom() != null && getTo() != null;
	}

	public boolean isViewPreviousPeriod() {
		return viewPreviousPeriod;
	}
	public StatParams setViewPreviousPeriod(boolean viewPreviousPeriod) {
		this.viewPreviousPeriod = viewPreviousPeriod;
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
	
	public Integer getRegistry() {
		return registry;
	}
	public StatParams setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	
	public Integer getProduct() {
		return product;
	}
	public StatParams setProduct(Integer product) {
		this.product = product;
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

	public void clean() {
		registry = null;
		product = null;
		for (StatFilterItem item : getFilterItems()) {
			item.setSelected(false);
		}
	}
	public boolean hasSegmentFilter() {
		boolean found = false;
		for (StatFilterItem item : getFilterItems() ) {
			if (item.getType() == StatFilterType.SEGMENT && item.isSelected()) {
				found = true;
				break;
			}
		}
		return found;
	}
	public boolean hasTagFilter() {
		boolean found = false;
		for (StatFilterItem item : getFilterItems() ) {
			if (item.getType() == StatFilterType.PRODUCT_TAG && item.isSelected()) {
				found = true;
				break;
			}
		}
		return found;
	}

	public boolean isSalesSelected() {
		boolean found = true;
		for (StatFilterItem item : getFilterItems() ) {
			if (item.getType() == StatFilterType.INVOICE_TYPE && item.isSelected()) {
				if (InvoiceType.valueOf(item.getId()) == InvoiceType.SALES) {
					found = true;
					break;
				} else {
					found = false;
				}
			}
		}
		return found;
	}

	public boolean isPurchaseSelected() {
		boolean found = true;
		for (StatFilterItem item : getFilterItems() ) {
			if (item.getType() == StatFilterType.INVOICE_TYPE && item.isSelected()) {
				if (InvoiceType.valueOf(item.getId()) == InvoiceType.PURCHASE) {
					found = true;
					break;
				} else {
					found = false;
				}
			}
		}
		return found;
	}
}
