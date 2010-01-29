package com.code.aon.ui.purchase.event;

import java.util.List;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.purchase.ItemSupplier;
import com.code.aon.purchase.dao.IPurchaseAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ItemSupplierControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		IController controller = event.getController();
		ItemSupplier itemSupplier = (ItemSupplier) controller.getTo();
		try {
			int index = controller.getModel().getRowCount();
			itemSupplier.setPriority(index);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		IController controller = event.getController();
		try {
			Criteria criteria = controller.getCriteria();
			String field = controller.getFieldName(IPurchaseAlias.ITEM_SUPPLIER_PRIORITY);
			criteria.addOrder(field);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		IController controller = event.getController();
		ItemSupplier itemSupplier = (ItemSupplier) controller.getTo();
		try {
			List<ItemSupplier> list = (List<ItemSupplier>) controller.getModel().getWrappedData();
			for (int i = itemSupplier.getPriority() + 1; i < list.size(); i++) {
				ItemSupplier is = list.get(i);
				is.setPriority(i - 1);
				controller.getManagerBean().update(is);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}
