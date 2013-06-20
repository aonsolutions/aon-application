package com.code.aon.ui.purchase.controller;

import static com.code.aon.faces.controller.IRichConstants.LOG_PANEL_CONTROLLER_NAME;
import static com.code.aon.ui.purchase.IPurchaseMessages.BUNDLE_KEY;
import static com.code.aon.ui.purchase.IPurchaseMessages.PURCHASE_SEND_EMAIL_NUMBER;
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
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.IMailAccount;

public class PurchasePrintController extends PurchaseController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchasePrintController.class.getName());
	
	public void onInitSendEmail( ActionEvent event ) {
		MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		controller.onPrepareEmailWindow(event);
		if ( controller.isShowNewMessageWindow() ) {			
			controller.setAppendSignature(false);
			controller.setSaveSent(false);
			try {		
				controller.onNewMessage(event);
				PurchaseController purchaseController = (PurchaseController) AonUtil.getRegisteredBean(IPurchaseConstants.PURCHASE_CONTROLLER_NAME);
				PurchaseEmailUtil emailUtil = purchaseController.getEmailController();
				emailUtil.initMessageController(controller);
				controller.setSubject( emailUtil.getEmailSubject() );
				String body = emailUtil.getEmailBody();
				controller.updateMessageBody( emailUtil.getEmailContent(body, AonUtil.getMessage(IPurchaseMessages.BUNDLE_KEY, IPurchaseMessages.PURCHASE_EMAIL_BODY_HEADER)) );
			} catch (Throwable th) {
				LOGGER.error(th.getMessage(), th);
				AonUtil.addErrorMessage(th.getMessage());
				throw new AbortProcessingException(th.getMessage(), th);
			}
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
					emailUtil.sendPurchase( (Purchase) to, getMoreRecipients(), messageController.getRecipientsCc(), messageController.getRecipientsBcc(), subject, content  );
					super.updatePurchaseCommunication((Purchase) to);
				} else {
					break;
				}
			}
			logger.info( AonUtil.getMessage(BUNDLE_KEY, IPurchaseMessages.PURCHASE_SEND_EMAIL_SENDED_COUNT) + (list.size()-logger.getErrors().size()) );
			logger.info( AonUtil.getMessage(BUNDLE_KEY, IPurchaseMessages.PURCHASE_SEND_EMAIL_ERROR_COUNT) + logger.getErrors().size() );
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			AonUtil.addErrorMessage(th.getMessage());
			throw new AbortProcessingException(th.getMessage(), th);
		} finally {
			logger.info( AonUtil.getMessage(BUNDLE_KEY, IPurchaseMessages.PURCHASE_SEND_EMAIL_FNINISH) );
			logger.finish();
			messageController.setShowNewMessageWindow(false);
			this.initializeModel();
		}
	}	
	
}