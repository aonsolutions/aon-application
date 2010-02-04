package com.code.aon.ui.product.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.product.Item;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.product.util.ItemControllerUtil;

public class ItemController extends BasicController {

	private String selectedTab;
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public double getSalesPrice() {
		return ItemControllerUtil.getSalesPrice( (Item) getTo() );
	}

	public void setSalesPrice(double salesPrice) {
	}

	public void onSalesPriceChanged(ValueChangeEvent event) {
		ItemControllerUtil.onSalesPriceChanged(event, (Item) getTo() );
	}
	
}