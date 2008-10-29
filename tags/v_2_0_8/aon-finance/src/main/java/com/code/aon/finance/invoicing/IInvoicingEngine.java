package com.code.aon.finance.invoicing;

import com.code.aon.common.ManagerBeanException;

public interface IInvoicingEngine {

	public void invoice(InvoicingParameters params) throws ManagerBeanException;
	
	public IInvoicingDAO getInvoicingDAO();
	
	public void setInvoicingDAO(IInvoicingDAO invoicingDAO);
	
	public IInvoicingFeedBack getInvoicingFeedBack();
	
	public void setInvoicingFeedBack(IInvoicingFeedBack invoicingFeedBack);
	
}
