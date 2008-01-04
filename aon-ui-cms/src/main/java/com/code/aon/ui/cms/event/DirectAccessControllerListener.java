package com.code.aon.ui.cms.event;

import com.code.aon.cms.DirectAccess;
import com.code.aon.ui.cms.controller.DirectAccessController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class DirectAccessControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		DirectAccessController dac = (DirectAccessController)event.getController();
		DirectAccess da = (DirectAccess)dac.getTo();
		da.setDirectAccessGroup(dac.getCurrentGroup());
		da.setPosition(dac.getLastPosition());
	}

}
