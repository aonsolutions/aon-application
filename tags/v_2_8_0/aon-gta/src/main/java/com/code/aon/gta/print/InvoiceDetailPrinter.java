package com.code.aon.gta.print;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.tas.SupportOrder;
import com.code.aon.tas.SupportOrderInsurance;
import com.code.aon.tas.dao.ITASAlias;
import com.code.aon.tasDelivery.TasDelivery;
import com.code.aon.tasDelivery.dao.ITasDeliveryAlias;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class InvoiceDetailPrinter {

	private static final Logger LOGGER = Logger.getLogger(InvoiceDetailPrinter.class.getName());

	public InvoiceDetailPrinter getInstance() {
		return new InvoiceDetailPrinter();
	}

	@SuppressWarnings("unchecked")
	public Collection getCollection(Integer invoiceId) {
		List<ReportInvoiceDetail> reportInvoiceDetailList = new LinkedList<ReportInvoiceDetail>();
		Map<Integer, List<ReportInvoiceDetail>> deliveryMap = new HashMap<Integer, List<ReportInvoiceDetail>>();
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoiceId);
			criteria.addOrder(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_ITEM_PRODUCT_TYPE));
			criteria.addOrder(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_ID));
			Iterator iterator = invoiceDetailBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				InvoiceDetail invoiceDetail = (InvoiceDetail)iterator.next();
				Delivery delivery = obtainDelivery(invoiceDetail.getSourceId());
				SupportOrder supportOrder = (delivery==null)?null:obtainSupportOrder(delivery.getId());

				ReportInvoiceDetail reportInvoiceDetail = new ReportInvoiceDetail();
				reportInvoiceDetail.setInvoiceDetail(invoiceDetail);
				reportInvoiceDetail.setDelivery(delivery);
				reportInvoiceDetail.setSupportOrder(supportOrder);
				reportInvoiceDetail.setSupportOrderInsurance((supportOrder==null)?null:obtainSupportOrderInsurance(supportOrder.getId()));

				if (delivery == null) {
					reportInvoiceDetailList.add(reportInvoiceDetail);
				} else {
					if (!deliveryMap.containsKey(delivery.getId())) {
						List<ReportInvoiceDetail> deliveryInvoiceDetailList = new LinkedList<ReportInvoiceDetail>();
						deliveryMap.put(delivery.getId(), deliveryInvoiceDetailList);
					}
					deliveryMap.get(delivery.getId()).add(reportInvoiceDetail);
				}
			}

			if (deliveryMap.size() > 0) {
				reportInvoiceDetailList.addAll(getListOrdered(deliveryMap));
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining invoiceDetailList Collection", e);
		} catch (ExpressionException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining invoiceDetailList Collection", e);
		}
		return reportInvoiceDetailList;
	}
	
	@SuppressWarnings("unchecked")
	protected Delivery obtainDelivery(Integer deliveryDetailId) {
		if (deliveryDetailId != null) {
			try {
				IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_ID), deliveryDetailId);
				Iterator iter = deliveryDetailBean.getList(criteria).iterator();
				if(iter.hasNext()){
					return ((DeliveryDetail)iter.next()).getDelivery();
				}
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error obtaining delivery of delivery detail with id= " + deliveryDetailId, e);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected SupportOrder obtainSupportOrder(Integer deliveryId) {
		if (deliveryId != null) {
			try {
				IManagerBean tasDeliveryBean = BeanManager.getManagerBean(TasDelivery.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(tasDeliveryBean.getFieldName(ITasDeliveryAlias.TAS_DELIVERY_DELIVERY_ID), deliveryId);
				Iterator iter = tasDeliveryBean.getList(criteria).iterator();
				if(iter.hasNext()){
					return ((TasDelivery)iter.next()).getSupportOrder();
				}
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error obtaining TAS delivery of delivery with id= " + deliveryId, e);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected SupportOrderInsurance obtainSupportOrderInsurance(Integer supportOrderId) {
		if (supportOrderId != null) {
			try {
				IManagerBean supportOrderInsuranceBean = BeanManager.getManagerBean(SupportOrderInsurance.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(supportOrderInsuranceBean.getFieldName(ITASAlias.SUPPORT_ORDER_INSURANCE_SUPPORT_ORDER_ID), supportOrderId);
				Iterator iter = supportOrderInsuranceBean.getList(criteria).iterator();
				if(iter.hasNext()){
					return (SupportOrderInsurance)iter.next();
				}
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error obtaining Support Order Insurance of support Order with id= " + supportOrderId, e);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected List<ReportInvoiceDetail> getListOrdered(Map<Integer, List<ReportInvoiceDetail>> deliveryMap) throws ManagerBeanException, ExpressionException {
		List<ReportInvoiceDetail> reportInvoiceDetailList = new LinkedList<ReportInvoiceDetail>();
		IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
		Criteria criteria = new Criteria();
		Iterator iterator = deliveryMap.keySet().iterator();
		while (iterator.hasNext()) {
			criteria.addOrExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_ID), ((Integer)iterator.next()).toString());
		}
		criteria.addOrder(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_ISSUE_TIME));
		criteria.addOrder(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_SERIES));
		criteria.addOrder(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_NUMBER));
		iterator = deliveryBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			Delivery delivery = (Delivery)iterator.next();
			reportInvoiceDetailList.addAll(deliveryMap.get(delivery.getId()));
		}

		return reportInvoiceDetailList;
	}

}