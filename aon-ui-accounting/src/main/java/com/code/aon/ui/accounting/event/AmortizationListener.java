package com.code.aon.ui.accounting.event;

import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.listener.LinesControllerListener;

public class AmortizationListener extends LinesControllerListener {

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanAdded(event);
		getLinesController().initModel();
	}
	
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanUpdated(event);
		getLinesController().initModel();
	}
}
