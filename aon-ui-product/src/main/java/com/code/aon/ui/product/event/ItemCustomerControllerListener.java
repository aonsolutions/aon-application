package com.code.aon.ui.product.event;

import com.code.aon.AonVersion;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.enumeration.RegistryMode;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.controller.ItemCustomerController;

public class ItemCustomerControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ItemCustomerController controller = (ItemCustomerController)event.getController();
		RegistryItem registryItem = (RegistryItem)controller.getTo();
		registryItem.setType(RegistryMode.CUSTOMER);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		event.getController().initializeModel();
	}

}
