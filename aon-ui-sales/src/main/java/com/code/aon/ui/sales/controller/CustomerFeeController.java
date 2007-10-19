package com.code.aon.ui.sales.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.sales.CustomerFee;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class CustomerFeeController extends BasicController {

	private static final Logger LOGGER = Logger.getLogger(CustomerFeeController.class.getName());
	
	private static final String CUSTOMER_CONTROLLER_NAME = "customer";
	
	@SuppressWarnings("unused")
	public void onCustomerFee(ActionEvent event){
		CustomerController customerController = (CustomerController)AonUtil.getController(CUSTOMER_CONTROLLER_NAME);
		Customer customer = (Customer)customerController.getTo();
		try {
			IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
			this.clearCriteria();
			getCriteria().addEqualExpression(customerFeeBean.getFieldName(ISalesAlias.CUSTOMER_FEE_CUSTOMER_ID), customer.getId());
			this.onSearch(null);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading fees related with customer with id= " + customer.getId(), e);
		}
	}
}
