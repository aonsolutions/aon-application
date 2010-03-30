package com.code.aon.ui.commercial.controller;

import com.code.aon.commercial.CommissionItem;
import com.code.aon.product.Item;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;

public class CommissionItemController extends LinesController {

	public void itemData(LookupChangeEvent event) {
		CommissionItem commissionItem = (CommissionItem) this.getTo();
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Item item = (Item)event.getNewValue();
			commissionItem.setItem(item);
		}
	}

}