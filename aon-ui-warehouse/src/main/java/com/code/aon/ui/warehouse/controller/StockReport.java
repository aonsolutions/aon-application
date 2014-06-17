package com.code.aon.ui.warehouse.controller;

import static com.code.aon.ui.common.ICommonMessages.STOCK_BY_ITEM_KEY;
import static com.code.aon.ui.common.ICommonMessages.STOCK_BY_ITEM_VALUED_KEY;
import static com.code.aon.ui.common.ICommonMessages.STOCK_BY_WAREHOUSE_KEY;
import static com.code.aon.ui.common.ICommonMessages.STOCK_BY_WAREHOUSE_VALUED_KEY;

import java.io.Serializable;
import java.util.Collection;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.enumeration.PriceType;
import com.code.aon.warehouse.stock.StockManager;
import com.esferalia.aon.entity.IEntityAlias;

public class StockReport implements ICollectionProvider, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	// Report Keys 
	private static final String BY_ITEM_REPORT_KEY = "stockItemList";
	private static final String BY_WAREHOUSE_REPORT_KEY = "stockWarehouseList";
	private static final String BY_ITEM_VALUED_REPORT_KEY = "stockItemValuedList";
	private static final String BY_WAREHOUSE_VALUED_REPORT_KEY = "stockWarehouseValuedList";

	private Warehouse warehouse;
	private Item item;
	private String quantity;
	private PriceType priceType;
	private boolean byItem;
	private boolean valued;
	private StockManager manager;
	
	public StockManager getManager() {
		if (manager == null) {
			manager = new StockManager(AonUtil.getDomainName(), AccountingUtil.getDefaultSettings());
		}
		return manager;
	}

	public boolean isByItem() {
		return byItem;
	}
	public void setByItem(boolean byItem) {
		this.byItem = byItem;
	}
	public boolean isValued() {
		return valued;
	}
	public void setValued(boolean valued) {
		this.valued = valued;
	}
	public Warehouse getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public PriceType getPriceType() {
		return priceType;
	}
	public void setPriceType(PriceType priceType) {
		this.priceType = priceType;
	}
	public String getBeanName() {
		return "stockReport";
	}
	public String getTitle() {
		if (isValued()) {
			return isByItem()?
					AonUtil.getMessage(STOCK_BY_ITEM_VALUED_KEY):
					AonUtil.getMessage(STOCK_BY_WAREHOUSE_VALUED_KEY);
		}
		return isByItem()?
			AonUtil.getMessage(STOCK_BY_ITEM_KEY):
			AonUtil.getMessage(STOCK_BY_WAREHOUSE_KEY);
	}
	
	public void onReportByWarehouse(ActionEvent event) {
		try {
			initialize(false,false); 
		} catch (ManagerBeanException e) {
			String msg = "Imposible inicializar los parámetros del listado";
 			AonUtil.addErrorMessage(msg);
 			throw new AbortProcessingException(msg, e);
		}
	}
	
	public void onReportByItem(ActionEvent event) {
		try {
			initialize(true,false);
		} catch (ManagerBeanException e) {
			String msg = "Imposible inicializar los parámetros del listado";
 			AonUtil.addErrorMessage(msg);
 			throw new AbortProcessingException(msg, e);
		}
	}
	public void onReportByWarehouseValued(ActionEvent event) {
		try {
			initialize(false,true); 
		} catch (ManagerBeanException e) {
			String msg = "Imposible inicializar los parámetros del listado";
 			AonUtil.addErrorMessage(msg);
 			throw new AbortProcessingException(msg, e);
		}
	}
	
	public void onReportByItemValued(ActionEvent event) {
		try {
			initialize(true,true);
		} catch (ManagerBeanException e) {
			String msg = "Imposible inicializar los parámetros del listado";
 			AonUtil.addErrorMessage(msg);
 			throw new AbortProcessingException(msg, e);
		}
	}

	private void initialize(boolean byItem, boolean valued) throws ManagerBeanException {
		setWarehouse((Warehouse) BeanManager.getManagerBean(Warehouse.class).createNewTo());
		setItem((Item) BeanManager.getManagerBean(Item.class).createNewTo());
		setQuantity(null);
		setPriceType(PriceType.COST_PRICE);
		setByItem(byItem);
		setValued(valued);
	}
	
	public String getReportKey() {
		if (isValued()) {
			return isByItem()?BY_ITEM_VALUED_REPORT_KEY:BY_WAREHOUSE_VALUED_REPORT_KEY;	
		}
		return isByItem()?BY_ITEM_REPORT_KEY:BY_WAREHOUSE_REPORT_KEY;
	}
	
	@Override
	public Collection<?> getCollection() {
		try {
			return getCollection(false);
		} catch (ManagerBeanException e) {
			String msg = "Imposible realizar el listado";
 			AonUtil.addErrorMessage(msg);
 			throw new AbortProcessingException(msg, e);
		}
	}
	@Override
	public Collection<?> getCollection(boolean forceRefresh) throws ManagerBeanException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Stock.class);
			Criteria criteria = new Criteria();
			if (getWarehouse() != null && getWarehouse().getId() != null) {
				criteria.addEqualExpression( bean.getFieldName(IEntityAlias.STOCK_WAREHOUSE_ID), getWarehouse().getId() );
			} else {
				UserUtils.getInstance().addScopeFilterToCriteria(criteria, bean.getFieldName(IEntityAlias.STOCK_WAREHOUSE_WORK_PLACE_SCOPE_ID));
			}
			if (getItem() != null && getItem().getId() != null) {
				criteria.addEqualExpression( bean.getFieldName(IEntityAlias.STOCK_ITEM_ID), getItem().getId() );
			}
			if (StringUtils.isNotBlank(getQuantity())) {
				criteria.addExpression(bean.getFieldName(IEntityAlias.STOCK_QUANTITY), getQuantity());
			}
			if (isByItem()) {
				criteria.addOrder(bean.getFieldName(IEntityAlias.STOCK_ITEM_ID));
				criteria.addOrder(bean.getFieldName(IEntityAlias.STOCK_WAREHOUSE_ID));
			} else {
				criteria.addOrder(bean.getFieldName(IEntityAlias.STOCK_WAREHOUSE_ID));
				criteria.addOrder(bean.getFieldName(IEntityAlias.STOCK_ITEM_ID));
			}
			return bean.getList(criteria);
		} catch (ManagerBeanException e) {
			String msg = "Imposible realizar el listado";
 			AonUtil.addErrorMessage(msg);
 			throw new AbortProcessingException(msg, e);
		} catch (ExpressionException e) {
			String msg = "Imposible realizar el listado";
 			AonUtil.addErrorMessage(msg);
 			throw new AbortProcessingException(msg, e);
		}
	}
	
	
}
