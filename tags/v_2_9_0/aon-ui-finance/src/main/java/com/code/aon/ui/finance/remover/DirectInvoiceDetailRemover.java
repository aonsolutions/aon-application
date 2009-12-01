package com.code.aon.ui.finance.remover;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class DirectInvoiceDetailRemover implements IInvoiceDetailRemover {

	private static final Logger LOGGER = Logger.getLogger(DirectInvoiceDetailRemover.class.getName());
	
	@Override
	public void removeDetail(InvoiceDetail invoiceDetail) {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			if(invoiceDetail.getDeliveryDetail() != null){
				removeDeliveryDetail(invoiceDetail.getDeliveryDetail());
			}
			invoiceDetailBean.remove(invoiceDetail);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Error removing Details");
			LOGGER.log(Level.SEVERE, "Error removing Details", e);
			throw new AbortProcessingException(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private void removeDeliveryDetail(Integer deliveryDetailId) throws ManagerBeanException {
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_ID), deliveryDetailId);
		Iterator iter = deliveryDetailBean.getList(criteria, 0, 1).iterator();
		if(iter.hasNext()){
			DeliveryDetail deliveryDetail = (DeliveryDetail)iter.next();
			removeSalesDetail(deliveryDetail.getSalesDetail());
			removeParentDelivery(deliveryDetail.getDelivery());
		}
	}

	private void removeSalesDetail(SalesDetail salesDetail) throws ManagerBeanException {
		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		salesDetailBean.remove(salesDetail);
		removeParentSales(salesDetail.getSales());
	}

	private void removeParentDelivery(Delivery delivery) throws ManagerBeanException {
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
		if(deliveryDetailBean.getCount(criteria) == 0 ){
			IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
			deliveryBean.remove(delivery);
		}
	}

	private void removeParentSales(Sales sales) throws ManagerBeanException {
		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(salesDetailBean.getFieldName(ISalesAlias.SALES_DETAIL_SALES_ID), sales.getId());
		if(salesDetailBean.getCount(criteria) == 0 ){
			IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
			deliveryBean.remove(sales);
		}
	}
}