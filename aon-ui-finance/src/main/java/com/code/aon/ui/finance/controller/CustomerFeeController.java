package com.code.aon.ui.finance.controller;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.finance.CustomerFee;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.product.Item;
import com.code.aon.product.Tariff;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;

public class CustomerFeeController extends BasicController {

	private static final Logger LOGGER = Logger.getLogger(CustomerFeeController.class.getName());
	
	private static final String CUSTOMER_CONTROLLER_NAME = "customer";
	
	private IPriceStrategy priceStrategy;

	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

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

	public void onItemChanged(LookupChangeEvent event) {
		CustomerFee fee = (CustomerFee)getTo();
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			fee.setItem(item);
			fee.setDescription(item.getProduct().getName() + " " + (item.getDetail()!=null?item.getDetail():""));

			Date date = fee.getInitialDate();
			CustomerController customerController = (CustomerController)FormUtil.getController(CUSTOMER_CONTROLLER_NAME);
			Customer customer = (Customer)customerController.getTo();
			Tariff tariff = customer.getTariff();
			price = getPriceStrategy().getUnitPrice(fee, date, tariff);
		}
		fee.setPrice(price);
		
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
		CustomerFee fee = (CustomerFee)getTo();
		if (fee.getItem() != null && fee.getItem().getId() != null) {
			double price = 0;
			if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
				fee.setQuantity((Double)event.getNewValue());
	
				Date date = fee.getInitialDate();
				CustomerController customerController = (CustomerController)FormUtil.getController(CUSTOMER_CONTROLLER_NAME);
				Customer customer = (Customer)customerController.getTo();
				Tariff tariff = customer.getTariff();
				price = getPriceStrategy().getUnitPrice(fee, date, tariff);
			}
			fee.setPrice(price);
		}
	}

}
