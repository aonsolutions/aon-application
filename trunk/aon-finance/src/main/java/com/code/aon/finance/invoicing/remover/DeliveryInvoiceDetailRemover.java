package com.code.aon.finance.invoicing.remover;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

public class DeliveryInvoiceDetailRemover implements IInvoiceDetailRemover {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryInvoiceDetailRemover.class.getName());

	@Override
	public boolean accept(InvoiceSource source) {
		return source.equals(InvoiceSource.DELIVERY);
	}

	@Override
	public void removeDetail(InvoiceDetail invoiceDetail) throws InvoicingException{
		try {
			if (invoiceDetail.getSourceId() != null) {
				IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
				DeliveryDetail deliveryDetail = (DeliveryDetail)deliveryDetailBean.get(invoiceDetail.getSourceId());

				IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
				Delivery delivery = deliveryDetail.getDelivery();
				delivery.setStatus(DeliveryStatus.PENDING);
				deliveryBean.update(delivery);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error removing Details", e);
			throw new InvoicingException(e.getMessage(),e);
		}
	}

}