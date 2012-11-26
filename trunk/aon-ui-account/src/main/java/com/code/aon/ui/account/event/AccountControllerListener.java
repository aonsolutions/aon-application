package com.code.aon.ui.account.event;

import com.code.aon.account.Account;
import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.entity.IEntityAlias;
import com.code.aon.ui.account.controller.AccountController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AccountControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Account account = (Account) event.getController().getTo();
		String id = account.getCode();
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
			AccountController ac = (AccountController) event.getController();
			if ( ac.getOrderAlias() == null ) {
					ac.getCriteria().addOrder( ac.getFieldName(IEntityAlias.ACCOUNT_CODE) );
			} else {
				ac.getCriteria().addOrder( ac.getOrderAlias() );
			}
		} catch (ManagerBeanException e) {
			// no habrá orden en la select.
		}
	}
}
