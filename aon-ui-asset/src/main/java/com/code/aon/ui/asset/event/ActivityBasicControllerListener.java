package com.code.aon.ui.asset.event;

import com.code.aon.ui.asset.controller.ActivityBasicController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ActivityBasicControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		((ActivityBasicController)this.getController()).buildFromTime();
		((ActivityBasicController)this.getController()).buildToTime();
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		((ActivityBasicController)this.getController()).setControllerTime();
	}

}
