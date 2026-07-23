package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.type.AmortizationPeriod;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonUtils;

public class Amortization implements Serializable {

	private static final long serialVersionUID = -2794598210833859367L;
	
	private boolean dirty;
	
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
	private LinkedList<AmortizationDetail> details;
	private LinkedList<String> messages;
	
	public Integer getId() {
		return id;
	}
	public Amortization setId(Integer id) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.id, id) );
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public Amortization setDomain(Integer domain) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.domain, domain) );
		this.domain = domain;
		return this;
	}
	
	public InvestAsset getInvestAsset() {
		return investAsset;
	}
	public Amortization setInvestAsset(InvestAsset investAsset) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.investAsset, investAsset) );
		this.investAsset = investAsset;
		return this;
	}
	
	public Account getAllocationAccount() {
		return allocationAccount;
	}
	public Amortization setAllocationAccount(Account allocationAccount) {
		Integer newId = allocationAccount != null ? allocationAccount.getId() : null;
		Integer oldId = this.allocationAccount != null ? this.allocationAccount.getId() : null;
		this.setDirty( isDirty()?true:AonUtils.notEquals(oldId, newId) );
		this.allocationAccount = allocationAccount;
		return this;
	}
	
	public Account getAccumulatedAccount() {
		return accumulatedAccount;
	}
	public Amortization setAccumulatedAccount(Account accumulatedAccount) {
		Integer newId = accumulatedAccount != null ? accumulatedAccount.getId() : null;
		Integer oldId = this.accumulatedAccount != null ? this.accumulatedAccount.getId() : null;
		this.setDirty( isDirty()?true:AonUtils.notEquals(oldId, newId) );
		this.accumulatedAccount = accumulatedAccount;
		return this;
	}
	
	public Account getFixedAssetAccount() {
		return fixedAssetAccount;
	}
	public Amortization setFixedAssetAccount(Account fixedAssetAccount) {
		Integer newId = fixedAssetAccount != null ? fixedAssetAccount.getId() : null;
		Integer oldId = this.fixedAssetAccount != null ? this.fixedAssetAccount.getId() : null;
		this.setDirty( isDirty()?true:AonUtils.notEquals(oldId, newId) );
		this.fixedAssetAccount = fixedAssetAccount;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	public Amortization setDescription(String description) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.description, description) );
		this.description = description;
		return this;
	}
	
	public Date getInitialDate() {
		return initialDate;
	}
	public Amortization setInitialDate(Date initialDate) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.initialDate, initialDate) );
		this.initialDate = initialDate;
		return this;
	}
	
	public Date getDeadline() {
		return deadline;
	}
	public Amortization setDeadline(Date deadline) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.deadline, deadline) );
		this.deadline = deadline;
		return this;
	}
	
	public Double getAmount() {
		return amount;
	}
	public Amortization setAmount(Double amount) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.amount, amount) );
		this.amount = amount;
		return this;
	}
	
	public AmortizationPeriod getFeePeriod() {
		return feePeriod;
	}
	public Amortization setFeePeriod(AmortizationPeriod feePeriod) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.feePeriod, feePeriod) );
		this.feePeriod = feePeriod;
		return this;
	}
	
	public Double getSaleAmount() {
		return saleAmount;
	}
	public Amortization setSaleAmount(Double saleAmount) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.saleAmount, saleAmount) );
		this.saleAmount = saleAmount;
		return this;
	}
	
	public String getComments() {
		return comments;
	}
	public Amortization setComments(String comments) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.comments, comments) );
		this.comments = comments;
		return this;
	}
	
	public double getPercentage() {
		return percentage;
	}
	public Amortization setPercentage(double percentage) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.percentage, percentage) );
		this.percentage = percentage;
		return this;
	}
	
    public int getYears() {
    	return (AonMathUtils.isZero(getPercentage()))? 0 : (int) AonMathUtils.round( 100 / getPercentage(),0);	
	}

	public void setYears(int years) {
		if (AonMathUtils.isNotZero(years)) {
			setPercentage(AonMathUtils.round( 100.0 / years));
		} else {
			setPercentage(0);	
		}
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public Amortization setSecurityLevel(SecurityLevel securityLevel) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.securityLevel, securityLevel) );
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
		Integer newId = amortizationType != null ? amortizationType.getId() : null;
		Integer oldId = this.amortizationType != null ? this.amortizationType.getId() : null;
		this.setDirty( isDirty()?true:AonUtils.notEquals(oldId, newId) );
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
	
	public Stream<String> messageStream() {
		return AonCollectionUtils.stream(this.messages);
	}
	private List<String> ensureMessages() {
	    if ( this.messages == null ) messages = new LinkedList<>();
	    return this.messages;
	}
	public boolean hasMessages() {
		return AonCollectionUtils.isNotEmpty(this.messages);
	}
	public int getMessagesSize() {
		return AonCollectionUtils.size(this.messages);
	}
	public Amortization addMessage(String message) {
		ensureMessages().add(message);
	    return this;
	}
	
	// ---------------------------------------------------------- DIRTY
	public boolean isDirty() {
		return dirty || areDetailsDirty();
	}
	public Amortization setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
	private boolean areDetailsDirty() {
		return detailStream().anyMatch(d -> d.isDirty());
	}
	
	public boolean isNotDisposed() {
		// Si el importe de venta es nulo o cero y la fecha de amortización es nula
		// se considera que el activo no se ha vendido o dado de baja.
		return AonMathUtils.isNullOrZero( this.getSaleAmount())
			&& this.getDeadline() == null
		;
	}
	public boolean isDisposed() {
		return !isNotDisposed();
	}
}
