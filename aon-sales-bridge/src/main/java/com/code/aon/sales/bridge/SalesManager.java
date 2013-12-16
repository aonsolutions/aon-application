package com.code.aon.sales.bridge;

import java.util.Date;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.bridge.util.SalesBridgeUtil;
import com.code.aon.sales.enumeration.DocumentType;
import com.code.aon.sales.enumeration.SalesDetailStatus;
import com.code.aon.sales.enumeration.SalesStatus;
import com.esferalia.aon.entity.IEntityAlias;

public class SalesManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(SalesManager.class.getName());
	
	private SalesBridgeUtil salesBridgeUtil;

	public SalesBridgeUtil getSalesBridgeUtil() {
		if (salesBridgeUtil == null) {
			salesBridgeUtil = new SalesBridgeUtil();
		}
		return salesBridgeUtil;
	}

	public Sales salesOrder(Offer offer, String series, int number, Date issueDate) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);
			
			updateOfferStatus(offer);
			Sales sales = createSales(offer, series, number, issueDate);
			createSalesDetails(sales, offer);

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
			
			return sales;
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg,daoe);
			}
			LOGGER.error(e.getMessage());
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private void updateOfferStatus(Offer offer) throws ManagerBeanException {
		IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
		offer.setStatus(OfferStatus.APPROVED);
		offerBean.restoreNullSubPOJOs(offer);
		offerBean.update(offer);
	}

	private Sales createSales(Offer offer, String series, int number, Date issueDate) throws ManagerBeanException {
		Customer customer = getSalesBridgeUtil().obtainCustomer(offer);

		Sales sales = new Sales();
		sales.setProject(offer.getProject());
		sales.setSeries(series);
		sales.setNumber((number > 0) ? number : obtainMaxNumber(series));
		sales.setCustomer(customer);
		sales.setShippingAddress(offer.getAddress());
		sales.setSeller(offer.getSeller());
		sales.setDiscountExpression(offer.getDiscountExpression());
		sales.setIssueDate(issueDate);
		sales.setDocumentType(offer.getType().equals(OfferType.INTERNET) ? DocumentType.INTERNET : DocumentType.NORMAL);
		sales.setSecurityLevel(offer.getSecurityLevel());
		sales.setStatus(SalesStatus.PENDING);
		sales.setWorkPlace(offer.getWorkPlace());
		sales.setScope(offer.getScope());
		sales.setPayMethod(offer.getPayMethod());
		sales.setNumberOfPayments(offer.getNumberOfPayments());
		sales.setDaysToFirstPayment(offer.getDaysToFirstPayment());
		sales.setDaysBetweenPayments(offer.getDaysBetweenPayments());
		sales.setPaymentDays(offer.getPaymentDays());
		sales.setBankAccount(offer.getBankAccount());
		sales.setBankAlias(offer.getBankAlias());
		sales.setBic(offer.getBic());

		IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
		return (Sales)salesBean.insert(sales);
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
    	return SeriesNumberUtil.obtainNumber(seriesId, "Sales");
	}

	private void createSalesDetails(Sales sales, Offer offer) throws ManagerBeanException {
		int line = 0;

		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ID), offer.getId());
		criteria.addEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_STATUS), OfferDetailStatus.PENDING);
		criteria.addOrder(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_LINE));
		Iterator<?> iterator = offerDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			OfferDetail offerDetail = (OfferDetail)iterator.next();
			if (offerDetail.getItem() != null && offerDetail.getItem().getId() != null) {
				SalesDetail salesDetail = new SalesDetail();
				salesDetail.setSales(sales);
				salesDetail.setLine(++line);
				salesDetail.setItem(offerDetail.getItem());
				salesDetail.setDescription(offerDetail.getDescription());
				salesDetail.setQuantity(offerDetail.getQuantity());
				salesDetail.setPrice(offerDetail.getPrice());
				salesDetail.setDiscountExpression(offerDetail.getDiscountExpression());
				salesDetail.setStatus(SalesDetailStatus.PENDING);
				salesDetail.setOfferDetail(offerDetail);
				salesDetailBean.insert(salesDetail);
			}

			offerDetail.setStatus(OfferDetailStatus.ON_SALE);
			offerDetailBean.update(offerDetail);
		}
	}

}
