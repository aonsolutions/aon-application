package com.esferalia.aon.gwt.common.client.widget;

import com.esferalia.aon.gwt.common.client.widget.solutions.AonModuleOptions;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;

public class InvoiceRegistryNameBox extends InvoiceRegistryBox {

	public InvoiceRegistryNameBox(AonModuleOptions<?> options) {
		super(options);
		suggestTextBox.setVisibleLength(30);
		suggestTextBox.setMaxLength(40);
	}
	
	public void setVisibleLength(int length) {
		suggestTextBox.setVisibleLength(length);
	}
	public void setMaxLength(int length) {
		suggestTextBox.setMaxLength(length);
	}

	@Override
	protected String getDisplayValue(InvoiceRegistry invoiceRegistry) {
		return invoiceRegistry == null ? null : invoiceRegistry.getName();
	}
	

}
   