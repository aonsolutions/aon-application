package com.code.aon.ui.finance.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IRegistry;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class InvoiceRegistryVetoListener extends ControllerAdapter {

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		IRegistry iregistry = (IRegistry)event.getController().getTo();
		try {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_REGISTRY_ID), iregistry.getRegistry().getId());
			if (invoiceBean.getList(criteria).size() > 0) {
				throw new ControllerListenerException("Imposible borrar registro. Tiene facturas asociadas.");
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}
}