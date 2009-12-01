package com.code.aon.ui.product.util;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.Item;
import com.code.aon.product.Product;

public class ItemControllerUtil {

	public static double getSalesPrice( Item item ) {
		double price = item.getPrice();
		Product product = item.getProduct();
		double vatPercent = (product.getVat()!=null)?product.getVat().getPercentage():0;
		double retentionPercent = (product.getRetention()!=null)?product.getRetention().getPercentage():0;
		return CommonUtil.round(price * (1 + vatPercent / 100 - retentionPercent / 100));
	}

	public static void onSalesPriceChanged(ValueChangeEvent event, Item item) {
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			double salesPrice = new Double(event.getNewValue().toString()).doubleValue();
			Product product = item.getProduct();
			double vatPercent = (product.getVat()!=null)?product.getVat().getPercentage():0;
			double retentionPercent = (product.getRetention()!=null)?product.getRetention().getPercentage():0;
			price = CommonUtil.round(salesPrice / (1 + vatPercent / 100 - retentionPercent / 100));
		}
		item.setPrice(price);
	}
}