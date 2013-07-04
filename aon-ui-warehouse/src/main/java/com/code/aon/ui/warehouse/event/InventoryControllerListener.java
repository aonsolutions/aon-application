package com.code.aon.ui.warehouse.event;

import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.warehouse.controller.IWarehouseConstants;
import com.code.aon.ui.warehouse.controller.InventoryDetailController;

public class InventoryControllerListener extends ControllerAdapter {
	
	
	@Override
	public void beforeBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		InventoryDetailController controller = (InventoryDetailController)FormUtil.getController(IWarehouseConstants.INVENTORY_DETAIL_CONTROLLER_NAME);
		controller.resetSearchPanel();
	}

	
}