package com.code.aon.ui.product.controller;

import static com.code.aon.ui.product.controller.IItemConstants.ITEM;

import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tax;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class ProductController extends BasicController {

	private boolean showNewItemWindow;
	
	private Item item;

	public boolean isShowNewItemWindow() {
		return showNewItemWindow;
	}

	public void setShowNewItemWindow(boolean showNewItemWindow) {
		this.showNewItemWindow = showNewItemWindow;
	}
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public static void updateVat( Product product ) throws ManagerBeanException {
		if (product.getVat() == null || product.getVat().getId() == null) {
			ConfigCollectionsController collections = (ConfigCollectionsController)AonUtil.getRegisteredBean(ConfigConstants.CONFIG_COLLECTIONS);
			List<SelectItem> vats = collections.getVatTaxes();
        	if (vats.size() > 0) {
        		Tax vat = (Tax) vats.get(0).getValue();
        		product.setVat(vat);
        	}
		}		
	}	
	
	public void onNewItem(ActionEvent event) {
		ItemController controller = (ItemController) AonUtil.getRegisteredBean(ITEM);
		controller.onReset(event);
		Product product = (Product) getTo();
		Item item = (Item) controller.getTo();
		item.setProduct(product);
	}

	public void onSaveItem(ActionEvent event) {
		ItemController controller = (ItemController) AonUtil.getRegisteredBean(ITEM);
		controller.onAccept(event);
		Product product = (Product) getTo();
		Item item = (Item) controller.getTo();
		item.setProduct(product);
	}
	
	public void onRemoveItem(ActionEvent event) throws ManagerBeanException {
		ItemController controller = (ItemController) AonUtil.getRegisteredBean(ITEM);
		controller.onRemove(event);
		if (controller.getModel().getRowCount() > 0) {
			controller.getModel().setRowIndex(0);
			controller.onSelect(null);
		}
	}
	
	public void onPurchasePriceChanged(ValueChangeEvent event) {
		ItemController controller = (ItemController) AonUtil.getRegisteredBean(ITEM);
		controller.getPricesManager().onPurchasePriceChanged( getItem(), event.getNewValue());
	}

	public void onProfitChanged(ValueChangeEvent event) {
		ItemController controller = (ItemController) AonUtil.getRegisteredBean(ITEM);
		controller.getPricesManager().onProfitChanged( getItem(), event.getNewValue());
	}

	public void onPriceChanged(ValueChangeEvent event) {
		ItemController controller = (ItemController) AonUtil.getRegisteredBean(ITEM);
		controller.getPricesManager().onPriceChanged( getItem(), event.getNewValue());
	}

	public void onSalesPriceChanged(ValueChangeEvent event) {
		ItemController controller = (ItemController) AonUtil.getRegisteredBean(ITEM);
		controller.getPricesManager().onSalesPriceChanged( getItem(), event.getNewValue());
	}
	
}