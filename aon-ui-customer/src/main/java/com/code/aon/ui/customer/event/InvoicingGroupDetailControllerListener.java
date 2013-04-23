package com.code.aon.ui.customer.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.finance.CustomerFee;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.customer.ICustomerMessages;
import com.code.aon.ui.customer.controller.ICustomerConstants;
import com.code.aon.ui.customer.controller.InvoicingGroupDetailController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoicingGroupDetailControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		((InvoicingGroupDetailController)event.getController()).setFeesIncluded(true);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		checkInvoicingGroup((Customer)event.getController().getTo());
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		if (((InvoicingGroupDetailController)event.getController()).isFeesIncluded()) {
			Customer customer = (Customer)event.getController().getTo();
			try {
				IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
				Criteria criteria = new Criteria();
	        	criteria.addEqualExpression(customerFeeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_ID), customer.getId());
	        	criteria.addNullExpression(customerFeeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_INVOICING_GROUP));
	        	for (ITransferObject ito : customerFeeBean.getList(criteria)) {
	        		CustomerFee customerFee = (CustomerFee)ito;
	        		customerFee.setInvoicingGroup(customer.getInvoicingGroup());
	        		customerFeeBean.update(customerFee);
	        	}

	        	FormUtil.getController(ICustomerConstants.INVOICING_GROUP_FEE_CONTROLLER_NAME).onSearch(null);
			} catch (ManagerBeanException ex) {
				throw new ControllerListenerException(ex.getMessage(), ex);
			}
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try {
			Customer customer = (Customer)event.getController().getModel().getRowData();
			IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
			Criteria criteria = new Criteria();
        	criteria.addEqualExpression(customerFeeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_ID), customer.getId());
        	criteria.addEqualExpression(customerFeeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_INVOICING_GROUP_ID), customer.getInvoicingGroup().getId());
        	for (ITransferObject ito : customerFeeBean.getList(criteria)) {
        		CustomerFee customerFee = (CustomerFee)ito;
        		customerFee.setInvoicingGroup(null);
        		customerFeeBean.update(customerFee);
        	}

        	FormUtil.getController(ICustomerConstants.INVOICING_GROUP_FEE_CONTROLLER_NAME).onSearch(null);
		} catch (ManagerBeanException ex) {
			throw new ControllerListenerException(ex.getMessage(), ex);
		}
	}

	private void checkInvoicingGroup(Customer customer) throws ControllerListenerException {
    	try {
         	IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
        	Criteria criteria = new Criteria();
        	criteria.addEqualExpression(customerBean.getFieldName(IEntityAlias.CUSTOMER_ID), customer.getId());
        	criteria.addNotNullExpression(customerBean.getFieldName(IEntityAlias.CUSTOMER_INVOICING_GROUP));
        	if (customerBean.getCount(criteria) > 0) {
        		String message = AonUtil.getMessage(ICustomerMessages.BUNDLE_KEY, ICustomerMessages.INVALID_INVOICING_GROUP_DETAIL_CHILD_KEY);
        		throw new ControllerListenerException(message);
        	}
	   	} catch (ManagerBeanException ex) {
	   		throw new ControllerListenerException(ex.getMessage(), ex);
    	}
    }

}