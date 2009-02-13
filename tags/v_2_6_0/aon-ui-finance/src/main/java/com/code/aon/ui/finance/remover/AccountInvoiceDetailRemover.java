package com.code.aon.ui.finance.remover;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;

public class AccountInvoiceDetailRemover implements IInvoiceDetailRemover {
	
	private static final Logger LOGGER = Logger.getLogger(AccountInvoiceDetailRemover.class.getName());

	@Override
	public void removeDetail(InvoiceDetail invoiceDetail) {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			removeInvoiceTaxes(invoiceDetail);
			invoiceDetailBean.remove(invoiceDetail);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Error removing Details");
			LOGGER.log(Level.SEVERE, "Error removing Details", e);
			throw new AbortProcessingException(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private void removeInvoiceTaxes(InvoiceDetail invoiceDetail) throws ManagerBeanException {
		IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
		Criteria criteria =  new Criteria();
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IFinanceAlias.INVOICE_TAX_INVOICE_DETAIL_ID), invoiceDetail.getId());
		Iterator iter = invoiceTaxBean.getList(criteria).iterator();
		while(iter.hasNext()){
			invoiceTaxBean.remove((InvoiceTax)iter.next());
		}
	}
}