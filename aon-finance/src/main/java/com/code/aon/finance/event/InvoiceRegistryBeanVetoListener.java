package com.code.aon.finance.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IRegistry;

public class InvoiceRegistryBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		IRegistry iregistry = (IRegistry)evt.getTo();
		try {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_REGISTRY_ID), iregistry.getRegistry().getId());
			if (invoiceBean.getList(criteria).size() > 0) {
				throw new ManagerBeanVetoListenerException("Imposible borrar registro. Tiene facturas asociadas.");
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}
}