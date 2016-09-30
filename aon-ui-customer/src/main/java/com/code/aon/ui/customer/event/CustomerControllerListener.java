package com.code.aon.ui.customer.event;

import com.code.aon.AonVersion;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CustomerControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beforeEditSearch(ControllerEvent event)
			throws ControllerListenerException {
		CustomerController controller = (CustomerController) event.getController();
		controller.setShowAlumnData(false);
	}
	
}