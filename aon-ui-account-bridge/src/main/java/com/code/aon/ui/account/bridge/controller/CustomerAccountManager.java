package com.code.aon.ui.account.bridge.controller;

import com.code.aon.account.bridge.CustomerAccount;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;

public class CustomerAccountManager extends LinesController {

	public void customerChanged(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Customer customer = (Customer)event.getNewValue();
			((CustomerAccount) this.getTo()).setCustomer(customer);
		}
	}

}
