package com.code.aon.ui.commercial.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.commercial.Target;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.sales.bridge.util.SalesBridgeUtil;
import com.code.aon.ui.registry.controller.RegistryRichLookupBean;

public class TargetLookupController extends RegistryRichLookupBean {

	public void onCreateCustomer(ActionEvent event) throws ManagerBeanException {
		SalesBridgeUtil salesUtil = new SalesBridgeUtil();
		Customer customer = salesUtil.createCustomer((Target)getTo());
		((Target)getTo()).setCustomer(customer.getId()!=null);
	}

}