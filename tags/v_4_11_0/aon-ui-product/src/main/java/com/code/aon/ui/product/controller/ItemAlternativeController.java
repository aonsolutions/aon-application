package com.code.aon.ui.product.controller;

import com.code.aon.product.Item;
import com.code.aon.product.ItemAlternative;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;

public class ItemAlternativeController extends LinesController {

	public void onAlternativeItemChanged(LookupChangeEvent event) {
		ItemAlternative itemAlternative = (ItemAlternative)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item alternativeItem = (Item)event.getNewValue();
			itemAlternative.setAlternativeItem(alternativeItem);
		}
	}	

}