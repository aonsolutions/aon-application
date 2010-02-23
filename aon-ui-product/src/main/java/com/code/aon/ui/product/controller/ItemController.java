package com.code.aon.ui.product.controller;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
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

	public void onSalesPriceChanged(ValueChangeEvent event) {
		double price = 0;
		if (event.getNewValue() != null	&& !event.getNewValue().toString().equals("")) {
			double salesPrice = new Double(event.getNewValue().toString()).doubleValue();
			Product product = ((Item)this.getTo()).getProduct();
			double vatPercent = (product.getVat() != null) ? product.getVat().getPercentage() : 0;
			double retentionPercent = (product.getRetention() != null) ? product.getRetention().getPercentage()	: 0;
			price = CommonUtil.round(salesPrice	/ (1 + vatPercent / 100 - retentionPercent / 100));
		}
		((Item)this.getTo()).setPrice(price);
	}
	
	public void onProductData(ActionEvent e){
		ProductStatEngineController controller =(ProductStatEngineController)AonUtil.getRegisteredBean("productStat");
		controller.setItem((Item)this.getTo());
		controller.getProductData();
	}
}