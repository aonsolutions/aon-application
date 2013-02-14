package com.code.aon.ui.cms.event;

import com.code.aon.cms.MenuOption;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.controller.MenuOptionController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class MenuOptionControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		MenuOptionController controller = (MenuOptionController) event.getController();
		try {
			controller.orderedControllerSupport.reorderObjects(controller);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		MenuOptionController controller = (MenuOptionController)event.getController();
		controller.orderedControllerSupport.addListenerSupport(controller);
	}
	

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		MenuOptionController moc = (MenuOptionController)event.getController();
		MenuOption mo = (MenuOption)moc.getTo();
		mo.setMenu(moc.getCurrentMenu());
		mo.setPosition(moc.orderedControllerSupport.getLastPosition(moc));
	}

	
}
