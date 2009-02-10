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
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

public class DeliveryInvoiceDetailRemover implements IInvoiceDetailRemover {
	
	private static final Logger LOGGER = Logger.getLogger(FeeInvoiceDetailRemover.class.getName());
	
	public void removeDetail(InvoiceDetail invoiceDetail) {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class.getName());
			invoiceDetailBean.remove(invoiceDetail);
			DeliveryDetail deliveryDetail = obtainDeliveryDetail(invoiceDetail.getSourceId());
			/* ACTUALIZA EL ESTADO DEL ALBARÁN */
			updateDelivery(deliveryDetail.getDelivery());
			/* BORRAR LA LINEA DE FACTURA */
			invoiceDetailBean.remove(invoiceDetail);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Error removing Details");
			LOGGER.log(Level.SEVERE, "Error removing Details", e);
			throw new AbortProcessingException(e.getMessage());
		}
	}
	
	private void updateDelivery(Delivery delivery) throws ManagerBeanException {
		IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
		delivery.setStatus(DeliveryStatus.PENDING);
		deliveryBean.update(delivery);
	}

	@SuppressWarnings("unchecked")
	private DeliveryDetail obtainDeliveryDetail(Integer deliveryDetailId) throws ManagerBeanException {
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_ID), deliveryDetailId);
		Iterator iter = deliveryDetailBean.getList(criteria, 0, 1).iterator();
		if(iter.hasNext()){
			return (DeliveryDetail)iter.next();
		}
		return null;
	}
}