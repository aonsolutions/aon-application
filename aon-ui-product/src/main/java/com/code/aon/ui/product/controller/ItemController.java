package com.code.aon.ui.product.controller;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.Item;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.stat.controller.ProductStatEngineController;
import com.code.aon.ui.util.AonUtil;

public class ItemController extends BasicController {

	private String selectedTab;
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public void onPurchasePriceChanged(ValueChangeEvent event) {
		Item item = (Item)this.getTo();
		if (event.getNewValue() != null	&& !event.getNewValue().toString().equals("")) {
			double purchasePrice = CommonUtil.round(((Double)event.getNewValue()).doubleValue(), 4);
			if (purchasePrice == 0) {
				item.setProfitPercent(0);
			} else {
				item.setPrice(getPrice(purchasePrice, item.getProfitPercent()));
			}
		} else {
			item.setPurchasePrice(0);
			item.setProfitPercent(0);
		}
	}

	public void onProfitChanged(ValueChangeEvent event) {
		Item item = (Item)this.getTo();
		if (event.getNewValue() != null	&& !event.getNewValue().toString().equals("")) {
			double profit = CommonUtil.round(((Double)event.getNewValue()).doubleValue(), 3);
			item.setPrice(getPrice(item.getPurchasePrice(), profit));
		} else {
			item.setProfitPercent(0);
			if (item.getPurchasePrice() != 0) {
				item.setPrice(item.getPurchasePrice());
			}
		}
	}

	private double getPrice(double purchasePrice, double profit) {
		double price = 0;
		for (int i=2; i<=4; i++) {
			price = CommonUtil.round(purchasePrice * (1 + profit / 100), i);
			if (profit == CommonUtil.round((price / purchasePrice - 1) * 100, 3)) {
				return price;
			} else {
				price = CommonUtil.truncate(purchasePrice * (1 + profit / 100), i);
				if (profit == CommonUtil.round((price / purchasePrice - 1) * 100, 3)) {
					return price;
				}
			}
		}
		return price;
	}

	public void onPriceChanged(ValueChangeEvent event) {
		Item item = (Item)this.getTo();
		if (event.getNewValue() != null	&& !event.getNewValue().toString().equals("")) {
			double price = CommonUtil.round(((Double)event.getNewValue()).doubleValue(), 4);
			if (price == 0) {
				item.setProfitPercent(0);
			} else {
				item.setProfitPercent(getProfit(item.getPurchasePrice(), price));
			}
		} else {
			item.setPrice(0);
			item.setProfitPercent(0);
		}
	}

	private double getProfit(double purchasePrice, double price) {
		if (purchasePrice == 0) {
			return 0;
		}
		return CommonUtil.round((price / purchasePrice - 1) * 100, 3);
	}

	public void onSalesPriceChanged(ValueChangeEvent event) {
		Item item = (Item)this.getTo();
		if (event.getNewValue() != null	&& !event.getNewValue().toString().equals("")) {
			double salesPrice = CommonUtil.round(((Double)event.getNewValue()).doubleValue());
			if (salesPrice == 0) {
				item.setPrice(0);
				item.setProfitPercent(0);
			} else {
				item.setPrice(getPrice(item, salesPrice));
				item.setProfitPercent(getProfit(item.getPurchasePrice(), item.getPrice()));
			}
		} else {
			item.setPrice(0);
			item.setProfitPercent(0);
		}
	}

	private double getPrice(Item item, double salesPrice) {
		double vatPercent = (item.getProduct().getVat() != null) ? item.getProduct().getVat().getPercentage() : 0;
		double retentionPercent = (item.getProduct().getRetention() != null) ? item.getProduct().getRetention().getPercentage()	: 0;
		double price = 0;
		for (int i=2; i<=4; i++) {
			price = CommonUtil.round(salesPrice / (1 + vatPercent / 100 - retentionPercent / 100), i);
			if (salesPrice == item.getSalesPrice(price)) {
				return price;
			} else {
				price = CommonUtil.truncate(salesPrice / (1 + vatPercent / 100 - retentionPercent / 100), i);
				if (salesPrice == item.getSalesPrice(price)) {
					return price;
				}
			}
		}
		return price;
	}

	public void onProductHistory(ActionEvent e){
		ProductStatEngineController controller =(ProductStatEngineController)AonUtil.getRegisteredBean("productStat");
		controller.setItem((Item)this.getTo());
		controller.getProductData();
	}
}