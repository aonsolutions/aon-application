package com.code.aon.account.bridge.event;

import java.util.Iterator;

import com.code.aon.account.bridge.InvoiceTaxAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.ql.Criteria;

public class InvoiceTaxBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		InvoiceTax invoiceTax = (InvoiceTax) evt.getTo();
		try {
			removeInvoiceTaxAccount(invoiceTax);
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	private void removeInvoiceTaxAccount(InvoiceTax invoiceTax) throws ManagerBeanException {
		IManagerBean invoiceAccountBean = BeanManager.getManagerBean(InvoiceTaxAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceAccountBean.getFieldName(IAccountBridgeAlias.INVOICE_TAX_ACCOUNT_INVOICE_TAX_ID), invoiceTax.getId());
		Iterator<?> iterator = invoiceAccountBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			InvoiceTaxAccount invoiceTaxAccount = (InvoiceTaxAccount) iterator.next();
			invoiceAccountBean.remove(invoiceTaxAccount);
		}
	}
	
}