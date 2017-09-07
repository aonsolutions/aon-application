package com.code.aon.ui.product.controller;

import static com.code.aon.common.enumeration.AppParam.AON_PRODUCT_DETAIL_LEVEL;
import static com.code.aon.ui.common.ICommonMessages.PRODUCT_COMPOSITION_REMOVE_INFO;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Tax;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.ProductCategory;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.product.event.ItemSearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ProductController extends BasicController implements IAuditableController, IItemConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private int detailLevel;
	private Item item;
	private boolean showNewItemWindow;
	private boolean showSearchItemWindow;
	private Item saveStateItem;
	private boolean showAccountsWindow;
	private boolean showAuditInfoWindow;

	public ProductController() {
		String value = AppParamUtil.getValue(AON_PRODUCT_DETAIL_LEVEL);
		if (value != null) {
			detailLevel = NumberUtils.toInt(value);
		}
	}
	
	public int getDetailLevel() {
		return detailLevel;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	
	public boolean isShowNewItemWindow() {
		return showNewItemWindow;
	}

	public void setShowNewItemWindow(boolean showNewItemWindow) {
		this.showNewItemWindow = showNewItemWindow;
	}
	
	public boolean isShowSearchItemWindow() {
		return showSearchItemWindow;
	}

	public void setShowSearchItemWindow(boolean showSearchItemWindow) {
		this.showSearchItemWindow = showSearchItemWindow;
	}
	
	public boolean isShowAccountsWindow() {
		return showAccountsWindow;
	}

	public void setShowAccountsWindow(boolean showAccountsWindow) {
		this.showAccountsWindow = showAccountsWindow;
	}
	
	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}

	public ItemController getItemController() {
		return (ItemController)AonUtil.getRegisteredBean(ITEM);
	}

	public static void updateVat(Product product) throws ManagerBeanException {
		if (product.getVat() == null || product.getVat().getId() == null) {
			ConfigCollectionsController configCollections = (ConfigCollectionsController)AonUtil.getRegisteredBean(ConfigConstants.CONFIG_COLLECTIONS);
			List<SelectItem> vats = configCollections.getVatTaxes();
        	if (vats.size() > 0) {
    			ApplicationParameter appParam = AppParamUtil.getParameter(AppParam.ACC_DEFAULT_VAT_PERCENT);
    			if (appParam != null && StringUtils.isNotBlank(appParam.getValue())) {
    				for (SelectItem selectItem : vats) {
    					Tax vat = (Tax)selectItem.getValue();
    					if (vat.getId().intValue() == Integer.parseInt(appParam.getValue())) {
    						product.setVat(vat);
    						break;
    					}
    				}
    			}

    			if (product.getVat() == null || product.getVat().getId() == null) {
    	    		Tax vat = (Tax) vats.get(0).getValue();
    	    		product.setVat(vat);
    			}
        	}
		}		
	}	
	
	public void onAcceptItem(ActionEvent event) throws ManagerBeanException {
		ItemController itemController = getItemController();
		Item item = (Item)itemController.getTo();
		ItemController.clearBarcode(item);
		updateItem(item);
		itemController.accept(event);
		getManagerBean().initializePOJO(getTo());
	}

	public void onCancelItem(ActionEvent event) throws ManagerBeanException {
		ItemController itemController = getItemController();
		Item item = (Item)itemController.getTo();
		itemController.onCancel(event);
		itemController.setCurrentItem(saveStateItem);
		updateItem(item);
	}

	public void onResetItem(ActionEvent event) throws ManagerBeanException {
		ItemController itemController = getItemController();
		this.saveStateItem = (Item)itemController.getTo();
		itemController.onReset(event);
		Item item = (Item)itemController.getTo();
		updateItem(item);
		updateItemData(item);
	}

	public void onSelectItem(ActionEvent event) {
		ItemController itemController = getItemController();
		itemController.onSelect(event);
		Item item = (Item)itemController.getTo();
		updateItem(item);
	}
	
	public void onRemoveItem(ActionEvent event) throws ManagerBeanException {
		ItemController itemController = getItemController();
		itemController.onRemove(event);
		if (itemController.getModel().getRowCount() > 0) {
			itemController.getModel().setRowIndex(0);
			onSelectItem(event);
		}
	}

	public void updateItem(Item item) {
		item.setProduct((Product)getTo());		
		if (!isShowDetail()) {
			item.setDetail(null);
			item.setDetail2(null);
			item.setDetail3(null);
		} else if (!isShowDetail2()) {
			item.setDetail2(null);
			item.setDetail3(null);
		} else if (!isShowDetail3()) {
			item.setDetail3(null);
		}
		if (!item.getProduct().isSerializable()) {
			item.setSerialNumber(null);
			item.setSerialDate(null);
		}
	}

	public void updateItemData(Item item) throws ManagerBeanException {
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_ID), item.getProduct().getId());
		criteria.addOrder(itemBean.getFieldName(IEntityAlias.ITEM_ID), item.getProduct().isSerializable());
		List<ITransferObject> itemList = itemBean.getList(criteria, 0, 1);
		if (!itemList.isEmpty()) {
			Item lastItem = (Item)itemList.get(0);
			item.setPurchasePrice(lastItem.getPurchasePrice());
			item.setProfitPercent(lastItem.getProfitPercent());
			item.setPrice(lastItem.getPrice());

			if (item.getProduct().isPackaged()) {
    			item.setPackFormatTag(lastItem.getPackFormatTag());
    			item.setStockUnitTag(lastItem.getStockUnitTag());
    			item.setPackUnitsTag(lastItem.getPackUnitsTag());
    			item.setPackUnits(lastItem.getPackUnits());
    			item.setPackMeasurementTag(lastItem.getPackMeasurementTag());
    			item.setPackMeasurement(lastItem.getPackMeasurement());
			}
		}
	}

	public void onSearchAllItem(ActionEvent event) throws ManagerBeanException {
		ItemController itemController = (ItemController)AonUtil.getRegisteredBean(ITEM);
		itemController.onEditSearch(event);
		searchItems(true);
	}
	
	public void onSearchItem(ActionEvent event) throws ManagerBeanException {
		ItemController itemController = (ItemController)AonUtil.getRegisteredBean(ITEM);
		itemController.clearCriteria();
		searchItems(false);
	}
	
	public void searchItems(ActionEvent event) throws ManagerBeanException {
		searchItems(false);
	}

	private void searchItems(boolean allItems) throws ManagerBeanException {
		ItemController itemController = (ItemController)AonUtil.getRegisteredBean(ITEM);
		Product product = (Product)getTo();
		ItemSearchListener itemSearch = (ItemSearchListener)AonUtil.getRegisteredBean(ITEM_SEARCH_CONTROLLER_NAME);
		itemSearch.setProduct(product);
		if (!product.isSerializable()) {
			if (allItems) {
				itemSearch.setItemStatuses(null);
			}
		} else {
			itemController.getCriteria().addOrder(itemController.getFieldName(IEntityAlias.ITEM_SERIAL_NUMBER));
		}
		if (product.getCategory() != null && StringUtils.isNotEmpty(product.getCategory().getDetail())) {
			itemController.getCriteria().addOrder(itemController.getFieldName(IEntityAlias.ITEM_DETAIL));
			if (StringUtils.isNotEmpty(product.getCategory().getDetail2())) {
				itemController.getCriteria().addOrder(itemController.getFieldName(IEntityAlias.ITEM_DETAIL2));
			}
			if (StringUtils.isNotEmpty(product.getCategory().getDetail3())) {
				itemController.getCriteria().addOrder(itemController.getFieldName(IEntityAlias.ITEM_DETAIL3));
			}
		}
		itemController.onSearch(null);
		if (itemController.getModel().getRowCount() > 0) {
			itemController.getModel().setRowIndex(0);
			itemController.onSelect(null);
		} else {
			itemController.onReset(null);
		}
		Item item = (Item)itemController.getTo();
		item.setProduct(product);
	}

	public void onSerializableChanged(ValueChangeEvent event) {
		Product product = (Product) getTo();
		if (event.getNewValue() != null && (Boolean)event.getNewValue()) {
			product.setInventoriable(true);
		}
	}

	public void onManufacturedChanged(ValueChangeEvent event) {
		Product product = (Product) getTo();
		if (event.getNewValue() != null && !(Boolean)event.getNewValue()) {
			try {
				if (product.getCompositionCount() > 0) {
					AonUtil.addInfoMessageFromBundle(PRODUCT_COMPOSITION_REMOVE_INFO);
				}
			} catch (ManagerBeanException ex) {
				throw new AbortProcessingException(ex.getMessage());
			}
		}
	}

	public void onCompositionChanged(ValueChangeEvent event) {
		Product product = (Product) getTo();
		if (event.getNewValue() != null && !(Boolean)event.getNewValue()) {
			try {
				if (product.getCompositionCount() > 0) {
					AonUtil.addInfoMessageFromBundle(PRODUCT_COMPOSITION_REMOVE_INFO);
				}
			} catch (ManagerBeanException ex) {
				throw new AbortProcessingException(ex.getMessage());
			}
		}
	}

	private ProductCategory getCategory() {
		Product product = (Product) getTo();
		return product.getCategory();
	}
	
	public boolean isShowDetail() {
		if (getDetailLevel() > 0) {
			ProductCategory category = getCategory();
			if (category != null) {
				return StringUtils.isNotBlank(category.getDetail());
			}			
		}
		return false;
	}

	public boolean isShowDetail2() {
		if (getDetailLevel() > 1) {
			ProductCategory category = getCategory();
			if (category != null) {
				return StringUtils.isNotBlank(category.getDetail2());
			}			
		}
		return false;
	}

	public boolean isShowDetail3() {
		if (getDetailLevel() > 2) {
			ProductCategory category = getCategory();
			if (category != null) {
				return StringUtils.isNotBlank(category.getDetail3());
			}			
		}
		return false;
	}
	
	public void onPurchasePriceChanged(ValueChangeEvent event) {
		onPurchasePriceChanged(getItem(), event.getNewValue());
	}

	public void onPurchasePriceChanged(Item item, Object value) {
		getItemController().getPricesManager().onPurchasePriceChanged(item, value);
	}

	public void onProfitChanged(ValueChangeEvent event) {
		onProfitChanged(getItem(), event.getNewValue());
	}

	public void onProfitChanged(Item item, Object value) {
		getItemController().getPricesManager().onProfitChanged(item, value);
	}

	public void onSalesProfitChanged(ValueChangeEvent event) {
		onSalesProfitChanged(getItem(), event.getNewValue());
	}

	public void onSalesProfitChanged(Item item, Object value) {
		getItemController().getPricesManager().onSalesProfitChanged(item, value);
	}

	public void onPriceChanged(ValueChangeEvent event) {
		onPriceChanged(getItem(), event.getNewValue());
	}

	public void onPriceChanged(Item item, Object value) {
		getItemController().getPricesManager().onPriceChanged(item, value);
	}

	public void onSalesPriceChanged(ValueChangeEvent event) {
		onSalesPriceChanged(getItem(), event.getNewValue());
	}

	public void onSalesPriceChanged(Item item, Object value) {
		getItemController().getPricesManager().onSalesPriceChanged(item, value);
	}

}