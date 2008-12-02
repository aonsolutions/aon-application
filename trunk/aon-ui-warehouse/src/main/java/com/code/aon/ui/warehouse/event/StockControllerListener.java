package com.code.aon.ui.warehouse.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.warehouse.controller.StockController;

public class StockControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			StockController stockController = (StockController)event.getController();
			stockController.initTransferData();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

}
