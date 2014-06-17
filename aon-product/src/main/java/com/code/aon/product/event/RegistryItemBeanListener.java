package com.code.aon.product.event;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.enumeration.RegistryMode;
import com.esferalia.aon.entity.IEntityAlias;

public class RegistryItemBeanListener extends ManagerBeanListenerAdapter {

	private boolean updating = false;

	@Override
	public void beanInserted(ManagerBeanEvent event) throws ManagerBeanException {
		RegistryItem rItem = (RegistryItem)event.getTo();
		if (rItem.isPurchaseType()) {
			IManagerBean rItemBean = BeanManager.getManagerBean(RegistryItem.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_ITEM_ID), rItem.getItem().getId());
			criteria.addNotEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_ID), rItem.getId());
			criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_TYPE), RegistryMode.SUPPLIER);
			criteria.addGreaterThanOrEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_PRIORITY), rItem.getPriority());
			criteria.addOrder(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_PRIORITY));
			List<ITransferObject> list = rItemBean.getList(criteria);
			int index = rItem.getPriority();
			for (ITransferObject to : list) {
				RegistryItem registryItem = (RegistryItem)to;
				if (index == registryItem.getPriority()) {
					registryItem.setPriority(index + 1);
					rItemBean.update(registryItem);
					++index;
				}
			}
		}
	}
	
	@Override
	public void beanUpdated(ManagerBeanEvent event) throws ManagerBeanException {
		if (!updating) {
			updating = true;

			RegistryItem rItem = (RegistryItem)event.getTo();
			if (rItem.isPurchaseType()) {
				IManagerBean rItemBean = BeanManager.getManagerBean(RegistryItem.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_ITEM_ID), rItem.getItem().getId());
				criteria.addNotEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_ID), rItem.getId());
				criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_TYPE), RegistryMode.SUPPLIER);
				criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_PRIORITY), rItem.getPriority());
				if (rItemBean.getCount(criteria) > 0) {
					criteria = new Criteria();
					criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_ITEM_ID), rItem.getItem().getId());
					criteria.addNotEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_ID), rItem.getId());
					criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_TYPE), RegistryMode.SUPPLIER);
					criteria.addOrder(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_PRIORITY));
					List<ITransferObject> list = rItemBean.getList(criteria);
					int index = 0;
					for (ITransferObject to : list) {
						RegistryItem registryItem = (RegistryItem)to;
						if (index == rItem.getPriority()) {
							++index;
						}
						if (registryItem.getPriority() != index) {
							registryItem.setPriority(index);
							rItemBean.update(registryItem);
						}
						++index;
					}
				}
			}

			updating = false;
		}
	}

	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		RegistryItem rItem = (RegistryItem)evt.getTo();
		if (rItem.isPurchaseType()) {
			IManagerBean rItemBean = BeanManager.getManagerBean(RegistryItem.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_ITEM_ID), rItem.getItem().getId());
			criteria.addNotEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_ID), rItem.getId());
			criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_TYPE), RegistryMode.SUPPLIER);
			criteria.addGreaterThanOrEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_PRIORITY), rItem.getPriority());
			criteria.addOrder(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_PRIORITY));
			List<ITransferObject> list = rItemBean.getList(criteria);
			int index = rItem.getPriority() + 1;
			for (ITransferObject to : list) {
				RegistryItem registryItem = (RegistryItem)to;
				if (index == registryItem.getPriority()) {
					registryItem.setPriority(index - 1);
					rItemBean.update(registryItem);
					++ index;
				}
			}
		}
	}

}
