package com.code.aon.warehouse.event;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.WarehouseTransferDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class WarehouseTransferDetailBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		try {
			WarehouseTransferDetail wtd = (WarehouseTransferDetail) evt.getTo();
			updateStockable(wtd.getId());
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}	
	}

	private void updateStockable(Integer id) throws ManagerBeanException {
		String select = "SELECT wtd.item item, wt.source_warehouse source, wt.target_warehouse target, wtd.quantity quantity " 
						+ " FROM warehouse_transfer_detail as wtd " 
						+ " INNER JOIN warehouse_transfer as wt ON wtd.warehouse_transfer = wt.id"
						+ " WHERE wtd.id = " + id;
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		SQLQuery query = session.createSQLQuery(select);
        List<?> list = query
        	.addScalar("item", Hibernate.INTEGER)
        	.addScalar("source", Hibernate.INTEGER)
        	.addScalar("target", Hibernate.INTEGER)
        	.addScalar("quantity", Hibernate.DOUBLE)
        	.list();
        Iterator<?> iterator = list.iterator();
        if (iterator.hasNext()) {
        	Object[] obj = (Object[])iterator.next();
            Integer itemId = (Integer)obj[0];
        	Integer sourceId = (Integer)obj[1];
        	Integer targetId = (Integer)obj[2];
            double quantity =(Double)obj[3];
            if (sourceId != null) {
            	updateStock(obtainItem(itemId), obtainWarehouse(sourceId), quantity, true);	
            }
            if (targetId != null) {
            	updateStock(obtainItem(itemId), obtainWarehouse(targetId), quantity, false);	
            }
        }
	}

    private Item obtainItem(Integer itemId) throws ManagerBeanException {
    	IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
    	return (Item)itemBean.get(itemId);
    }

    private Warehouse obtainWarehouse(Integer warehouseId) throws ManagerBeanException {
    	IManagerBean warehouseBean = BeanManager.getManagerBean(Warehouse.class);
    	return (Warehouse)warehouseBean.get(warehouseId);
    }

    private void updateStock(Item item, Warehouse warehouse, double quantity, boolean entry) throws ManagerBeanException {
		IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
		if (item.getProduct().isInventoriable()) {
			Stock stock = obtainStock(item, warehouse);
			quantity = quantity * ((entry) ? 1 : (-1));
			if (stock == null) {
				stock = new Stock();
				stock.setItem(item);
				stock.setWarehouse(warehouse);
				stock.setQuantity(quantity);

				stockBean.insert(stock);
			} else{
				stock.setQuantity(stock.getQuantity().doubleValue() + quantity);

				stockBean.update(stock);
			}
		}
	}

	private Stock obtainStock(Item item, Warehouse warehouse) throws ManagerBeanException {
		IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(stockBean.getFieldName(IEntityAlias.STOCK_ITEM_ID), item.getId());
		criteria.addEqualExpression(stockBean.getFieldName(IEntityAlias.STOCK_WAREHOUSE_ID), warehouse.getId());
		Iterator<?> iterator = stockBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (Stock)iterator.next();
		}
		return null;
	}

}