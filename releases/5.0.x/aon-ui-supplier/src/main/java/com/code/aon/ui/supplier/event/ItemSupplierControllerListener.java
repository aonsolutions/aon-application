package com.code.aon.ui.supplier.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.supplier.ItemSupplier;
import com.code.aon.supplier.dao.ISupplierAlias;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.supplier.controller.ItemSupplierController;

public class ItemSupplierControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ItemSupplierController controller = (ItemSupplierController)event.getController();
		ItemSupplier itemSupplier = (ItemSupplier)controller.getTo();

		try {
			itemSupplier.setPriority(calculateNextPriority((Item)controller.getMasterController().getTo()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		event.getController().initializeModel();
	}

	private	Integer calculateNextPriority(Item item) throws ManagerBeanException {
		IManagerBean itemSupplierBean = BeanManager.getManagerBean(ItemSupplier.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemSupplierBean.getFieldName(ISupplierAlias.ITEM_SUPPLIER_ITEM_ID), item.getId());
		Projection projection = Projection.max(itemSupplierBean.getFieldName(ISupplierAlias.ITEM_SUPPLIER_PRIORITY));
		Object value = itemSupplierBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

}
