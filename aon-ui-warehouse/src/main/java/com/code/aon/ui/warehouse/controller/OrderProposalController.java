package com.code.aon.ui.warehouse.controller;

import java.util.Collection;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.warehouse.ItemWarehouse;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class OrderProposalController implements ICollectionProvider{

	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection() {
		try {
			return getCollection(false);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ItemWarehouse.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(IWarehouseAlias.ITEM_WAREHOUSE_ITEM_PRODUCT_NAME));
		return bean.getList(null);
	}

}
