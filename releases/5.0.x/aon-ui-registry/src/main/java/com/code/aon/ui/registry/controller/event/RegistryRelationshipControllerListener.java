package com.code.aon.ui.registry.controller.event;

import com.code.aon.registry.IRegistry;
import com.code.aon.registry.RegistryRelationship;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class RegistryRelationshipControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		refreshRegistry(event);
	}
	
	private void refreshRegistry(ControllerEvent event) {
		IController controller = ((LinesController)getController()).getMasterController();
		IRegistry to = (IRegistry)controller.getTo();
		((RegistryRelationship)event.getController().getTo()).setRegistry(to.getRegistry());
	}
}
