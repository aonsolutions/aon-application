package com.code.aon.ui.product.controller;

import com.code.aon.registry.RegistryItem;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;

public class ItemSupplierController extends LinesController {

	public void onSupplierChanged(LookupChangeEvent event) {
		Supplier supplier = (Supplier) event.getNewValue();
		RegistryItem registryItem = (RegistryItem)getTo();
		registryItem.setRegistry(supplier.getRegistry());
	}

}

