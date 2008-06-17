package com.code.ui.gbp.stats;

public class ByAreaEvolution {

	private String area;
	
	private Long campaigns;
	
	private Double budget;
	
	private Double offer;
	
	private Double invoice;

	private Double difference;

	public ByAreaEvolution	(
			String area,
			Long campaigns,
			Double budget,
			Double offer,
			Double invoice){
		this.area = area; 
		this.campaigns = campaigns;
		this.budget = budget; 
		this.offer = offer==null?new Double(0):offer;
		this.invoice = invoice==null?new Double(0):invoice;
		this.difference = new Double(this.offer.doubleValue() - this.invoice.doubleValue());
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

	
}
