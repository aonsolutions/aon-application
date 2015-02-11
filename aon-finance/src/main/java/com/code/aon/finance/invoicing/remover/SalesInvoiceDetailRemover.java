package com.code.aon.finance.invoicing.remover;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.enumeration.SalesDetailStatus;
import com.code.aon.sales.enumeration.SalesStatus;

public class SalesInvoiceDetailRemover implements IInvoiceDetailRemover {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalesInvoiceDetailRemover.class.getName());

	@Override
	public boolean accept(InvoiceSource source) {
		return source.equals(InvoiceSource.SALES);
	}

	@Override
	public void removeDetail(InvoiceDetail invoiceDetail) throws InvoicingException{
		try {
			if (invoiceDetail.getSourceId() != null) {
				IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
				SalesDetail salesDetail = (SalesDetail)salesDetailBean.get(invoiceDetail.getSourceId());
				salesDetail.setDelivered(0);
				salesDetail.setStatus(SalesDetailStatus.PENDING);
				salesDetailBean.update(salesDetail);

				IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
				Sales sales = salesDetail.getSales();
				if (sales.getStatus() != SalesStatus.PENDING) {
					sales.setStatus(SalesStatus.PENDING);
					salesBean.update(sales);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error removing Details", e);
			throw new InvoicingException(e.getMessage(),e);
		}
	}

}