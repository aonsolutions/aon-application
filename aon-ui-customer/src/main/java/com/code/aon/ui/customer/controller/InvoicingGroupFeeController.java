package com.code.aon.ui.customer.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.CustomerFee;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class InvoicingGroupFeeController extends LinesController implements ICustomerConstants {

	@Override
	protected void remove() throws ManagerBeanException {
		CustomerFee customerFee = (CustomerFee)getModel().getRowData();
		customerFee.setInvoicingGroup(null);
		setTo(customerFee);
		getManagerBean().restoreNullSubPOJOs(customerFee);
		update();
	}

	public void onLoadCustomer(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			CustomerFee customerFee = (CustomerFee)getModel().getRowData();
			CustomerController customerController = (CustomerController)AonUtil.getRegisteredBean(CUSTOMER_CONTROLLER_NAME);
			customerController.setSelectedTab(ICustomerConstants.CUSTOMER_FEE_TAB);
			customerController.onLoad(event, customerFee.getCustomer().getId(), INVOICING_GROUP_FORM_NAME, INVOICING_GROUP_CONTROLLER_NAME + ".refreshChilds");
		}
	}

}