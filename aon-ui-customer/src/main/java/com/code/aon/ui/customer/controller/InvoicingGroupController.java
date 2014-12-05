package com.code.aon.ui.customer.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.InvoicingGroup;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class InvoicingGroupController extends BasicController implements ICustomerConstants, IAuditableController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String selectedTab;
	
	private boolean showAuditInfoWindow;

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public void onCustomerChanged(LookupChangeEvent event) throws ManagerBeanException {
		InvoicingGroup invoicingGroup = (InvoicingGroup)getTo();
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Customer customer = (Customer)event.getNewValue();
			invoicingGroup.setCustomer(customer);
			invoicingGroup.setDescription(customer.getRegistry().getName());
		}
	}

	public void onLoadCustomer(ActionEvent event) throws ManagerBeanException {
		InvoicingGroup invoicingGroup = (InvoicingGroup)getTo();
		BasicController customerController = (BasicController)AonUtil.getRegisteredBean(CUSTOMER_CONTROLLER_NAME);
		customerController.onLoad(event, invoicingGroup.getCustomer().getId(), INVOICING_GROUP_FORM_NAME, INVOICING_GROUP_CONTROLLER_NAME + ".refreshChilds");
	}

	public void refreshChilds(ActionEvent event) {
		FormUtil.getController(INVOICING_GROUP_DETAIL_CONTROLLER_NAME).onSearch(event);
		FormUtil.getController(INVOICING_GROUP_FEE_CONTROLLER_NAME).onSearch(event);
	}

	@Override
	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	@Override
	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}
	
}