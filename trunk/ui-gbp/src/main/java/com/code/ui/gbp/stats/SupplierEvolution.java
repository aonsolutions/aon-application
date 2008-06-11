/**
 * 
 */
package com.code.ui.gbp.stats;

import java.util.Date;

public class SupplierEvolution {
	private Integer supplierId;
	private String supplierName;
	private Integer campaignId;
	private String campaignName;
	private Double offer;
	private Double invoice;
	private Double invoiceTotal;
	
	public SupplierEvolution() {
	}

	public SupplierEvolution(
			Integer supplierId,
			String supplierName,
			Integer campaignId,
			String campaignName,
			Double offer,
			Double invoice,
			Double invoiceTotal) {
		this.supplierId = supplierId;
		this.supplierName = supplierName;
		this.campaignId = campaignId;
		this.campaignName = campaignName;
		this.offer = offer;
		this.invoice = invoice;
		this.invoiceTotal = invoiceTotal;
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
	
	public Double getInvoiceTotal() {
		return invoiceTotal;
	}

	public void setInvoiceTotal(Double invoiceTotal) {
		this.invoiceTotal = invoiceTotal;
	}

	public double getPercent() {
		double percent = (getInvoice() * 100 / getInvoiceTotal());
		percent = Math.round(100*percent); 
		percent = percent / 100; 
        return percent;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public Integer getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Integer campaignId) {
		this.campaignId = campaignId;
	}

}