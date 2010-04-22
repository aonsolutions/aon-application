package com.code.aon.ui.product.controller;

import com.code.aon.product.CatalogueItem;
import com.code.aon.product.Item;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;

public class CatalogueItemController extends LinesController {

	public void itemData(LookupChangeEvent event) {
		CatalogueItem catalogueItem = (CatalogueItem) this.getTo();
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Item item = (Item)event.getNewValue();
			catalogueItem.setItem(item);

			price = item.getPrice();
		}
		catalogueItem.setPrice(price);
	}

}