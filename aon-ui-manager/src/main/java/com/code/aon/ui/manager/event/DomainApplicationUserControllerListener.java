package com.code.aon.ui.manager.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.manager.DomainApplicationUser;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.AbstractPojoController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.manager.controller.DomainApplicationController;
import com.code.aon.ui.manager.controller.DomainApplicationUserController;
import com.code.aon.ui.manager.controller.DomainUserController;
import com.code.aon.ui.manager.controller.IManagerConstants;
import com.code.aon.ui.manager.controller.ManagerController;
import com.code.aon.ui.util.AonUtil;

public class DomainApplicationUserControllerListener extends ControllerAdapter implements IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplicationUserControllerListener.class);

	private ManagerController getManager() {
		return (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		getManager().resetTermsOfServiceAccepted();
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationUser user = (DomainApplicationUser) event.getController().getTo();
		DomainApplicationController dac = (DomainApplicationController) AonUtil.getRegisteredBean(DOMAIN_APPLICATION_CONTROLLER_NAME);
		if ( dac.isAonDB() ) {
			DomainUserController duc = (DomainUserController) AonUtil.getRegisteredBean(DOMAIN_USER_CONTROLLER_NAME);
			try {		
				duc.registerScopeInDBs(user.getCommonName(), GENERAL_SCOPE);		
			} catch (Throwable e) {
				LOGGER.error(e.getMessage(), e);
				throw new ControllerListenerException( e.getMessage(), e );
			}		
			update(event);
		}		
		getManager().getLogger().domainApplicationUserAddded(user);
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationUser user = (DomainApplicationUser) event.getController().getTo();		
		getManager().getLogger().domainApplicationUserRemoved(user);
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationController dac = (DomainApplicationController) AonUtil.getRegisteredBean(DOMAIN_APPLICATION_CONTROLLER_NAME);
		if ( dac.isAonDB() ) {
			update(event);
		}
	}

	private void update( ControllerEvent event ) throws ControllerListenerException {
		DomainApplicationUserController dauc = (DomainApplicationUserController) event.getController();
		DomainUserController duc = (DomainUserController) AonUtil.getRegisteredBean(DOMAIN_USER_CONTROLLER_NAME);
		DomainApplicationUser dau = dauc.getDomainApplicationUser();
		try {		
			User user = duc.ensureDBUser( dau.getCommonName() );
			dauc.setUser(user);
			updateLines( dauc.getDomainApplicationUser() );
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
	private void updateLines( DomainApplicationUser user ) throws ManagerBeanException {
		IController us = FormUtil.getController(USER_SCOPE_CONTROLLER_NAME);
		updateLine( us, user );
		IController uwg = FormUtil.getController(USER_WORK_GROUP_CONTROLLER_NAME);
		updateLine( uwg, user );
	}	
	
	private void updateLine( IController controller, DomainApplicationUser user ) throws ManagerBeanException {
		controller.clearCriteria();
		Criteria criteria = controller.getCriteria();
		String alias = ((AbstractPojoController)controller).getPojoShortName() + ".user.login";
		criteria.addEqualExpression(alias, user.getCommonName());
		controller.onSearch(null);
	}

}
