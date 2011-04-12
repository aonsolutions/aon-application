package com.code.aon.finance.event;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.ql.Criteria;

public class InvoiceDetailBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceDetailBeanVetoListener.class.getName());

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)evt.getTo();
		if(!invoiceDetail.getSource().equals(InvoiceSource.ACCOUNT)){
			try {
				removeInvoiceTax(invoiceDetail);
			} catch (ManagerBeanException e) {
				LOGGER.error("Error removing invoiceTax for invoiceDetail with id= " + invoiceDetail.getId(), e);
			}
		}
	}

	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)evt.getTo();
		if(!invoiceDetail.getSource().equals(InvoiceSource.ACCOUNT)){
			try {
				removeInvoiceTax(invoiceDetail);
			} catch (ManagerBeanException e) {
				LOGGER.error("Error removing invoiceTax for invoiceDetail with id= " + invoiceDetail.getId(), e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void removeInvoiceTax(InvoiceDetail invoiceDetail) throws ManagerBeanException {
		IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IFinanceAlias.INVOICE_TAX_INVOICE_DETAIL_ID),invoiceDetail.getId());
		Iterator iter = invoiceTaxBean.getList(criteria).iterator();
		while(iter.hasNext()){
			invoiceTaxBean.remove((InvoiceTax)iter.next());
		}
	}
}
