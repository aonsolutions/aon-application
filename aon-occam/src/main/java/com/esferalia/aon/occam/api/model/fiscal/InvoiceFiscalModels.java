package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.finance.Invoice;

public class InvoiceFiscalModels implements Serializable {

	private static final long serialVersionUID = -4257693906610270088L;
	
	private Invoice invoice;
	private LinkedList<FiscalModel> models;
	
	public Invoice getInvoice() {
		return invoice;
	}
	public InvoiceFiscalModels setInvoice(Invoice invoice) {
		this.invoice = invoice;
		return this;
	}
	
	public LinkedList<FiscalModel> getModels() {
		return models;
	}
	public InvoiceFiscalModels setModels(LinkedList<FiscalModel> models) {
		this.models = models;
		return this;
	}
	
}

