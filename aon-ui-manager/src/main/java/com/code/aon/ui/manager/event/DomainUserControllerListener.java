package com.code.aon.ui.manager.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.manager.DomainUser;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.AbstractPojoController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.manager.controller.DomainController;
import com.code.aon.ui.manager.controller.DomainUserController;
import com.code.aon.ui.manager.controller.IManagerConstants;
import com.code.aon.ui.manager.controller.ManagerController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;

public class DomainUserControllerListener extends ControllerAdapter implements IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainUserControllerListener.class);
	
	private ManagerController getManager() {
		return (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		DomainUserController duc = (DomainUserController) event.getController();
		DomainUser user = duc.getDomainUser();
		duc.setWebmail( duc.hasWebmail(user) );
		if ( duc.isWebmail() ) {
			updateWebmail(user);
		}
		updateDBUser( duc, user );
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainUserController duc = (DomainUserController) event.getController();
		duc.resetPassword( duc.getDomainUser() );
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainUserController duc = (DomainUserController) event.getController();
		DomainUser user = duc.getDomainUser();
		try {		
			duc.registerUserInApplication(user, AON_DESKTOP, USUARIO_PROFILE);
			duc.registerUserInApplication(user, AON_WEBMAIL, USUARIO_PROFILE);
			duc.registerInDBs(user.getUid(), GENERAL_SCOPE);
			updateDBUser( duc, user );
			duc.createMailAccount(user);
			DomainController controller = (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
			duc.addDefaultWebmailData(user, controller.getCompany());
			duc.setWebmail(true);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
		getManager().getLogger().domainUserAddded(user);
		updateWebmail(user);
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		DomainUserController duc = (DomainUserController) event.getController();
		DomainUser user = duc.getDomainUser();
		try {
			duc.deactiveDBUser(user);
			duc.removeMailAccount(user);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}
		getManager().getLogger().domainUserdRemoved(user);
	}

	private void updateWebmail( DomainUser user ) {
		ManagerController.updateController(IWebMailConstants.BEAN_SIGNATURE, user.getId());
		ManagerController.updateController(IWebMailConstants.BEAN_MAIL_ACCOUNT, user.getId());
		ManagerController.updateController(IWebMailConstants.BEAN_CONTACT, user.getId());
	}	

	private void updateDBUser( DomainUserController duc, DomainUser domainUser ) throws ControllerListenerException {
		try {		
			User user = duc.ensureDBUser( domainUser.getUid() );
			duc.setUser(user);
			updateLines( domainUser );
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
	private void updateLines( DomainUser user ) throws ManagerBeanException {
		IController us = FormUtil.getController(USER_SCOPE_CONTROLLER_NAME);
		updateLine( us, user );
		IController uwg = FormUtil.getController(USER_WORK_GROUP_CONTROLLER_NAME);
		updateLine( uwg, user );
	}	
	
	private void updateLine( IController controller, DomainUser user ) throws ManagerBeanException {
		controller.clearCriteria();
		Criteria criteria = controller.getCriteria();
		String alias = ((AbstractPojoController)controller).getPojoShortName() + ".user.login";
		criteria.addEqualExpression(alias, user.getUid());
		controller.onSearch(null);
	}
	
}
