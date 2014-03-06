package com.code.aon.ui.product.event;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

public class ItemControllerActiveListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			IController controller = event.getController();
			controller.getCriteria().addEqualExpression(controller.getFieldName(IEntityAlias.ITEM_STATUS), ProductStatus.ACTIVE);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
}
