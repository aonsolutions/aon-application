package com.esferalia.aon.gwt.common.client.widget;

import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;

class InvoiceRegistrySuggestion extends MultiWordSuggestion {
	
	private InvoiceRegistry invoiceRegistry;
	
	InvoiceRegistrySuggestion(InvoiceRegistry invoiceRegistry, String replacementString, String displayString) {
		super( replacementString, displayString );
		this.invoiceRegistry = invoiceRegistry;
	}
	
	public InvoiceRegistry getInvoiceRegistry() {
		return invoiceRegistry;
	}
	
}
