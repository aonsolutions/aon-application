package com.esferalia.aon.occam.api.model.invoice;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;

public abstract class InvoiceCommunicationPhaseListener {
	
	private Integer oldNumber;
	private boolean proforma;
	
	protected Integer getOldNumber() {
		return oldNumber;
	}
	protected void setOldNumber(Integer oldNumber) {
		this.oldNumber = oldNumber;
	}
	protected boolean isProforma() {
		return proforma;
	}
	protected void setProforma(boolean proforma) {
		this.proforma = proforma;
	}
	
	public abstract void beforeAll(AONContext ctx, InvoiceCommunicatorContext icc) throws InvoiceCommunicationException;
	public abstract void beforeInvoice(AONContext ctx, InvoiceCommunicatorContext icc, Invoice invoice);
	public abstract void afterRightInvoice(AONContext ctx, InvoiceCommunicatorContext icc, Invoice invoice);
	public abstract void afterWrongInvoice(AONContext ctx, InvoiceCommunicatorContext icc, Invoice invoice) throws InvoiceCommunicationException;
	public abstract void afterAll(AONContext ctx, InvoiceCommunicatorContext icc) throws InvoiceCommunicationException;
}
