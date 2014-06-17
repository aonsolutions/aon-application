package com.code.aon.ui.registry.controller.event;

import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.enumeration.RegistryMode;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class RegistryItemControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		RegistryItem rItem = (RegistryItem)event.getController().getTo();
		rItem.setType(RegistryMode.TARGET);
	}

}
