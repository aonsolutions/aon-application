package com.code.aon.ui.marketing.controller;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.SEND_EMAIL_FINISH;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.commercial.Target;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.enumeration.ActionTargetStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.bean.AonMessage;
import com.code.aon.webmail.bean.AonServer;

public class EmailCommunicationController implements IMarketingConstants {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SurveyResponseController.class.getName());
	
	private CommunicationCenterController getCommunicationController() {
		return (CommunicationCenterController) AonUtil.getRegisteredBean(COMMUNICATION_CENTER_CONTROLLER_NAME);
	}
	
	private List<String> getEmails( Target target ) throws ManagerBeanException {
		String[] list = CompanyEmailUtil.getCommercialEmails(target.getRegistry());
		if (! ArrayUtils.isEmpty(list) ) {
			List<String> emails = new LinkedList<String>();
			for( String value : list ) {
				String email = StringUtils.trimToNull(value);
				if ( EmailValidator.getInstance().isValid(email) ) {
					emails.add(email);
				} else {
					log( TARGET_INVALID_EMAIL, true, target, email );
				}							
			}
			return emails;
		}
		return Collections.emptyList();
	}
	
	private boolean sendEmail( MessageController messageController, AonServer server, List<String> emails ) {
		boolean result = true;
		try {
			String recipients = StringUtils.join(emails, ",");
	    	LOGGER.debug( "Sending email to: {}", recipients );			
			messageController.setRecipientsBcc(recipients);
	    	AonMessage aonMessage = messageController.compoundMessage(server);
	    	server.sendMessage(aonMessage);
		} catch ( Throwable th ) {
			LOGGER.error("Error sending email to " + emails, th );
			result = false;
		}
		return result;
	}
	
	private void updateActionTarget( ActionTarget actionTarget) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ActionTarget.class);
		bean.update(actionTarget);
	}	
	
	private void log( String message, boolean error, Target target, String argument) {
		String rawText = AonUtil.getMessage(BUNDLE_NAME, message);
		String targetName = target.getRegistry().getFullName();
		String text = MessageFormat.format(rawText, targetName, argument);
		LogPanelController logger = LogPanelController.getInstance();
		if ( error ) {
			logger.error( text );
		} else {
			logger.info( text );	
		}
	}
	
	private boolean sendEmail( AonServer server, List<ActionTarget> list ) throws ManagerBeanException {
		LogPanelController logger = LogPanelController.getInstance();
		MessageController messageController = (MessageController)AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MESSAGE);
		List<String> emails = new LinkedList<String>();
		for( ActionTarget actionTarget : list ) {    		
			List<String> targetEmails = getEmails(actionTarget.getTarget());
			if ( targetEmails.isEmpty() ) {					
				String rawText = AonUtil.getMessage(BUNDLE_NAME, TARGET_WITHOUT_COMMERCIAL_EMAIL);
				logger.error( MessageFormat.format(rawText, actionTarget.getTarget().getRegistry().getFullName()) );				
				actionTarget.setStatus(ActionTargetStatus.CANCEL);				
			} else {
				emails.addAll(targetEmails);
			}
		}
		ActionTargetStatus status = ActionTargetStatus.INCORRECT;
		if ( sendEmail(messageController, server, emails) ) {
			status = ActionTargetStatus.SENT;
		}
		for( ActionTarget actionTarget : list ) {
			if ( actionTarget.getStatus() != ActionTargetStatus.CANCEL ) {
				actionTarget.setStatus(status);
			}
			updateActionTarget(actionTarget);
		}		
		return (status == ActionTargetStatus.SENT);
	}
	
	private void logResult( List<ActionTarget> list, int offset, boolean sent) throws ManagerBeanException {
		LogPanelController logger = LogPanelController.getInstance();
		Target target = null;
		String emails = null;
		if ( list.size() == 1) {
			target = ((ActionTarget)list.get(0)).getTarget();
			emails = StringUtils.join( getEmails(target), ", ");
		}
		if ( sent ) {
			if ( target != null ) {
				log( TARGET_EMAIL_SENT, false, target, emails );				
			} else {
				String rawText = AonUtil.getMessage(BUNDLE_NAME, TARGET_BULK_EMAIL_SENT);
				logger.info( MessageFormat.format(rawText, offset+1, (offset + list.size())) );
			}
		} else {
			if ( target != null ) {
				log( TARGET_SEND_EMAIL_ERROR, true, target, emails );				
			} else {
				String rawText = AonUtil.getMessage(BUNDLE_NAME, TARGET_SEND_BULK_EMAIL_ERROR);
				logger.error( MessageFormat.format(rawText, offset+1, (offset + list.size())) );				
			}			
		}
	}

	private void updateMessageContent( MessageController messageController ) {
		String content = messageController.getContent();
		Locale locale = AonUtil.getCurrentLocale();
		String timeStamp = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, locale).format(new Date());
		content = StringUtils.replace(content, "${timestamp}", timeStamp);
		messageController.setContent(content);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void send(ActionEvent event) {
    	LogPanelController logger = LogPanelController.getInstance();
    	try {
    		CommunicationCenterController ccc = getCommunicationController();
    		MessageController messageController = (MessageController)AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MESSAGE);
    		updateMessageContent(messageController);
    		AonServer server = new AonServer(messageController.getSenderMailAccount());
    		IManagerBean bean = BeanManager.getManagerBean(ActionTarget.class);
    		Criteria criteria = ccc.getPendingTargetsCriteria(bean);
    		int count = ccc.getNumberOfTargetsInEmail();
    		int offset = ccc.getPendingTargets() - count;
    		List<ActionTarget> list = null;
    		do {
    			list = (List) bean.getList(criteria, offset, count);
    			if (! list.isEmpty() ) {
    				logResult( list, offset, sendEmail(server, list) );
    				count = Math.min(offset, count);
    				offset -= count;
    			}
    		} while ( count > 0 );
		} catch (Throwable e) {
			LOGGER.error("Error sending emails", e);
			logger.error( e.getMessage() );
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			logger.info( AonUtil.getMessage(IWebMailConstants.BUNDLE_NAME, SEND_EMAIL_FINISH) );			
		}
		getCommunicationController().onInit(event);
    }
	
}