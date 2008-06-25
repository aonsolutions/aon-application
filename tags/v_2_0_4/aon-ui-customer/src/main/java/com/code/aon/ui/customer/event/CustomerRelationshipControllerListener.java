package com.code.aon.ui.customer.event;

import com.code.aon.customer.Customer;
import com.code.aon.registry.RegistryRelationship;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CustomerRelationshipControllerListener extends ControllerAdapter {

	
	private static final String CUSTOMER_CONTROLLER_NAME = "customer";

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		CustomerController customerController = (CustomerController)AonUtil.getController(CUSTOMER_CONTROLLER_NAME);
		Customer customer = (Customer)customerController.getTo();
		((RegistryRelationship)event.getController().getTo()).setRegistry(customer.getRegistry());
	}
}
