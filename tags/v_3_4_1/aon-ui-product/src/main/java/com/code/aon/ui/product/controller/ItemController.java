package com.code.aon.ui.product.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.Item;
import com.code.aon.ui.form.BasicController;

public class ItemController extends BasicController {

	public double getSalesPrice() {
		double price = ((Item)getTo()).getPrice();
		double vatPercent = (((Item)getTo()).getProduct().getVat())!=null?((Item)getTo()).getProduct().getVat().getPercentage():0;
		double retentionPercent = (((Item)getTo()).getProduct().getRetention())!=null?((Item)getTo()).getProduct().getRetention().getPercentage():0;
		return CommonUtil.round(price * (1 + vatPercent / 100 - retentionPercent / 100));
	}

	public void setSalesPrice(double salesPrice) {
	}

	public void onSalesPriceChanged(ValueChangeEvent event) {
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			double salesPrice = new Double(event.getNewValue().toString()).doubleValue();
			double vatPercent = (((Item)getTo()).getProduct().getVat())!=null?((Item)getTo()).getProduct().getVat().getPercentage():0;
			double retentionPercent = (((Item)getTo()).getProduct().getRetention())!=null?((Item)getTo()).getProduct().getRetention().getPercentage():0;
			price = CommonUtil.round(salesPrice / (1 + vatPercent / 100 - retentionPercent / 100));
		}
		((Item)getTo()).setPrice(price);
	}

}