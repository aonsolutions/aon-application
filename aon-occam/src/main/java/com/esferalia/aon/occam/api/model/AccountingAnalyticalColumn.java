package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountingAnalyticalColumn implements Serializable, Comparable<AccountingAnalyticalColumn> {

	private static final long serialVersionUID = 3412611142698585498L;
	
	private String name;
	private double percent;
	private boolean totalColumn;
	private boolean defaultColumn;
	
	public AccountingAnalyticalColumn() {
		super();
	}

	public String getName() {
		return name;
	}
	public AccountingAnalyticalColumn setName(String name) {
		this.name = name;
		return this;
	}
	public double getPercent() {
		return percent;
	}
	public AccountingAnalyticalColumn setPercent(double percent) {
		this.percent = percent;
		return this;
	}
	
	public boolean isTotalColumn() {
		return totalColumn;
	}
	public AccountingAnalyticalColumn setTotalColumn(boolean totalColumn) {
		this.totalColumn = totalColumn;
		return this;
	}
	public boolean isMain() {
		return defaultColumn;
	}
	public AccountingAnalyticalColumn setDefaultColumn(boolean defaultColumn) {
		this.defaultColumn = defaultColumn;
		return this;
	}
	@Override
	public int compareTo(AccountingAnalyticalColumn other) {
		return AonStringUtils.compare(getName(), other.getName());
	}
}
