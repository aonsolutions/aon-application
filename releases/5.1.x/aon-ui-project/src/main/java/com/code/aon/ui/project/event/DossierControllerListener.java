package com.code.aon.ui.project.event;

import com.code.aon.customer.Customer;
import com.code.aon.project.Dossier;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class DossierControllerListener extends ControllerAdapter {
	
	private static final String CUSTOMER_CONTROLLER_NAME = "customer";

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		CustomerController customerController = (CustomerController)FormUtil.getController(CUSTOMER_CONTROLLER_NAME);
		Customer customer = (Customer)customerController.getTo();
		((Dossier)event.getController().getTo()).setCustomer(customer);
	}
}