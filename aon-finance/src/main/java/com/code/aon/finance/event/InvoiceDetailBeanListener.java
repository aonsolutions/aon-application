package com.code.aon.finance.event;

import java.util.Date;
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
import com.code.aon.config.enumeration.WithholdingType;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.finance.invoicing.remover.IInvoiceDetailRemover;
import com.code.aon.finance.invoicing.remover.InvoiceRemoverFactory;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceDetailBeanListener extends ManagerBeanListenerAdapter {
	
	@Override
	public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
		InvoiceDetail detail = (InvoiceDetail)evt.getTo();
		if (InvoiceType.UNDEDUCTIBLE != detail.getInvoice().getType() && !detail.isPrepayment() && detail.getItem() != null) {
			InvoiceTax detailVat = getInvoiceTax(detail, detail.getItem().getProduct().getVat(), null);
			IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
			invoiceTaxBean.insert(detailVat);
			if (detail.getInvoice().isWithholding() && detail.getItem().getProduct().isWithholding()) {
				InvoiceTax detailRetention = getInvoiceTax(detail, detail.getItem().getProduct().getRetention(), detailVat);
				invoiceTaxBean.insert(detailRetention);
			}
		}

		IManagerBean detailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), detail.getInvoice().getId());
		criteria.addNotEqualExpression(detailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ID), detail.getId());
		criteria.addGreaterThanOrEqualExpression(detailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE), detail.getLine());
		criteria.addOrder(detailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE));
		List<ITransferObject> list = detailBean.getList(criteria);
		int index = detail.getLine();
		for (ITransferObject to : list) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)to;
			if (index == invoiceDetail.getLine()) {
				invoiceDetail.setLine(index + 1);
				invoiceDetail.setUpdateEnabled(false);
				invoiceDetail.getInvoice().setUpdateEnabled(false);
				detailBean.update(invoiceDetail);
				++index;
			}
		}

		Invoice invoice = (Invoice)BeanManager.getManagerBean(Invoice.class).get(detail.getInvoice().getId());
		invoice.setUpdateEnabled(detail.getInvoice().isUpdateEnabled());
		if (invoice.getProject() == null && detail.getProject() != null && detail.getProject().getId() != null) {
			invoice.setProject(detail.getProject());
		}
		if (invoice.getSeller() == null && detail.getSeller() != null && detail.getSeller().getId() != null) {
			invoice.setSeller(detail.getSeller());
		}
		updateInvoiceTotals(invoice, detail.isSkipServiceProcess());
		detail.setInvoice(invoice);
	}
	
	@Override
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
		InvoiceDetail detail = (InvoiceDetail)evt.getTo();
		if (detail.isUpdateEnabled()) {
			if (InvoiceType.UNDEDUCTIBLE != detail.getInvoice().getType() && !detail.isPrepayment() && detail.getItem() != null) {
				InvoiceTax detailVat = getInvoiceTax(detail, detail.getItem().getProduct().getVat(), null);
				IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
				invoiceTaxBean.insert(detailVat);
				if (detail.getInvoice().isWithholding() && detail.getItem().getProduct().isWithholding()) {
					InvoiceTax detailRetention = getInvoiceTax(detail, detail.getItem().getProduct().getRetention(), detailVat);
					invoiceTaxBean.insert(detailRetention);
				}
			}
	
			IManagerBean detailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), detail.getInvoice().getId());
			criteria.addNotEqualExpression(detailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ID), detail.getId());
			criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE), detail.getLine());
			if (detailBean.getCount(criteria) > 0) {
				criteria = new Criteria();
				criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), detail.getInvoice().getId());
				criteria.addNotEqualExpression(detailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ID), detail.getId());
				criteria.addOrder(detailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE));
				List<ITransferObject> list = detailBean.getList(criteria);
				int index = 1;
				for (ITransferObject to : list) {
					InvoiceDetail invoiceDetail = (InvoiceDetail)to;
					if (index == detail.getLine()) {
						++index;
					}
					invoiceDetail.setLine(index);
					invoiceDetail.setUpdateEnabled(false);
					invoiceDetail.getInvoice().setUpdateEnabled(false);
					detailBean.update(invoiceDetail);
					++index;
				}
			}

			updateInvoiceTotals(detail.getInvoice(), detail.isSkipServiceProcess());
		}
		detail.setUpdateEnabled(true);
		detail.getInvoice().setUpdateEnabled(true);
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

		if (detail.isUpdateEnabled()) {
			IManagerBean detailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), detail.getInvoice().getId());
			criteria.addNotEqualExpression(detailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ID), detail.getId());
			criteria.addGreaterThanOrEqualExpression(detailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE), detail.getLine());
			criteria.addOrder(detailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE));
			List<ITransferObject> list = detailBean.getList(criteria);
			int index = detail.getLine() + 1;
			for (ITransferObject to : list) {
				InvoiceDetail invoiceDetail = (InvoiceDetail)to;
				if (index == invoiceDetail.getLine()) {
					invoiceDetail.setLine(index - 1);
					invoiceDetail.setUpdateEnabled(false);
					invoiceDetail.getInvoice().setUpdateEnabled(false);
					detailBean.update(invoiceDetail);
					++ index;
				}
			}

			updateInvoiceTotals(detail.getInvoice(), detail.isSkipServiceProcess());
		}
	}

	private InvoiceTax getInvoiceTax(InvoiceDetail invoiceDetail, Tax tax, InvoiceTax detailVat) throws ManagerBeanException {
		InvoiceTax invoiceTax = new InvoiceTax();
		invoiceTax.setInvoiceDetail(invoiceDetail);
		invoiceTax.setTaxType(tax.getType());
		invoiceTax.setVatDeductionType(tax.getVatDeductionType());
		invoiceTax.setWithholdingType(tax.getWithholdingType());
		double base = invoiceDetail.getTaxableBase();
		double percentage = 0.0;
		double quota = 0.0;
		double surcharge = 0.0;
		double surchargeQuota = 0.0;

		Invoice invoice = (!invoiceDetail.getInvoice().isRectifier()) ? invoiceDetail.getInvoice() : invoiceDetail.getInvoice().getRectificationInvoice();
		if (invoice.isNational() || !invoice.isSales()) {
			if (tax.isRetention() && tax.getWithholdingType() == WithholdingType.FARMER && invoiceDetail.getInvoice().isWithholdingFarmer()) {
				double detailVatBase = 0;
				if (detailVat.getQuota() != 0) {
					detailVatBase = CommonUtil.round(detailVat.getQuota() + detailVat.getSurchargeQuota());
				} else {
					detailVatBase = CommonUtil.round(detailVat.getBase() * (detailVat.getPercentage() + detailVat.getSurcharge()) / 100);
				}
				base = CommonUtil.round(base + detailVatBase, 4);
			}

			if (invoiceDetail.isTaxDataInDetail()) {
				percentage = (tax.isVat()) ? invoiceDetail.getVatPercent() : invoiceDetail.getRetentionPercent();
				quota = (tax.isVat()) ? invoiceDetail.getVatQuota() : invoiceDetail.getRetentionQuota();
				surcharge = (tax.isVat()) ? invoiceDetail.getSurchargePercent() : 0;
				surchargeQuota = (tax.isVat()) ? invoiceDetail.getSurchargeQuota() : 0;
			} else {
				if (invoice.getIssueDate().before(tax.getStartDate())) {
					tax = obtainTax(tax.getId(), invoice.getIssueDate());
				}
				percentage = tax.getPercentage();
				if (invoice.isSurcharge()) {
					surcharge = tax.getSurcharge();
				} else {
					if (tax.isVat() && percentage == 4 && base % 0.125 == 0 && base % 0.250 != 0) {
						base = CommonUtil.round(base + 0.005);
						quota = CommonUtil.round(base * percentage / 100 - 0.005);

						boolean detailUpdate = invoiceDetail.isUpdateEnabled();
						boolean invoiceUpdate = invoiceDetail.getInvoice().isUpdateEnabled();
						invoiceDetail.setTaxableBase(base);
						invoiceDetail.setUpdateEnabled(false);
						invoiceDetail.getInvoice().setUpdateEnabled(false);
						BeanManager.getManagerBean(InvoiceDetail.class).update(invoiceDetail);
						invoiceDetail.setUpdateEnabled(detailUpdate);
						invoiceDetail.getInvoice().setUpdateEnabled(invoiceUpdate);
					}
				}
			}
		}
		invoiceTax.setBase(base);
		invoiceTax.setPercentage(percentage);
		invoiceTax.setQuota(quota);
		invoiceTax.setSurcharge(surcharge);
		invoiceTax.setSurchargeQuota(surchargeQuota);

		return invoiceTax;
	}

	private Tax obtainTax(Integer id, Date date) throws ManagerBeanException {
		IManagerBean taxDetailBean = BeanManager.getManagerBean(TaxDetail.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_TAX_ID), id);
    	criteria.addLessThanOrEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_START_DATE), date);
    	criteria.addGreaterThanOrEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_END_DATE), date);
    	for (ITransferObject ito : taxDetailBean.getList(criteria)) {
    		TaxDetail taxDetail = (TaxDetail)ito;
    		Tax tax = new Tax();
    		tax.setId(taxDetail.getTax().getId());
    		tax.setPercentage(taxDetail.getValue());
    		tax.setSurcharge(taxDetail.getSurcharge());
    		tax.setType(taxDetail.getTax().getType());
    		return tax;
    	}
		return null;
	}

	private void updateInvoiceTotals(Invoice invoice, boolean skipServiceProcess) throws ManagerBeanException {
		if (invoice.isUpdateEnabled()) {
			InvoicePriceStrategy priceStrategy = new InvoicePriceStrategy();
			double taxableBase = priceStrategy.getCalculatedTaxableBase(invoice);
			double vatQuota = priceStrategy.getCalculatedTotalVatQuota(invoice, invoice);
			double retentionQuota = priceStrategy.getCalculatedTotalRetentionQuota(invoice, invoice);

			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			invoice.setUpdateEnabled(false);
			invoice.setTaxableBase(taxableBase);
			invoice.setVatQuota(vatQuota);
			invoice.setRetentionQuota(retentionQuota);
			invoice.setTotal(CommonUtil.round(taxableBase + vatQuota - retentionQuota));

			if (!skipServiceProcess) {
				invoice.setService(isServiceInvoice(invoice, taxableBase));	
			}
			invoiceBean.update(invoice);
		}
	}

	private boolean isServiceInvoice(Invoice invoice, double invoiceTaxableBase) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Projection projection = Projection.sum(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_TAXABLE_BASE));
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ITEM_PRODUCT_TYPE), ProductType.SERVICE);
		Double amount = (Double)invoiceDetailBean.getUniqueResult(projection, criteria);
		return (amount!=null) ? amount.doubleValue() > CommonUtil.round(invoiceTaxableBase / 2) : false;
	}

}
