package com.code.aon.finance.event;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

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
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.finance.invoicing.remover.IInvoiceDetailRemover;
import com.code.aon.finance.invoicing.remover.InvoiceRemoverFactory;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceDetailBeanListener extends ManagerBeanListenerAdapter {
	
	private static final String STMT = "SELECT SUM(id.taxable_base)" 
			+" FROM invoice_detail id"
			+" INNER JOIN item it ON id.item = it.id"
			+" INNER JOIN product pr ON it.product =  pr.id"
			+" WHERE id.invoice = ?"
			+" AND pr.type = 1";
	
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
		updateInvoiceTotals(invoice, detail.isSkipServiceProcess());
		detail.setInvoice(invoice);
	}
	
	@Override
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
		InvoiceDetail detail = (InvoiceDetail)evt.getTo();
		if (detail.isUpdateEnabled()) {
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
		if (invoice.isNational() || !invoice.isSales()) {
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
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
			ps = HibernateUtil.getSQLConnection(sessionName).prepareStatement(STMT,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setInt(1, invoice.getId());
			rs = ps.executeQuery();
			double serviceTaxableBase = 0;
			if (rs.next()) {
				serviceTaxableBase = rs.getDouble(1);	
			}
			return (serviceTaxableBase > CommonUtil.round(invoiceTaxableBase / 2));  
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
}
