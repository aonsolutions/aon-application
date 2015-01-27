package com.code.aon.ui.purchase.controller;

import static com.code.aon.ui.common.ICommonMessages.PURCHASE_EMAIL_BODY_HEADER;
import static com.code.aon.ui.common.ICommonMessages.PURCHASE_SEND_EMAIL_ERROR_COUNT;
import static com.code.aon.ui.common.ICommonMessages.PURCHASE_SEND_EMAIL_FNINISH;
import static com.code.aon.ui.common.ICommonMessages.PURCHASE_SEND_EMAIL_NUMBER;
import static com.code.aon.ui.common.ICommonMessages.PURCHASE_SEND_EMAIL_SENDED_COUNT;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.purchase.Purchase;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.purchase.util.PurchaseEmailUtil;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.IMailAccount;
import com.esferalia.aon.entity.IEntityAlias;

public class PurchasePrintController extends PurchaseController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
				controller.updateMessageBody( emailUtil.getEmailContent(body, AonUtil.getMessage(PURCHASE_EMAIL_BODY_HEADER)) );
			} catch (Throwable th) {
				LOGGER.error(th.getMessage(), th);
				AonUtil.addErrorMessage(th.getMessage());
				throw new AbortProcessingException(th.getMessage(), th);
			}
		}
	}	
	
	@SuppressWarnings("unchecked")
	public void onSendPurchasesByEmail( ActionEvent event ) {
		LogPanelController logger = LogPanelController.getInstance();
		PurchaseReportManager purchaseReportManager = (PurchaseReportManager) AonUtil.getRegisteredBean(PURCHASE_REPORT_CONTROLLER_NAME);
		purchaseReportManager.setValued(true);
		PurchaseController controller = (PurchaseController) AonUtil.getRegisteredBean(PURCHASE_CONTROLLER_NAME);
		PurchaseEmailUtil emailUtil = controller.getEmailController();
		MessageController messageController = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		messageController.setShowNewMessageWindow(false);
		String subject = messageController.getSubject();
		String content = messageController.getContent();
		IMailAccount account = messageController.getSenderMailAccount();
		try {
			emailUtil.changeMailAccount(account);
			emailUtil.setNumberOfMessagesPerTransport(10);
    		String idAlias = getManagerBean().getFieldName(IEntityAlias.PURCHASE_ID);
    		ProjectionList pl = new ProjectionList(Projection.property(idAlias));
    		List<Integer> ids = getManagerBean().getList(pl, getCriteria());
			logger.info( AonUtil.getMessage(PURCHASE_SEND_EMAIL_NUMBER, ids.size()) );
    		for( int i = 0; i < ids.size(); i++ ) {
				if ( logger.isActivePoll() ) {
					Purchase purchase = (Purchase) getManagerBean().get(ids.get(i));
					super.fireBeforeEmailSend(event, purchase);
					emailUtil.sendPurchase( i+1, purchase, getMoreRecipients(), messageController.getRecipientsCc(), messageController.getRecipientsBcc(), subject, content  );
					super.updatePurchaseCommunication((Purchase) purchase);
				} else {
					break;
				}
			}
			logger.info( AonUtil.getMessage(PURCHASE_SEND_EMAIL_SENDED_COUNT) + (ids.size()-logger.getErrors().size()) );
			logger.info( AonUtil.getMessage(PURCHASE_SEND_EMAIL_ERROR_COUNT) + logger.getErrors().size() );
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			AonUtil.addErrorMessage(th.getMessage());
			throw new AbortProcessingException(th.getMessage(), th);
		} finally {
			emailUtil.close();
			logger.info( AonUtil.getMessage(PURCHASE_SEND_EMAIL_FNINISH) );
			logger.finish();
			this.initializeModel();
		}
	}
	
}