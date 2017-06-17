package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

public class VatParams implements Serializable {
	
	private static final long serialVersionUID = 2683060057390169937L;
	
	private int domain;
	private Integer registry;
	private Integer activity;
	private Date fromDate;
	private Date toDate;
	
	private Boolean output;
	private VatSummaryType type;
	private Double percent;
	private Boolean surcharge;
	private Boolean farmerRegime;
	private Boolean accrualRegime;
	private Boolean investment;
	private Boolean service;
	
	Integer[] invoices;
	
	public Integer[] getInvoices() {
		return invoices;
	}
	public void setInvoices(Integer[] invoices) {
		this.invoices = invoices;
	}
	public int getDomain() {
		return domain;
	}
	public VatParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public Integer getRegistry() {
		return registry;
	}
	public VatParams setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	public Integer getActivity() {
		return activity;
	}
	public VatParams setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public VatParams setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}
	public Date getToDate() {
		return toDate;
	}
	public VatParams setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}
	public Boolean getOutput() {
		return output;
	}
	public VatParams setOutput(Boolean output) {
		this.output = output;
		return this;
	}
	public VatSummaryType getVatSummaryType() {
		return type;
	}
	public VatParams setVatSummaryType(VatSummaryType type) {
		this.type = type;
		return this;
	}
	public Double getPercent() {
		return percent;
	}
	public VatParams setPercent(Double percent) {
		this.percent = percent;
		return this;
	}
	public Boolean getSurcharge() {
		return surcharge;
	}
	public VatParams setSurcharge(Boolean surcharge) {
		this.surcharge = surcharge;
		return this;
	}
	public Boolean getFarmerRegime() {
		return farmerRegime;
	}
	public VatParams setFarmerRegime(Boolean farmerRegime) {
		this.farmerRegime = farmerRegime;
		return this;
	}
	public Boolean getAccrualRegime() {
		return accrualRegime;
	}
	public VatParams setAccrualRegime(Boolean accrualRegime) {
		this.accrualRegime = accrualRegime;
		return this;
	}
	public Boolean getInvestment() {
		return investment;
	}
	public VatParams setInvestment(Boolean investment) {
		this.investment = investment;
		return this;
	}
	public Boolean getService() {
		return service;
	}
	public VatParams setService(Boolean service) {
		this.service = service;
		return this;
	}
	
}
