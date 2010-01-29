package com.code.aon.ui.warehouse.controller;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class ItemStock {
	private boolean panelVisible;
	private Item item;
	private List<ITransferObject> list;

	public boolean isPanelVisible() {
		return panelVisible;
	}

	public void setPanelVisible(boolean panelVisible) {
		this.panelVisible = panelVisible;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public List<ITransferObject> getList() {
		return list;
	}

	public void setList(List<ITransferObject> list) {
		this.list = list;
	}

	public void onShowPanel(ActionEvent event) {
		try {
			setPanelVisible(true);
			loadList();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public void onHidePanel(ActionEvent event) {
		setPanelVisible(false);
	}

	private void loadList() throws ManagerBeanException {
		if (getItem() == null) {
			setPanelVisible(true);
			String msg = AonUtil.getMessage("warehouseBundle", "warehouse_item_stock_error");
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_ITEM_ID), item
				.getId());
		setList(stockBean.getList(criteria)); 
	}
}
