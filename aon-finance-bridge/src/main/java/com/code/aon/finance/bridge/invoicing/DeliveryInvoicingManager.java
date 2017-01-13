package com.code.aon.finance.bridge.invoicing;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.ProgressionInvoicingFeedBack;
import com.code.aon.finance.invoicing.engine.IInvoicingEngine;
import com.code.aon.finance.invoicing.engine.InvoicingEngineFactory;
import com.code.aon.finance.invoicing.engine.delivery.DeliveryInvoicingDAO;
import com.code.aon.finance.invoicing.engine.delivery.DeliveryInvoicingEngine;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.seller.Seller;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.enumeration.DeliveryStatus;
import com.esferalia.aon.entity.IEntityAlias;

public class DeliveryInvoicingManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryInvoicingManager.class.getName());

	private IPriceStrategy priceStrategy;
	private FinanceGenerator financeGenerator;

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

	public Invoice invoice(Delivery delivery, String series, int number, Date issueDate) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			Invoice invoice = createInvoice(delivery, series, number, issueDate);
			createInvoiceDetails(invoice, delivery);
			double invoiceTotal = getPriceStrategy().getTotalPrice(invoice, invoice);
			if (invoiceTotal != 0) {
				if (delivery.getPayMethod() != null && delivery.getPayMethod().getId() != null) {
					getFinanceGenerator().generateFinances(invoice, delivery, invoiceTotal, true, true);
				} else {
					getFinanceGenerator().generateFinances(invoice, invoiceTotal, true);
				}
			}
			updateDeliveryStatus(sessionName, delivery);

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

	private Invoice createInvoice(Delivery delivery, String series, int number, Date issueDate) throws ManagerBeanException {
		Invoice invoice = new Invoice();
		invoice.setProject(delivery.getProject());
		invoice.setSeries(StringUtils.isNotBlank(series) ? series : null);
		invoice.setNumber((number > 0) ? number : obtainMaxNumber(series));
		invoice.setRegistry(delivery.getCustomer().getRegistry());
		invoice.setRegistryDocument(delivery.getCustomer().getRegistry().getDocument());
		invoice.setRegistryDocumentType(delivery.getCustomer().getRegistry().getDocumentType());
		invoice.setRegistryDocumentCountry(delivery.getCustomer().getRegistry().getDocumentCountry());
		invoice.setRegistryName(delivery.getCustomer().getRegistry().getFullName());
		invoice.setRegistryAddress(delivery.getRegistryAddress());
		invoice.setIssueDate(issueDate);
		invoice.setTaxDate(issueDate);
		invoice.setSecurityLevel(delivery.getSecurityLevel());
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.SALES);
		invoice.setScope(delivery.getScope());
		invoice.setSeller(obtainSeller(delivery.getId()));

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoiceBean.restoreNullSubPOJOs(invoice);
		return (Invoice)invoiceBean.insert(invoice);
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
    	return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}

	private Seller obtainSeller(Integer deliveryId) throws ManagerBeanException {
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_DELIVERY_ID), deliveryId);
		criteria.addNotNullExpression(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_SALES_DETAIL_ID));
		criteria.addOrder(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_LINE));
		for (ITransferObject ito : deliveryDetailBean.getList(criteria)) {
			DeliveryDetail deliveryDetail = (DeliveryDetail)ito;
			if (deliveryDetail.getSalesDetail().getSales().getSeller() != null) {
				return deliveryDetail.getSalesDetail().getSales().getSeller();
			}
		}
		return null;
	}

	private void createInvoiceDetails(Invoice invoice, Delivery delivery) throws ManagerBeanException {
		int line = 0;
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
		criteria.addOrder(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_LINE));
		List<ITransferObject> deliveryDetailList = deliveryDetailBean.getList(criteria);
		for (ITransferObject ito : deliveryDetailList) {
			DeliveryDetail deliveryDetail = (DeliveryDetail)ito;
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setProject(delivery.getProject());
			invoiceDetail.setLine(++line);
			invoiceDetail.setItem(deliveryDetail.getItem());
			invoiceDetail.setDescription(deliveryDetail.getDescription());
			invoiceDetail.setQuantity(deliveryDetail.getQuantity());
			invoiceDetail.setPrice(deliveryDetail.getPrice());
			invoiceDetail.setDiscountExpression(deliveryDetail.getDiscountExpression());
			invoiceDetail.setWorkPlace(delivery.getWorkPlace());
			invoiceDetail.setSource(InvoiceSource.DELIVERY);
			invoiceDetail.setSourceId(deliveryDetail.getId());
			invoiceDetail.setSeller((deliveryDetail.getSalesDetail() != null) ? deliveryDetail.getSalesDetail().getSales().getSeller() : null);
			invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
			invoiceDetail.getInvoice().setUpdateEnabled(line == deliveryDetailList.size());
			invoiceDetailBean.restoreNullSubPOJOs(invoiceDetail);
			invoiceDetailBean.insert(invoiceDetail);
		}
	}

	public void transferDeliveries(Invoice invoice, List<Delivery> deliveryList, List<ITransferObject> invoicedDeliveryList) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			for (ITransferObject ito : invoicedDeliveryList) {
				Delivery delivery = (Delivery)ito;
				if (!deliveryList.contains(delivery)) {
					removeInvoicedDelivery(invoice, delivery);
				}
				deliveryList.remove(delivery);
			}

			InvoicingEngineFactory.register(InvoicingEngineFactory.DELIVERY_ENGINE_KEY, new DeliveryInvoicingEngine());
			IInvoicingEngine engine = InvoicingEngineFactory.getInvoicingEngine(InvoicingEngineFactory.DELIVERY_ENGINE_KEY);
			engine.setInvoicingDAO(new DeliveryInvoicingDAO());
			engine.setInvoicingFeedBack(new ProgressionInvoicingFeedBack());
			engine.setHibernateSession(HibernateUtil.getSession(sessionName));
			((DeliveryInvoicingEngine)engine).invoiceDeliveryList(invoice, deliveryList);

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
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

	private void removeInvoicedDelivery(Invoice invoice, Delivery delivery) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
		List<ITransferObject> deliveryDetailList = deliveryDetailBean.getList(criteria);
		for (ITransferObject ito : deliveryDetailList) {
			DeliveryDetail deliveryDetail = (DeliveryDetail)ito;
			criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.DELIVERY);
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE_ID), deliveryDetail.getId());
			if (invoiceDetailBean.getList(criteria).iterator().hasNext()) {
				InvoiceDetail invoiceDetail = (InvoiceDetail)invoiceDetailBean.getList(criteria).iterator().next();
				invoiceDetail.setUpdateEnabled(deliveryDetailList.indexOf(deliveryDetail) == (deliveryDetailList.size() - 1));
				invoiceDetailBean.remove(invoiceDetail);
			}
		}
	}

	private void updateDeliveryStatus(String sessionName, Delivery delivery) throws ManagerBeanException {
		IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
		delivery.setStatus(DeliveryStatus.INVOICED);
		deliveryBean.restoreNullSubPOJOs(delivery);
		delivery = (Delivery)HibernateUtil.getSession(sessionName).merge(delivery);	
		delivery = (Delivery)deliveryBean.update(delivery);
	}

}
