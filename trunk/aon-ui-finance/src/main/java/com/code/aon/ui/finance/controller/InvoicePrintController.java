package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.finance.IFinanceMessages.BUNDLE_KEY;
import static com.code.aon.ui.finance.IFinanceMessages.FINANCE_INVOICE_SEND_EMAIL_FNINISH;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_CONFIG;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ui.finance.util.FinanceEmailUtil;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.IMailAccount;

public class InvoicePrintController extends InvoiceController implements IFinanceConstants {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InvoicePrintController.class.getName());
	
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
	
	public void onInitSendEmail( ActionEvent event ) {
		try {		
			MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
			if (mailConfig.getMailAccountCount() > 0) {
				MessageController messageController = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
				messageController.initNewMessage();
				messageController.setAppendSignature(true);
				InvoiceController controller = (InvoiceController) AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
				FinanceEmailUtil emailUtil = controller.getEmailController();
				messageController.setSubject( emailUtil.getEmailSubject() );
				String body = emailUtil.getEmailBody();
				messageController.updateMessageBody( emailUtil.getEmailContent(body) );
				messageController.setShowNewMessageWindow(true);
			} else {
				AonUtil.addErrorMessageFromBundle(IWebMailConstants.BUNDLE_NAME, IWebMailConstants.NOT_MAIL_ACCOUNTS);
			}
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			AonUtil.addErrorMessage(th.getMessage());
			throw new AbortProcessingException(th.getMessage(), th);
		}
	}	
	
	public void onSendInvoicesByEmail( ActionEvent event ) {
		InvoiceController controller = (InvoiceController) AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
		FinanceEmailUtil emailUtil = controller.getEmailController();
		MessageController messageController = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		String subject = messageController.getSubject();
		String content = messageController.getContent();
		IMailAccount account = messageController.getSenderMailAccount();
		try {
			emailUtil.changeMailAccount(account);
			List<ITransferObject> list = getManagerBean().getList(getCriteria());
			for( ITransferObject to : list ) {
				emailUtil.sendInvoice( (Invoice) to, subject, content  );	
			}
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			AonUtil.addErrorMessage(th.getMessage());
			throw new AbortProcessingException(th.getMessage(), th);
		} finally {
			LogPanelController logger = LogPanelController.getInstance();
			logger.info( AonUtil.getMessage(BUNDLE_KEY, FINANCE_INVOICE_SEND_EMAIL_FNINISH) );			
			messageController.setShowNewMessageWindow(false);
		}
	}	
	
}