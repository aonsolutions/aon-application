package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;
import java.util.List;

import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;

@Deprecated
public class InvoiceDetailExtended implements Serializable {

	private static final long serialVersionUID = -1426818107053877548L;
	
	private InvoiceDetail detail;
	private List<String> segments;
	private String sellerSupport;
	
	public InvoiceDetail getDetail() {
		return detail;
	}

	public InvoiceDetailExtended setDetail(InvoiceDetail detail) {
		this.detail = detail;
		return this;
	}

	public List<String> getSegments() {
		return segments;
	}
	public InvoiceDetailExtended setSegments(List<String> segments) {
		this.segments = segments;
		return this;
	}
	
	public String getSellerSupport() {
		return sellerSupport;
	}
	public InvoiceDetailExtended setSellerSupport(String sellerSupport) {
		this.sellerSupport = sellerSupport;
		return this;
	}
}
