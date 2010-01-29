package com.code.aon.ui.account.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.account.controller.AccountController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AccountControllerListener extends ControllerAdapter {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AccountControllerListener.class);

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Account account = (Account) event.getController().getTo();
		String id = account.getId();
		if (id.length() > 4) {
			id = id.substring(0,3);
			if ("40".equals(id) || "41".equals(id) || "42".equals(id) || "43".equals(id)) {
				throw new ControllerListenerException("Las cuentas de clientes, proveedores y acreedores se deben crear desde los mantenimientos correspondientes.");
			}
		}
	}

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			AccountController controller = (AccountController) event.getController();
			if (controller.getOrderColumn() != null) {
				controller.getCriteria().addOrder(controller.getFieldName( controller.getOrderColumn() ));
			}
		} catch (ManagerBeanException e) {
			LOGGER.warn("Imposible Añadir Orden.",e);
		}
	}
}
