package com.code.aon.ui.supplier.controller;

import com.code.aon.supplier.ItemSupplier;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;

public class ItemSupplierController extends LinesController {

	public void onSupplierChanged( LookupChangeEvent event) {
		Supplier supplier = (Supplier) event.getNewValue();
		ItemSupplier itemSupplier = (ItemSupplier) getTo();
		itemSupplier.setSupplier(supplier);
	}

}

