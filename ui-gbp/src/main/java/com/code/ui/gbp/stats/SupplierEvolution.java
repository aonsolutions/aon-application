/**
 * 
 */
package com.code.ui.gbp.stats;

public class SupplierEvolution {
	private Integer supplierId;
	private String supplierName;
	private String campaignName;
	private double offer;
	private double invoice;
	
	public SupplierEvolution() {
	}

	public SupplierEvolution(
			Integer supplierId,
			String supplierName,
			String campaignName,
			double offer,
			double invoice) {
		
		this.supplierId = supplierId;
		this.supplierName = supplierName;
		this.campaignName = campaignName;
		this.offer = offer;
		this.invoice = invoice;
	}

			
			

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supoplierName) {
		this.supplierName = supoplierName;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public double getOffer() {
		return offer;
	}

	public void setOffer(double offer) {
		this.offer = offer;
	}

	public double getInvoice() {
		return invoice;
	}

	public void setInvoice(double invoice) {
		this.invoice = invoice;
	}
	
	public double getPercent() {
		return getInvoice() * 100 / getOffer();
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}
}