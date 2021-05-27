package com.code.aon.ui.customer.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.InvoicingGroup;
import com.code.aon.finance.CustomerFee;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoicingGroupDetailController extends LinesController implements ICustomerConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean feesIncluded;

	public boolean isFeesIncluded() {
		return feesIncluded;
	}

	public void setFeesIncluded(boolean value) {
		this.feesIncluded = value;
	}

	public boolean isCustomerFeesAvailables() {
		Customer customer = (Customer)getTo();
		if (customer != null && customer.getId() != null) {
			try {
				IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
				Criteria criteria = new Criteria();
	        	criteria.addEqualExpression(customerFeeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_ID), customer.getId());
	        	criteria.addNullExpression(customerFeeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_INVOICING_GROUP));
	        	return customerFeeBean.getCount(criteria) > 0;
			} catch (ManagerBeanException ex) {
				String msg = "Error obtaining Customer Fee List";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		}
		return false;
	}

	public void onCustomerChanged(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Customer customer = (Customer)event.getNewValue();
			setTo(customer);
		} else {
			setTo(getManagerBean().createNewTo());
			setFeesIncluded(false);
		}
	}

	@Override
	protected ITransferObject add() throws ManagerBeanException {
		Customer customer = (Customer)getTo();
		customer.setInvoicingGroup((InvoicingGroup)getMasterController().getTo());
		getManagerBean().restoreNullSubPOJOs(customer);
		return update();
	}

	@Override
	protected void remove() throws ManagerBeanException {
		Customer customer = (Customer)getModel().getRowData();
		customer.setInvoicingGroup(null);
		setTo(customer);
		getManagerBean().restoreNullSubPOJOs(customer);
		update();
	}

	public void onLoadCustomer(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			Customer customer = (Customer)getModel().getRowData();
			BasicController customerController = (BasicController)AonUtil.getRegisteredBean(CUSTOMER_CONTROLLER_NAME);
			customerController.onLoad(event, customer.getId(), INVOICING_GROUP_FORM_NAME, INVOICING_GROUP_CONTROLLER_NAME + ".refreshChilds");
		}
	}

}