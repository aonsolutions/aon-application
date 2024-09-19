package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.List;

public class InvoiceFlatExtended implements Serializable {

	private static final long serialVersionUID = -1426818107053877548L;
	
	private InvoiceFlat invoiceFlat;
	private List<String> segments;
	private String sellerSupport;
	
	public InvoiceFlat getInvoiceFlat() {
		return invoiceFlat;
	}

	public InvoiceFlatExtended setDetail(InvoiceFlat invoiceFlat) {
		this.invoiceFlat = invoiceFlat;
		return this;
	}

	public List<String> getSegments() {
		return segments;
	}
	public InvoiceFlatExtended setSegments(List<String> segments) {
		this.segments = segments;
		return this;
	}
	
	public String getSellerSupport() {
		return sellerSupport;
	}
	public InvoiceFlatExtended setSellerSupport(String sellerSupport) {
		this.sellerSupport = sellerSupport;
		return this;
	}
}
