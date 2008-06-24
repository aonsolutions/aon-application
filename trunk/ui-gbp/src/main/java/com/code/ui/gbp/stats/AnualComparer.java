package com.code.ui.gbp.stats;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

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
			BigDecimal offer,
			BigDecimal invoice
			){
		this.start = start;
		this.end = end;
		this.campaignNumber = campaignNumber;
		this.offer = offer==null?new Double(0):new Double(offer.doubleValue());
		this.invoice = invoice==null?new Double(0):new Double(invoice.doubleValue());
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

	/**
	 * Ruta base del fichero de mensajes.
	 */
	private static final String BASE_NAME = "com.code.ui.gbp.i18n.enumeration";
	
    /**
     * Prefijo de la llave de mensajes. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_month_";

    public String getInitMonth() {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME);
		return bundle.getString(MSG_KEY_PREFIX + this.start.get(Calendar.MONTH));
	}

    public String getEndMonth() {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME);
		return bundle.getString(MSG_KEY_PREFIX + this.end.get(Calendar.MONTH));
	}

}
