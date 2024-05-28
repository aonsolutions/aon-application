package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

public class InvoiceCommunicationTracking implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	InvoiceBatch invoiceBatch;
	InvoiceBatchDetail invoiceBatchDetail;
		
	public InvoiceBatchDetail getInvoiceBatchDetail() {
		if(invoiceBatchDetail == null) {
			invoiceBatchDetail = new InvoiceBatchDetail();
		}
		return invoiceBatchDetail;
	}
	
	public InvoiceCommunicationTracking setInvoiceBatchDetail(InvoiceBatchDetail invoiceBatchDetail) {
		this.invoiceBatchDetail = invoiceBatchDetail;
		return this;
	}
	
	public InvoiceBatch getInvoiceBatch() {
		if(invoiceBatch == null) {
			invoiceBatch = new InvoiceBatch();
		}
		return invoiceBatch;
	}
	
	public InvoiceCommunicationTracking setInvoiceBatch(InvoiceBatch invoiceBatch) {
		this.invoiceBatch = invoiceBatch;
		return this;
	}
	
	public boolean isEmpty() {
		return getInvoiceBatch().isEmpty() && getInvoiceBatchDetail().isEmpty();
	}
}
