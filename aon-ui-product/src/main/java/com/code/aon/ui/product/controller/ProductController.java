package com.code.aon.ui.product.controller;

import static com.code.aon.ui.product.controller.IItemConstants.ITEM;

import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

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
	
	private ItemController getItemController() {
		return (ItemController) AonUtil.getRegisteredBean(ITEM);
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
	
	private void updateItem( ItemController controller ) {
		Product product = (Product) getTo();
		Item item = (Item) controller.getTo();
		item.setProduct(product);		
		if (! isShowDetail() ) {
			item.setDetail(null);
			item.setDetail2(null);
		} else if (! isShowDetail2() ) {
			item.setDetail2(null);
		}
	}

	public void acceptItem(ActionEvent event) {
		ItemController controller = getItemController();
		controller.accept(event);
		updateItem(controller);
	}
	
	public void onResetItem(ActionEvent event) {
		ItemController controller = getItemController();
		controller.onReset(event);
		updateItem(controller);
	}

	public void onSelectItem(ActionEvent event) {
		ItemController controller = getItemController();
		controller.onSelect(event);
		updateItem(controller);		
	}
	
	public void onRemoveItem(ActionEvent event) throws ManagerBeanException {
		ItemController controller = getItemController();
		controller.onRemove(event);
		if (controller.getModel().getRowCount() > 0) {
			controller.getModel().setRowIndex(0);
			controller.onSelect(null);
			updateItem(controller);
		}
	}
	
	public void onPurchasePriceChanged(ValueChangeEvent event) {
		getItemController().getPricesManager().onPurchasePriceChanged( getItem(), event.getNewValue());
	}

	public void onProfitChanged(ValueChangeEvent event) {
		getItemController().getPricesManager().onProfitChanged( getItem(), event.getNewValue());
	}

	public void onPriceChanged(ValueChangeEvent event) {
		getItemController().getPricesManager().onPriceChanged( getItem(), event.getNewValue());
	}

	public void onSalesPriceChanged(ValueChangeEvent event) {
		getItemController().getPricesManager().onSalesPriceChanged( getItem(), event.getNewValue());
	}
	
	public boolean isShowDetail() {
		Product product = (Product) getTo();
		if ( product.getCategory() != null ) {
			return ! StringUtils.isEmpty(product.getCategory().getDetail());
		}
		return false;
	}

	public boolean isShowDetail2() {
		Product product = (Product) getTo();
		if ( product.getCategory() != null ) {
			return ! StringUtils.isEmpty(product.getCategory().getDetail2());
		}
		return false;
	}

	public String getLabelDetail() {
		Product product = (Product) getTo();
		return product.getCategory().getDetail();
	}
	
	public String getLabelDetail2() {
		Product product = (Product) getTo();
		return product.getCategory().getDetail2();
	}
	
}