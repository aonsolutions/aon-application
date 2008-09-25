package com.code.aon.ui.salesPurchase.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.salesPurchase.controller.PurchaseSalesController;
import com.code.aon.ui.util.AonUtil;

public class IncomeControllerPurchaseSalesListener extends ControllerAdapter{

	private static final String PURCHASE_SALES_CONTROLLER_NAME = "purchaseSales";

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		PurchaseSalesController purchaseSalesController = (PurchaseSalesController)AonUtil.getController(PURCHASE_SALES_CONTROLLER_NAME);
		purchaseSalesController.onInitialize();
	}
}