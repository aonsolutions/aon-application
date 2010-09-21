package com.code.aon.ui.product.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.faces.controller.RichLookupBean;
import com.code.aon.product.Item;
import com.code.aon.product.Product;

public class ItemLookup extends RichLookupBean {

	public double getSalesPrice() {
		return ((Item) getTo()).getSalesPrice();
	}

	public void setSalesPrice(double salesPrice) {
	}

	public void onSalesPriceChanged(ValueChangeEvent event) {
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			double salesPrice = new Double(event.getNewValue().toString()).doubleValue();
			Product product = ((Item)this.getTo()).getProduct();
			double vatPercent = (product.getVat() != null) ? product.getVat().getPercentage() : 0;
			double retentionPercent = (product.getRetention() != null) ? product.getRetention().getPercentage()	: 0;
			price = CommonUtil.round(salesPrice	/ (1 + vatPercent / 100 - retentionPercent / 100));
		}
		((Item)this.getTo()).setPrice(price);
	}
	
}
