package com.code.aon.ui.purchase.controller;

import java.util.Map;

import com.code.aon.common.ITransferObject;
import com.code.aon.purchase.Supplier;
import com.code.aon.ui.form.LookupController;

public class SupplierLookupController extends LookupController {

	/** 
	 * The Constant SUPPLIER_FULL_NAME used to retrieve the complete name of the supplier 
	 * using the lookup. 
	 */
	private static final String SUPPLIER_FULL_NAME = "Supplier_full_name";
	
	@Override
	protected void customizeLookupMap(ITransferObject to, Map<String, Object> map) {
		Supplier supplier = (Supplier)to;
		map.put(SUPPLIER_FULL_NAME, supplier.getName() + " " + ((supplier.getSurname() == null) ? "" : supplier.getSurname()) );
	}
}
