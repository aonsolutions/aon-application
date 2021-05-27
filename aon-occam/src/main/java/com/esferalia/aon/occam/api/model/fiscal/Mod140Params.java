package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

public class Mod140Params implements Serializable {
	
	private static final long serialVersionUID = -6326188602123004617L;

	private int domain;
	
	private boolean filterByTaxDateEnabled;
	private Date fromDate;
	private Date toDate;
	
	public int getDomain() {
		return domain;
	}
	public void setDomain(int domain) {
		this.domain = domain;
	}
	public boolean isFilterByTaxDateEnabled() {
		return filterByTaxDateEnabled;
	}
	public void setFilterByTaxDateEnabled(boolean filterByTaxDateEnabled) {
		this.filterByTaxDateEnabled = filterByTaxDateEnabled;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

}
