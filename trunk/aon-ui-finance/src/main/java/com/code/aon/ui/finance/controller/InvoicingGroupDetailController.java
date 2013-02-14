package com.code.aon.ui.finance.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoicingGroupDetail;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class InvoicingGroupDetailController extends LinesController implements IFinanceConstants {

	public void onLoadCustomer(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			InvoicingGroupDetail invoicingGroupDetail = (InvoicingGroupDetail)getModel().getRowData();
			BasicController customerController = (BasicController)AonUtil.getRegisteredBean(CUSTOMER_CONTROLLER_NAME);
			customerController.onLoad(event, invoicingGroupDetail.getChild().getId(), INVOICING_GROUP_FORM_NAME, INVOICING_GROUP_DETAIL_CONTROLLER_NAME + ".onSearch");
		}
	}

}