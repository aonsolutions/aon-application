package com.code.aon.ui.customer.event;

import com.code.aon.customer.Customer;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CustomerPersonControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Customer customer = (Customer)event.getController().getTo();
		customer.getRegistry().setType(RegistryType.NATURAL);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Customer customer = (Customer)event.getController().getTo();
		customer.getRegistry().setType(RegistryType.NATURAL);
	}
}
