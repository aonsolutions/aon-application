package com.code.ui.gbp.stats;

public class OfferControl {

	private Integer campaignCode;
	
	private String campaignName;
	
	private String campaignStatus;
	
	private Double offer;
	
	private Double invoice;

	private Double difference;

	public OfferControl(){
	}
	
	public OfferControl	(Integer campaignCode,
			String campaignName,
			String campaignStatus,
			Double offer,
			Double invoice){
		this.campaignCode = campaignCode; 
		this.campaignName = campaignName;
		this.campaignStatus = campaignStatus; 
		this.offer = offer==null?new Double(0):offer;
		this.invoice = invoice==null?new Double(0):invoice;
		this.difference = new Double(this.offer.doubleValue() - this.invoice.doubleValue());
	}


	public Integer getCampaignCode() {
		return campaignCode;
	}

	public void setCampaignCode(Integer campaignCode) {
		this.campaignCode = campaignCode;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getCampaignStatus() {
		return campaignStatus;
	}

	public void setCampaignStatus(String campaignStatus) {
		this.campaignStatus = campaignStatus;
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

}
