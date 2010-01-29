package com.code.aon.ui.product.controller;

import com.code.aon.product.CatalogueItem;
import com.code.aon.product.Item;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;

public class CatalogueItemController extends LinesController {

	public void itemData(LookupChangeEvent event) {
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Item item = (Item)event.getNewValue();
			price = item.getPrice();
		}
		((CatalogueItem) this.getTo()).setPrice(price);
	}

}