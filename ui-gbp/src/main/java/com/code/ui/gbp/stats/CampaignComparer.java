package com.code.ui.gbp.stats;

import com.code.gbp.Campaign;

public class CampaignComparer {

	private String supplierType;
	private Campaign campaign_1;
	private Campaign campaign_2;
	private Long supplierCount_1;
	private Long supplierCount_2;
	private Double invoiceAmount_1;
	private Double invoiceAmount_2;
	private Integer supplierDiff;
	private Double invoiceAmountDiff;

	public CampaignComparer(){
	}
	
	public CampaignComparer	(
			String supplierType,
			Campaign campaign_1,
			Campaign campaign_2,
			Long supplierCount_1,
			Double invoiceAmount_1,
			Long supplierCount_2,
			Double invoiceAmount_2
			){
		this.supplierType = supplierType;
		this.campaign_1 = campaign_1;
		this.campaign_2 = campaign_2;
		this.supplierCount_1 = supplierCount_1==null?new Integer(0):supplierCount_1;
		this.supplierCount_2 = supplierCount_2==null?new Integer(0):supplierCount_2;
		this.invoiceAmount_1 = invoiceAmount_1==null?new Double(0):invoiceAmount_1;
		this.invoiceAmount_2 = invoiceAmount_2==null?new Double(0):invoiceAmount_2;
		this.supplierDiff = new Integer(supplierCount_1.intValue() - supplierCount_1.intValue());
		this.invoiceAmountDiff = new Double(this.invoiceAmount_1.doubleValue() - this.invoiceAmount_2.doubleValue());
	}

	public Campaign getCampaign_1() {
		return campaign_1;
	}

	public void setCampaign_1(Campaign campaign_1) {
		this.campaign_1 = campaign_1;
	}

	public Campaign getCampaign_2() {
		return campaign_2;
	}

	public void setCampaign_2(Campaign campaign_2) {
		this.campaign_2 = campaign_2;
	}

	public Long getSupplierCount_1() {
		return supplierCount_1;
	}

	public void setSupplierCount_1(Long supplierCount_1) {
		this.supplierCount_1 = supplierCount_1;
	}

	public Long getSupplierCount_2() {
		return supplierCount_2;
	}

	public void setSupplierCount_2(Long supplierCount_2) {
		this.supplierCount_2 = supplierCount_2;
	}

	public Double getInvoiceAmount_1() {
		return invoiceAmount_1;
	}

	public void setInvoiceAmount_1(Double invoiceAmount_1) {
		this.invoiceAmount_1 = invoiceAmount_1;
	}

	public Double getInvoiceAmount_2() {
		return invoiceAmount_2;
	}

	public void setInvoiceAmount_2(Double invoiceAmount_2) {
		this.invoiceAmount_2 = invoiceAmount_2;
	}

	public Integer getSupplierDiff() {
		return supplierDiff;
	}

	public Double getInvoiceAmountDiff() {
		return invoiceAmountDiff;
	}

	public String getSupplierType() {
		return supplierType;
	}

	public void setSupplierType(String supplierType) {
		this.supplierType = supplierType;
	}

	
	
}
