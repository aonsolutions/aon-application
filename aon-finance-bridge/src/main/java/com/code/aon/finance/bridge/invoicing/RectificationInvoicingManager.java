package com.code.aon.finance.bridge.invoicing;

import java.util.Date;
import java.util.Iterator;

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
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class RectificationInvoicingManager {

	private IPriceStrategy priceStrategy;

	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

	public Invoice specialRectifyInvoice(Invoice invoice, String series, int number, Date issueDate, String cause, double percent) 
			throws ManagerBeanException {
		Invoice rectifier = createRectifierInvoice(invoice, series, number, issueDate, cause, RectificationType.SPECIAL_RECTIFIER);
		createInvoiceAddress(rectifier, invoice);
		createRectifierInvoiceDetails(rectifier, invoice, true, percent);
		// No se duplican los vencimientos, puesto que lo único que cambia es la cuota de IVA.
		updateRectifiedInvoice(rectifier, invoice);
		return rectifier;
	}
	
	public Invoice rectifyInvoice(Invoice invoice, String series, int number, Date issueDate, String cause) throws ManagerBeanException {
		Invoice rectifier = createRectifierInvoice(invoice, series, number, issueDate, cause, RectificationType.NORMAL_RECTIFIER);
		createInvoiceAddress(rectifier, invoice);
		createRectifierInvoiceDetails(rectifier, invoice, false, 0.0);
		createRectifierInvoiceFinances(rectifier, invoice);
		updateRectifiedInvoice(rectifier, invoice);
		return rectifier;
	}

	private Invoice createRectifierInvoice(Invoice invoice, String series, int number, Date issueDate, String cause, 
			RectificationType rectificationtype) throws ManagerBeanException {
		Invoice rectifier = new Invoice();
		rectifier.setSeries(series);
		rectifier.setNumber((number > 0) ? number : obtainMaxNumber(series));
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
		rectifier.setRectificationType(rectificationtype);
		rectifier.setRectificationInvoice(invoice);
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoiceBean.restoreNullSubPOJOs(rectifier);
		return (Invoice)invoiceBean.insert(rectifier);
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
    	return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
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
			rectifierAddress.setGeozone(invoiceAddress.getGeozone());
			invoiceAddressBean.insert(rectifierAddress);
		}
	}

	private void createRectifierInvoiceDetails(Invoice rectifier, Invoice invoice, boolean taxDataInDetail, double percent) 
			throws ManagerBeanException {
		IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		criteria.addOrder(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE));
		for (ITransferObject ito : invoiceDetailBean.getList(criteria)) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
			InvoiceDetail rectifierDetail = new InvoiceDetail();
			rectifierDetail.setInvoice(rectifier);
			rectifierDetail.setLine(invoiceDetail.getLine());
			rectifierDetail.setItem(invoiceDetail.getItem());
			rectifierDetail.setDescription(invoiceDetail.getDescription());
			rectifierDetail.setTaxDataInDetail(taxDataInDetail);
			rectifierDetail.setQuantity((taxDataInDetail) ? 0.0 : CommonUtil.round(0 - invoiceDetail.getQuantity(), 3));
			rectifierDetail.setPrice((taxDataInDetail) ? 0.0 : invoiceDetail.getPrice());
			rectifierDetail.setDiscountExpression((taxDataInDetail) ? new DiscountExpression("0.0") : invoiceDetail.getDiscountExpression());
			rectifierDetail.setSource(InvoiceSource.DIRECT_INVOICE);
			rectifierDetail.setSourceId(null);
			rectifierDetail.setTaxableBase(getPriceStrategy().getBasePrice(rectifierDetail));
			rectifierDetail.setWorkPlace(invoiceDetail.getWorkPlace());
			if (taxDataInDetail) {
				criteria = new Criteria();
				criteria.addEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_INVOICE_DETAIL_ID), invoiceDetail.getId());
				criteria.addEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_TAX_TYPE), TaxType.VAT);
				Iterator<?> iterator = invoiceTaxBean.getList(criteria).iterator();
				if (iterator.hasNext()) {
					InvoiceTax invoiceTax = (InvoiceTax)iterator.next();
					double quota = CommonUtil.round(invoiceDetail.getTaxableBase() * invoiceTax.getPercentage() / 100); 
					rectifierDetail.setVatPercent(invoiceTax.getPercentage());
					rectifierDetail.setVatQuota(CommonUtil.round(quota * percent * (-1) / 100));
				}
			}
			invoiceDetailBean.insert(rectifierDetail);
		}
	}

	private void createRectifierInvoiceFinances(Invoice rectifier, Invoice invoice) throws ManagerBeanException {
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
			rectifierFinance.setAmount(CommonUtil.round(0 - finance.getTotalAmount()));
			rectifierFinance.setExpenses(0);
			rectifierFinance.setConcept(rectifier.getDocumentNumber()); 
			rectifierFinance.setDueDate(finance.getDueDate());
			rectifierFinance.setPayMethod(finance.getPayMethod());
			rectifierFinance.setBank(finance.getBank());
			rectifierFinance.setBankAccount(finance.getBankAccount());
			rectifierFinance.setFinanceStatus(FinanceStatus.PENDING);
			rectifierFinance.setSecurityLevel(finance.getSecurityLevel());
			rectifierFinance.setScope(finance.getScope());
			financeBean.insert(rectifierFinance);
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

}
