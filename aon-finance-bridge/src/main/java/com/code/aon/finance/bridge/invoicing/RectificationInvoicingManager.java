package com.code.aon.finance.bridge.invoicing;

import java.util.Date;
import java.util.Iterator;
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
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;

public class RectificationInvoicingManager {

	private IPriceStrategy priceStrategy;

	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

	public Invoice specialRectifyInvoice(Invoice invoice, String series, int number, Date issueDate, String cause, double percent) throws ManagerBeanException {
		Invoice rectifier = createRectifierInvoice(invoice, series, number, issueDate, cause, RectificationType.SPECIAL_RECTIFIER);
		createRectifierInvoiceDetails(rectifier, invoice, true, percent);
		createInvoiceAddress(rectifier, invoice);
		// No se duplican los vencimientos, puesto que lo único que cambia es la cuota de IVA.
		updateRectifiedInvoice(rectifier, invoice);
		return rectifier;
	}
	
	public Invoice rectifyInvoice(Invoice invoice, String series, int number, Date issueDate, String cause) throws ManagerBeanException {
		Invoice rectifier = createRectifierInvoice(invoice, series, number, issueDate, cause, RectificationType.NORMAL_RECTIFIER);
		createRectifierInvoiceDetails(rectifier, invoice, false, 0.0);
		createInvoiceAddress(rectifier, invoice);
		createRectifierInvoiceFinances(rectifier, invoice);
		updateRectifiedInvoice(rectifier, invoice);
		return rectifier;
	}

	private Invoice createRectifierInvoice(Invoice invoice, String series, int number, Date issueDate, String cause, RectificationType rectificationtype) throws ManagerBeanException {
		Invoice rectifier = new Invoice();
		rectifier.setId(null);
		rectifier.setSeries(series);
		rectifier.setNumber((number > 0) ? number : obtainMaxNumber(series));
		rectifier.setIssueDate(issueDate);
		rectifier.setTaxDate(issueDate);
		rectifier.setComments(cause);
		rectifier.setRectificationType(rectificationtype);
		rectifier.setRectificationInvoice(invoice);
		rectifier.setStatus(InvoiceStatus.PENDING);
		rectifier.setRegistry(invoice.getRegistry());
		rectifier.setRegistryDocument(invoice.getRegistryDocument());
		rectifier.setRegistryDocumentType(invoice.getRegistryDocumentType());
		rectifier.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry());
		rectifier.setRegistryName(invoice.getRegistryName());
		rectifier.setRegistryAddress(invoice.getRegistryAddress());
		rectifier.setSecurityLevel(invoice.getSecurityLevel());
		rectifier.setService(invoice.isService());
		rectifier.setType(invoice.getType());
		rectifier.setScope(invoice.getScope());
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
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
		criteria.addEqualExpression(invoiceAddressBean.getFieldName(IFinanceAlias.INVOICE_ADDRESS_INVOICE_ID), invoice.getId());
		Iterator<?> iterator = invoiceAddressBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			InvoiceAddress invoiceAddress = (InvoiceAddress) iterator.next();
			InvoiceAddress newAddress = new InvoiceAddress();
			newAddress.setInvoice(rectifier);
			newAddress.setStreetType(invoiceAddress.getStreetType());
			newAddress.setAddress(invoiceAddress.getAddress());
			newAddress.setNumber(invoiceAddress.getNumber());
			newAddress.setAddress2(invoiceAddress.getAddress2());
			newAddress.setZip(invoiceAddress.getZip());
			newAddress.setCity(invoiceAddress.getCity());
			newAddress.setGeozone(invoiceAddress.getGeozone());
			invoiceAddressBean.insert(newAddress);
		}
	}

	private void createRectifierInvoiceDetails(Invoice rectifier, Invoice invoice, boolean taxDataInDetail, double percent) throws ManagerBeanException {
		IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		criteria.addOrder(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_LINE));
		Iterator<?> iterator = invoiceDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)iterator.next();
			InvoiceDetail newDetail = new InvoiceDetail();
			newDetail.setId(null);
			newDetail.setLine(invoiceDetail.getLine());
			newDetail.setDescription(invoiceDetail.getDescription());
			newDetail.setItem(invoiceDetail.getItem());
			newDetail.setInvoice(rectifier);

			newDetail.setQuantity(taxDataInDetail?0.0:CommonUtil.round(0 - invoiceDetail.getQuantity(), 3));
			newDetail.setPrice(taxDataInDetail?0.0:invoiceDetail.getPrice());
			newDetail.setDiscountExpression(taxDataInDetail?new DiscountExpression("0"):invoiceDetail.getDiscountExpression());
			
			newDetail.setSource(obtainRectifierSource(invoiceDetail.getSource()));
			newDetail.setWorkPlace(invoiceDetail.getWorkPlace());
			newDetail.setSourceId(null);
			newDetail.setTaxableBase(getPriceStrategy().getBasePrice(newDetail));
			newDetail.setTaxDataInDetail( taxDataInDetail );
			if (taxDataInDetail) {
				Criteria c = new Criteria();
				c.addEqualExpression(invoiceTaxBean.getFieldName(IFinanceAlias.INVOICE_TAX_INVOICE_DETAIL_ID), invoiceDetail.getId());
				c.addEqualExpression(invoiceTaxBean.getFieldName(IFinanceAlias.INVOICE_TAX_TAX_TYPE), TaxType.VAT);
				Iterator<ITransferObject> iter = invoiceTaxBean.getList(c).iterator();
				if (iter.hasNext()) {
					InvoiceTax invoiceTax = (InvoiceTax) iter.next();
					newDetail.setVatPercent(invoiceTax.getPercentage());
					double quota = CommonUtil.round(getPriceStrategy().getBasePrice(invoiceDetail) * invoiceTax.getPercentage() / 100); 
					newDetail.setVatQuota(CommonUtil.round(quota * percent / 100));
				}
			}
			invoiceDetailBean.insert(newDetail);
		}
	}

	private InvoiceSource obtainRectifierSource(InvoiceSource source) {
		if (source == InvoiceSource.DELIVERY) {
			source = InvoiceSource.DIRECT_SALES;
		} else if (source == InvoiceSource.INCOME) {
			source = InvoiceSource.DIRECT_PURCHASE;
		} else if (source == InvoiceSource.OFFER) {
			source = InvoiceSource.DIRECT_INVOICE;
		} else if (source == InvoiceSource.FEE) {
			source = InvoiceSource.DIRECT_INVOICE;
		}
		return source;
	}

	private void createRectifierInvoiceFinances(Invoice rectifier, Invoice invoice) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID), invoice.getId());
		criteria.addOrder(financeBean.getFieldName(IFinanceAlias.FINANCE_ID));
		Iterator<?> iterator = financeBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			Finance newFinance = new Finance();
			newFinance.setId(null);
			newFinance.setInvoice(rectifier);
			newFinance.setConcept(newFinance.getInvoice().getDocumentNumber()); 
			newFinance.setAmount(CommonUtil.round(0 - finance.getTotalAmount()));
			newFinance.setFinanceStatus(FinanceStatus.PENDING);
			newFinance.setPayment(finance.isPayment());
			newFinance.setRegistry(finance.getRegistry());
			newFinance.setRegistryName(finance.getRegistryName());
			newFinance.setRegistryDocument(finance.getRegistryDocument());
			newFinance.setRegistryDocumentType(finance.getRegistryDocumentType());
			newFinance.setRegistryDocumentCountry(finance.getRegistryDocumentCountry());
			newFinance.setExpenses(0);
			newFinance.setDueDate(finance.getDueDate());
			newFinance.setPayMethod(finance.getPayMethod());
			newFinance.setBank(finance.getBank());
			newFinance.setBankAccount(finance.getBankAccount());
			newFinance.setSecurityLevel(finance.getSecurityLevel());
			newFinance.setScope(finance.getScope());
			financeBean.insert(newFinance);
		}
	}

	private void updateRectifiedInvoice(Invoice rectifier, Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoice.setRectificationInvoice(invoice.getRectificationType() == RectificationType.NONE ? rectifier : null);
		invoice.setRectificationType(RectificationType.RECTIFIED);
		invoiceBean.restoreNullSubPOJOs(invoice);
		String sessionFactoryName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		invoice = (Invoice) HibernateUtil.getSession(sessionFactoryName).merge(invoice);
		invoiceBean.update(invoice);
	}

}
