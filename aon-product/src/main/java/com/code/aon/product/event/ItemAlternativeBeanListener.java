package com.code.aon.product.event;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.product.ItemAlternative;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;

public class ItemAlternativeBeanListener extends ManagerBeanListenerAdapter {

	private boolean updating = false;

	@Override
	public void beanInserted(ManagerBeanEvent event) throws ManagerBeanException {
		ItemAlternative altItem = (ItemAlternative)event.getTo();
		IManagerBean altItemBean = BeanManager.getManagerBean(ItemAlternative.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(altItemBean.getFieldName(IProductAlias.ITEM_ALTERNATIVE_ITEM_ID), altItem.getItem().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(altItemBean.getFieldName(IProductAlias.ITEM_ALTERNATIVE_ID), altItem.getId()));
		criteria.addGreaterThanOrEqualExpression(altItemBean.getFieldName(IProductAlias.ITEM_ALTERNATIVE_PRIORITY), altItem.getPriority());
		criteria.addOrder(altItemBean.getFieldName(IProductAlias.ITEM_ALTERNATIVE_PRIORITY));
		List<ITransferObject> list = altItemBean.getList(criteria);
		int index = altItem.getPriority();
		for (ITransferObject to : list) {
			ItemAlternative itemAlternative = (ItemAlternative)to;
			if (index == itemAlternative.getPriority()) {
				itemAlternative.setPriority(index + 1);
				altItemBean.update(itemAlternative);
				++index;
			}
		}
	}
	
	@Override
	public void beanUpdated(ManagerBeanEvent event) throws ManagerBeanException {
		if (!updating) {
			updating = true;

			ItemAlternative altItem = (ItemAlternative)event.getTo();
			IManagerBean altItemBean = BeanManager.getManagerBean(ItemAlternative.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(altItemBean.getFieldName(IProductAlias.ITEM_ALTERNATIVE_ITEM_ID), altItem.getItem().getId());
			criteria.addExpression(ExpressionUtilities.getNotEqualExpression(altItemBean.getFieldName(IProductAlias.ITEM_ALTERNATIVE_ID), altItem.getId()));
			criteria.addEqualExpression(altItemBean.getFieldName(IProductAlias.ITEM_ALTERNATIVE_PRIORITY), altItem.getPriority());
			if (altItemBean.getCount(criteria) > 0) {
				criteria = new Criteria();
				criteria.addEqualExpression(altItemBean.getFieldName(IProductAlias.ITEM_ALTERNATIVE_ITEM_ID), altItem.getItem().getId());
				criteria.addExpression(ExpressionUtilities.getNotEqualExpression(altItemBean.getFieldName(IProductAlias.ITEM_ALTERNATIVE_ID), altItem.getId()));
				criteria.addOrder(altItemBean.getFieldName(IProductAlias.ITEM_ALTERNATIVE_PRIORITY));
				List<ITransferObject> list = altItemBean.getList(criteria);
				int index = 1;
				for (ITransferObject to : list) {
					ItemAlternative itemAlternative = (ItemAlternative)to;
					if (index == altItem.getPriority()) {
						++index;
					}
					itemAlternative.setPriority(index);
					altItemBean.update(itemAlternative);
					++index;
				}
			}

			updating = false;
		}
	}

	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		ItemAlternative altItem = (ItemAlternative)evt.getTo();
		IManagerBean altItemBean = BeanManager.getManagerBean(ItemAlternative.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(altItemBean.getFieldName(IProductAlias.ITEM_ALTERNATIVE_ITEM_ID), altItem.getItem().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(altItemBean.getFieldName(IProductAlias.ITEM_ALTERNATIVE_ID), altItem.getId()));
		criteria.addGreaterThanOrEqualExpression(altItemBean.getFieldName(IProductAlias.ITEM_ALTERNATIVE_PRIORITY), altItem.getPriority());
		criteria.addOrder(altItemBean.getFieldName(IProductAlias.ITEM_ALTERNATIVE_PRIORITY));
		List<ITransferObject> list = altItemBean.getList(criteria);
		int index = altItem.getPriority() + 1;
		for (ITransferObject to : list) {
			ItemAlternative itemAlternative = (ItemAlternative)to;
			if (index == itemAlternative.getPriority()) {
				itemAlternative.setPriority(index - 1);
				altItemBean.update(itemAlternative);
				++ index;
			}
		}
	}

}
