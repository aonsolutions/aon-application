package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;

public class SellerWorkloadContent implements Serializable {

	private static final long serialVersionUID = -4021529272657422784L;
	
	List<InvoiceDetail> invoiceDetails;
	List<Fee> fees;
	
	public SellerWorkloadContent() {
		super();
		invoiceDetails = new ArrayList<InvoiceDetail>();
		fees = new ArrayList<Fee>();
	}

	public List<InvoiceDetail> getInvoiceDetails() {
		return invoiceDetails;
	}

	public SellerWorkloadContent setInvoiceDetails(List<InvoiceDetail> invoiceDetails) {
		this.invoiceDetails = invoiceDetails;
		return this;
	}

	public List<Fee> getFees() {
		return fees;
	}

	public SellerWorkloadContent setFees(List<Fee> fees) {
		this.fees = fees;
		return this;
	}

	
	
}
