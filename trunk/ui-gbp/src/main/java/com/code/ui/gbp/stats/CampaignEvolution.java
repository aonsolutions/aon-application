/**
 * 
 */
package com.code.ui.gbp.stats;

import com.code.gbp.Campaign;
import com.code.gbp.CampaignSupplier;
import com.code.gbp.Supplier;

public class CampaignEvolution {

	private Campaign campaign;
	private Supplier supplier;
	private Double campaignTotal;
	private Long invoiceCount;
	private Double invoice;

	public CampaignEvolution() {
	}

	public CampaignEvolution(Campaign campaign, Supplier supplier, Double campaignTotal,
			Long invoiceCount, Double invoice) {

		this.campaign = campaign;
		this.supplier = supplier;
		this.campaignTotal = campaignTotal;
		this.invoiceCount = invoiceCount;
		this.invoice = invoice;
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Double getCampaignTotal() {
		return campaignTotal;
	}

	public void setCampaignTotal(Double campaignTotal) {
		this.campaignTotal = campaignTotal;
	}

	public Long getInvoiceCount() {
		return invoiceCount;
	}

	public void setInvoiceCount(Long invoiceCount) {
		this.invoiceCount = invoiceCount;
	}

	public Double getInvoice() {
		return invoice;
	}

	public void setInvoice(Double invoice) {
		this.invoice = invoice;
	}

	public double getPercent() {
		double percent = (getInvoice() * 100 / getCampaignTotal());
		return Math.round(100 * percent) / 100;
	}

}