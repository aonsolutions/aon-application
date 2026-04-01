package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.type.AmortizationPeriod;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class Amortization implements Serializable {

	private static final long serialVersionUID = -2794598210833859367L;
	
	private Integer id;
	private Integer domain;
	private InvestAsset investAsset;
	private Account allocationAccount;
	private Account accumulatedAccount;
	private Account fixedAssetAccount;
	private String description;
	private Date initialDate;
	private Date deadline;
	private Double amount;
	private AmortizationPeriod feePeriod;
	private Double saleAmount;
	private String comments;
	private double percentage;
	private SecurityLevel securityLevel;

	private AmortizationType amortizationType;
	LinkedList<AmortizationDetail> details;
	
	public Integer getId() {
		return id;
	}
	public Amortization setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public Amortization setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public InvestAsset getInvestAsset() {
		return investAsset;
	}
	public Amortization setInvestAsset(InvestAsset investAsset) {
		this.investAsset = investAsset;
		return this;
	}
	
	public Account getAllocationAccount() {
		return allocationAccount;
	}
	public Amortization setAllocationAccount(Account allocationAccount) {
		this.allocationAccount = allocationAccount;
		return this;
	}
	
	public Account getAccumulatedAccount() {
		return accumulatedAccount;
	}
	public Amortization setAccumulatedAccount(Account accumulatedAccount) {
		this.accumulatedAccount = accumulatedAccount;
		return this;
	}
	
	public Account getFixedAssetAccount() {
		return fixedAssetAccount;
	}
	public Amortization setFixedAssetAccount(Account fixedAssetAccount) {
		this.fixedAssetAccount = fixedAssetAccount;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	public Amortization setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public Date getInitialDate() {
		return initialDate;
	}
	public Amortization setInitialDate(Date initialDate) {
		this.initialDate = initialDate;
		return this;
	}
	
	public Date getDeadline() {
		return deadline;
	}
	public Amortization setDeadline(Date deadline) {
		this.deadline = deadline;
		return this;
	}
	
	public Double getAmount() {
		return amount;
	}
	public Amortization setAmount(Double amount) {
		this.amount = amount;
		return this;
	}
	
	public AmortizationPeriod getFeePeriod() {
		return feePeriod;
	}
	public Amortization setFeePeriod(AmortizationPeriod feePeriod) {
		this.feePeriod = feePeriod;
		return this;
	}
	
	public Double getSaleAmount() {
		return saleAmount;
	}
	public Amortization setSaleAmount(Double saleAmount) {
		this.saleAmount = saleAmount;
		return this;
	}
	
	public String getComments() {
		return comments;
	}
	public Amortization setComments(String comments) {
		this.comments = comments;
		return this;
	}
	
	public double getPercentage() {
		return percentage;
	}
	public Amortization setPercentage(double percentage) {
		this.percentage = percentage;
		return this;
	}
	
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public Amortization setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	public Amortization setConfidential(boolean confidential) {
		return setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
	}

	public AmortizationType getAmortizationType() {
		return amortizationType;
	}
	public Amortization setAmortizationType(AmortizationType amortizationType) {
		this.amortizationType = amortizationType;
		return this;
	}
	
	public Stream<AmortizationDetail> detailStream() {
		return AonCollectionUtils.stream(details);
	}
	public void clearDetails() {
		AonCollectionUtils.clear(details);
	}
	public void addDetail(AmortizationDetail detail) {
		if (details == null) details = new LinkedList<>();
		details.add(detail);
	}
	
}
