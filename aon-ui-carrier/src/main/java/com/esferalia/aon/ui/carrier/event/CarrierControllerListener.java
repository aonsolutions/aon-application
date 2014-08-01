package com.esferalia.aon.ui.carrier.event;

import com.code.aon.AonVersion;
import com.code.aon.registry.Registry;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.carrier.Carrier;

public class CarrierControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		Carrier carrier = (Carrier)event.getController().getTo(); 
		
		carrier.setRegistry(new Registry());
		carrier.getRegistry().setDocumentType(DocumentType.CIF);
	}
		
}
