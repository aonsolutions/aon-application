package com.code.aon.finance.event;

import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.ql.Criteria;

/**
 * The InvoiceBeanListener. Listener to be added to Invoice.class
 */
public class InvoiceBeanListener extends ManagerBeanListenerAdapter {
	
	/**
	 * Updates InvoiceDetailTaxes when an Invoice is updated.
	 * 
	 * @param evt the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
		Invoice invoice = (Invoice) evt.getTo();
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator iter = invoiceDetailBean.getList(criteria).iterator();
		while(iter.hasNext()){
			InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
			if(!invoiceDetail.getSource().equals(InvoiceSource.DIRECT_EXPENSE) && !invoiceDetail.getSource().equals(InvoiceSource.ACCOUNT)){
				invoiceDetailBean.update(invoiceDetail);
			}
		}
	}

}
