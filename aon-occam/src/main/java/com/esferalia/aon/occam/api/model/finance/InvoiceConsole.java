package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

public class InvoiceConsole implements Serializable {

	private static final long serialVersionUID = 2303454794885109516L;
	
	private Invoice invoice;
	
	public Invoice getInvoice() {
		return invoice;
	}
	public InvoiceConsole setInvoice(Invoice invoice) {
		this.invoice = invoice;
		return this;
	}
}

