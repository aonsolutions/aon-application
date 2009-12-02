package com.code.aon.ui.marketing.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.bridge.plugin.UserManager;
import com.code.aon.commercial.Target;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.enumeration.ActionTargetStatus;
import com.code.aon.messaging.sms.Message;
import com.code.aon.messaging.util.Utils;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.messaging.controller.IMessagingConstants;
import com.code.aon.ui.messaging.controller.SMSController;
import com.code.aon.ui.util.AonUtil;

public class SMSCommunicationController implements IMarketingConstants {
	
	private static final Logger LOGGER = Logger.getLogger(SurveyResponseController.class.getName());
	
	private boolean executable;
	
	private Map<String,ActionTarget> cellularMap;
	
	public SMSCommunicationController() {
		this.executable = calculateExecutable();
	}

	public boolean isExecutable() {
		return executable;
	}

	@SuppressWarnings("unchecked")
	private boolean calculateExecutable() {
		UserManager userManager = new UserManager();
		userManager.findUser(UserUtils.getInstance().getLoggedUser().getLogin());
		try {
			List<IApplication> applications = userManager.getUserApplications();
			for( IApplication application : applications ) {
				if ( SMSController.AON_SMS_APPLICATION.equals(application.getId()) ) {
					return true;
				}
			}
		} catch (DeploymentException e) {
			LOGGER.log(Level.SEVERE, "Error checking authorization for sending sms", e);
		}
		return false;
	}

	private CommunicationCenterController getCommunicationController() {
		return (CommunicationCenterController) AonUtil.getRegisteredBean(COMMUNICATION_CENTER_CONTROLLER_NAME);
	}

	private SMSController getSMSController() {
		return (SMSController) AonUtil.getRegisteredBean(IMessagingConstants.SMS_CONTROLLER_NAME);
	}
	
	private List<String> getCellulars( Target target ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteria = new Criteria();
		String mediaType = bean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_MEDIA_TYPE);
		criteria.addEqualExpression(mediaType, MediaType.CELLULAR);
		String registryId = bean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID);
		criteria.addEqualExpression(registryId, target.getRegistry().getId());
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			List<String> cellulars = new LinkedList<String>();
			for( ITransferObject to : list ) {
				RegistryMedia media = (RegistryMedia) to;
				String cellular = StringUtils.trimToNull(media.getValue());
				if (! StringUtils.isBlank(cellular) ) {
					cellulars.add( Utils.parsePhoneNumber(cellular) );
				}
			}
			return cellulars;
		}
		return Collections.emptyList();
	}
	
	private boolean sendSMS( SMSController sms, String phone ) {
		boolean result = true;
		try {
			Message message = (Message) sms.getMessage().clone();
			message.add(phone);
			sms.sendMessage(message);
		} catch ( Throwable th ) {
			LOGGER.log(Level.SEVERE, "Error sending sms to " + phone, th );
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
    		SMSController sms = getSMSController();
    		for( Map.Entry<String,ActionTarget> entry : cellularMap.entrySet() ) {
    			ActionTarget actionTarget = entry.getValue();
    			if ( sendSMS(sms, entry.getKey()) ) {
    				actionTarget.setStatus(ActionTargetStatus.SENT);
    			} else {
    				actionTarget.setStatus(ActionTargetStatus.INCORRECT);
    			}
    			updateActionTarget(actionTarget);
    		}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error sending sms", e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		getCommunicationController().onInit(event);
    }

    private void fillRecipients( SMSController sms ) throws ManagerBeanException {
    	CommunicationCenterController ccc = getCommunicationController();
    	cellularMap = new HashMap<String, ActionTarget>();
    	for( ActionTarget actionTarget : ccc.getActionTargets(false) ) {
    		List<String> cellulars = getCellulars(actionTarget.getTarget());
    		if (! cellulars.isEmpty() ) {
	   			for( String phone : cellulars ) {
	   				cellularMap.put( phone, actionTarget );
	   				String displayName = actionTarget.getTarget().getRegistry().getFullName();
	   				sms.getRecipients().add( displayName + "-" + phone );
	   			}
    		} else {
    			actionTarget.setStatus(ActionTargetStatus.CANCEL);
    			updateActionTarget(actionTarget);
    		}
    	}    	
    }
    
    public void onInit( ActionEvent event ) {
    	SMSController sms = getSMSController();
		String username = UserUtils.getInstance().getLoggedUser().getLogin();
		sms.setUsername(username);
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(CompanyController.COMPANY_NAME);
		sms.setOrganization(companyController.obtainCompany().getAlias());
		String domainName = UserUtils.getInstance().getPrincipal().getDomain();
		sms.setDomainName(domainName);
		sms.reset(event);
		try {
			fillRecipients(sms);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error retrieving cellular phones", e);
		}
    }
    
}
