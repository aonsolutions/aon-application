package com.code.aon.ui.purchase.event;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ManufacturingOrderDetailControllerListener extends PurchaseDetailControllerListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		super.afterModelInitialized(event);
		IController controller = event.getController();
		try {
			if(controller.getModel().getRowCount()<=0){
				controller.onReset(null);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

}