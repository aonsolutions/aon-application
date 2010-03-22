package com.code.aon.ui.cms.event;

import com.code.aon.cms.MenuOption;
import com.code.aon.ui.cms.controller.MenuOptionController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class MenuOptionControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		MenuOptionController moc = (MenuOptionController)event.getController();
		MenuOption mo = (MenuOption)moc.getTo();
		mo.setMenu(moc.getCurrentMenu());
		mo.setPosition(moc.getLastPosition());
	}

}
