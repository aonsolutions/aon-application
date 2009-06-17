package com.code.aon.ui.finance.controller;

import java.util.Date;

import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.config.Tariff;
import com.code.aon.customer.Customer;
import com.code.aon.finance.CustomerFee;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;

public class CustomerFeeController extends LinesController {

	private static final String CUSTOMER_CONTROLLER_NAME = "customer";
	
	private IPriceStrategy priceStrategy;

	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public void onItemChanged(LookupChangeEvent event) {
		CustomerFee fee = (CustomerFee)getTo();
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			fee.setItem(item);
			String description = item.getProduct().getName();
			if (! StringUtils.isBlank(item.getDetail()) ) {
				description += " " + item.getDetail();
			}
			fee.setDescription( description );

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
