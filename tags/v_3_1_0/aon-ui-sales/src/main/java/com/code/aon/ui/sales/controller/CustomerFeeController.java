package com.code.aon.ui.sales.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.finance.CustomerFee;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;

public class CustomerFeeController extends BasicController {

	private static final Logger LOGGER = Logger.getLogger(CustomerFeeController.class.getName());
	
	private static final String CUSTOMER_CONTROLLER_NAME = "customer";
	
	public void onCustomerFee(ActionEvent event){
		CustomerController customerController = (CustomerController)FormUtil.getController(CUSTOMER_CONTROLLER_NAME);
		Customer customer = (Customer)customerController.getTo();
		try {
			IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
			this.clearCriteria();
			getCriteria().addEqualExpression(customerFeeBean.getFieldName(IFinanceAlias.CUSTOMER_FEE_CUSTOMER_ID), customer.getId());
			this.onSearch(null);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading fees related with customer with id= " + customer.getId(), e);
		}
	}
}
