package com.esferalia.aon.gwt.common.client.widget;

import com.esferalia.aon.gwt.common.client.widget.solutions.AonModuleOptions;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceRegistryDocumentBox extends InvoiceRegistryBox {

	public InvoiceRegistryDocumentBox(AonModuleOptions<?> options) {
		super(options);
	}

	@Override
	protected String getDisplayValue(InvoiceRegistry invoiceRegistry) {
		if (invoiceRegistry == null) {
			return null;
		}
		return AonStringUtils.defaultIfBlank( DocumentType.name(invoiceRegistry.getDocumentType()), "???" )
			+ "-"
			+ AonStringUtils.defaultIfBlank( Country.name(invoiceRegistry.getDocumentCountry()), "??" )
			+ "-"
			+ invoiceRegistry.getDocument();
	}
	

}
   