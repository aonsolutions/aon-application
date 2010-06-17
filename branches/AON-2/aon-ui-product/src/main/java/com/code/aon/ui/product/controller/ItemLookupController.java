package com.code.aon.ui.product.controller;

import java.util.Map;

import com.code.aon.common.ITransferObject;
import com.code.aon.product.Item;
import com.code.aon.ui.form.LookupController;

public class ItemLookupController extends LookupController {
	
	/** The Constant ITEM_FULL_NAME used to retrieve the complete name of the item using the lookup. */
	private static final String ITEM_FULL_NAME = "Item_full_name";

	@Override
	protected void customizeLookupMap(ITransferObject to, Map<String, Object> map) {
		Item item = (Item)to;
		map.put(ITEM_FULL_NAME, item.getProduct().getName() + " " + ((item.getDetail() == null) ? "" : item.getDetail()) );
	}
}
