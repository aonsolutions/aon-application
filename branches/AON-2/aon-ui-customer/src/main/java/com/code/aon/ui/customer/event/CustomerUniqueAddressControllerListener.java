package com.code.aon.ui.customer.event;

import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CustomerUniqueAddressControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		((RegistryAddress)event.getController().getTo()).setAddressType(AddressType.MAIN);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		((RegistryAddress)event.getController().getTo()).setAddressType(AddressType.MAIN);
	}
}
