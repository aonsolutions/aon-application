package com.code.aon.ui.account.bridge.controller;

import com.code.aon.account.bridge.ProductAccount;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;

public class ProductAccountManager extends LinesController {

	public void itemChanged(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Item item = (Item) event.getNewValue();
			((ProductAccount) this.getTo()).setProduct(item.getProduct());
		}
	}

}
