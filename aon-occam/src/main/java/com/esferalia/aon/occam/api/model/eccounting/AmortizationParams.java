package com.esferalia.aon.occam.api.model.eccounting;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.type.AmortizationDetailStatus;
import com.esferalia.aon.occam.api.model.type.AmortizationPeriod;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class AmortizationParams implements Serializable {

	private static final long serialVersionUID = 2683060057390169937L;
	
	private Integer fromId;
	private Integer toId;
	private Integer domain;
	private Integer activity;
	
	private Integer investAsset;
	private Integer allocationAccount;
	private Integer accumulatedAccount;
	private Integer fixedAssetAccount;
	private String description;
	private Date initialDate;
	private Date deadline;
	private Double amount;
	private AmortizationPeriod feePeriod;
	private Double saleAmount;
	private String comments;
	private Double percentage;
	private SecurityLevel securityLevel;
	private AmortizationDetailStatus status;

	private int offset;
	private int limit = 50;

	public Integer getFromId() {
		return fromId;
	}
	public AmortizationParams setFromId(Integer fromId) {
		this.fromId = fromId;
		return this;
	}
	
	public Integer getToId() {
		return toId;
	}
	public AmortizationParams setToId(Integer toId) {
		this.toId = toId;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public AmortizationParams setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getActivity() {
		return activity;
	}
	public AmortizationParams setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	
	public Integer getInvestAsset() {
		return investAsset;
	}
	public AmortizationParams setInvestAsset(Integer investAsset) {
		this.investAsset = investAsset;
		return this;
	}
	
	public Integer getAllocationAccount() {
		return allocationAccount;
	}
	public AmortizationParams setAllocationAccount(Integer allocationAccount) {
		this.allocationAccount = allocationAccount;
		return this;
	}
	
	public Integer getAccumulatedAccount() {
		return accumulatedAccount;
	}
	public AmortizationParams setAccumulatedAccount(Integer accumulatedAccount) {
		this.accumulatedAccount = accumulatedAccount;
		return this;
	}
	
	public Integer getFixedAssetAccount() {
		return fixedAssetAccount;
	}
	public AmortizationParams setFixedAssetAccount(Integer fixedAssetAccount) {
		this.fixedAssetAccount = fixedAssetAccount;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	public AmortizationParams setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public Date getInitialDate() {
		return initialDate;
	}
	public AmortizationParams setInitialDate(Date initialDate) {
		this.initialDate = initialDate;
		return this;
	}
	
	public Date getDeadline() {
		return deadline;
	}
	public AmortizationParams setDeadline(Date deadline) {
		this.deadline = deadline;
		return this;
	}
	
	public Double getAmount() {
		return amount;
	}
	public AmortizationParams setAmount(Double amount) {
		this.amount = amount;
		return this;
	}
	
	public AmortizationPeriod getFeePeriod() {
		return feePeriod;
	}
	public AmortizationParams setFeePeriod(AmortizationPeriod feePeriod) {
		this.feePeriod = feePeriod;
		return this;
	}
	
	public Double getSaleAmount() {
		return saleAmount;
	}
	public AmortizationParams setSaleAmount(Double saleAmount) {
		this.saleAmount = saleAmount;
		return this;
	}
	
	public String getComments() {
		return comments;
	}
	public AmortizationParams setComments(String comments) {
		this.comments = comments;
		return this;
	}
	
	public Double getPercentage() {
		return percentage;
	}
	public AmortizationParams setPercentage(Double percentage) {
		this.percentage = percentage;
		return this;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public AmortizationParams setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	
	public AmortizationDetailStatus getStatus() {
		return status;
	}
	public AmortizationParams setStatus(AmortizationDetailStatus status) {
		this.status = status;
		return this;
	}
	
	public int getOffset() {
		return offset;
	}
	public AmortizationParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	
	public int getLimit() {
		return limit;
	}
	public AmortizationParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	
}
