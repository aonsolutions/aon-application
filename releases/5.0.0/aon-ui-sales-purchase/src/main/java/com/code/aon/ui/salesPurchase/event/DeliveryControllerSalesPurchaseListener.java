package com.code.aon.ui.salesPurchase.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.salesPurchase.controller.SalesPurchaseController;
import com.code.aon.ui.util.AonUtil;

public class DeliveryControllerSalesPurchaseListener extends ControllerAdapter {
	
	private static final String SALES_PURCHASE_CONTROLLER_NAME = "salesPurchase";

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		SalesPurchaseController salesPurchaseController = (SalesPurchaseController)AonUtil.getController(SALES_PURCHASE_CONTROLLER_NAME);
		salesPurchaseController.onInitialize();
	}
}
