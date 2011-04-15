package com.code.aon.finance.event;

import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.ql.Criteria;

public class InvoiceBeanListener extends ManagerBeanListenerAdapter {
	
	@Override
	@SuppressWarnings("unchecked")
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
		Invoice invoice = (Invoice) evt.getTo();
		if (invoice.isUpdateEnabled()) {
			if (InvoiceType.SALES == invoice.getType() || InvoiceType.PURCHASE == invoice.getType()) {
				IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
				Iterator iter = invoiceDetailBean.getList(criteria).iterator();
				while(iter.hasNext()){
					InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
					if(!invoiceDetail.getSource().equals(InvoiceSource.ACCOUNT)){
						invoiceDetail.getInvoice().setUpdateEnabled(false);
						invoiceDetailBean.update(invoiceDetail);
					}
				}
			}

			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID), invoice.getId());
			Iterator iter = financeBean.getList(criteria).iterator();
			while(iter.hasNext()){
				Finance finance = (Finance)iter.next();
				finance.setRegistry(invoice.getRegistry());
				if (finance.getFinanceStatus() == FinanceStatus.PENDING || finance.getFinanceStatus() == FinanceStatus.RETURNED) {
					finance.setRegistryName(invoice.getRegistryName());
					finance.setRegistryDocument(invoice.getRegistryDocument());
					finance.setRegistryDocumentType(invoice.getRegistryDocumentType());
					finance.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry());
				}
				finance.setConcept(invoice.getDocumentNumber());
				finance.setSecurityLevel(invoice.getSecurityLevel());
				financeBean.update(finance);
			}

			updateTotals(invoice);
		}
	}

	private void updateTotals(Invoice invoice) throws ManagerBeanException {
		if (invoice.isUpdateEnabled()) {
			InvoicePriceStrategy priceStrategy = new InvoicePriceStrategy();
			double taxableBase = priceStrategy.getCalculatedTaxableBase(invoice);
			double vatQuota = priceStrategy.getCalculatedTotalVatQuota(invoice, invoice);
			double retentionQuota = CommonUtil.round(0 - priceStrategy.getCalculatedTotalRetentionQuota(invoice, invoice));

			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			invoice.setUpdateEnabled(false);
			invoice.setTaxableBase(taxableBase);
			invoice.setVatQuota(vatQuota);
			invoice.setRetentionQuota(retentionQuota);
			invoice.setTotal(CommonUtil.round(taxableBase + vatQuota - retentionQuota));
			invoiceBean.update(invoice);
			invoice.setUpdateEnabled(true);
		}
	}

}
