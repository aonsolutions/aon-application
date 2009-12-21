package com.code.ui.gbp.stats;

import java.math.BigDecimal;

public class ByAreaEvolution {

	private String area;

	private String areaGroup;

	private Double areaBudget;
	
	private Double areaEstimate;
	
	private Long campaigns;
	
	private Double budget;
	
	private Double offer;
	
	private Double invoice;

	private Double difference;

	public ByAreaEvolution	(
			String area,
			String areaGroup,
			BigDecimal areaBudget,
			BigDecimal areaEstimate,
			Long campaigns,
			BigDecimal budget,
			BigDecimal offer,
			BigDecimal invoice){
		this.area = area; 
		this.areaGroup = areaGroup;
		this.areaBudget = areaBudget==null?new Double(0):new Double(areaBudget.doubleValue());
		this.areaEstimate = areaEstimate==null?new Double(0):new Double(areaEstimate.doubleValue());
		this.campaigns = campaigns;
		this.budget = new Double(budget.doubleValue()); 
		this.offer = offer==null?new Double(0):new Double(offer.doubleValue());
		this.invoice = invoice==null?new Double(0):new Double(invoice.doubleValue());
		this.difference = new Double(this.areaBudget.doubleValue() - this.invoice.doubleValue());
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Long getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(Long campaigns) {
		this.campaigns = campaigns;
	}

	public Double getBudget() {
		return budget;
	}

	public void setBudget(Double budget) {
		this.budget = budget;
	}

	public Double getOffer() {
		return offer;
	}

	public void setOffer(Double offer) {
		this.offer = offer;
	}

	public Double getInvoice() {
		return invoice;
	}

	public void setInvoice(Double invoice) {
		this.invoice = invoice;
	}

	public Double getDifference() {
		return difference;
	}

	public void setDifference(Double difference) {
		this.difference = difference;
	}

	public Double getAreaBudget() {
		return areaBudget;
	}

	public void setAreaBudget(Double areaBudget) {
		this.areaBudget = areaBudget;
	}

	public Double getAreaEstimate() {
		return areaEstimate;
	}

	public void setAreaEstimate(Double areaEstimate) {
		this.areaEstimate = areaEstimate;
	}

	public String getAreaGroup() {
		return areaGroup;
	}

	public void setAreaGroup(String areaGroup) {
		this.areaGroup = areaGroup;
	}

	
}
