package com.code.aon.supplier.event;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.supplier.ItemSupplier;
import com.code.aon.supplier.dao.ISupplierAlias;

public class ItemSupplierBeanListener extends ManagerBeanListenerAdapter {

	private boolean updating = false;

	@Override
	public void beanInserted(ManagerBeanEvent event) throws ManagerBeanException {
		ItemSupplier supItem = (ItemSupplier)event.getTo();
		IManagerBean supItemBean = BeanManager.getManagerBean(ItemSupplier.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(supItemBean.getFieldName(ISupplierAlias.ITEM_SUPPLIER_ITEM_ID), supItem.getItem().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(supItemBean.getFieldName(ISupplierAlias.ITEM_SUPPLIER_ID), supItem.getId()));
		criteria.addGreaterThanOrEqualExpression(supItemBean.getFieldName(ISupplierAlias.ITEM_SUPPLIER_PRIORITY), supItem.getPriority());
		criteria.addOrder(supItemBean.getFieldName(ISupplierAlias.ITEM_SUPPLIER_PRIORITY));
		List<ITransferObject> list = supItemBean.getList(criteria);
		int index = supItem.getPriority();
		for (ITransferObject to : list) {
			ItemSupplier itemSupplier = (ItemSupplier)to;
			if (index == itemSupplier.getPriority()) {
				itemSupplier.setPriority(index + 1);
				supItemBean.update(itemSupplier);
				++index;
			}
		}
	}
	
	@Override
	public void beanUpdated(ManagerBeanEvent event) throws ManagerBeanException {
		if (!updating) {
			updating = true;

			ItemSupplier supItem = (ItemSupplier)event.getTo();
			IManagerBean supItemBean = BeanManager.getManagerBean(ItemSupplier.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(supItemBean.getFieldName(ISupplierAlias.ITEM_SUPPLIER_ITEM_ID), supItem.getItem().getId());
			criteria.addExpression(ExpressionUtilities.getNotEqualExpression(supItemBean.getFieldName(ISupplierAlias.ITEM_SUPPLIER_ID), supItem.getId()));
			criteria.addEqualExpression(supItemBean.getFieldName(ISupplierAlias.ITEM_SUPPLIER_PRIORITY), supItem.getPriority());
			if (supItemBean.getCount(criteria) > 0) {
				criteria = new Criteria();
				criteria.addEqualExpression(supItemBean.getFieldName(ISupplierAlias.ITEM_SUPPLIER_ITEM_ID), supItem.getItem().getId());
				criteria.addExpression(ExpressionUtilities.getNotEqualExpression(supItemBean.getFieldName(ISupplierAlias.ITEM_SUPPLIER_ID), supItem.getId()));
				criteria.addOrder(supItemBean.getFieldName(ISupplierAlias.ITEM_SUPPLIER_PRIORITY));
				List<ITransferObject> list = supItemBean.getList(criteria);
				int index = 1;
				for (ITransferObject to : list) {
					ItemSupplier itemSupplier = (ItemSupplier)to;
					if (index == supItem.getPriority()) {
						++index;
					}
					itemSupplier.setPriority(index);
					supItemBean.update(itemSupplier);
					++index;
				}
			}

			updating = false;
		}
	}

	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		ItemSupplier supItem = (ItemSupplier)evt.getTo();
		IManagerBean supItemBean = BeanManager.getManagerBean(ItemSupplier.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(supItemBean.getFieldName(ISupplierAlias.ITEM_SUPPLIER_ITEM_ID), supItem.getItem().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(supItemBean.getFieldName(ISupplierAlias.ITEM_SUPPLIER_ID), supItem.getId()));
		criteria.addGreaterThanOrEqualExpression(supItemBean.getFieldName(ISupplierAlias.ITEM_SUPPLIER_PRIORITY), supItem.getPriority());
		criteria.addOrder(supItemBean.getFieldName(ISupplierAlias.ITEM_SUPPLIER_PRIORITY));
		List<ITransferObject> list = supItemBean.getList(criteria);
		int index = supItem.getPriority() + 1;
		for (ITransferObject to : list) {
			ItemSupplier itemSupplier = (ItemSupplier)to;
			if (index == itemSupplier.getPriority()) {
				itemSupplier.setPriority(index - 1);
				supItemBean.update(itemSupplier);
				++ index;
			}
		}
	}

}
