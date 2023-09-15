package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

public class InvestAssetParams implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String domainName; 
	private int domain;
	private String user;
	
	private String description;
	private Integer activity;
	private Byte type;
	private Byte regime;
	private Double vatPercent;
	private Double retentionPercent;
	private Date startDate;
	private Date endDate;
	
	private int limit;
	private int offset;
	
	public String getDomainName() {
		return domainName;
	}
	public InvestAssetParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public InvestAssetParams setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	public InvestAssetParams setUser(String user) {
		this.user = user;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public InvestAssetParams setDescription(String description) {
		this.description = description;
		return this;
	}
	public Integer getActivity() {
		return activity;
	}
	public InvestAssetParams setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	public Byte getType() {
		return type;
	}
	public InvestAssetParams setType(Byte type) {
		this.type = type;
		return this;
	}
	public Byte getRegime() {
		return regime;
	}
	public InvestAssetParams setRegime(Byte regime) {
		this.regime = regime;
		return this;
	}
	public Double getVatPercent() {
		return vatPercent;
	}
	public InvestAssetParams setVatPercent(Double vatPercent) {
		this.vatPercent = vatPercent;
		return this;
	}
	public Double getRetentionPercent() {
		return retentionPercent;
	}
	public InvestAssetParams setRetentionPercent(Double retentionPercent) {
		this.retentionPercent = retentionPercent;
		return this;
	}
	public Date getStartDate() {
		return startDate;
	}
	public InvestAssetParams setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	public Date getEndDate() {
		return endDate;
	}
	public InvestAssetParams setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	public int getLimit() {
		return limit;
	}
	public InvestAssetParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	public int getOffset() {
		return offset;
	}
	public InvestAssetParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	
	
	
}
