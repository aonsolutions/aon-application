package com.code.aon.ui.registry.controller.event;

import com.code.aon.AonVersion;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class RegistryPayMethodControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		RegistryPayMethod rPayMethod = (RegistryPayMethod)event.getController().getTo();
		rPayMethod.setNumberOfPayments(1);
	}
	
}