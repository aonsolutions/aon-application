package com.code.aon.finance.event;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IRegistry;
import com.esferalia.aon.entity.IEntityAlias;

public class FinanceRegistryBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		IRegistry iregistry = (IRegistry)evt.getTo();
		try {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_REGISTRY_ID), iregistry.getRegistry().getId());
			if (invoiceBean.getList(criteria).size() > 0) {
				throw new ManagerBeanVetoListenerException("Imposible borrar registro. Tiene facturas asociadas.");
			}

			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			criteria = new Criteria();
			criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_REGISTRY_ID), iregistry.getRegistry().getId());
			if (financeBean.getList(criteria).size() > 0) {
				throw new ManagerBeanVetoListenerException("Imposible borrar registro. Tiene cobros/pagos asociados.");
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}
}