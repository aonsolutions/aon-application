package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

public class InvoiceModelReportParams implements Serializable {
	
	private static final long serialVersionUID = 2683060057390169937L;
	
	private int domain;
	private Date fromDate;
	private Date toDate;
	private Integer activity;
	private String activityDescription;

	public int getDomain() {
		return domain;
	}
	public InvoiceModelReportParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public Integer getActivity() {
		return activity;
	}
	public InvoiceModelReportParams setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	public String getActivityDescription() {
		return activityDescription;
	}
	public InvoiceModelReportParams setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
		return this;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public InvoiceModelReportParams setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}
	public Date getToDate() {
		return toDate;
	}
	public InvoiceModelReportParams setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}
}
