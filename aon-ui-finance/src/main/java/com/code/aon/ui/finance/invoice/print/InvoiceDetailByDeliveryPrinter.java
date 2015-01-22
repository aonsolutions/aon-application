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
import com.code.aon.finance.Finance;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceDetailByDeliveryPrinter {

	private static final Logger LOGGER = Logger.getLogger(InvoiceDetailByDeliveryPrinter.class.getName());

	public InvoiceDetailByDeliveryPrinter getInstance() {
		return new InvoiceDetailByDeliveryPrinter();
	}

	public Double getPrepaymentsTotal(Integer invoiceId, boolean productTypeOrder) {
		Double total = null;
		Collection<InvoiceDetail> collection = getCollection(invoiceId, productTypeOrder, true);
		if(!collection.isEmpty()){
			total = 0.0;
			for(InvoiceDetail detail: collection){
				total += detail.getTaxableBase();
			}
		}
		return total!=null?total:null;
	}

	public Double getIncreaseTotal(Integer invoiceId, boolean productTypeOrder) {
		Double total = null;
		Collection<InvoiceDetail> collection = getCollection(invoiceId, productTypeOrder, false, true, null);
		if(!collection.isEmpty()){
			total = 0.0;
			for(InvoiceDetail detail: collection){
				total += detail.getTaxableBase();
			}
		}
		return total!=null?total:null;
	}
	
	public Double getTotalCommercialProduct(Integer invoiceId) {
		Double total = null;
		Collection<InvoiceDetail> collection = getCollection(invoiceId, false, null, ProductType.COMMERCIAL_PRODUCT);
		if(!collection.isEmpty()){
			total = 0.0;
			for(InvoiceDetail detail: collection){
				total += detail.getTaxableBase();
			}
		}
		return total!=null?total:null;
	}
	
	public Collection<InvoiceDetail> getCollection(Integer invoiceId, boolean productTypeOrder) {
		return getCollection(invoiceId, productTypeOrder, null);
	}
	
	public Collection<InvoiceDetail> getCollection(Integer invoiceId, boolean productTypeOrder, Boolean searchPrepayments) {
		return getCollection(invoiceId, productTypeOrder, searchPrepayments, true, null);
	}
	
	public Collection<InvoiceDetail> getCollection(Integer invoiceId, boolean productTypeOrder, Boolean searchPrepayments, Boolean searchIncrease) {
		return getCollection(invoiceId, productTypeOrder, searchPrepayments, searchIncrease, null);
	}

	public Collection<InvoiceDetail> getCollection(Integer invoiceId, boolean productTypeOrder, Boolean searchPrepayments, ProductType type) {
		return getCollection(invoiceId, productTypeOrder, searchPrepayments, true, type);
	}
	
	public Collection<InvoiceDetail> getCollection(Integer invoiceId, boolean productTypeOrder, Boolean searchPrepayments, Boolean searchIncrease, ProductType type) {
		List<InvoiceDetail> invoiceDetailList = new LinkedList<InvoiceDetail>();
		Map<Integer, List<InvoiceDetail>> deliveryMap = new HashMap<Integer, List<InvoiceDetail>>();
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoiceId);
			if (searchPrepayments!=null) {
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_PREPAYMENT), searchPrepayments);
			}
			if (searchIncrease!=null && searchIncrease) {
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ITEM_PRODUCT_TYPE), ProductType.INCREASE);
			} else if (searchIncrease!=null && !searchIncrease) {
				criteria.addNotEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ITEM_PRODUCT_TYPE), ProductType.INCREASE);
			}
			if (productTypeOrder) {
				criteria.addOrder(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ITEM_PRODUCT_TYPE));
			}
			if (type!=null) {
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ITEM_PRODUCT_TYPE), type);
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
	
	public Double getTotalFinanceAdvance(Integer invoiceId) {
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), invoiceId);
			criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_ADVANCE), true);

			Projection projection = Projection.sum(financeBean.getFieldName(IEntityAlias.FINANCE_AMOUNT));
			Object value = financeBean.getUniqueResult(projection, criteria);
			return ((value!=null)?((Double)value):(null));
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining finances total amount", e);
		}
		return null;
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