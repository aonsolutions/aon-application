package com.code.aon.ui.finance.mailing;

import java.util.Collection;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.mailing.MailingManager;

public class MailingController {

	private static final String CUSTOMER_CONTROLLER_NAME = "customer";

	@SuppressWarnings("unchecked")
	public void onGenerateCustomerMailing(ActionEvent event) throws ManagerBeanException {
        CustomerController customerController = (CustomerController)FormUtil.getController(CUSTOMER_CONTROLLER_NAME);
        Collection collection = customerController.getManagerBean().getList(customerController.getCriteria());
        MailingManager.generateMailing(collection);
	}

}
