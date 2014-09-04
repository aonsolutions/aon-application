package com.code.aon.ui.product.controller;

import com.code.aon.AonVersion;
import com.code.aon.registry.RegistryItem;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;

public class ItemSupplierController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void onSupplierChanged(LookupChangeEvent event) {
		RegistryItem registryItem = (RegistryItem)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Supplier supplier = (Supplier)event.getNewValue();
			registryItem.setRegistry(supplier.getRegistry());
		}
	}

}

