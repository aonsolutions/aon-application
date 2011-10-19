package com.code.aon.warehouse.event;

import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.warehouse.IStockable;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class StockableBeanListener extends ManagerBeanListenerAdapter {
	
	@Override
	public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
		IStockable stockable = (IStockable)evt.getTo();
		updateStock(stockable, stockable.isEntry());
	}

	@Override
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
		IStockable stockable = (IStockable)evt.getTo();
		updateStock(stockable, stockable.isEntry());
	}

	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		IStockable stockable = (IStockable)evt.getTo();
		updateStock(stockable, !stockable.isEntry());
	}

	private void updateStock(IStockable stockable, boolean entry) throws ManagerBeanException {
		Item item = stockable.getItem();
		Warehouse warehouse = stockable.getWarehouse();
		if (item != null && item.getId() != null && item.getProduct().isInventoriable() && warehouse != null && warehouse.getId() != null) {
			IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
			Stock stock = obtainStock(stockable);
			double quantity = stockable.getQuantity() * ((entry) ? 1 : (-1));
			if (stock == null) {
				stock = new Stock();
				stock.setItem(item);
				stock.setWarehouse(warehouse);
				stock.setQuantity(CommonUtil.round(quantity, 3));
				stockBean.insert(stock);
			} else{
				stock.setQuantity(CommonUtil.round(stock.getQuantity().doubleValue() + quantity, 3));
				stockBean.update(stock);
			}
		}
	}

	private Stock obtainStock(IStockable stockable) throws ManagerBeanException {
		IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_ITEM_ID), stockable.getItem().getId());
		criteria.addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_WAREHOUSE_ID), stockable.getWarehouse().getId());
		Iterator<?> iterator = stockBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (Stock)iterator.next();
		}
		return null;
	}
	
}