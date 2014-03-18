package com.code.aon.ui.marketing.controller;

import java.io.Serializable;
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
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.enumeration.ActionTargetStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.bean.AonServer;
import com.esferalia.aon.entity.IEntityAlias;

public class EmailCommunicationController implements IMarketingConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
					log( ICommonMessages.TARGET_INVALID_EMAIL, true, target, email );
				}							
			}
			return emails;
		}
		return Collections.emptyList();
	}
	
	private boolean sendEmail( AonServer server, MessageController messageController, String recipients ) {
		boolean result = true;
		try {
	    	LOGGER.debug( "Sending email to: {}", recipients );			
			messageController.setRecipientsBcc(recipients);
			messageController.send(server);
		} catch ( Throwable th ) {
			LOGGER.error("Error sending email to " + recipients, th );
			result = false;
		}
		return result;
	}
	
	private void updateActionTarget( ActionTarget actionTarget) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ActionTarget.class);
		bean.update(actionTarget);
	}	
	
	private void log( String message, boolean error, Target target, String argument) {
		String rawText = AonUtil.getMessage(message);
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
				String rawText = AonUtil.getMessage(ICommonMessages.TARGET_WITHOUT_COMMERCIAL_EMAIL);
				logger.error( MessageFormat.format(rawText, actionTarget.getTarget().getRegistry().getFullName()) );				
				actionTarget.setStatus(ActionTargetStatus.CANCEL);				
			} else {
				emails.addAll(targetEmails);
				actionTarget.setStatus(ActionTargetStatus.PENDING);
			}
		}
		ActionTargetStatus status = ActionTargetStatus.INCORRECT;
		if (! emails.isEmpty() ) {
			String recipients = StringUtils.join(emails, ","); 
			if ( sendEmail(server, messageController, recipients) ) {
				status = ActionTargetStatus.SENT;
			}			
			logResult(list, recipients, status);
		}
		for( ActionTarget actionTarget : list ) {
			if ( actionTarget.getStatus() != ActionTargetStatus.CANCEL ) {
				actionTarget.setStatus(status);
			}
			updateActionTarget(actionTarget);
		}		
		return status==ActionTargetStatus.SENT;
	}
	
	private void logResult( List<ActionTarget> list, int offset, boolean sent) throws ManagerBeanException {
		LogPanelController logger = LogPanelController.getInstance();
		if ( list.size() > 1) {
			if ( sent ) {
				String rawText = AonUtil.getMessage(ICommonMessages.TARGET_BULK_EMAIL_SENT);
				logger.info( MessageFormat.format(rawText, offset+1, offset + list.size()) );
			} else {
				String rawText = AonUtil.getMessage(ICommonMessages.TARGET_SEND_BULK_EMAIL_ERROR);
				logger.error( MessageFormat.format(rawText, offset+1, offset + list.size()) );				
			}
		}
	}
	
	private void logResult( List<ActionTarget> list, String recipients, ActionTargetStatus status) {
		if ( list.size() == 1 ) {
			Target target = ((ActionTarget)list.get(0)).getTarget();
			if ( status == ActionTargetStatus.SENT ) {
				log( ICommonMessages.TARGET_EMAIL_SENT, false, target, recipients );				
			} else {
				log( ICommonMessages.TARGET_SEND_EMAIL_ERROR, true, target, recipients );				
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
	private List<ActionTarget> getActionTargetList( IManagerBean bean, List<Integer> ids, int offset, int count ) throws ManagerBeanException {
		List<Integer> subList = ids.subList(offset, Math.min(offset+count, ids.size()) );
		Criteria criteria = new Criteria();
		String id = bean.getFieldName(IEntityAlias.ACTION_TARGET_ID);
		criteria.addInExpression(id, subList);
		return (List) bean.getList(criteria);
	}
	
	@SuppressWarnings("unchecked")
	public void send(ActionEvent event) {
    	LogPanelController logger = LogPanelController.getInstance();
		MessageController messageController = (MessageController)AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MESSAGE);
    	try {
    		CommunicationCenterController ccc = getCommunicationController();
    		updateMessageContent(messageController);
    		AonServer server = new AonServer(messageController.getSenderMailAccount());
    		IManagerBean bean = BeanManager.getManagerBean(ActionTarget.class);
    		Criteria criteria = ccc.getPendingTargetsCriteria(bean);
    		criteria.addOrder("ActionTarget.target.registry.name");
    		String id = bean.getFieldName(IEntityAlias.ACTION_TARGET_ID);
    		ProjectionList pl = new ProjectionList(Projection.property(id));
    		List<Integer> ids = bean.getList(pl, criteria);
    		if (! ids.isEmpty() ) {
        		int count = ccc.getNumberOfTargetsInEmail();
        		int offset = 0;
        		do {
        			List<ActionTarget> list = getActionTargetList(bean, ids, offset, count);
        			if (! list.isEmpty() ) {
        				logResult( list, offset, sendEmail(server, list) );
        			}
    				offset += count;        			
        		} while ( offset < ids.size() );
    		}
		} catch (Throwable e) {
			LOGGER.error("Error sending emails", e);
			logger.error( e.getMessage() );
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			messageController.finishMessage();
			logger.info( AonUtil.getMessage(ICommonMessages.SEND_EMAIL_FINISH) );			
		}
		getCommunicationController().onInit(event);
    }
	
}