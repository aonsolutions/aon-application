package com.code.aon.ui.registry.controller.event;

import com.code.aon.common.AonVersion;
import com.code.aon.common.enumeration.Country;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class PersonControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		initDocument(((IRegistry)event.getController().getTo()).getRegistry());
	}

	public void initDocument(Registry registry) {
		registry.setType(RegistryType.NATURAL);
		registry.setNationality(Country.ES);
		registry.setDocumentCountry(Country.ES);
		registry.setDocumentType(DocumentType.NIF);		
	}	

}
