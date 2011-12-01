package com.code.aon.ui.cms.event;

import com.code.aon.cms.SidebarOption;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.controller.SidebarOptionController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class SidebarOptionControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		SidebarOptionController controller = (SidebarOptionController) event.getController();
		try {
			controller.orderedControllerSupport.reorderObjects(controller);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		SidebarOptionController controller = (SidebarOptionController)event.getController();
		controller.orderedControllerSupport.addListenerSupport(controller);
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		SidebarOptionController soc = (SidebarOptionController)event.getController();
		SidebarOption so = (SidebarOption)soc.getTo();
		so.setSidebar(soc.getCurrentSidebar());
		so.setPosition(soc.orderedControllerSupport.getLastPosition(soc));
	}

}
