package com.code.aon.ui.marketing.controller;

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
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.enumeration.ActionTargetStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.ui.webmail.controller.WebMailController;
import com.code.aon.webmail.bean.AonMessage;
import com.code.aon.webmail.bean.AonMessageUtils;
import com.code.aon.webmail.bean.AonServer;

public class EmailCommunicationController implements IMarketingConstants {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SurveyResponseController.class.getName());
	
	private CommunicationCenterController getCommunicationController() {
		return (CommunicationCenterController) AonUtil.getRegisteredBean(COMMUNICATION_CENTER_CONTROLLER_NAME);
	}
	
	private ActionTarget getNextActionTarget() throws ManagerBeanException {
    	CommunicationCenterController ccc = getCommunicationController();
    	List<ActionTarget> list = ccc.getActionTargets(true);
    	if (! list.isEmpty() ) {
    		ActionTarget actionTarget = list.get(0);
    		ccc.setActionTarget(actionTarget);
    		return actionTarget;
    	}
    	return null;
	}
	
	private List<String> getEmails( Target target ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteria = new Criteria();
		String mediaType = bean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_MEDIA_TYPE);
		criteria.addEqualExpression(mediaType, MediaType.EMAIL);
		String registryId = bean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID);
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
	
	private boolean sendEmail( List<String> emails ) {
		boolean result = true;
		try {
			MessageController messageController = (MessageController)AonUtil.getRegisteredBean(WebMailConstants.BEAN_MESSAGE);
			String recipientsTo = StringUtils.join(emails, ",");
			messageController.setRecipientsTo(recipientsTo);
	    	AonMessage aonMessage = messageController.compoundMessage();
	    	WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(WebMailConstants.BEAN_WEBMAIL);
	    	AonServer server = webMailController.getServer();
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
    	try {
			ActionTarget actionTarget = getNextActionTarget();
			while ( actionTarget != null ) {
				List<String> emails = getEmails(actionTarget.getTarget());
				if ( emails.isEmpty() ) {
					actionTarget.setStatus(ActionTargetStatus.CANCEL);
				} else {
					if ( sendEmail(emails) ) {
						actionTarget.setStatus(ActionTargetStatus.SENT);
					} else {
						actionTarget.setStatus(ActionTargetStatus.INCORRECT);
					}
				}
				updateActionTarget(actionTarget);
				actionTarget = getNextActionTarget();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error retrieving next ActionTarget", e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		getCommunicationController().onInit(event);
    }
	
}
