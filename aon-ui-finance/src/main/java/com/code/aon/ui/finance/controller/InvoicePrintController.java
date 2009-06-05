package com.code.aon.ui.finance.controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.finance.util.EmailUtilController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.EmailSender;

public class InvoicePrintController extends InvoiceController implements IFinanceConstants {
	
	private static final Logger LOGGER = Logger.getLogger(InvoicePrintController.class.getName());
	
	private IPriceStrategy priceStrategy;
	
	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

	public double getInvoiceTotalPrice() throws ManagerBeanException{
		Invoice invoice = (Invoice)this.getModel().getRowData();
		return getPriceStrategy().getTotalPrice(invoice, invoice);
	}

	public void sendInvoicesByEmail( ActionEvent event ) {
		/*
		EmailUtilController emailController = (EmailUtilController) AonUtil.getRegisteredBean(EMAIL_UTIL_CONTROLLER_NAME);
		try {
			EmailSender sender = emailController.getEmailSender();
			sender.connect();
			Criteria criteria = getCriteria();
			List<ITransferObject> list = getManagerBean().getList(criteria);
			for( ITransferObject to : list ) {
				emailController.sendInvoice( (Invoice) to );	
			}
			sender.disconnect();
		} catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
			AonUtil.addErrorMessage(th.getMessage());
			throw new AbortProcessingException(th.getMessage(), th);
		}
		*/
	}	
}