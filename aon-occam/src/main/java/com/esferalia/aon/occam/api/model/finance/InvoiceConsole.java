package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.InvoiceSource;

public class InvoiceConsole implements Serializable {

	private static final long serialVersionUID = 2303454794885109516L;

	private Integer id;
	private boolean annulled;
	
	private Invoice invoice;
	private InvoiceSource source;
	
	
	public Integer getId() {
		return id;
	}
	public InvoiceConsole setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public boolean isAnnulled() {
		return annulled;
	}
	public InvoiceConsole setAnnulled(boolean annulled) {
		this.annulled = annulled;
		return this;
	}

	public Invoice getInvoice() {
		return invoice;
	}
	public InvoiceConsole setInvoice(Invoice invoice) {
		this.invoice = invoice;
		return this;
	}
	
	public InvoiceSource getSource() {
		return source;
	}
	public InvoiceConsole setSource(InvoiceSource source) {
		this.source = source;
		return this;
	}
	
}

