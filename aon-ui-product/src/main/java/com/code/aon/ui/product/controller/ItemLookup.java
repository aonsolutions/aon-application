package com.code.aon.ui.product.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.faces.controller.RichLookupBean;
import com.code.aon.product.Item;

public class ItemLookup extends RichLookupBean {

	private ItemPricesManager pricesManager;

	public ItemPricesManager getPricesManager() {
		if (pricesManager == null) {
			pricesManager = new ItemPricesManager();
		}
		return pricesManager;
	}

	public void onPurchasePriceChanged(ValueChangeEvent event) {
		getPricesManager().onPurchasePriceChanged((Item)this.getTo(), event.getNewValue());
	}

	public void onProfitChanged(ValueChangeEvent event) {
		getPricesManager().onProfitChanged((Item)this.getTo(), event.getNewValue());
	}

	public void onPriceChanged(ValueChangeEvent event) {
		getPricesManager().onPriceChanged((Item)this.getTo(), event.getNewValue());
	}

	public void onSalesPriceChanged(ValueChangeEvent event) {
		getPricesManager().onSalesPriceChanged((Item)this.getTo(), event.getNewValue());
	}

}
