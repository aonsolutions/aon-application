package com.code.aon.finance.event;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tax;
import com.code.aon.config.TaxDetail;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.finance.invoicing.remover.IInvoiceDetailRemover;
import com.code.aon.finance.invoicing.remover.InvoiceRemoverFactory;
import com.code.aon.ql.Criteria;

public class InvoiceDetailBeanListener extends ManagerBeanListenerAdapter {
	
	private boolean updating = false;

	@Override
	public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
		InvoiceDetail detail = (InvoiceDetail)evt.getTo();
		if (InvoiceType.UNDEDUCTIBLE != detail.getInvoice().getType() && detail.getItem() != null) {
			InvoiceTax detailVat = getInvoiceTax(detail, detail.getItem().getProduct().getVat());
			IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
			invoiceTaxBean.insert(detailVat);
			if (detail.getInvoice().isWithholding() && detail.getItem().getProduct().getRetention() != null) {
				InvoiceTax detailRetention = getInvoiceTax(detail, detail.getItem().getProduct().getRetention());
				invoiceTaxBean.insert(detailRetention);
			}
		}

		IManagerBean detailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(detailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), detail.getInvoice().getId());
		criteria.addNotEqualExpression(detailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_ID), detail.getId());
		criteria.addGreaterThanOrEqualExpression(detailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_LINE), detail.getLine());
		criteria.addOrder(detailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_LINE));
		List<ITransferObject> list = detailBean.getList(criteria);
		int index = detail.getLine();
		for (ITransferObject to : list) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)to;
			if (index == invoiceDetail.getLine()) {
				invoiceDetail.setLine(index + 1);
				invoiceDetail.getInvoice().setUpdateEnabled(false);
				detailBean.update(invoiceDetail);
				++index;
			}
		}

		updateInvoiceTotals((Invoice)BeanManager.getManagerBean(Invoice.class).get(detail.getInvoice().getId()));
	}
	
	@Override
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
		InvoiceDetail detail = (InvoiceDetail)evt.getTo();
		if (InvoiceType.UNDEDUCTIBLE != detail.getInvoice().getType() && detail.getItem() != null) {
			InvoiceTax detailVat = getInvoiceTax(detail, detail.getItem().getProduct().getVat());
			IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
			invoiceTaxBean.insert(detailVat);
			if (detail.getInvoice().isWithholding() && detail.getItem().getProduct().getRetention() != null) {
				InvoiceTax detailRetention = getInvoiceTax(detail, detail.getItem().getProduct().getRetention());
				invoiceTaxBean.insert(detailRetention);
			}
		}

		if (!updating) {
			updating = true;
			IManagerBean detailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(detailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), detail.getInvoice().getId());
			criteria.addNotEqualExpression(detailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_ID), detail.getId());
			criteria.addEqualExpression(detailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_LINE), detail.getLine());
			if (detailBean.getCount(criteria) > 0) {
				criteria = new Criteria();
				criteria.addEqualExpression(detailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), detail.getInvoice().getId());
				criteria.addNotEqualExpression(detailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_ID), detail.getId());
				criteria.addOrder(detailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_LINE));
				List<ITransferObject> list = detailBean.getList(criteria);
				int index = 1;
				for (ITransferObject to : list) {
					InvoiceDetail invoiceDetail = (InvoiceDetail)to;
					if (index == detail.getLine()) {
						++index;
					}
					invoiceDetail.setLine(index);
					invoiceDetail.getInvoice().setUpdateEnabled(false);
					detailBean.update(invoiceDetail);
					++index;
				}
			}
			updating = false;

			updateInvoiceTotals(detail.getInvoice());
		}
	}
	
	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		InvoiceDetail detail = (InvoiceDetail)evt.getTo();
		try {
			IInvoiceDetailRemover remover = InvoiceRemoverFactory.getInvoiceDetailRemover(detail.getSource());
			remover.removeDetail(detail);
		} catch (InvoicingException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}

		IManagerBean detailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(detailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), detail.getInvoice().getId());
		criteria.addNotEqualExpression(detailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_ID), detail.getId());
		criteria.addGreaterThanOrEqualExpression(detailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_LINE), detail.getLine());
		criteria.addOrder(detailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_LINE));
		List<ITransferObject> list = detailBean.getList(criteria);
		int index = detail.getLine() + 1;
		for (ITransferObject to : list) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)to;
			if (index == invoiceDetail.getLine()) {
				invoiceDetail.setLine(index - 1);
				invoiceDetail.getInvoice().setUpdateEnabled(false);
				detailBean.update(invoiceDetail);
				++ index;
			}
		}

		updateInvoiceTotals(detail.getInvoice());
	}

	private void updateInvoiceTotals(Invoice invoice) throws ManagerBeanException {
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

	private InvoiceTax getInvoiceTax(InvoiceDetail invoiceDetail, Tax tax) throws ManagerBeanException {
		InvoiceTax invoiceTax = new InvoiceTax();
		invoiceTax.setInvoiceDetail(invoiceDetail);
		invoiceTax.setTaxType(tax.getType());
		invoiceTax.setVatDeductionType(tax.getVatDeductionType());
		invoiceTax.setWithholdingType(tax.getWithholdingType());
		double percentage = 0.0;
		double surcharge = 0.0;
		double quota = 0.0;

		Invoice invoice = (!invoiceDetail.getInvoice().isRectifier()) ? invoiceDetail.getInvoice() : invoiceDetail.getInvoice().getRectificationInvoice();
		if (invoice.getTransaction() == InvoiceTransactionType.NATIONAL || invoice.getTransaction() == InvoiceTransactionType.INTRACOMMUNITY) {
			if (invoiceDetail.isTaxDataInDetail()) {
				percentage = (TaxType.VAT == tax.getType()) ? invoiceDetail.getVatPercent() : invoiceDetail.getRetentionPercent();
				quota = (TaxType.VAT == tax.getType()) ? invoiceDetail.getVatQuota() : invoiceDetail.getRetentionQuota();
			} else {
				if (invoice.getIssueDate().before(tax.getStartDate())) {
					tax = obtainTax(tax.getId(), invoice.getIssueDate());
				}
				percentage = tax.getPercentage();
				if (invoice.isSurcharge()) {
					surcharge = tax.getSurcharge();
				}
			}
		}
		invoiceTax.setPercentage(percentage);
		invoiceTax.setSurcharge(surcharge);
		invoiceTax.setQuota(quota);

		return invoiceTax;
	}

	@SuppressWarnings("unchecked")
	private Tax obtainTax(Integer id, Date date) throws ManagerBeanException {
		IManagerBean taxDetailBean = BeanManager.getManagerBean(TaxDetail.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(taxDetailBean.getFieldName(IConfigAlias.TAX_DETAIL_TAX_ID), id);
    	criteria.addLessThanOrEqualExpression(taxDetailBean.getFieldName(IConfigAlias.TAX_DETAIL_START_DATE), date);
    	criteria.addGreaterThanOrEqualExpression(taxDetailBean.getFieldName(IConfigAlias.TAX_DETAIL_END_DATE), date);
    	Iterator iter = taxDetailBean.getList(criteria).iterator();
    	if (iter.hasNext()) {
    		TaxDetail taxDetail = (TaxDetail)iter.next();
    		Tax tax = new Tax();
    		tax.setId(taxDetail.getTax().getId());
    		tax.setPercentage(taxDetail.getValue());
    		tax.setSurcharge(taxDetail.getSurcharge());
    		tax.setType(taxDetail.getTax().getType());
    		return tax;
    	}
		return null;
	}

}
