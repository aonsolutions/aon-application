package com.code.aon.ui.marketing.controller;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.SEND_EMAIL_FINISH;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.commercial.Target;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.enumeration.ActionTargetStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.bean.AonMessage;
import com.code.aon.webmail.bean.AonMessageUtils;
import com.code.aon.webmail.bean.AonServer;
import com.esferalia.aon.entity.IEntityAlias;

public class EmailCommunicationController implements IMarketingConstants {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SurveyResponseController.class.getName());
	
	private CommunicationCenterController getCommunicationController() {
		return (CommunicationCenterController) AonUtil.getRegisteredBean(COMMUNICATION_CENTER_CONTROLLER_NAME);
	}
	
	private List<String> getEmails( Target target ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteria = new Criteria();
		String mediaType = bean.getFieldName(IEntityAlias.REGISTRY_MEDIA_MEDIA_TYPE);
		criteria.addEqualExpression(mediaType, MediaType.EMAIL);
		String registryId = bean.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID);
		criteria.addEqualExpression(registryId, target.getRegistry().getId());
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			List<String> emails = new LinkedList<String>();
			for( ITransferObject to : list ) {
				RegistryMedia media = (RegistryMedia) to;
				String email = StringUtils.trimToNull(media.getValue());
				if ( AonMessageUtils.isValidEmail(email) ) {
					emails.add(email);
				} else {
					LOGGER.warn("Target " + target.getId() + " has invalid email: " + email );
				}
			}
			return emails;
		}
		return Collections.emptyList();
	}
	
	private boolean sendEmail( MessageController messageController, AonServer server, List<String> emails ) {
		boolean result = true;
		try {
			String recipientsTo = StringUtils.join(emails, ",");
			messageController.setRecipientsTo(recipientsTo);
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

    public void send(ActionEvent event) {
		LogPanelController logger = LogPanelController.getInstance();
    	try {
    		CommunicationCenterController ccc = getCommunicationController();
    		MessageController messageController = (MessageController)AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MESSAGE);
    		AonServer server = new AonServer(messageController.getSenderMailAccount());
    		for( ActionTarget actionTarget : ccc.getActionTargets() ) {    		
				List<String> emails = getEmails(actionTarget.getTarget());
    			String targetName = actionTarget.getTarget().getRegistry().getFullName();
				if ( emails.isEmpty() ) {
					actionTarget.setStatus(ActionTargetStatus.CANCEL);					
					String text = AonUtil.getMessage(BUNDLE_NAME, TARGET_WITHOUT_EMAIL);
					logger.error( MessageFormat.format(text, targetName) );					
				} else {
					if ( sendEmail(messageController, server, emails) ) {
						String text = AonUtil.getMessage(BUNDLE_NAME, IMarketingConstants.TARGET_EMAIL_SENT);
						logger.info( MessageFormat.format(text, targetName, emails) );											
						actionTarget.setStatus(ActionTargetStatus.SENT);
					} else {
						String text = AonUtil.getMessage(BUNDLE_NAME, TARGET_SEND_EMAIL_ERROR);
						logger.error( MessageFormat.format(text, targetName) );					
						actionTarget.setStatus(ActionTargetStatus.INCORRECT);
					}
				}
				updateActionTarget(actionTarget);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error retrieving next ActionTarget", e);
			logger.error( e.getMessage() );
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			logger.info( AonUtil.getMessage(IWebMailConstants.BUNDLE_NAME, SEND_EMAIL_FINISH) );			
		}
		getCommunicationController().onInit(event);
    }
	
}