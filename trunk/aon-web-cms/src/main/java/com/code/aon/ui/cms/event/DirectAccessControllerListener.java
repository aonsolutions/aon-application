package com.code.aon.ui.cms.event;

import com.code.aon.cms.DirectAccess;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.controller.DirectAccessController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class DirectAccessControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		DirectAccessController controller = (DirectAccessController) event.getController();
		try {
			controller.orderedControllerSupport.reorderObjects(controller);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		DirectAccessController controller = (DirectAccessController)event.getController();
		controller.orderedControllerSupport.addListenerSupport(controller);
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		DirectAccessController dac = (DirectAccessController)event.getController();
		DirectAccess da = (DirectAccess)dac.getTo();
		da.setDirectAccessGroup(dac.getCurrentGroup());
		da.setPosition(dac.orderedControllerSupport.getLastPosition(dac));
	}

}
