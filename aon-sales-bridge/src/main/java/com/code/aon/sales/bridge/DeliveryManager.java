package com.code.aon.sales.bridge;

import java.util.Date;
import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.sales.enumeration.SalesDetailStatus;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.DeliveryDetailSource;
import com.code.aon.warehouse.enumeration.DeliveryDetailType;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

public class DeliveryManager {

	public Delivery salesDelivery(Sales sales, String series, int number, Date issueDate, Warehouse warehouse, DeliveryDetailType type) 
		throws ManagerBeanException {
		updateSalesStatus(sales);
		Delivery delivery = createDelivery(sales, series, number, issueDate);
		createDeliveryDetails(delivery, sales, warehouse, type);
		return delivery;
	}

	private void updateSalesStatus(Sales sales) throws ManagerBeanException {
		IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
		sales.setStatus(SalesStatus.CLOSED);
		salesBean.restoreNullSubPOJOs(sales);
		salesBean.update(sales);
	}

	private Delivery createDelivery(Sales sales, String series, int number, Date issueDate) throws ManagerBeanException {
		Delivery delivery = new Delivery();
		delivery.setSeries(series);
		delivery.setNumber((number > 0) ? number : obtainMaxNumber(series));
		delivery.setCustomer(sales.getCustomer());
		delivery.setRaddress(sales.getShippingAddress());
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
		delivery.setBank(sales.getBank());
		delivery.setBankAccount(sales.getBankAccount());

		IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
		return (Delivery)deliveryBean.insert(delivery);
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
    	return SeriesNumberUtil.obtainNumber(seriesId, "Delivery");
	}

	private void createDeliveryDetails(Delivery delivery, Sales sales, Warehouse warehouse, DeliveryDetailType type) throws ManagerBeanException {
		int line = 0;

		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(salesDetailBean.getFieldName(ISalesAlias.SALES_DETAIL_SALES_ID), sales.getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(salesDetailBean.getFieldName(ISalesAlias.SALES_DETAIL_STATUS), SalesDetailStatus.SETTLED));
		criteria.addOrder(salesDetailBean.getFieldName(ISalesAlias.SALES_DETAIL_LINE));
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
			deliveryDetail.setType(type);
			deliveryDetail.setSource(DeliveryDetailSource.SALES);
			deliveryDetail.setSalesDetail(salesDetail);
			deliveryDetailBean.insert(deliveryDetail);

			salesDetail.setDelivered(salesDetail.getQuantity());
			salesDetail.setStatus(SalesDetailStatus.SETTLED);
			salesDetailBean.update(salesDetail);
		}
	}

	public DeliveryDetail transferDeliveryDetail(Delivery delivery, SalesDetail salesDetail, Warehouse warehouse) throws ManagerBeanException {
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
		deliveryDetail.setType(DeliveryDetailType.MANUAL);
		deliveryDetail.setSource(DeliveryDetailSource.SALES);
		deliveryDetail.setSalesDetail(salesDetail);
		deliveryDetail = (DeliveryDetail)deliveryDetailBean.insert(deliveryDetail);

		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		salesDetail.setDelivered(salesDetail.getDelivered() + salesDetail.getTransfered());
		salesDetail.setStatus((salesDetail.getQuantity() > salesDetail.getDelivered()) ? SalesDetailStatus.PARTIAL_SETTLED : SalesDetailStatus.SETTLED);
		salesDetailBean.update(salesDetail);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(salesDetailBean.getFieldName(ISalesAlias.SALES_DETAIL_SALES_ID), salesDetail.getSales().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(salesDetailBean.getFieldName(ISalesAlias.SALES_DETAIL_STATUS), SalesDetailStatus.SETTLED));
		if (salesDetailBean.getCount(criteria) == 0) {
			updateSalesStatus(salesDetail.getSales());
		}

		return deliveryDetail;
	}

	private	Integer calculateNextLine(Delivery delivery) throws ManagerBeanException {
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
		Projection projection = Projection.max(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_LINE));
		Object value = deliveryDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

}
