package com.code.aon.finance.bridge.invoicing;

import java.util.Date;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class RectificationInvoicingManager {

	public Invoice specialRectifyInvoice(Invoice invoice, String series, int number, Date issueDate, String cause, double percent) 
			throws ManagerBeanException {
		Invoice rectifier = createRectifierInvoice(invoice, series, number, null, issueDate, cause, RectificationType.SPECIAL_RECTIFIER);
		createInvoiceAddress(rectifier, invoice);
		createRectifierInvoiceDetails(rectifier, invoice, percent);
		// No se duplican los vencimientos, puesto que lo único que cambia es la cuota de IVA.
		updateRectifiedInvoice(rectifier, invoice);
		return rectifier;
	}
	
	public Invoice rectifyInvoice(Invoice invoice, String series, int number, Date issueDate, String cause, boolean settleFinance) throws ManagerBeanException {
		Invoice rectifier = createRectifierInvoice(invoice, series, number, null, issueDate, cause, RectificationType.NORMAL_RECTIFIER);
		createInvoiceAddress(rectifier, invoice);
		createRectifierInvoiceDetails(rectifier, invoice, 0.0);
		createRectifierInvoiceFinances(rectifier, invoice, settleFinance);
		updateRectifiedInvoice(rectifier, invoice);
		return rectifier;
	}

	public Invoice rectifyReceivedInvoice(Invoice invoice, String referenceCode, Date issueDate, String cause, boolean settleFinance) throws ManagerBeanException {
		Invoice rectifier = createRectifierInvoice(invoice, null, 0, referenceCode, issueDate, cause, RectificationType.NORMAL_RECTIFIER);
		createInvoiceAddress(rectifier, invoice);
		createRectifierInvoiceDetails(rectifier, invoice, 0.0);
		createRectifierInvoiceFinances(rectifier, invoice, settleFinance);
		updateRectifiedInvoice(rectifier, invoice);
		return rectifier;
	}

	private Invoice createRectifierInvoice(Invoice invoice, String series, int number, String referenceCode, Date issueDate, String cause, 
			RectificationType rectificationtype) throws ManagerBeanException {
		Invoice rectifier = new Invoice();
		rectifier.setProject(invoice.getProject());
		if (invoice.isSales()) {
			rectifier.setSeries(series);
			rectifier.setNumber((number > 0) ? number : obtainMaxNumber(series));
		} else {
			rectifier.setReferenceCode(referenceCode);
		}
		rectifier.setRegistry(invoice.getRegistry());
		rectifier.setRegistryDocument(invoice.getRegistryDocument());
		rectifier.setRegistryDocumentType(invoice.getRegistryDocumentType());
		rectifier.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry());
		rectifier.setRegistryName(invoice.getRegistryName());
		rectifier.setRegistryAddress(invoice.getRegistryAddress());
		rectifier.setIssueDate(issueDate);
		rectifier.setTaxDate(issueDate);
		rectifier.setSecurityLevel(invoice.getSecurityLevel());
		rectifier.setStatus(InvoiceStatus.PENDING);
		rectifier.setType(invoice.getType());
		rectifier.setComments(cause);
		rectifier.setScope(invoice.getScope());
		rectifier.setService(invoice.isService());
		rectifier.setAdvance(invoice.isAdvance());
		rectifier.setRectificationType(rectificationtype);
		rectifier.setRectificationInvoice(invoice);
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoiceBean.restoreNullSubPOJOs(rectifier);
		return (Invoice)invoiceBean.insert(rectifier);
	}

	private void createInvoiceAddress(Invoice rectifier, Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceAddressBean = BeanManager.getManagerBean(InvoiceAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceAddressBean.getFieldName(IEntityAlias.INVOICE_ADDRESS_INVOICE_ID), invoice.getId());
		for (ITransferObject ito : invoiceAddressBean.getList(criteria)) {
			InvoiceAddress invoiceAddress = (InvoiceAddress)ito;
			InvoiceAddress rectifierAddress = new InvoiceAddress();
			rectifierAddress.setInvoice(rectifier);
			rectifierAddress.setStreetType(invoiceAddress.getStreetType());
			rectifierAddress.setAddress(invoiceAddress.getAddress());
			rectifierAddress.setNumber(invoiceAddress.getNumber());
			rectifierAddress.setAddress2(invoiceAddress.getAddress2());
			rectifierAddress.setZip(invoiceAddress.getZip());
			rectifierAddress.setCity(invoiceAddress.getCity());
			rectifierAddress.setProvince(invoiceAddress.getProvince());
			rectifierAddress.setGeozone(invoiceAddress.getGeozone());
			invoiceAddressBean.insert(rectifierAddress);
		}
	}

	private void createRectifierInvoiceDetails(Invoice rectifier, Invoice invoice, double percent) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		criteria.addOrder(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE));
		List<ITransferObject> invoiceDetailList = invoiceDetailBean.getList(criteria);
		for (ITransferObject ito : invoiceDetailList) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
			InvoiceDetail rectifierDetail = new InvoiceDetail();
			rectifierDetail.setInvoice(rectifier);
			rectifierDetail.setProject(invoiceDetail.getProject());
			rectifierDetail.setLine(invoiceDetail.getLine());
			rectifierDetail.setItem(invoiceDetail.getItem());
			rectifierDetail.setDescription(invoiceDetail.getDescription());
			rectifierDetail.setQuantity((invoice.isSpecialRectifier()) ? 0.0 : CommonUtil.round(invoiceDetail.getQuantity() * (-1), 3));
			rectifierDetail.setPrice((invoice.isSpecialRectifier()) ? 0.0 : invoiceDetail.getPrice());
			rectifierDetail.setDiscountExpression((invoice.isSpecialRectifier()) ? new DiscountExpression("0.0") : invoiceDetail.getDiscountExpression());
			rectifierDetail.setSource((invoiceDetail.getSource() == InvoiceSource.RESERVATION) ? invoiceDetail.getSource() : InvoiceSource.DIRECT_INVOICE);
			rectifierDetail.setSourceId((invoiceDetail.getSource() == InvoiceSource.RESERVATION) ? invoiceDetail.getSourceId() : null);
			rectifierDetail.setTaxableBase((invoice.isSpecialRectifier()) ? 0.0 : CommonUtil.round(invoiceDetail.getTaxableBase() * (-1), 4));
			rectifierDetail.setWorkPlace(invoiceDetail.getWorkPlace());
			rectifierDetail.setTaxDataInDetail(true);

			InvoiceTax invoiceVatTax = obtainInvoiceTax(invoiceDetail, TaxType.VAT);
			if (invoiceVatTax != null) {
				if (invoice.isSpecialRectifier()) {
					double quota = invoiceVatTax.getQuota();
					double surchargeQuota = invoiceVatTax.getSurchargeQuota();
					if (quota == 0) {
						quota = CommonUtil.round(invoiceDetail.getTaxableBase() * invoiceVatTax.getPercentage() / 100); 
						surchargeQuota = CommonUtil.round(invoiceDetail.getTaxableBase() * invoiceVatTax.getSurcharge() / 100); 
					}
					rectifierDetail.setVatPercent(invoiceVatTax.getPercentage());
					rectifierDetail.setVatQuota(CommonUtil.round(quota * percent * (-1) / 100));
					rectifierDetail.setSurchargePercent(invoiceVatTax.getSurcharge());
					rectifierDetail.setSurchargeQuota(CommonUtil.round(surchargeQuota * percent * (-1) / 100));
				} else {
					rectifierDetail.setVatPercent(invoiceVatTax.getPercentage());
					rectifierDetail.setVatQuota(CommonUtil.round(invoiceVatTax.getQuota() * (-1)));
					rectifierDetail.setSurchargePercent(invoiceVatTax.getSurcharge());
					rectifierDetail.setSurchargeQuota(CommonUtil.round(invoiceVatTax.getSurchargeQuota() * (-1)));
				}
			}
			InvoiceTax invoiceRetentionTax = obtainInvoiceTax(invoiceDetail, TaxType.RETENTION);
			if (invoiceRetentionTax != null) {
				rectifierDetail.setRetentionPercent(invoiceRetentionTax.getPercentage());
				rectifierDetail.setRetentionQuota(CommonUtil.round(invoiceRetentionTax.getQuota() * (-1)));
			}

			boolean lastDetail = invoiceDetailList.indexOf(invoiceDetail) == (invoiceDetailList.size() - 1);
			rectifierDetail.setSkipServiceProcess(true);
			rectifierDetail.setUpdateEnabled(lastDetail);
			rectifierDetail.getInvoice().setUpdateEnabled(lastDetail);
			invoiceDetailBean.insert(rectifierDetail);
		}
	}

	private void createRectifierInvoiceFinances(Invoice rectifier, Invoice invoice, boolean settleFinance) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), invoice.getId());
		criteria.addOrder(financeBean.getFieldName(IEntityAlias.FINANCE_ID));
		for (ITransferObject ito : financeBean.getList(criteria)) {
			Finance finance = (Finance)ito;
			Finance rectifierFinance = new Finance();
			rectifierFinance.setInvoice(rectifier);
			rectifierFinance.setPayment(finance.isPayment());
			rectifierFinance.setRegistry(finance.getRegistry());
			rectifierFinance.setRegistryDocument(finance.getRegistryDocument());
			rectifierFinance.setRegistryDocumentType(finance.getRegistryDocumentType());
			rectifierFinance.setRegistryDocumentCountry(finance.getRegistryDocumentCountry());
			rectifierFinance.setRegistryName(finance.getRegistryName());
			rectifierFinance.setAmount(CommonUtil.round(finance.getTotalAmount() * (-1)));
			rectifierFinance.setExpenses(0);
			rectifierFinance.setConcept(rectifier.getDocumentNumber()); 
			rectifierFinance.setDueDate(rectifier.getIssueDate());
			rectifierFinance.setPayMethod(finance.getPayMethod());
			rectifierFinance.setBankAccount(finance.getBankAccount());
			rectifierFinance.setBankAlias(finance.getBankAlias());
			rectifierFinance.setBic(finance.getBic());
			rectifierFinance.setFinanceStatus(FinanceStatus.PENDING);
			rectifierFinance.setSecurityLevel(finance.getSecurityLevel());
			rectifierFinance.setScope(finance.getScope());
			financeBean.insert(rectifierFinance);

			if (settleFinance && finance.getFinanceStatus() == FinanceStatus.PENDING) {
				finance.setFinanceStatus(FinanceStatus.SETTLED);
				finance = (Finance)financeBean.update(finance);
				FinanceTrackingWriter.addFinanceTracking(finance, new Date(), FinanceTrackingType.SETTLED, "Saldado");
				
				rectifierFinance.setFinanceStatus(FinanceStatus.SETTLED);
				rectifierFinance = (Finance)financeBean.update(rectifierFinance);
				FinanceTrackingWriter.addFinanceTracking(rectifierFinance, new Date(), FinanceTrackingType.SETTLED, "Saldado");
			}
		}
	}

	private void updateRectifiedInvoice(Invoice rectifier, Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoice.setRectificationInvoice(invoice.getRectificationType() == RectificationType.NONE ? rectifier : null);
		invoice.setRectificationType(RectificationType.RECTIFIED);
		invoiceBean.restoreNullSubPOJOs(invoice);
		String sessionFactoryName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		invoice = (Invoice) HibernateUtil.getSession(sessionFactoryName).merge(invoice);
		invoice.setUpdateEnabled(false);
		invoiceBean.update(invoice);
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
    	return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}

	private InvoiceTax obtainInvoiceTax(InvoiceDetail invoiceDetail, TaxType taxType) throws ManagerBeanException {
		IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_INVOICE_DETAIL_ID), invoiceDetail.getId());
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_TAX_TYPE), taxType);
		for (ITransferObject ito : invoiceTaxBean.getList(criteria)) {
			return (InvoiceTax)ito;
		}
		return null;
	}

}
