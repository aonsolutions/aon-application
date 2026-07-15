package com.esferalia.aon.occam.api.model.eccounting;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.type.AmortizationDetailStatus;
import com.esferalia.aon.occam.api.model.type.AmortizationPeriod;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonStringUtils;

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
	
	private Date fromInitialDate;
	private Date toInitialDate;
	
	private Date fromDeadline;
	private Date toDeadline;
	
	private Double amount;
	private AmortizationPeriod feePeriod;
	private Double saleAmount;
	private String comments;
	private Double percentage;
	private SecurityLevel securityLevel;
	private AmortizationDetailStatus status;
	private AmortizationParamsOrderBy orderBy;

	private int offset;
	private int limit = 50;

	public Optional<Integer> getFromId() {
		return Optional.ofNullable(fromId);
	}
	public AmortizationParams setFromId(Integer fromId) {
		this.fromId = fromId;
		return this;
	}
	
	public Optional<Integer> getToId() {
		return Optional.ofNullable(toId);
	}
	public AmortizationParams setToId(Integer toId) {
		this.toId = toId;
		return this;
	}
	
	public Optional<Integer> getDomain() {
		return Optional.ofNullable(domain);
	}
	public AmortizationParams setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Optional<Integer> getActivity() {
		return Optional.ofNullable(activity);
	}
	public AmortizationParams setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	
	public Optional<Integer> getInvestAsset() {
		return Optional.ofNullable(investAsset);
	}
	public AmortizationParams setInvestAsset(Integer investAsset) {
		this.investAsset = investAsset;
		return this;
	}
	
	public Optional<Integer> getAllocationAccount() {
		return Optional.ofNullable(allocationAccount);
	}
	public AmortizationParams setAllocationAccount(Integer allocationAccount) {
		this.allocationAccount = allocationAccount;
		return this;
	}
	
	public Optional<Integer> getAccumulatedAccount() {
		return Optional.ofNullable(accumulatedAccount);
	}
	public AmortizationParams setAccumulatedAccount(Integer accumulatedAccount) {
		this.accumulatedAccount = accumulatedAccount;
		return this;
	}
	
	public Optional<Integer> getFixedAssetAccount() {
		return Optional.ofNullable(fixedAssetAccount);
	}
	public AmortizationParams setFixedAssetAccount(Integer fixedAssetAccount) {
		this.fixedAssetAccount = fixedAssetAccount;
		return this;
	}
	
	public Optional<String> getDescription() {
		return Optional.ofNullable(AonStringUtils.trimToNull(description));
	}
	public AmortizationParams setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public Optional<Date> getFromInitialDate() {
		return Optional.ofNullable(fromInitialDate);
	}
	public AmortizationParams setFromInitialDate(Date fromInitialDate) {
		this.fromInitialDate = fromInitialDate;
		return this;
	}
	
	public Optional<Date> getToInitialDate() {
		return Optional.ofNullable(toInitialDate);
	}
	public AmortizationParams setToInitialDate(Date toInitialDate) {
		this.toInitialDate = toInitialDate;
		return this;
	}
	
	public Optional<Date> getFromDeadline() {
		return Optional.ofNullable(fromDeadline);
	}
	public AmortizationParams setFromDeadline(Date fromDeadline) {
		this.fromDeadline = fromDeadline;
		return this;
	}
	
	public Optional<Date> getToDeadline() {
		return Optional.ofNullable(toDeadline);
	}
	public AmortizationParams setToDeadline(Date toDeadline) {
		this.toDeadline = toDeadline;
		return this;
	}
	
	public Optional<Double > getAmount() {
		return Optional.ofNullable(amount);
	}
	public AmortizationParams setAmount(Double amount) {
		this.amount = amount;
		return this;
	}
	
	public Optional<AmortizationPeriod> getFeePeriod() {
		return Optional.ofNullable(feePeriod);
	}
	public AmortizationParams setFeePeriod(AmortizationPeriod feePeriod) {
		this.feePeriod = feePeriod;
		return this;
	}
	
	public Optional<Double> getSaleAmount() {
		return Optional.ofNullable(saleAmount);
	}
	public AmortizationParams setSaleAmount(Double saleAmount) {
		this.saleAmount = saleAmount;
		return this;
	}
	
	public Optional<String> getComments() {
		return Optional.ofNullable(comments);
	}
	public AmortizationParams setComments(String comments) {
		this.comments = comments;
		return this;
	}
	
	public Optional<Double> getPercentage() {
		return Optional.ofNullable(percentage);
	}
	public AmortizationParams setPercentage(Double percentage) {
		this.percentage = percentage;
		return this;
	}

	public Optional<SecurityLevel> getSecurityLevel() {
		return Optional.ofNullable(securityLevel);
	}
	public AmortizationParams setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	
	public Optional<AmortizationDetailStatus> getStatus() {
		return Optional.ofNullable(status);
	}
	public AmortizationParams setStatus(AmortizationDetailStatus status) {
		this.status = status;
		return this;
	}
	
	public Optional<AmortizationParamsOrderBy> getOrderBy() {
		return Optional.ofNullable(orderBy);
	}
	public AmortizationParams setOrderBy(AmortizationParamsOrderBy orderBy) {
		this.orderBy = orderBy;
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
	
	public static enum AmortizationParamsOrderBy {
		DESCRIPTION_ASC( "Descripci\u00F3n" )		
			{ @Override	public <T> T visit(AmortizationParamsOrderByVisitor<T> visitor) {return visitor.visitDescriptionAsc(); }}, 
		DESCRIPTION_DESC( "Descripci\u00F3n (descendente)" )
			{ @Override	public <T> T visit(AmortizationParamsOrderByVisitor<T> visitor) {return visitor.visitDescriptionDesc(); }}, 
		INITIAL_DATE_ASC( "Fecha de inicio de utilizaci\u00F3n" )
			{ @Override	public <T> T visit(AmortizationParamsOrderByVisitor<T> visitor) {return visitor.visitInitialDateAsc(); }},
		INITIAL_DATE_DESC( "Fecha de inicio de utilizaci\u00F3n (descendente)" )
			{ @Override	public <T> T visit(AmortizationParamsOrderByVisitor<T> visitor) {return visitor.visitInitialDateDesc(); }},
		ID_ASC( "C\u00F3digo" )
			{ @Override	public <T> T visit(AmortizationParamsOrderByVisitor<T> visitor) {return visitor.visitIdAsc(); }}, 
		ID_DESC( "C\u00F3digo (descendente)" )
			{ @Override	public <T> T visit(AmortizationParamsOrderByVisitor<T> visitor) {return visitor.visitIdDesc(); }}, 
		;
		
		private String description;
		
		private AmortizationParamsOrderBy(String description) {
			this.description = description;
		}
		
		public String getDescription() {
			return description;
		}

		public abstract <T> T visit(AmortizationParamsOrderByVisitor<T> visitor);
	}
	
	public interface AmortizationParamsOrderByVisitor<T> {
		T visitIdAsc();
		T visitIdDesc();
		T visitDescriptionAsc();
		T visitDescriptionDesc();
		T visitInitialDateAsc();
		T visitInitialDateDesc();
	} 
	
}
