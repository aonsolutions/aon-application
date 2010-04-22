package com.code.aon.ui.academy.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AlumnInvoiceControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), false);
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IFinanceAlias.INVOICE_SERIES));
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IFinanceAlias.INVOICE_NUMBER));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

}