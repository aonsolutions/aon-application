package com.code.aon.finance.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.seller.Seller;
import com.code.aon.tas.ProjectTas;
import com.code.aon.tas.enumeration.ProjectStatus;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceBeanListener extends ManagerBeanListenerAdapter {
	
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
			Project project = (invoice.getProject() != null && invoice.getProject().getId() != null) ? invoice.getProject() : null;
			Seller seller = (invoice.getSeller() != null && invoice.getSeller().getId() != null) ? invoice.getSeller() : null;
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			for (ITransferObject ito : invoiceDetailBean.getList(criteria)) {
				InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
				if (invoiceDetail.getProject() == null || invoiceDetail.getProject().getId() == null) {
					invoiceDetail.setProject(project);
				}
				if (invoiceDetail.getSeller() == null || invoiceDetail.getSeller().getId() == null) {
					invoiceDetail.setSeller(seller);
				}
				invoiceDetail.setUpdateEnabled(isUpdateDetailsEnabled(invoice));
				invoiceDetail.getInvoice().setUpdateEnabled(false);
				invoiceDetailBean.update(invoiceDetail);
			}

			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			criteria = new Criteria();
			criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), invoice.getId());
			for (ITransferObject ito : financeBean.getList(criteria)) {
				Finance finance = (Finance)ito;
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
			if (projectTas.getStatus() != status) {
				projectTas.setStatus(status);
				projectTasBean.update(projectTas);
			}
		}
	}

	private boolean isUpdateDetailsEnabled(Invoice invoice) {
		return (invoice.isSales()) ? invoice.getPos() == null || invoice.getPos().getId() == null : invoice.isPurchase();	
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
