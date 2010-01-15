package com.code.aon.account.bridge.event;

import java.util.Iterator;

import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.ql.Criteria;

public class InvoiceDetailBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail) evt.getTo();
		try {
			removeInvoiceDetailAccount(invoiceDetail);
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	private void removeInvoiceDetailAccount(InvoiceDetail invoiceDetail) throws ManagerBeanException {
		IManagerBean invoiceAccountBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
		Criteria criteria = new Criteria();
		criteria
				.addEqualExpression(
						invoiceAccountBean
								.getFieldName(IAccountBridgeAlias.INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL_ID),
						invoiceDetail.getId());
		Iterator<?> iterator = invoiceAccountBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			InvoiceDetailAccount invoiceDetailAccount = (InvoiceDetailAccount) iterator.next();
			invoiceAccountBean.remove(invoiceDetailAccount);
		}
	}
	
}