package com.code.aon.ui.product.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ItemPricesControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			IController controller = event.getController();
			controller.getCriteria().addOrder(controller.getManagerBean().getFieldName(IProductAlias.ITEM_PRODUCT_NAME));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
}
