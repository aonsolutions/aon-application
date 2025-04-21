package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class InvoiceConsole implements Serializable {

	private static final long serialVersionUID = 2303454794885109516L;
	
	private Invoice invoice;
	private Attach attach;
	private InvoiceSource source;
	
	public Invoice getInvoice() {
		return invoice;
	}
	public InvoiceConsole setInvoice(Invoice invoice) {
		this.invoice = invoice;
		return this;
	}
	
	public Attach getAttach() {
		return attach;
	}
	public InvoiceConsole setAttach(Attach attach) {
		this.attach = attach;
		return this;
	}
	public boolean hastAttach() {
		return attach == null || attach.getId() == null;
	}
	
	public InvoiceSource getSource() {
		return source;
	}
	public InvoiceConsole setSource(InvoiceSource source) {
		this.source = source;
		return this;
	}
	
}

