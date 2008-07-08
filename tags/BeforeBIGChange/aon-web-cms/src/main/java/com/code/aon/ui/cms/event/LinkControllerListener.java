package com.code.aon.ui.cms.event;

import com.code.aon.cms.Link;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.controller.LinkController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class LinkControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		LinkController controller = (LinkController) event.getController();
		try {
			controller.orderedControllerSupport.reorderObjects(controller);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		LinkController controller = (LinkController)event.getController();
		controller.orderedControllerSupport.addListenerSupport(controller);
	}
	

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		LinkController lc = (LinkController)event.getController();
		Link l = (Link)event.getController().getTo();
		l.setLinkCategory(lc.getCurrentLinkCategory());
		l.setPosition(lc.orderedControllerSupport.getLastPosition(lc));
	}
	
}
