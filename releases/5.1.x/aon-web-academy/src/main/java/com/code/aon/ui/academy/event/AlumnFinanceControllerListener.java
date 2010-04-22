package com.code.aon.ui.academy.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AlumnFinanceControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IFinanceAlias.FINANCE_DUE_DATE), false);
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IFinanceAlias.FINANCE_INVOICE_SERIES));
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IFinanceAlias.FINANCE_INVOICE_NUMBER));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

}