package com.code.aon.ui.infoweb.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.infoweb.WebInfoPage;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.infoweb.controller.CompanyWebInfoPageController;

public class CompanyWebInfoPageControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CompanyWebInfoPageController wipc = (CompanyWebInfoPageController)event.getController();
		WebInfoPage wip = (WebInfoPage)wipc.getTo();
		wip.setPosition(wipc.getLastPosition());
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		CompanyWebInfoPageController wipc = (CompanyWebInfoPageController)event.getController();
		try {
			wipc.reorderObjects();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
}
