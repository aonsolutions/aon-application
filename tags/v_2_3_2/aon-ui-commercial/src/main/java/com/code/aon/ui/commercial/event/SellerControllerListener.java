package com.code.aon.ui.commercial.event;

import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class SellerControllerListener extends ControllerAdapter implements ICommercialConstants {
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		cancelChildControllers();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		cancelChildControllers();
	}
	private void cancelChildControllers() {
    	AonUtil.getController(SELLER_ADDRESS_CONTROLLER_NAME).onCancel(null);
       	AonUtil.getController(SELLER_MEDIA_CONTROLLER_NAME).onCancel(null);
	}
	
}