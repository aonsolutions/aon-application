package com.code.aon.ui.customer.controller;

import java.util.Map;

import com.code.aon.common.ITransferObject;
import com.code.aon.customer.Customer;
import com.code.aon.ui.form.LookupController;

public class CustomerLookupController extends LookupController {
	
	/** The Constant SUPPLIER_FULL_NAME used to retrieve the complete name of the supplier using the lookup. */
	private static final String CUSTOMER_FULL_NAME = "Customer_full_name";

	@Override
	protected void customizeLookupMap(ITransferObject to, Map<String, Object> map) {
		Customer customer = (Customer)to;
		map.put(CUSTOMER_FULL_NAME, customer.getRegistry().getName() + " " + ((customer.getRegistry().getSurname() == null) ? "" : customer.getRegistry().getSurname()) );
	}
}
