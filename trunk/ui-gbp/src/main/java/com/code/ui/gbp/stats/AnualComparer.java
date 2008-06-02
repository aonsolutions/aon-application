package com.code.ui.gbp.stats;

import java.util.GregorianCalendar;

public class AnualComparer {

	private GregorianCalendar start;
	private GregorianCalendar end;
	private Long campaignNumber;
	private Double offer;
	private Double invoice;
	private Double diff;

	public AnualComparer(){
	}
	
	public AnualComparer	(
			GregorianCalendar start,
			GregorianCalendar end,
			Long campaignNumber,
			Double offer,
			Double invoice
			){
		this.start = start;
		this.end = end;
		this.campaignNumber = campaignNumber;
		this.offer = offer==null?new Long(0):offer;
		this.invoice = invoice==null?new Double(0):invoice;
		this.diff = new Double(this.offer.doubleValue() - this.invoice.doubleValue());
	}

	public Long getCampaignNumber() {
		return campaignNumber;
	}

	public void setCampaignNumber(Long campaignNumber) {
		this.campaignNumber = campaignNumber;
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

	public Double getDiff() {
		return diff;
	}

	public void setDiff(Double diff) {
		this.diff = diff;
	}

	public GregorianCalendar getStart() {
		return start;
	}

	public void setStart(GregorianCalendar start) {
		this.start = start;
	}

	public GregorianCalendar getEnd() {
		return end;
	}

	public void setEnd(GregorianCalendar end) {
		this.end = end;
	}
	
}
