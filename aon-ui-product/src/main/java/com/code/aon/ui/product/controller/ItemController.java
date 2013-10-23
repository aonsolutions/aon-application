package com.code.aon.ui.product.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.pricing.ItemPricesManager;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.stat.controller.ProductStatEngineController;
import com.code.aon.ui.util.AonUtil;

public class ItemController extends BasicController {

	private String selectedTab;
	private boolean showNewTariffWindow;
	private boolean showNewCatalogueWindow;
	private boolean showNewSupplierWindow;
	private boolean showNewAlternativeWindow;
	private boolean showNewAddInfoWindow;
	private boolean showNewAttachmentWindow;
	private ItemPricesManager pricesManager;

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public boolean isShowNewTariffWindow() {
		return showNewTariffWindow;
	}

	public void setShowNewTariffWindow(boolean showNewTariffWindow) {
		this.showNewTariffWindow = showNewTariffWindow;
	}

	public boolean isShowNewCatalogueWindow() {
		return showNewCatalogueWindow;
	}

	public void setShowNewCatalogueWindow(boolean showNewCatalogueWindow) {
		this.showNewCatalogueWindow = showNewCatalogueWindow;
	}

	public boolean isShowNewSupplierWindow() {
		return showNewSupplierWindow;
	}

	public void setShowNewSupplierWindow(boolean showNewSupplierWindow) {
		this.showNewSupplierWindow = showNewSupplierWindow;
	}

	public boolean isShowNewAlternativeWindow() {
		return showNewAlternativeWindow;
	}

	public void setShowNewAlternativeWindow(boolean showNewAlternativeWindow) {
		this.showNewAlternativeWindow = showNewAlternativeWindow;
	}

	public boolean isShowNewAddInfoWindow() {
		return showNewAddInfoWindow;
	}

	public void setShowNewAddInfoWindow(boolean showNewAddInfoWindow) {
		this.showNewAddInfoWindow = showNewAddInfoWindow;
	}

	public boolean isShowNewAttachmentWindow() {
		return showNewAttachmentWindow;
	}

	public void setShowNewAttachmentWindow(boolean showNewAttachmentWindow) {
		this.showNewAttachmentWindow = showNewAttachmentWindow;
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

	@Override
	public void accept(ActionEvent event) {
		try {
			super.accept(event);	
		} finally {
			try {
				IManagerBean productBean = BeanManager.getManagerBean(Product.class);
				productBean.initializePOJO(((Item)getTo()).getProduct());
			} catch (ManagerBeanException e) {
				throw new AbortProcessingException(e.getMessage(), e);
			}			
		}
	}

	public void onProductHistory(ActionEvent e){
		ProductStatEngineController controller =(ProductStatEngineController)AonUtil.getRegisteredBean("productStat");
		controller.setItem((Item)this.getTo());
		controller.getProductData();
	}

	public void setCurrentItem( Item item ) {
		setTo(item);
	}

	public static void clearBarcode( Item item ) {
		if ( StringUtils.isEmpty(item.getBarcode()) ) {
			item.setBarcode(null);
		}
	}	
	
}