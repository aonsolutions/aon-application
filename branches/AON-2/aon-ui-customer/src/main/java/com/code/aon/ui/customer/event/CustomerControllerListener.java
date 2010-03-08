package com.code.aon.ui.customer.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener added to CustomerController.
 */
public class CustomerControllerListener extends ControllerAdapter{

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		Criteria criteria;
		try {
			criteria = event.getController().getCriteria();
			criteria.addOrder(event.getController().getManagerBean().getFieldName(ICustomerAlias.CUSTOMER_REGISTRY_SURNAME));
			criteria.addOrder(event.getController().getManagerBean().getFieldName(ICustomerAlias.CUSTOMER_REGISTRY_NAME));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
}