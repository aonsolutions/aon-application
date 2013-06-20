package com.code.aon.ui.product.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.product.ItemTariff;
import com.code.aon.product.enumeration.ItemTariffType;
import com.code.aon.product.pricing.ItemPricesManager;
import com.code.aon.ui.form.LinesController;

public class ItemTariffController extends LinesController {

	private ItemPricesManager pricesManager;

	public ItemPricesManager getPricesManager() {
		if (pricesManager == null) {
			pricesManager = new ItemPricesManager();
		}
		return pricesManager;
	}

	public void onTariffTypeChanged(ValueChangeEvent event) {
		ItemTariff itemTariff = (ItemTariff)this.getTo();
		itemTariff.setType((ItemTariffType)event.getNewValue());
		if (itemTariff.isFixed()) {
			itemTariff.setProfitPercent(0);
		} else {
			getPricesManager().onProfitChanged(itemTariff, new Double(itemTariff.getProfitPercent()));
		}
	}

	public void onProfitChanged(ValueChangeEvent event) {
		getPricesManager().onProfitChanged((ItemTariff)this.getTo(), event.getNewValue());
	}

	public void onPriceChanged(ValueChangeEvent event) {
		ItemTariff itemTariff = (ItemTariff)this.getTo();
		if (!itemTariff.isFixed()) {
			getPricesManager().onPriceChanged(itemTariff, event.getNewValue());
		}
	}

	public void onSalesPriceChanged(ValueChangeEvent event) {
		ItemTariff itemTariff = (ItemTariff)this.getTo();
		getPricesManager().onSalesPriceChanged(itemTariff, event.getNewValue(), !itemTariff.isFixed());
	}

}