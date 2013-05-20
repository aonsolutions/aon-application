package com.code.aon.ui.purchase.controller;

import static com.code.aon.faces.controller.IRichConstants.LOG_PANEL_CONTROLLER_NAME;
import static com.code.aon.ui.purchase.IPurchaseMessages.BUNDLE_KEY;
import static com.code.aon.ui.purchase.IPurchaseMessages.PURCHASE_SEND_EMAIL_NUMBER;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_CONFIG;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.purchase.Purchase;
import com.code.aon.ui.purchase.IPurchaseMessages;
import com.code.aon.ui.purchase.util.PurchaseEmailUtil;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.IMailAccount;

public class PurchasePrintController extends PurchaseController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchasePrintController.class.getName());
	
	public void onInitSendEmail( ActionEvent event ) {
		try {		
			MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
			if (mailConfig.getMailAccountCount() > 0) {
				MessageController messageController = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
				messageController.initNewMessage();
				PurchaseController controller = (PurchaseController) AonUtil.getRegisteredBean(IPurchaseConstants.PURCHASE_CONTROLLER_NAME);
				PurchaseEmailUtil emailUtil = controller.getEmailController();
				messageController.setSubject( emailUtil.getEmailSubject() );
				String body = emailUtil.getEmailBody();
				messageController.updateMessageBody( emailUtil.getEmailContent(body, AonUtil.getMessage(IPurchaseMessages.BUNDLE_KEY, IPurchaseMessages.PURCHASE_EMAIL_BODY_HEADER)) );
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
	
	public void onSendPurchasesByEmail( ActionEvent event ) {
		LogPanelController logger = LogPanelController.getInstance();
		PurchaseReportManager purchaseReportManager = (PurchaseReportManager) AonUtil.getRegisteredBean(PURCHASE_REPORT_CONTROLLER_NAME);
		purchaseReportManager.setValued(true);
		PurchaseController controller = (PurchaseController) AonUtil.getRegisteredBean(PURCHASE_CONTROLLER_NAME);
		PurchaseEmailUtil emailUtil = controller.getEmailController();
		MessageController messageController = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		String subject = messageController.getSubject();
		String content = messageController.getContent();
		IMailAccount account = messageController.getSenderMailAccount();
		try {
			emailUtil.changeMailAccount(account);
			List<ITransferObject> list = getManagerBean().getList(getCriteria());
			logger.info( AonUtil.getMessage(BUNDLE_KEY, PURCHASE_SEND_EMAIL_NUMBER, list.size()) );
			LogPanelController logPanel = (LogPanelController) AonUtil.getRegisteredBean(LOG_PANEL_CONTROLLER_NAME);
			for( ITransferObject to : list ) {
				if ( logPanel.isActivePoll() ) {
					super.fireBeforeEmailSend(event, to);
					emailUtil.sendPurchase( (Purchase) to, getMoreRecipients(), subject, content  );
					super.setPurchaseSended((Purchase) to);
				} else {
					break;
				}
			}
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			AonUtil.addErrorMessage(th.getMessage());
			throw new AbortProcessingException(th.getMessage(), th);
		} finally {
			logger.info( AonUtil.getMessage(BUNDLE_KEY, IPurchaseMessages.PURCHASE_SEND_EMAIL_FNINISH) );			
			messageController.setShowNewMessageWindow(false);
		}
	}	
	
}