package com.code.aon.ui.sales.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class SellerControllerListener extends ControllerAdapter {
	
	/** SellerAddress Controller name. */
	private final String SELLER_ADDRESS_CONTROLLER_NAME = "sellerAddress";

	/** SellerMedia Controller name. */
	private final String SELLER_MEDIA_CONTROLLER_NAME = "sellerMedia";
	
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