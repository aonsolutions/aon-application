package com.code.aon.ui.product.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.ItemAlternative;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.controller.ItemAlternativeController;

public class ItemAlternativeControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ItemAlternativeController controller = (ItemAlternativeController)event.getController();
		ItemAlternative itemAlternative = (ItemAlternative)controller.getTo();

		try {
			itemAlternative.setPriority(calculateNextPriority((Item)controller.getMasterController().getTo()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		event.getController().initializeModel();
	}

	private	Integer calculateNextPriority(Item item) throws ManagerBeanException {
		IManagerBean itemAlternativeBean = BeanManager.getManagerBean(ItemAlternative.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemAlternativeBean.getFieldName(IProductAlias.ITEM_ALTERNATIVE_ITEM_ID), item.getId());
		Projection projection = Projection.max(itemAlternativeBean.getFieldName(IProductAlias.ITEM_ALTERNATIVE_PRIORITY));
		Object value = itemAlternativeBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

}