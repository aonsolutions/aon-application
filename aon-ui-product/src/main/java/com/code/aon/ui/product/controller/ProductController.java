package com.code.aon.ui.product.controller;

import static com.code.aon.common.enumeration.AppParam.AON_PRODUCT_DETAIL_LEVEL;
import static com.code.aon.ui.product.controller.IItemConstants.ITEM;

import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tax;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.ProductCategory;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Stock;
import com.esferalia.aon.entity.IEntityAlias;

public class ProductController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
	
	private boolean showNewItemWindow;
	
	private Item item;
	
	private Item saveStateItem;
	
	private int detailLevel;
	
	public ProductController() {
		String value = AppParamUtil.getValue(AON_PRODUCT_DETAIL_LEVEL);
		if ( value != null ) {
			detailLevel = NumberUtils.toInt(value);
		}
	}
	
	public int getDetailLevel() {
		return detailLevel;
	}

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
			item.setDetail3(null);
		} else if (! isShowDetail2() ) {
			item.setDetail2(null);
			item.setDetail3(null);
		} else if (! isShowDetail3() ) {
			item.setDetail3(null);
		}
	}

	private void updateItemPrices( ItemController controller ) throws ManagerBeanException {
		Item item = (Item) controller.getTo();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(controller.getFieldName(IEntityAlias.ITEM_PRODUCT_ID), item.getProduct().getId());
		criteria.addOrder(controller.getFieldName(IEntityAlias.ITEM_ID), false);
		List<ITransferObject> list = controller.getManagerBean().getList(criteria, 0, 1);
		if (! list.isEmpty() ) {
			Item lastItem = (Item) list.get(0);
			item.setPurchasePrice(lastItem.getPurchasePrice());
			item.setProfitPercent(lastItem.getProfitPercent());
			item.setPrice(lastItem.getPrice());
		}
	}
	
	public void acceptItem(ActionEvent event) throws ManagerBeanException {
		ItemController controller = getItemController();
		ItemController.clearBarcode( (Item) controller.getTo() );
		controller.accept(event);
		updateItem(controller);
		getManagerBean().initializePOJO(getTo());
	}

	public void onCancelItem(ActionEvent event) throws ManagerBeanException {
		ItemController controller = getItemController();
		controller.onCancel(event);
		controller.setCurrentItem(saveStateItem);
		updateItem(controller);
	}
	
	public void onResetItem(ActionEvent event) throws ManagerBeanException {
		ItemController controller = getItemController();
		this.saveStateItem = (Item) controller.getTo();	
		controller.onReset(event);
		updateItem(controller);
		updateItemPrices(controller);
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
	
	private ProductCategory getCategory() {
		Product product = (Product) getTo();
		return product.getCategory();
	}
	
	public boolean isShowDetail() {
		if ( getDetailLevel() > 0 ) {
			ProductCategory category = getCategory();
			if ( category != null ) {
				return ! StringUtils.isEmpty(category.getDetail());
			}			
		}
		return false;
	}

	public boolean isShowDetail2() {
		if ( getDetailLevel() > 1 ) {
			ProductCategory category = getCategory();
			if ( category != null ) {
				return ! StringUtils.isEmpty(category.getDetail2());
			}			
		}
		return false;
	}

	public boolean isShowDetail3() {
		if ( getDetailLevel() > 2 ) {
			ProductCategory category = getCategory();
			if ( category != null ) {
				return ! StringUtils.isEmpty(category.getDetail3());
			}			
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

	public String getLabelDetail3() {
		Product product = (Product) getTo();
		return product.getCategory().getDetail3();
	}
	
	public boolean isOnStock() {
		Product product = (Product) getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(Stock.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression("Stock.item.product.id", product.getId());
			criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.STOCK_QUANTITY), 0.0);
			return bean.getCount(criteria) > 0;
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return false;
	}
	
}