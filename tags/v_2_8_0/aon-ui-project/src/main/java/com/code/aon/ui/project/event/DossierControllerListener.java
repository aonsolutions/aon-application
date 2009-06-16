package com.code.aon.ui.project.event;

import com.code.aon.customer.Customer;
import com.code.aon.project.Dossier;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class DossierControllerListener extends ControllerAdapter {
	
	private static final String CUSTOMER_CONTROLLER_NAME = "customer";

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		CustomerController customerController = (CustomerController)AonUtil.getController(CUSTOMER_CONTROLLER_NAME);
		Customer customer = (Customer)customerController.getTo();
		((Dossier)event.getController().getTo()).setCustomer(customer);
	}
}