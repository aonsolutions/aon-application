/**
 * 
 */
package com.code.ui.gbp.stats;

import java.math.BigDecimal;
import java.util.Date;

public class SupplierTypeEvolution {
	private Integer supplierTypeId;
	private String supplierTypeName;
	private Integer campaignId;
	private String campaignName;
	private double offer;
	private double invoice;
	
	public SupplierTypeEvolution() {
	}

	public SupplierTypeEvolution(
			Integer supplierTypeId,
			String supplierTypeName,
			Integer campaignId,
			String campaignName,
			BigDecimal offer,
			BigDecimal invoice) {
		this.supplierTypeId = supplierTypeId;
		this.supplierTypeName = supplierTypeName;
		this.campaignId = campaignId;
		this.campaignName = campaignName;
		this.offer = offer == null? 0 : offer.doubleValue();
		this.invoice = invoice == null? 0 : invoice.doubleValue();
	}

	public String getSupplierTypeName() {
		return supplierTypeName;
	}

	public void setSupplierTypeName(String supplierTypeName) {
		this.supplierTypeName = supplierTypeName;
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

	public Integer getSupplierTypeId() {
		return supplierTypeId;
	}

	public void setSupplierTypeId(Integer supplierTypeId) {
		this.supplierTypeId = supplierTypeId;
	}

	public Integer getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Integer campaignId) {
		this.campaignId = campaignId;
	}

}