package com.code.aon.product.event;

import java.util.List;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.product.ItemComposition;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class ItemCompositionBeanListener extends ManagerBeanListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean updating = false;

	@Override
	public void beanInserted(ManagerBeanEvent event) throws ManagerBeanException {
		ItemComposition cmpItem = (ItemComposition)event.getTo();
		IManagerBean cmpItemBean = BeanManager.getManagerBean(ItemComposition.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(cmpItemBean.getFieldName(IEntityAlias.ITEM_COMPOSITION_ITEM_ID), cmpItem.getItem().getId());
		criteria.addNotEqualExpression(cmpItemBean.getFieldName(IEntityAlias.ITEM_COMPOSITION_ID), cmpItem.getId());
		criteria.addGreaterThanOrEqualExpression(cmpItemBean.getFieldName(IEntityAlias.ITEM_COMPOSITION_SEQUENCE), cmpItem.getSequence());
		criteria.addOrder(cmpItemBean.getFieldName(IEntityAlias.ITEM_COMPOSITION_SEQUENCE));
		List<ITransferObject> list = cmpItemBean.getList(criteria);
		int index = cmpItem.getSequence();
		for (ITransferObject to : list) {
			ItemComposition itemComposition = (ItemComposition)to;
			if (index == itemComposition.getSequence()) {
				itemComposition.setSequence(index + 1);
				cmpItemBean.update(itemComposition);
				++index;
			}
		}
	}
	
	@Override
	public void beanUpdated(ManagerBeanEvent event) throws ManagerBeanException {
		if (!updating) {
			updating = true;

			ItemComposition cmpItem = (ItemComposition)event.getTo();
			IManagerBean cmpItemBean = BeanManager.getManagerBean(ItemComposition.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(cmpItemBean.getFieldName(IEntityAlias.ITEM_COMPOSITION_ITEM_ID), cmpItem.getItem().getId());
			criteria.addNotEqualExpression(cmpItemBean.getFieldName(IEntityAlias.ITEM_COMPOSITION_ID), cmpItem.getId());
			criteria.addEqualExpression(cmpItemBean.getFieldName(IEntityAlias.ITEM_COMPOSITION_SEQUENCE), cmpItem.getSequence());
			if (cmpItemBean.getCount(criteria) > 0) {
				criteria = new Criteria();
				criteria.addEqualExpression(cmpItemBean.getFieldName(IEntityAlias.ITEM_COMPOSITION_ITEM_ID), cmpItem.getItem().getId());
				criteria.addNotEqualExpression(cmpItemBean.getFieldName(IEntityAlias.ITEM_COMPOSITION_ID), cmpItem.getId());
				criteria.addOrder(cmpItemBean.getFieldName(IEntityAlias.ITEM_COMPOSITION_SEQUENCE));
				List<ITransferObject> list = cmpItemBean.getList(criteria);
				int index = 1;
				for (ITransferObject to : list) {
					ItemComposition itemComposition = (ItemComposition)to;
					if (index == cmpItem.getSequence()) {
						++index;
					}
					itemComposition.setSequence(index);
					cmpItemBean.update(itemComposition);
					++index;
				}
			}

			updating = false;
		}
	}

	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		ItemComposition cmpItem = (ItemComposition)evt.getTo();
		IManagerBean cmpItemBean = BeanManager.getManagerBean(ItemComposition.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(cmpItemBean.getFieldName(IEntityAlias.ITEM_COMPOSITION_ITEM_ID), cmpItem.getItem().getId());
		criteria.addNotEqualExpression(cmpItemBean.getFieldName(IEntityAlias.ITEM_COMPOSITION_ID), cmpItem.getId());
		criteria.addGreaterThanOrEqualExpression(cmpItemBean.getFieldName(IEntityAlias.ITEM_COMPOSITION_SEQUENCE), cmpItem.getSequence());
		criteria.addOrder(cmpItemBean.getFieldName(IEntityAlias.ITEM_COMPOSITION_SEQUENCE));
		List<ITransferObject> list = cmpItemBean.getList(criteria);
		int index = cmpItem.getSequence() + 1;
		for (ITransferObject to : list) {
			ItemComposition itemComposition = (ItemComposition)to;
			if (index == itemComposition.getSequence()) {
				itemComposition.setSequence(index - 1);
				cmpItemBean.update(itemComposition);
				++ index;
			}
		}
	}

}
