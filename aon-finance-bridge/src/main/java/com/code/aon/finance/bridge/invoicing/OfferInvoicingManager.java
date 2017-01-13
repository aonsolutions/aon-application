package com.code.aon.finance.bridge.invoicing;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.bridge.util.SalesBridgeUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class OfferInvoicingManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(SalesInvoicingManager.class.getName());

	private SalesBridgeUtil salesBridgeUtil;
	private IPriceStrategy priceStrategy;
	private FinanceGenerator financeGenerator;

	public SalesBridgeUtil getSalesBridgeUtil() {
		if (salesBridgeUtil == null) {
			salesBridgeUtil = new SalesBridgeUtil();
		}
		return salesBridgeUtil;
	}

	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

	public FinanceGenerator getFinanceGenerator() {
		if (financeGenerator == null) {
			financeGenerator = new FinanceGenerator();
		}
		return financeGenerator;
	}

	public Invoice invoice(Offer offer, String series, int number, Date issueDate) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			Invoice invoice = createInvoice(offer, series, number, issueDate);
			createInvoiceDetails(sessionName, invoice, offer);
			double invoiceTotal = getPriceStrategy().getTotalPrice(invoice, invoice);
			if (invoiceTotal != 0) {
				if (offer.getPayMethod() != null && offer.getPayMethod().getId() != null) {
					getFinanceGenerator().generateFinances(invoice, offer, invoiceTotal, true, true);
				} else {
					getFinanceGenerator().generateFinances(invoice, invoiceTotal, true);
				}
			}
			updateOfferStatus(sessionName, offer);

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);

			return invoice;
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg,daoe);
			}
			LOGGER.error(e.getMessage());
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private Invoice createInvoice(Offer offer, String series, int number, Date issueDate) throws ManagerBeanException {
		Customer customer = getSalesBridgeUtil().obtainCustomer(offer);

		Invoice invoice = new Invoice();
		invoice.setProject(offer.getProject());
		invoice.setSeries(StringUtils.isNotBlank(series) ? series : null);
		invoice.setNumber((number > 0) ? number : obtainMaxNumber(series));
		invoice.setRegistry(customer.getRegistry());
		invoice.setRegistryDocument(customer.getRegistry().getDocument());
		invoice.setRegistryDocumentType(customer.getRegistry().getDocumentType());
		invoice.setRegistryDocumentCountry(customer.getRegistry().getDocumentCountry());
		invoice.setRegistryName(customer.getRegistry().getFullName());
		invoice.setRegistryAddress(offer.getAddress());
		invoice.setIssueDate(issueDate);
		invoice.setTaxDate(issueDate);
		invoice.setSecurityLevel(offer.getSecurityLevel());
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.SALES);
		invoice.setScope(offer.getScope());
		invoice.setSeller(offer.getSeller());

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoiceBean.restoreNullSubPOJOs(invoice);
		return (Invoice)invoiceBean.insert(invoice);
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
    	return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}

	private void createInvoiceDetails(String sessionName, Invoice invoice, Offer offer) throws ManagerBeanException {
		int line = 0;
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ID), offer.getId());
		criteria.addNotNullExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_ITEM_ID));
		criteria.addOrder(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_LINE));
		List<ITransferObject> offerDetailList = offerDetailBean.getList(criteria);
		for (ITransferObject ito : offerDetailList) {
			OfferDetail offerDetail = (OfferDetail)ito;
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setProject(offer.getProject());
			invoiceDetail.setLine(++line);
			invoiceDetail.setItem(offerDetail.getItem());
			invoiceDetail.setDescription(offerDetail.getDescription());
			invoiceDetail.setQuantity(offerDetail.getQuantity());
			invoiceDetail.setPrice(offerDetail.getPrice());
			invoiceDetail.setDiscountExpression(offerDetail.getDiscountExpression());
			invoiceDetail.setSource(InvoiceSource.OFFER);
			invoiceDetail.setSourceId(offerDetail.getId());
			invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
			invoiceDetail.setSeller(offer.getSeller());
			invoiceDetail.setWorkPlace(offer.getWorkPlace());
			invoiceDetail.getInvoice().setUpdateEnabled(line == offerDetailList.size());
			invoiceDetailBean.restoreNullSubPOJOs(invoiceDetail);
			invoiceDetailBean.insert(invoiceDetail);

			offerDetail.setStatus(OfferDetailStatus.ON_INVOICE);
			offerDetailBean.restoreNullSubPOJOs(offerDetail);
			offerDetail = (OfferDetail)HibernateUtil.getSession(sessionName).merge(offerDetail);	
			offerDetailBean.update(offerDetail);
		}
	}

	private void updateOfferStatus(String sessionName, Offer offer) throws ManagerBeanException {
		IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
		offer.setStatus(OfferStatus.INVOICED);
		offerBean.restoreNullSubPOJOs(offer);
		offer = (Offer)HibernateUtil.getSession(sessionName).merge(offer);	
		offerBean.update(offer);
	}

}
