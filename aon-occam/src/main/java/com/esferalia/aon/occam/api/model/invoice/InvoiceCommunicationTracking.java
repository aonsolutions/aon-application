package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatchDetail;

public class InvoiceCommunicationTracking implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private InvoiceBatch invoiceBatch;
	private InvoiceBatchDetail invoiceBatchDetail;
		
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
