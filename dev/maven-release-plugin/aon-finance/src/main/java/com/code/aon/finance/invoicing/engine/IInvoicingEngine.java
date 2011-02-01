package com.code.aon.finance.invoicing.engine;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.invoicing.IInvoicingFeedBack;
import com.code.aon.finance.invoicing.InvoicingParameters;

public interface IInvoicingEngine {

	public void invoice(InvoicingParameters params) throws ManagerBeanException;
	
	public IInvoicingDAO getInvoicingDAO();
	
	public void setInvoicingDAO(IInvoicingDAO invoicingDAO);
	
	public IInvoicingFeedBack getInvoicingFeedBack();
	
	public void setInvoicingFeedBack(IInvoicingFeedBack invoicingFeedBack);
	
}
