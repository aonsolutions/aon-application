package com.code.aon.ui.account.bridge.controller;

import com.code.aon.account.bridge.SupplierAccount;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;

public class SupplierAccountManager extends LinesController {

	public void supplierChanged(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Supplier supplier = (Supplier)event.getNewValue();
			((SupplierAccount) this.getTo()).setSupplier(supplier);
		}
	}

}
