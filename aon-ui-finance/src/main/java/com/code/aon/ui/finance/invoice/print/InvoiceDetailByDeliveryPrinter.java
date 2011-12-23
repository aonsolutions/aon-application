package com.code.aon.ui.finance.invoice.print;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceDetailByDeliveryPrinter {

	private static final Logger LOGGER = Logger.getLogger(InvoiceDetailByDeliveryPrinter.class.getName());

	public InvoiceDetailByDeliveryPrinter getInstance() {
		return new InvoiceDetailByDeliveryPrinter();
	}

	@SuppressWarnings("unchecked")
	public Collection getCollection(Integer invoiceId, boolean productTypeOrder) {
		List<InvoiceDetail> invoiceDetailList = new LinkedList<InvoiceDetail>();
		Map<Integer, List<InvoiceDetail>> deliveryMap = new HashMap<Integer, List<InvoiceDetail>>();
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoiceId);
			if (productTypeOrder) {
				criteria.addOrder(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ITEM_PRODUCT_TYPE));
			}
			criteria.addOrder(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE));
			for (ITransferObject ito : invoiceDetailBean.getList(criteria)) {
				InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
				Delivery delivery = obtainDelivery(invoiceDetail);
				if (delivery == null) {
					invoiceDetailList.add(invoiceDetail);
				} else {
					if (!deliveryMap.containsKey(delivery.getId())) {
						deliveryMap.put(delivery.getId(), new LinkedList<InvoiceDetail>());
					}
					deliveryMap.get(delivery.getId()).add(invoiceDetail);
				}
			}

			if (deliveryMap.size() > 0) {
				invoiceDetailList.addAll(getListOrdered(deliveryMap));
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining invoiceDetailList Collection", e);
		} catch (ExpressionException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining invoiceDetailList Collection", e);
		}
		return invoiceDetailList;
	}
	
	protected Delivery obtainDelivery(InvoiceDetail invoiceDetail) {
		if (invoiceDetail.isDeliverySource() && invoiceDetail.getSourceId() != null) {
			try {
				return ((DeliveryDetail)BeanManager.getManagerBean(DeliveryDetail.class).get(invoiceDetail.getSourceId())).getDelivery();
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error obtaining delivery of delivery detail with id= " + invoiceDetail.getSourceId(), e);
			}
		}
		return null;
	}

	protected List<InvoiceDetail> getListOrdered(Map<Integer, List<InvoiceDetail>> deliveryMap) throws ManagerBeanException, ExpressionException {
		List<InvoiceDetail> invoiceDetailList = new LinkedList<InvoiceDetail>();
		IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
		Criteria criteria = new Criteria();
		for (Integer deliveryId : deliveryMap.keySet()) {
			criteria.addOrExpression(deliveryBean.getFieldName(IEntityAlias.DELIVERY_ID), deliveryId.toString());
		}
		criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_ISSUE_TIME));
		criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_SERIES));
		criteria.addOrder(deliveryBean.getFieldName(IEntityAlias.DELIVERY_NUMBER));
		for (ITransferObject ito : deliveryBean.getList(criteria)) {
			invoiceDetailList.addAll(deliveryMap.get(((Delivery)ito).getId()));
		}

		return invoiceDetailList;
	}

}