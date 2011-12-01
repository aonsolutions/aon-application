package com.code.aon.warehouse.event;

import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.ql.Criteria;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.WarehouseTransferDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class WarehouseTransferDetailBeanListener extends ManagerBeanListenerAdapter {

	@Override
	public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
		WarehouseTransferDetail wtd = (WarehouseTransferDetail) evt.getTo();
		Warehouse source = wtd.getWarehouseTransfer().getSourceWarehouse();
		if (source != null) {
			updateStock(wtd, source,false);	
		}
		Warehouse target = wtd.getWarehouseTransfer().getTargetWarehouse();
		if (target  != null) {
			updateStock(wtd, target,true);	
		}
		
	}

	@Override
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
		WarehouseTransferDetail wtd = (WarehouseTransferDetail) evt.getTo();
		Warehouse source = wtd.getWarehouseTransfer().getSourceWarehouse();
		if (source != null) {
			updateStock(wtd, source,false);	
		}
		Warehouse target = wtd.getWarehouseTransfer().getTargetWarehouse();
		if (target  != null) {
			updateStock(wtd, target,true);	
		}
	}

	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		WarehouseTransferDetail wtd = (WarehouseTransferDetail) evt.getTo();
		Warehouse source = wtd.getWarehouseTransfer().getSourceWarehouse();
		if (source != null) {
			updateStock(wtd, source,true);	
		}
		Warehouse target = wtd.getWarehouseTransfer().getTargetWarehouse();
		if (target  != null) {
			updateStock(wtd, target,false);	
		}
	}

	private void updateStock(WarehouseTransferDetail wtd, Warehouse warehouse ,boolean entry) throws ManagerBeanException {
		IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
		if (wtd.getItem().getProduct().isInventoriable()) {
			Stock stock = obtainStock(wtd,warehouse);
			double quantity = wtd.getQuantity() * ((entry) ? 1 : (-1));
			if (stock == null) {
				stock = new Stock();
				stock.setItem(wtd.getItem());
				stock.setWarehouse(warehouse);
				stock.setQuantity(quantity);
				stockBean.insert(stock);
			} else{
				stock.setQuantity(stock.getQuantity().doubleValue() + quantity);

				stockBean.update(stock);
			}
		}
	}

	private Stock obtainStock(WarehouseTransferDetail wtd, Warehouse warehouse) throws ManagerBeanException {
		IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_ITEM_ID), wtd.getItem().getId());
		criteria.addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_WAREHOUSE_ID), warehouse.getId());
		Iterator<?> iterator = stockBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (Stock)iterator.next();
		}
		return null;
	}
	
}
