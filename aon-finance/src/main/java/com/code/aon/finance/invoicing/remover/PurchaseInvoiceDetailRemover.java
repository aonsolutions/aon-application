package com.code.aon.finance.invoicing.remover;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;
import com.code.aon.purchase.enumeration.PurchaseStatus;

public class PurchaseInvoiceDetailRemover implements IInvoiceDetailRemover {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseInvoiceDetailRemover.class.getName());

	@Override
	public boolean accept(InvoiceSource source) {
		return source.equals(InvoiceSource.PURCHASE);
	}

	@Override
	public void removeDetail(InvoiceDetail invoiceDetail) throws InvoicingException{
		try {
			if (invoiceDetail.getSourceId() != null) {
				IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
				PurchaseDetail purchaseDetail = (PurchaseDetail)purchaseDetailBean.get(invoiceDetail.getSourceId());
				purchaseDetail.setDelivered(0);
				purchaseDetail.setStatus(PurchaseDetailStatus.PENDING);
				purchaseDetailBean.update(purchaseDetail);

				IManagerBean purchaseBean = BeanManager.getManagerBean(Purchase.class);
				Purchase purchase = purchaseDetail.getPurchase();
				if (purchase.getStatus() != PurchaseStatus.PENDING) {
					purchase.setStatus(PurchaseStatus.PENDING);
					purchaseBean.update(purchase);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error removing Details", e);
			throw new InvoicingException(e.getMessage(),e);
		}
	}

}