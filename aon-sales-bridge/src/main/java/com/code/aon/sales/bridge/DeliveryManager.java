package com.code.aon.sales.bridge;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.enumeration.SalesDetailStatus;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.enumeration.DeliveryStatus;
import com.esferalia.aon.entity.IEntityAlias;

public class DeliveryManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryManager.class.getName());

	public Delivery salesDelivery(Sales sales, String series, int number, Date issueDate, Warehouse warehouse) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);
			
			Delivery delivery = createDelivery(sales, series, number, issueDate);
			createDeliveryDetails(sessionName, delivery, sales, warehouse);
			updateSalesStatus(sessionName, sales);

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
			
			return delivery;
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

	private Delivery createDelivery(Sales sales, String series, int number, Date issueDate) throws ManagerBeanException {
		Delivery delivery = new Delivery();
		delivery.setProject(sales.getProject());
		delivery.setSeries(StringUtils.isNotBlank(series) ? series : null);
		delivery.setNumber((number > 0) ? number : obtainMaxNumber(series));
		delivery.setCustomer(sales.getCustomer());
		delivery.setRegistryAddress(sales.getShippingAddress());
		delivery.setIssueTime(issueDate);
		delivery.setSecurityLevel(sales.getSecurityLevel());
		delivery.setStatus(DeliveryStatus.PENDING);
		delivery.setWorkPlace(sales.getWorkPlace());
		delivery.setScope(sales.getScope());
		delivery.setPayMethod(sales.getPayMethod());
		delivery.setNumberOfPayments(sales.getNumberOfPayments());
		delivery.setDaysToFirstPayment(sales.getDaysToFirstPayment());
		delivery.setDaysBetweenPayments(sales.getDaysBetweenPayments());
		delivery.setPaymentDays(sales.getPaymentDays());
		delivery.setBankAccount(sales.getBankAccount());
		delivery.setBankAlias(sales.getBankAlias());
		delivery.setBic(sales.getBic());
		delivery.setCarrier(sales.getCarrier());
		delivery.setShippingPeriod(sales.getShippingPeriod());
		delivery.setShippingContact(sales.getShippingContact());
		delivery.setShippingAlternativeRecipient(sales.getShippingAlternativeRecipient());
		delivery.setShippingAlternativeAddress(sales.getShippingAlternativeAddress());
		delivery.setShippingAlternativeAddress2(sales.getShippingAlternativeAddress2());
		delivery.setShippingAlternativeZip(sales.getShippingAlternativeZip());
		delivery.setShippingAlternativeCity(sales.getShippingAlternativeCity());
		delivery.setShippingAlternativePhone(sales.getShippingAlternativePhone());
		
		IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
		deliveryBean.restoreNullSubPOJOs(delivery);
		return (Delivery)deliveryBean.insert(delivery);
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
    	return SeriesNumberUtil.obtainNumber(seriesId, "Delivery");
	}

	private void createDeliveryDetails(String sessionName, Delivery delivery, Sales sales, Warehouse warehouse) throws ManagerBeanException {
		int line = 0;

		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), sales.getId());
		criteria.addNotEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_STATUS), SalesDetailStatus.SETTLED);
		criteria.addOrder(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_LINE));
		Iterator<?> iterator = salesDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			SalesDetail salesDetail = (SalesDetail)iterator.next();
			DeliveryDetail deliveryDetail = new DeliveryDetail();
			deliveryDetail.setDelivery(delivery);
			deliveryDetail.setLine(++line);
			deliveryDetail.setItem(salesDetail.getItem());
			deliveryDetail.setDescription(salesDetail.getDescription());
			deliveryDetail.setWarehouse(warehouse);
			deliveryDetail.setQuantity(CommonUtil.round(salesDetail.getQuantity() - salesDetail.getDelivered()));
			deliveryDetail.setPrice(salesDetail.getPrice());
			deliveryDetail.setDiscountExpression(salesDetail.getDiscountExpression());
			deliveryDetail.setSalesDetail(salesDetail);
			deliveryDetailBean.restoreNullSubPOJOs(deliveryDetail);
			deliveryDetailBean.insert(deliveryDetail);

			salesDetail.setDelivered(salesDetail.getQuantity());
			salesDetail.setStatus(SalesDetailStatus.SETTLED);
			salesDetailBean.restoreNullSubPOJOs(salesDetail);
			salesDetail = (SalesDetail)HibernateUtil.getSession(sessionName).merge(salesDetail);	
			salesDetailBean.update(salesDetail);
		}
	}

	public void transferSalesDetails(Delivery delivery, List<SalesDetail> salesDetailList, Warehouse warehouse) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			for (SalesDetail salesDetail : salesDetailList) {
				if ((salesDetail.getPendingQuantity() > 0 && salesDetail.getTransfered() >= 0) 
						|| (salesDetail.getPendingQuantity() < 0 && salesDetail.getTransfered() <= 0)) {
					transferSalesDetail(sessionName, delivery, salesDetail, warehouse);
				}
			}

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

	private DeliveryDetail transferSalesDetail(String sessionName, Delivery delivery, SalesDetail salesDetail, Warehouse warehouse) throws ManagerBeanException {
		Double transferQuantity = salesDetail.getTransfered();
		boolean forcePendingQuantityCancel = salesDetail.isForcePendingQuantityCancel();
		
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		DeliveryDetail deliveryDetail = new DeliveryDetail();
		deliveryDetail.setDelivery(delivery);
		deliveryDetail.setLine(calculateNextLine(delivery));
		deliveryDetail.setItem(salesDetail.getItem());
		deliveryDetail.setDescription(salesDetail.getDescription());
		deliveryDetail.setWarehouse(warehouse);
		deliveryDetail.setQuantity(salesDetail.getTransfered());
		deliveryDetail.setPrice(salesDetail.getPrice());
		deliveryDetail.setDiscountExpression(salesDetail.getDiscountExpression());
		deliveryDetail.setSalesDetail(salesDetail);
		if (transferQuantity != 0) {
			deliveryDetailBean.restoreNullSubPOJOs(deliveryDetail);
			deliveryDetail = (DeliveryDetail)deliveryDetailBean.insert(deliveryDetail);
		}

		Project salesProject = salesDetail.getSales().getProject();
		if ((delivery.getProject() == null || delivery.getProject().getId() == null) && salesProject != null && salesProject.getId() != null ) {
			IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
			delivery.setProject(salesProject);
			deliveryBean.restoreNullSubPOJOs(delivery);
			delivery = (Delivery)HibernateUtil.getSession(sessionName).merge(delivery);	
			delivery = (Delivery)deliveryBean.update(delivery);
		}

		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		salesDetail = (SalesDetail) salesDetailBean.get(salesDetail.getId());
		salesDetail.setForcePendingQuantityCancel(forcePendingQuantityCancel);
		salesDetail.setDelivered(CommonUtil.round(salesDetail.getDelivered() + transferQuantity, 3));
		salesDetail.setStatus((salesDetail.getPendingQuantity() > 0) ? SalesDetailStatus.PARTIAL_SETTLED : SalesDetailStatus.SETTLED);
		salesDetailBean.restoreNullSubPOJOs(salesDetail);
		salesDetail = (SalesDetail)HibernateUtil.getSession(sessionName).merge(salesDetail);	
		salesDetailBean.update(salesDetail);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), salesDetail.getSales().getId());
		criteria.addNotEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_STATUS), SalesDetailStatus.SETTLED);
		if (salesDetailBean.getCount(criteria) == 0) {
			updateSalesStatus(sessionName, salesDetail.getSales());
		}

		return deliveryDetail;
	}

	private	Integer calculateNextLine(Delivery delivery) throws ManagerBeanException {
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
		Projection projection = Projection.max(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_LINE));
		Object value = deliveryDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

	private void updateSalesStatus(String sessionName, Sales sales) throws ManagerBeanException {
		IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
		sales.setStatus(SalesStatus.SERVED);
		salesBean.restoreNullSubPOJOs(sales);
		sales = (Sales)HibernateUtil.getSession(sessionName).merge(sales);	
		salesBean.update(sales);
	}

}
