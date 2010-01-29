package com.code.aon.ui.product.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.product.Item;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.product.util.ItemControllerUtil;

public class ItemController extends BasicController {

	public double getSalesPrice() {
		return ItemControllerUtil.getSalesPrice( (Item) getTo() );
	}

	public void setSalesPrice(double salesPrice) {
	}

	public void onSalesPriceChanged(ValueChangeEvent event) {
		ItemControllerUtil.onSalesPriceChanged(event, (Item) getTo() );
	}
	
	public void onAlternativeItemChanged(LookupChangeEvent event) {
		Item item = (Item)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item alternativeItem = (Item)event.getNewValue();
			item.setAlternativeItem(alternativeItem);
		}
	}	

}