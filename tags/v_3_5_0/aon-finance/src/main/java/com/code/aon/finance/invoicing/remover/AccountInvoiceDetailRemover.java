package com.code.aon.finance.invoicing.remover;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.ql.Criteria;

public class AccountInvoiceDetailRemover implements IInvoiceDetailRemover {
	
	private static final Logger LOGGER = Logger.getLogger(AccountInvoiceDetailRemover.class.getName());

	@Override
	public boolean accept(InvoiceSource source) {
		return source.equals(InvoiceSource.ACCOUNT);
	}

	@Override
	public void removeDetail(InvoiceDetail invoiceDetail) throws InvoicingException{
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			removeInvoiceTaxes(invoiceDetail);
			LOGGER.fine("Attempt to remove invoice detail: " + invoiceDetail.getId());
			invoiceDetailBean.remove(invoiceDetail);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error removing Details", e);
			throw new InvoicingException(e.getMessage(),e);
		}
	}

	@SuppressWarnings("unchecked")
	private void removeInvoiceTaxes(InvoiceDetail invoiceDetail) throws ManagerBeanException {
		IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
		Criteria criteria =  new Criteria();
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IFinanceAlias.INVOICE_TAX_INVOICE_DETAIL_ID), invoiceDetail.getId());
		Iterator iter = invoiceTaxBean.getList(criteria).iterator();
		while(iter.hasNext()){
			InvoiceTax it = (InvoiceTax)iter.next();
			LOGGER.fine("\tAttempt to remove invoice tax: " + it.getId());
			invoiceTaxBean.remove(it);
		}
	}

}