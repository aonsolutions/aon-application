package com.esferalia.aon.ui.payroll.event;

import com.code.aon.AonVersion;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class EnterpriseActivityControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		event.getController().initializeModel();
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		event.getController().initializeModel();
	}

}
