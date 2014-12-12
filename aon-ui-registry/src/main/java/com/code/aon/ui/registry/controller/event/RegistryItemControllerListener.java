package com.code.aon.ui.registry.controller.event;

import com.code.aon.AonVersion;
import com.code.aon.registry.RegistryItem;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.RegistryItemController;

public class RegistryItemControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		RegistryItemController controller = (RegistryItemController)event.getController();
		RegistryItem rItem = (RegistryItem)controller.getTo();
		rItem.setType(controller.getRegistryMode());
	}

}
