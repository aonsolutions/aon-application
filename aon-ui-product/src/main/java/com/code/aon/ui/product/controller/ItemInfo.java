package com.code.aon.ui.product.controller;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.ItemAlternative;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.Warehouse;
import com.esferalia.aon.entity.IEntityAlias;

public class ItemInfo implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean panelVisible;
	private boolean stockVisible;
	private boolean alternativesVisible;

	private Item item;
	private Warehouse warehouse;
	private Double rowStock;
	private String componentId;
	private String alternativeMethod;

	private List<ITransferObject> stockList;
	private List<ITransferObject> alternativesList;
	private DataModel stockModel;
	private DataModel alternativesModel;
	
	public boolean isPanelVisible() {
		return panelVisible;
	}

	public void setPanelVisible(boolean panelVisible) {
		this.panelVisible = panelVisible;
	}

	public boolean isStockVisible() {
		return stockVisible;
	}

	public void setStockVisible(boolean stockVisible) {
		this.stockVisible = stockVisible;
	}

	public boolean isAlternativesVisible() {
		return alternativesVisible;
	}

	public void setAlternativesVisible(boolean alternativesVisible) {
		this.alternativesVisible = alternativesVisible;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public Double getRowStock() {
		return rowStock;
	}

	public void setRowStock(Double rowStock) {
		this.rowStock = rowStock;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public String getAlternativeMethod() {
		return alternativeMethod;
	}

	public void setAlternativeMethod(String alternativeMethod) {
		this.alternativeMethod = alternativeMethod;
	}

	public String getElAlternativeMethod() {
		return ("#{" + alternativeMethod + "}");
	}

	public List<ITransferObject> getStockList() {
		return stockList;
	}

	public void setStockList(List<ITransferObject> stockList) {
		this.stockList = stockList;
	}

	public List<ITransferObject> getAlternativesList() {
		return alternativesList;
	}

	public void setAlternativesList(List<ITransferObject> alternativesList) {
		this.alternativesList = alternativesList;
	}

	public DataModel getStockModel() {
		return stockModel;
	}

	public void setStockModel(DataModel model) {
		this.stockModel = model;
	}

	public DataModel getAlternativesModel() {
		return alternativesModel;
	}

	public void setAlternativesModel(DataModel model) {
		this.alternativesModel = model;
	}

	public void onShowPanel(ActionEvent event) {
		try {
			setPanelVisible(true);
			if (isStockVisible()) {
				loadStock();
			}
			if (isAlternativesVisible()) {
				loadAlternatives();
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public void onHidePanel(ActionEvent event) {
		setPanelVisible(false);
	}

	private void loadStock() throws ManagerBeanException {
		IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(stockBean.getFieldName(IEntityAlias.STOCK_ITEM_ID), item.getId());
		criteria.addOrder(stockBean.getFieldName(IEntityAlias.STOCK_WAREHOUSE_ID));
		setStockList(stockBean.getList(criteria)); 
		setStockModel(new SerializableListDataModel(getStockList())); 
	}

	private void loadAlternatives() throws ManagerBeanException {
		IManagerBean altItemBean = BeanManager.getManagerBean(ItemAlternative.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(altItemBean.getFieldName(IEntityAlias.ITEM_ALTERNATIVE_ITEM_ID), item.getId());
		criteria.addOrder(altItemBean.getFieldName(IEntityAlias.ITEM_ALTERNATIVE_PRIORITY));
		setAlternativesList(altItemBean.getList(criteria)); 
		setAlternativesModel(new SerializableListDataModel(getAlternativesList()));
	}

	public Double getAlternativeStock() throws ManagerBeanException {
		ItemAlternative itemAlternative = (ItemAlternative)getAlternativesModel().getRowData();
		IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(stockBean.getFieldName(IEntityAlias.STOCK_ITEM_ID), itemAlternative.getAlternativeItem().getId());
		if (getWarehouse() != null) {
			criteria.addEqualExpression(stockBean.getFieldName(IEntityAlias.STOCK_WAREHOUSE_ID), warehouse.getId());
		}

		double quantity = 0;
		Iterator<?> iterator = stockBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			Stock stock = (Stock)iterator.next();
			quantity += stock.getQuantity();
		}
		return quantity;
	}

	public void onSelectAlternative(ActionEvent event) {
		ItemAlternative itemAlternative = (ItemAlternative)getAlternativesModel().getRowData();

		FacesContext ctx = FacesContext.getCurrentInstance();
		UIComponent component = getComponent(ctx.getViewRoot());
		LookupChangeEvent lookupEvent = new LookupChangeEvent(component, itemAlternative.getAlternativeItem());

		ELContext elctx = ctx.getELContext();
		ExpressionFactory factory = ctx.getApplication().getExpressionFactory();
		MethodExpression me = factory.createMethodExpression(elctx, getElAlternativeMethod(), Object.class, new Class[] { LookupChangeEvent.class });
		me.invoke(elctx, new Object[] { lookupEvent });

		setPanelVisible(false);
	}

	private UIComponent getComponent(UIComponent component) {
		UIComponent comp = component.findComponent(getComponentId());
		if (comp == null) {
			for(UIComponent c : component.getChildren()) {
				comp = getComponent(c);
				if (comp != null) {
					break;
				}
			}
		}
		return comp;
	}

}
