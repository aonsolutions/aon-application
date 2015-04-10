package com.code.aon.warehouse.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.product.Item;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.warehouse.Stock;

public class StockBeanListener extends ManagerBeanListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
		Stock stock = (Stock)evt.getTo();
		if (stock.getItem().getProduct().isSerializable()) {
			updateSerializableItemStatus(stock.getItem());
		}
	}

	private void updateSerializableItemStatus(Item item) throws ManagerBeanException {
		double stock = item.getStock();
		if (stock == 0 && item.isActive()) {
			item.setStatus(ProductStatus.DISCONTINUED);
			BeanManager.getManagerBean(Item.class).update(item);
		} else if (stock != 0 && !item.isActive()) {
			item.setStatus(ProductStatus.ACTIVE);
			BeanManager.getManagerBean(Item.class).update(item);
		}
	}

}