package com.code.aon.ui.admin.event;

import static com.code.aon.ui.admin.controller.IAdminConstants.ADMIN_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.GENERAL_SCOPE;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.ui.admin.controller.AdminMainController;
import com.code.aon.ui.admin.controller.DomainUserController;
import com.code.aon.ui.admin.controller.IAdminConstants;
import com.code.aon.ui.admin.controller.UserScopeController;
import com.code.aon.ui.admin.controller.UserWorkGroupController;
import com.code.aon.ui.audit.controller.ActionDeniedController;
import com.code.aon.ui.audit.controller.IAuditConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class DomainUserControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainUserControllerListener.class);
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		DomainUserController duc = (DomainUserController) event.getController();
		update( duc );
		duc.getIdCheck().setOldValue( duc.getDomainUser().getLogin() );
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		DomainUserController duc = (DomainUserController) event.getController();
		duc.getIdCheck().setOldValue( null );
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainUserController duc = (DomainUserController) event.getController();
		duc.resetPassword( duc.getDomainUser() );
		duc.getIdCheck().setOldValue( duc.getDomainUser().getLogin() );
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainUserController duc = (DomainUserController) event.getController();
		User user = duc.getDomainUser();
		try {		
			duc.registerScope(user, GENERAL_SCOPE);
			duc.registerWorkGroup(user, GENERAL_SCOPE);					
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
		update(duc);
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		DomainUserController duc = (DomainUserController) event.getController();
		Serializable id = ((User) duc.getTo()).getId();
		try {		
			duc.removeUserReferences(id);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}	
	}	
	
	private void update( DomainUserController duc ) throws ControllerListenerException {
		User user = duc.getDomainUser();
		try {
			duc.initApplicationInfos(user);
			duc.registerAllApplications();
			updateWebmail(user);
			updateDeniedOptions(user);
			updateScopes(user);
			updateWorkGroups(user);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}				
	}

	private void updateWebmail( User user ) throws ManagerBeanException {
		AdminMainController admin = (AdminMainController) AonUtil.getRegisteredBean(ADMIN_CONTROLLER_NAME);
		admin.initWebmail(user);
	}	

	private void updateDeniedOptions( User user ) {
		ActionDeniedController denied = (ActionDeniedController) AonUtil.getRegisteredBean(IAuditConstants.ACTION_DENIED_CONTROLLER_NAME);
		denied.initEdit(user);
		denied.updateActionList();
	}	

	private void updateScopes( User user ) {
		UserScopeController usc = (UserScopeController) AonUtil.getRegisteredBean(IAdminConstants.USER_SCOPE_EX_CONTROLLER_NAME);
		usc.init(user);
	}	

	private void updateWorkGroups( User user ) {
		UserWorkGroupController uwgc = (UserWorkGroupController) AonUtil.getRegisteredBean(IAdminConstants.USER_WORK_GROUP_EX_CONTROLLER_NAME);
		uwgc.init(user);
	}	
		
}
