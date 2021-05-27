package com.code.aon.warehouse.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.product.Item;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.warehouse.Stock;
import com.esferalia.aon.entity.IEntityAlias;

public class StockBeanListener extends ManagerBeanListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
		Stock stock = (Stock)evt.getTo();
		if (stock.getItem().getProduct().isSerializable()) {
			updateSerializableItemStatus(stock.getItem());
		}
	}

	@Override
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
		Stock stock = (Stock)evt.getTo();
		if (stock.getItem().getProduct().isSerializable()) {
			updateSerializableItemStatus(stock.getItem());
		}
	}

	private void updateSerializableItemStatus(Item item) throws ManagerBeanException {
		boolean hasStock = hasStock(item);
		if (!hasStock && item.isActive()) {
			item.setStatus(ProductStatus.DISCONTINUED);
			BeanManager.getManagerBean(Item.class).update(item);
		} else if (hasStock && !item.isActive()) {
			item.setStatus(ProductStatus.ACTIVE);
			BeanManager.getManagerBean(Item.class).update(item);
		}
	}

	private boolean hasStock(Item item) throws ManagerBeanException {
		IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(stockBean.getFieldName(IEntityAlias.STOCK_ITEM_ID), item.getId());
		criteria.addNotEqualExpression(stockBean.getFieldName(IEntityAlias.STOCK_QUANTITY), Double.valueOf(0));
		return stockBean.getCount(criteria) > 0;
	}

}