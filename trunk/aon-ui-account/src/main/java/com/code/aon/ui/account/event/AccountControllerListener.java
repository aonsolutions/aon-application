package com.code.aon.ui.account.event;

import com.code.aon.account.Account;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AccountControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Account account = (Account) event.getController().getTo();
		String id = account.getId();
		if (id.length() > 4) {
			id = id.substring(0,3);
			if ("400".equals(id) || "410".equals(id) || "430".equals(id)) {
				throw new ControllerListenerException("Las cuentas de clientes, proveedores y acreedores se deben crear desde los mantenimientos correspondientes.");
			}
		}
	}

}
