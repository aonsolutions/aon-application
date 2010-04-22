/**
 * 
 */
package com.code.ui.gbp.stats;

import java.math.BigDecimal;

import com.code.gbp.enumeration.SupplierStatus;

public class SupplierEvolution {
	private Integer supplierId;
	private String supplierName;
	private SupplierStatus supplierStatus;
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
			SupplierStatus supplierStatus,
			Integer campaignId,
			String campaignName,
			BigDecimal offer,
			BigDecimal invoice,
			BigDecimal invoiceTotal) {
		this.supplierId = supplierId;
		this.supplierName = supplierName;
		this.supplierStatus = supplierStatus;
		this.campaignId = campaignId;
		this.campaignName = campaignName;
		this.offer = offer==null?new Double(0):new Double(offer.doubleValue()); 
		this.invoice = invoice==null?new Double(0):new Double(invoice.doubleValue()); 
		this.invoiceTotal = invoiceTotal==null?new Double(0):new Double(invoiceTotal.doubleValue());
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
		double percent = 0;
		try{
			percent = (getInvoice() * 100 / getInvoiceTotal());
			percent = Math.round(100*percent); 
			percent = percent / 100;
	        return percent;
		}catch (Exception e){
	        return 0;
		}
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

	public SupplierStatus getSupplierStatus() {
		return supplierStatus;
	}

	public void setSupplierStatus(SupplierStatus supplierStatus) {
		this.supplierStatus = supplierStatus;
	}

	
	
}