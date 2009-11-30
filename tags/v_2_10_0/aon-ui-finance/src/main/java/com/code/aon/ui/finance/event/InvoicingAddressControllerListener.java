package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.finance.controller.InvoicingAddressController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class InvoicingAddressControllerListener extends ControllerAdapter {

	@Override
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		InvoicingAddressController controller = (InvoicingAddressController)event.getController();
		try {
			if (controller.getModel() != null && controller.getModel().getRowCount() > 0) {
				controller.onSelect(null);
			} else {
				controller.onReset(null);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

}