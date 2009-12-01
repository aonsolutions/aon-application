package com.code.aon.ui.audit.event;

import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class MasterControllerListener extends com.code.aon.ui.form.listener.MasterControllerListener {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		getDetailController().onCancel(null);
		super.afterBeanCreated(event);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		getDetailController().onCancel(null);
		super.afterBeanSelected(event);
	}
	
	

}
