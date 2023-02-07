package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.api.model.type.WithholdingTypeGroup;

public class IRPFParams implements Serializable {
	
	private static final long serialVersionUID = 2683060057390169937L;
	
	private int domain;
	private String domainName;
	private String user;	
	private Integer registry;
	private Integer activity;
	private Date fromDate;
	private Date toDate;
	
	private Boolean output;
	private WithholdingTypeGroup typeGroup;
	private WithholdingType type;
	private Double percent;
	private RectificationType rectified;
	private Boolean accrualRegime;
	private Boolean investment;
	private Boolean service;
	private IRPFParamsOrderBy orderBy;
	private IRPFParamsGroupedBy groupedBy;
	
	private Integer[] invoices;
	
	public Integer[] getInvoices() {
		return invoices;
	}
	public void setInvoices(Integer[] invoices) {
		this.invoices = invoices;
	}
	public int getDomain() {
		return domain;
	}
	public IRPFParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getDomainName() {
		return domainName;
	}
	public IRPFParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public String getUser() {
		return user;
	}
	public IRPFParams setUser(String user) {
		this.user = user;
		return this;
	}
	public Integer getRegistry() {
		return registry;
	}
	public IRPFParams setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	public Integer getActivity() {
		return activity;
	}
	public IRPFParams setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public IRPFParams setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}
	public Date getToDate() {
		return toDate;
	}
	public IRPFParams setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}
	public Boolean getOutput() {
		return output;
	}
	public IRPFParams setOutput(Boolean output) {
		this.output = output;
		return this;
	}
	public boolean isOutput() {
		return output != null && output.booleanValue();
	}
	
	public WithholdingTypeGroup getWithholdingTypeGroup() {
		return typeGroup;
	} 
	public IRPFParams setWithholdingTypeGroup(WithholdingTypeGroup typeGroup) {
		this.typeGroup = typeGroup;
		return this;
	} 
	public WithholdingType getWithholdingType() {
		return type;
	}
	public IRPFParams setWithholdingType(WithholdingType type) {
		this.type = type;
		return this;
	}
	public IRPFParamsOrderBy getOrderBy() {
		return orderBy;
	}
	public IRPFParams setOrderBy(IRPFParamsOrderBy orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	public IRPFParamsGroupedBy getGroupedBy() {
		return groupedBy;
	}
	public IRPFParams setGroupedBy(IRPFParamsGroupedBy groupedBy) {
		this.groupedBy = groupedBy;
		return this;
	}
	public Double getPercent() {
		return percent;
	}
	public IRPFParams setPercent(Double percent) {
		this.percent = percent;
		return this;
	}
	public RectificationType getRectificationType() {
		return rectified;
	}
	public IRPFParams setRectificationType(RectificationType rectified) {
		this.rectified = rectified;
		return this;
	}
	public Boolean getAccrualRegime() {
		return accrualRegime;
	}
	public IRPFParams setAccrualRegime(Boolean accrualRegime) {
		this.accrualRegime = accrualRegime;
		return this;
	}
	public Boolean getInvestment() {
		return investment;
	}
	public IRPFParams setInvestment(Boolean investment) {
		this.investment = investment;
		return this;
	}
	public Boolean getService() {
		return service;
	}
	public IRPFParams setService(Boolean service) {
		this.service = service;
		return this;
	}
	public boolean isService() {
		return service != null && service.booleanValue();
	}
	
}
