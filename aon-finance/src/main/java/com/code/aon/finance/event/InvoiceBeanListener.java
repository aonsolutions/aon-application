package com.code.aon.finance.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tax;
import com.code.aon.config.TaxDetail;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryTax;
import com.code.aon.tas.ProjectTas;
import com.code.aon.tas.enumeration.ProjectStatus;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceBeanListener extends ManagerBeanListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
		Invoice invoice = (Invoice) evt.getTo();
		if (invoice.isSales()) {
			modifyProjectStatus(((Invoice)evt.getTo()).getProject(), ProjectStatus.CLOSED);
		}
	}

	@Override
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
		Invoice invoice = (Invoice) evt.getTo();
		if (invoice.isSales()) {
			modifyProjectStatus(((Invoice)evt.getTo()).getProject(), ProjectStatus.CLOSED);
		}

		if (invoice.isUpdateEnabled()) {
			if (invoice.isUpdateDetails()) {
				updateDetails(invoice);
			}
			updateTotals(invoice);
		}
		invoice.setUpdateEnabled(true);
	}

	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		Invoice invoice = (Invoice) evt.getTo();
		if (invoice.isSales()) {
			modifyProjectStatus(((Invoice)evt.getTo()).getProject(), ProjectStatus.PENDING);
		}
	}

	private void modifyProjectStatus(Project project, ProjectStatus status) throws ManagerBeanException {
		if (project != null && project.isTas()) {
			IManagerBean projectTasBean = BeanManager.getManagerBean(ProjectTas.class);
			ProjectTas projectTas = (ProjectTas)projectTasBean.get(project.getId());
			if (projectTas != null && projectTas.getStatus() != status) {
				projectTas.setStatus(status);
				projectTasBean.update(projectTas);
			}
		}
	}

	private void updateDetails(Invoice invoice) throws ManagerBeanException {
		if ((invoice.isSales() || invoice.isPurchase()) && (invoice.getPosShift() == null || invoice.getPosShift().getId() == null)) {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			for (ITransferObject ito : invoiceDetailBean.getList(criteria)) {
				InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
				invoiceDetail.getInvoice().setUpdateEnabled(false);
				invoiceDetailBean.update(invoiceDetail);
			}
		} else if (invoice.isExpense()) {
			IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			for (ITransferObject ito : invoiceDetailBean.getList(criteria)) {
				InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
				criteria = new Criteria();
				criteria.addEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_INVOICE_DETAIL_ID), invoiceDetail.getId());
				for (ITransferObject itr : invoiceTaxBean.getList(criteria)) {
					InvoiceTax invoiceTax = (InvoiceTax)itr;
					Tax tax = invoiceTax.isVat() ? invoiceDetail.getItem().getVat() : invoiceDetail.getItem().getRetention();
					Tax newTax = getDetailNewTax(invoice, tax);
					if (invoiceTax.getPercentage() != newTax.getPercentage()) {
						invoiceTax.setQuota(CommonUtil.round(invoiceTax.getBase() * newTax.getPercentage() / 100));
						invoiceTax.setPercentage(newTax.getPercentage());
					}
					if (invoice.isSurcharge() && invoiceTax.getSurcharge() != newTax.getSurcharge()) {
						invoiceTax.setSurchargeQuota(CommonUtil.round(invoiceTax.getBase() * newTax.getSurcharge() / 100));
						invoiceTax.setSurcharge(newTax.getSurcharge());
					}
					if (invoiceTax.getVatDeductionType() != newTax.getVatDeductionType()) {
						invoiceTax.setVatDeductionType(newTax.getVatDeductionType());
					}
					if (invoiceTax.getWithholdingType() != newTax.getWithholdingType()) {
						invoiceTax.setWithholdingType(newTax.getWithholdingType());
					}
					invoiceTaxBean.update(invoiceTax);
				}
			}
		}
	}

	private Tax getDetailNewTax(Invoice invoice, Tax tax) throws ManagerBeanException {
		RegistryTax rTax = invoice.getRegistry().getTax(tax.getId(), invoice.getIssueDate());
		if (rTax != null) {
			Tax newTax = rTax.getTax();
			newTax.setPercentage(rTax.getPercentage());
			newTax.setSurcharge(rTax.getSurcharge());
			return newTax;
		} else {
			if (invoice.getIssueDate().before(tax.getStartDate())) {
				IManagerBean taxDetailBean = BeanManager.getManagerBean(TaxDetail.class);
		    	Criteria criteria = new Criteria();
		    	criteria.addEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_TAX_ID), tax.getId());
		    	criteria.addLessThanOrEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_START_DATE), invoice.getIssueDate());
		    	criteria.addGreaterThanOrEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_END_DATE), invoice.getIssueDate());
		    	for (ITransferObject ito : taxDetailBean.getList(criteria)) {
		    		TaxDetail taxDetail = (TaxDetail)ito;
		    		Tax newTax = taxDetail.getTax();
		    		newTax.setPercentage(taxDetail.getValue());
		    		newTax.setSurcharge(taxDetail.getSurcharge());
		    		return newTax;
		    	}
			}
		}
		return tax;
	}

	private void updateTotals(Invoice invoice) throws ManagerBeanException {
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
			invoiceBean.update(invoice);
			invoice.setUpdateEnabled(true);
		}
	}

}
