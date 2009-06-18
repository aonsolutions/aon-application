package com.code.aon.finance.bridge.invoicing.remover;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.finance.invoicing.remover.IInvoiceDetailRemover;
import com.code.aon.ql.Criteria;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

public class DeliveryInvoiceDetailRemover implements IInvoiceDetailRemover {
	
	private static final Logger LOGGER = Logger.getLogger(DeliveryInvoiceDetailRemover.class.getName());
	
	@Override
	public boolean accept(InvoiceSource source) {
		return (source.equals(InvoiceSource.DELIVERY));
	}

	@Override
	public void removeDetail(InvoiceDetail invoiceDetail) throws InvoicingException{
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class.getName());
			invoiceDetailBean.remove(invoiceDetail);
			DeliveryDetail deliveryDetail = obtainDeliveryDetail(invoiceDetail.getDeliveryDetail());
			/* ACTUALIZA EL ESTADO DEL ALBARÁN */
			updateDelivery(deliveryDetail.getDelivery());
			/* BORRAR LA LINEA DE FACTURA */
			invoiceDetailBean.remove(invoiceDetail);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error removing Details", e);
			throw new InvoicingException(e.getMessage(),e);
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