package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.fiscal.VatSummaryType;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class InvoiceConsoleParams implements Serializable {
	
	private static final long serialVersionUID = 2683060057390169937L;
	
	private Integer domain;
	private Date fromDate;
	private Date toDate;
	private Integer activity;
	
	private Integer registry;
	private Boolean output;
	private VatSummaryType vatSummaryType;
	private RectificationType rectified;
	private Boolean surcharge;
	private Boolean farmerRegime;
	private Boolean accrualRegime;
	private Boolean investment;
	private Boolean service;
	private Boolean recorded;
	private InvoiceSource source;

	private SecurityLevel securityLevel;
	
	private int offset;
	private int limit;

	public Integer getDomain() {
		return domain;
	}
	public InvoiceConsoleParams setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Date getFromDate() {
		return fromDate;
	}
	public InvoiceConsoleParams setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	public Date getToDate() {
		return toDate;
	}
	public InvoiceConsoleParams setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}

	public Integer getActivity() {
		return activity;
	}
	public InvoiceConsoleParams setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public InvoiceConsoleParams setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	
	public Integer getRegistry() {
		return registry;
	}
	public InvoiceConsoleParams setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	
	public Boolean getOutput() {
		return output;
	}
	public InvoiceConsoleParams setOutput(Boolean output) {
		this.output = output;
		return this;
	}
	public boolean isOutput() {
		return output != null && output.booleanValue();
	}
	public boolean isInput() {
		return output != null && !output.booleanValue();
	}
	
	public VatSummaryType getVatSummaryType() {
		return vatSummaryType;
	}
	public InvoiceConsoleParams setVatSummaryType(VatSummaryType vatSummaryType) {
		this.vatSummaryType = vatSummaryType;
		return this;
	}
	
	public RectificationType getRectificationType() {
		return rectified;
	}
	public InvoiceConsoleParams setRectificationType(RectificationType rectified) {
		this.rectified = rectified;
		return this;
	}
	
	public Boolean getSurcharge() {
		return surcharge;
	}
	public InvoiceConsoleParams setSurcharge(Boolean surcharge) {
		this.surcharge = surcharge;
		return this;
	}
	
	public Boolean getFarmerRegime() {
		return farmerRegime;
	}
	public InvoiceConsoleParams setFarmerRegime(Boolean farmerRegime) {
		this.farmerRegime = farmerRegime;
		return this;
	}
	
	public Boolean getAccrualRegime() {
		return accrualRegime;
	}
	public InvoiceConsoleParams setAccrualRegime(Boolean accrualRegime) {
		this.accrualRegime = accrualRegime;
		return this;
	}
	
	public Boolean getInvestment() {
		return investment;
	}
	public InvoiceConsoleParams setInvestment(Boolean investment) {
		this.investment = investment;
		return this;
	}
	
	public Boolean getService() {
		return service;
	}
	public InvoiceConsoleParams setService(Boolean service) {
		this.service = service;
		return this;
	}
	
	public Boolean getRecorded() {
		return recorded;
	}
	public InvoiceConsoleParams setRecorded(Boolean recorded) {
		this.recorded = recorded;
		return this;
	}
	
	public InvoiceSource getSource() {
		return source;
	}
	public InvoiceConsoleParams setSource(InvoiceSource source) {
		this.source = source;
		return this;
	}
	
	public int getOffset() {
		return offset;
	}
	public InvoiceConsoleParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	
	public int getLimit() {
		return limit;
	}
	public InvoiceConsoleParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
}
