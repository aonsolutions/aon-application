package com.code.aon.ui.product.controller;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.product.Item;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.stat.controller.ProductStatEngineController;
import com.code.aon.ui.util.AonUtil;

public class ItemController extends BasicController {

	private String selectedTab;
	private ItemPricesManager pricesManager;

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

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

	public void onProductHistory(ActionEvent e){
		ProductStatEngineController controller =(ProductStatEngineController)AonUtil.getRegisteredBean("productStat");
		controller.setItem((Item)this.getTo());
		controller.getProductData();
	}

}