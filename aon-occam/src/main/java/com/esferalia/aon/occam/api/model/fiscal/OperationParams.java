package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class OperationParams implements Serializable {
	
	private static final long serialVersionUID = 2683060057390169937L;
	
	private int domain;
	private Integer registry;
	private Integer account;
	private Integer activity;
	private String activityDescription;
	private Date fromDate;
	private Date toDate;
	
	private Boolean output;
	private Boolean type;
	private Boolean expenses;
	private Boolean irpf;
	private WithholdingType withholdingType;
	private Double percent;
	private RectificationType rectified;
	private Boolean accrualRegime;
	private Boolean investment;
	private Boolean service;
	private Integer orderBy;
	private Integer groupByNif;
	
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
	public OperationParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public Integer getRegistry() {
		return registry;
	}
	public OperationParams setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	public Integer getActivity() {
		return activity;
	}
	public OperationParams setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	public String getActivityDescription() {
		return activityDescription;
	}
	public OperationParams setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
		return this;
	}
	public Boolean getExpenses() {
		return expenses;
	}
	public OperationParams setExpenses(Boolean expenses) {
		this.expenses = expenses;
		return this;
	}
	public Boolean getIrpf() {
		return irpf;
	}
	public OperationParams setIrpf(Boolean irpf) {
		this.irpf = irpf;
		return this;
	}
	public Integer getAccount() {
		return account;
	}
	public OperationParams setAccount(Integer account) {
		this.account = account;
		return this;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public OperationParams setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}
	public Date getToDate() {
		return toDate;
	}
	public OperationParams setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}
	public Boolean getOutput() {
		return output;
	}
	public OperationParams setOutput(Boolean output) {
		this.output = output;
		return this;
	}
	public Integer getOrderBy() {
		return orderBy;
	}
	public OperationParams setOrderBy(Integer orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	public Integer getGroupByNif() {
		return groupByNif;
	}
	public OperationParams setGroupByNif(Integer groupByNif) {
		this.groupByNif = groupByNif;
		return this;
	}
	public Double getPercent() {
		return percent;
	}
	public OperationParams setPercent(Double percent) {
		this.percent = percent;
		return this;
	}
	public RectificationType getRectificationType() {
		return rectified;
	}
	public OperationParams setRectificationType(RectificationType rectified) {
		this.rectified = rectified;
		return this;
	}
	public Boolean getAccrualRegime() {
		return accrualRegime;
	}
	public OperationParams setAccrualRegime(Boolean accrualRegime) {
		this.accrualRegime = accrualRegime;
		return this;
	}
	public Boolean getInvestment() {
		return investment;
	}
	public OperationParams setInvestment(Boolean investment) {
		this.investment = investment;
		return this;
	}
	public Boolean getService() {
		return service;
	}
	public OperationParams setService(Boolean service) {
		this.service = service;
		return this;
	}
	
	public Boolean getType() {
		return type;
	}
	public OperationParams setType(Boolean type) {
		this.type = type;
		return this;
	}
	
	public WithholdingType getWithholdingType() {
		return withholdingType;
	}
	public OperationParams setWithholdingType(WithholdingType withholdingType) {
		this.withholdingType = withholdingType;
		return null;
	}
	
}
