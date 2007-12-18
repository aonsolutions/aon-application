package com.code.aon.ui.cms.event;

import com.code.aon.cms.SidebarOption;
import com.code.aon.ui.cms.controller.SidebarOptionController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class SidebarOptionControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		SidebarOptionController soc = (SidebarOptionController)event.getController();
		SidebarOption so = (SidebarOption)soc.getTo();
		so.setSidebar(soc.getCurrentSidebar());
		so.setPosition(soc.getLastPosition());
	}

}
