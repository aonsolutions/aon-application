package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

public class InvestAsset implements Serializable {

	private static final long serialVersionUID = 6972256004151447259L;
	
	private Integer id;
	private Integer domain;
	private String description;
	private EnterpriseActivity activity;
	private InvestAssetType type;
	private InvestAssetRegime regime;
	private Date startDate;
	private Date endDate;
	private double vatPercent;
	private double retentionPercent;
	private String properties;

	public Integer getId() {
		return id;
	}

	public InvestAsset setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	
	public InvestAsset setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public String getDescription() {
		return description;
	}

	public InvestAsset setDescription(String description) {
		this.description = description;
		return this;
	}

	public EnterpriseActivity getActivity() {
		if(activity == null) {
			activity = new EnterpriseActivity();
		}
		return activity;
	}

	public InvestAsset setActivity(EnterpriseActivity activity) {
		this.activity = activity;
		return this;
	}

	public InvestAssetType getType() {
		return type;
	}

	public InvestAsset setType(InvestAssetType type) {
		this.type = type;
		return this;
	}

	public InvestAssetRegime getRegime() {
		return regime;
	}

	public InvestAsset setRegime(InvestAssetRegime regime) {
		this.regime = regime;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public InvestAsset setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public InvestAsset setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public double getVatPercent() {
		return vatPercent;
	}

	public InvestAsset setVatPercent(double vatPercent) {
		this.vatPercent = vatPercent;
		return this;
	}

	public double getRetentionPercent() {
		return retentionPercent;
	}

	public InvestAsset setRetentionPercent(double retentionPercent) {
		this.retentionPercent = retentionPercent;
		return this;
	}

	public String getProperties() {
		return properties;
	}

	public InvestAsset setProperties(String properties) {
		this.properties = properties;
		return this;
	}
	
	

}
