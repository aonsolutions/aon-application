package com.code.aon.ui.company.event;

import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CompanyAddressPosListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		RegistryAddress rAddress = (RegistryAddress)event.getController().getTo();
		if(rAddress.getAddressType() == null){
			rAddress.setAddressType(AddressType.DELEGATION);
		}
	}
}
