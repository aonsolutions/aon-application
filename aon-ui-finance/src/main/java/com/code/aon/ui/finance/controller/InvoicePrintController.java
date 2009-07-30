package com.code.aon.ui.finance.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.mail.MessagingException;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.finance.util.EmailUtilController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.EmailSender;
import com.code.aon.webmail.SecurityInfo;

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

	public void onSendInvoicesBySignedEmail( ActionEvent event ) {
		InvoiceSignerController invoiceSigner = (InvoiceSignerController) AonUtil.getRegisteredBean(INVOICE_SIGNER_CONTROLLER_NAME);
		if (! invoiceSigner.resolveCertificado() ) {
			return;
		}
		try {		
			sendInvoicesByEmail( invoiceSigner.getSecurityInfo() );
		} catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
			AonUtil.addErrorMessage(th.getMessage());
			throw new AbortProcessingException(th.getMessage(), th);
		} finally {
			invoiceSigner.setShowSignWindow(false);
		}
	}	
	
	public void onSendInvoicesByEmail( ActionEvent event ) {
		try {		
			sendInvoicesByEmail( null );
		} catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
			AonUtil.addErrorMessage(th.getMessage());
			throw new AbortProcessingException(th.getMessage(), th);
		}
	}	

	public void sendInvoicesByEmail( SecurityInfo si ) throws UnsupportedEncodingException, MessagingException, ManagerBeanException {
		EmailUtilController emailController = (EmailUtilController) AonUtil.getRegisteredBean(EMAIL_UTIL_CONTROLLER_NAME);
		EmailSender sender = emailController.getEmailSender();
		sender.connect();
		Criteria criteria = getCriteria();
		List<ITransferObject> list = getManagerBean().getList(criteria);
		for( ITransferObject to : list ) {
			emailController.sendInvoice( (Invoice) to, si );	
		}
		sender.disconnect();
	}	
	
}