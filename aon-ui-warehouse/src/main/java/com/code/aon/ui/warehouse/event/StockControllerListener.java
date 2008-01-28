package com.code.aon.ui.warehouse.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.warehouse.controller.StockController;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class StockControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			StockController stockController = (StockController)event.getController();
			stockController.setSourceWarehouseId(null);
			stockController.setTargetWarehouseId(null);
			stockController.setMovingQuantity(0.0);
			stockController.setMovingStock((Stock)event.getController().getTo());
			stockController.loadAvailableSourceWarehouses();
			event.getController().clearCriteria();
			event.getController().getCriteria().addEqualExpression(event.getController().getFieldName(IWarehouseAlias.STOCK_ITEM_ID), stockController.getMovingStock().getItem().getId());
			event.getController().onSearch(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
			event.getController().getCriteria().addOrder(stockBean.getFieldName(IWarehouseAlias.STOCK_WAREHOUSE_ID));
			event.getController().getCriteria().addOrder(stockBean.getFieldName(IWarehouseAlias.STOCK_ITEM_ID));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
}
