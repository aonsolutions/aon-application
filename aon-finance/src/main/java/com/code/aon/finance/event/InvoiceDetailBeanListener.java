package com.code.aon.finance.event;

import java.util.Date;
import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
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
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryTax;
import com.code.aon.tas.ProjectTas;
import com.code.aon.tas.enumeration.ProjectStatus;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;

public class InvoiceDetailBeanListener extends ManagerBeanListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
	@Override
	public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
		InvoiceDetail detail = (InvoiceDetail)evt.getTo();
		if (detail.getInvoice().isSales()) {
			updateProjectStatus(detail.getProject(), ProjectStatus.CLOSED);
		}

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
		if (detail.getInvoice().isSales()) {
			updateProjectStatus(detail.getProject(), ProjectStatus.CLOSED);
		}

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
		if (detail.getInvoice().isSales()) {
			updateProjectStatus(detail.getProject(), ProjectStatus.PENDING);
		}

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
		Domain domain = AON.getDomain(HibernateUtil.getSessionFactoryName(), invoiceDetail.getDomain());
	    TbaiConfiguration tbaiConfiguration = AON.getTbaiConfiguration(domain, "");
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
		if (!invoice.isSales() || invoice.isNational() || (invoice.isCanCeuMel() && tax.isRetention())) {
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
				RegistryTax rTax = obtainRegistryTax(invoice.getRegistry(), tax.getId(), invoice.getIssueDate());
				if (rTax != null) {
					percentage = rTax.getPercentage();
					if (invoice.isSurcharge()) {
						surcharge = rTax.getSurcharge();
					}
				} else {
					if (invoice.getIssueDate().before(tax.getStartDate())) {
						TaxDetail taxDetail = obtainTax(tax.getId(), invoice.getIssueDate());
						if (taxDetail != null) {
							percentage = taxDetail.getValue();
							if (invoice.isSurcharge()) {
								surcharge = taxDetail.getSurcharge();
							}
						}
					} else {
						percentage = tax.getPercentage();
						if (invoice.isSurcharge()) {
							surcharge = tax.getSurcharge();
						}
					}
				}
				if (isQuotaSavedInTax(invoiceDetail) || tbaiConfiguration.isActive()) {
					quota = CommonUtil.round(base * percentage / 100);
					if (invoice.isSurcharge()) {
						surchargeQuota = CommonUtil.round(base * surcharge / 100);	
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

	private RegistryTax obtainRegistryTax(Registry registry, Integer taxId, Date date) throws ManagerBeanException {
		if (registry != null && registry.getId() != null && taxId != null) {
			return registry.getTax(taxId, date);
		}
		return null;
	}

	private TaxDetail obtainTax(Integer taxId, Date date) throws ManagerBeanException {
		IManagerBean taxDetailBean = BeanManager.getManagerBean(TaxDetail.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_TAX_ID), taxId);
    	criteria.addLessThanOrEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_START_DATE), date);
    	criteria.addGreaterThanOrEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_END_DATE), date);
    	for (ITransferObject ito : taxDetailBean.getList(criteria)) {
    		return (TaxDetail)ito;
    	}
		return null;
	}

	private boolean isQuotaSavedInTax(InvoiceDetail invoiceDetail) throws ManagerBeanException {
		IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_INVOICE_DETAIL_INVOICE_ID), invoiceDetail.getInvoice().getId());
		criteria.addNotEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_QUOTA), Double.valueOf(0));
		return invoiceTaxBean.getCount(criteria) != 0;
	}
	
	private void updateProjectStatus(Project project, ProjectStatus status) throws ManagerBeanException {
		if (project != null && project.isTas()) {
			IManagerBean projectTasBean = BeanManager.getManagerBean(ProjectTas.class);
			ProjectTas projectTas = (ProjectTas)projectTasBean.get(project.getId());
			if (projectTas != null && projectTas.getStatus() != status) {
				boolean updateProjectStatus = true;
				if (status == ProjectStatus.PENDING) {
					IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
					Criteria criteria = new Criteria();
					String alias = invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_PROJECT_ID);
					Expression invoiceProjectExpr = ExpressionUtilities.getEqualExpression(alias, project.getId());
					alias = invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_PROJECT_ID);
					Expression detailProjectExpr = ExpressionUtilities.getEqualExpression(alias, project.getId());
					criteria.addExpression(ExpressionUtilities.getOrExpression(invoiceProjectExpr, detailProjectExpr));
					if (invoiceDetailBean.getCount(criteria) > 0) {
						updateProjectStatus = false;
					}
				}
				if (updateProjectStatus) {
					projectTas.setStatus(status);
					projectTasBean.update(projectTas);
				}
			}
		}
	}

	private void updateInvoiceTotals(Invoice invoice, boolean skipServiceProcess) throws ManagerBeanException {
		if (invoice.isUpdateEnabled()) {
			InvoicePriceStrategy priceStrategy = new InvoicePriceStrategy();

			double taxableBase = priceStrategy.getCalculatedTaxableBase(invoice);
			double vatQuota = priceStrategy.getCalculatedTotalVatQuota(invoice, invoice);
			double retentionQuota = priceStrategy.getCalculatedTotalRetentionQuota(invoice, invoice);

			invoice.setUpdateEnabled(false);
			invoice.setTaxableBase(taxableBase);
			invoice.setVatQuota(vatQuota);
			invoice.setRetentionQuota(retentionQuota);
			invoice.setTotal(CommonUtil.round(taxableBase + vatQuota - retentionQuota));
			if (!skipServiceProcess) {
				invoice.setService(invoice.isExpense() || invoice.isUndeductible() || isServiceInvoice(invoice, taxableBase));	
			}

			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			invoiceBean.restoreNullSubPOJOs(invoice);
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
		return amount != null && amount.doubleValue() > CommonUtil.round(invoiceTaxableBase / 2);
	}

}
