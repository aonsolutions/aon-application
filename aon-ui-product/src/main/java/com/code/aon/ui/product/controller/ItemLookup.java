package com.code.aon.ui.product.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.faces.controller.RichLookupBean;
import com.code.aon.product.Item;
import com.code.aon.ui.product.util.ItemControllerUtil;

public class ItemLookup extends RichLookupBean {

	public double getSalesPrice() {
		return ItemControllerUtil.getSalesPrice( (Item) getTo() );
	}

	public void setSalesPrice(double salesPrice) {
	}

	public void onSalesPriceChanged(ValueChangeEvent event) {
		ItemControllerUtil.onSalesPriceChanged(event, (Item) getTo() );
	}
	
}
