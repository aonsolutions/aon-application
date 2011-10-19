package com.code.aon.warehouse.event;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.warehouse.IStockable;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class StockableBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		try {
			IStockable stockable = (IStockable)evt.getTo();
			updateStockable(stockable);
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

    @SuppressWarnings("unchecked")
	private void updateStockable(IStockable stockable) throws ManagerBeanException {
		String select = "select stockable.item item, stockable.warehouse warehouse, stockable.quantity quantity " +
						" from " + stockable.getTableName() + " as stockable " +
						" where stockable.id = " + stockable.getId();
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		SQLQuery query = session.createSQLQuery(select);
        List list = query
        	.addScalar("item", Hibernate.INTEGER)
        	.addScalar("warehouse", Hibernate.INTEGER)
        	.addScalar("quantity", Hibernate.DOUBLE)
        	.list();
        Iterator iterator = list.iterator();
        if (iterator.hasNext()) {
        	Object[] obj = (Object[])iterator.next();
            Integer itemId = (Integer)obj[0];
        	Integer warehouseId = (Integer)obj[1];
            double quantity =(Double)obj[2];

     		updateStock(obtainItem(itemId), obtainWarehouse(warehouseId), quantity, !stockable.isEntry());
        }
	}

    private Item obtainItem(Integer itemId) throws ManagerBeanException {
    	IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
    	return (itemId != null) ? (Item)itemBean.get(itemId) : null;
    }

    private Warehouse obtainWarehouse(Integer warehouseId) throws ManagerBeanException {
    	IManagerBean warehouseBean = BeanManager.getManagerBean(Warehouse.class);
    	return (warehouseId != null) ? (Warehouse)warehouseBean.get(warehouseId) : null;
    }

    private void updateStock(Item item, Warehouse warehouse, double quantity, boolean entry) throws ManagerBeanException {
		if (item != null && item.getId() != null && item.getProduct().isInventoriable() && warehouse != null && warehouse.getId() != null) {
			IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
			Stock stock = obtainStock(item, warehouse);
			quantity = quantity * ((entry) ? 1 : (-1));
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

	private Stock obtainStock(Item item, Warehouse warehouse) throws ManagerBeanException {
		IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_ITEM_ID), item.getId());
		criteria.addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_WAREHOUSE_ID), warehouse.getId());
		Iterator<?> iterator = stockBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (Stock)iterator.next();
		}
		return null;
	}

}