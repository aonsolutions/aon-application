package com.code.aon.ui.product.controller;

import com.code.aon.AonVersion;
import com.code.aon.customer.Customer;
import com.code.aon.registry.RegistryItem;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;

public class ItemCustomerController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void onCustomerChanged(LookupChangeEvent event) {
		Customer customer = (Customer) event.getNewValue();
		RegistryItem registryItem = (RegistryItem)getTo();
		registryItem.setRegistry(customer.getRegistry());
	}

}

