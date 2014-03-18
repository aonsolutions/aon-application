package com.code.aon.ui.company.event;

import com.code.aon.AonVersion;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.StreetType;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CompanyAddressListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		RegistryAddress rAddress = (RegistryAddress) event.getController().getTo();
		rAddress.setStreetType(StreetType.CL);
	}	

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		RegistryAddress rAddress = (RegistryAddress) event.getController().getTo();
		if(rAddress.getAddressType() == null){
			rAddress.setAddressType(AddressType.DELEGATION);
		}
	}	

}