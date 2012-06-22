package com.code.aon.ui.admin.event;

import static com.code.aon.ui.admin.controller.IAdminConstants.ADMIN_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.APPLICATION_USER_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.GENERAL_SCOPE;
import static com.code.aon.ui.admin.controller.IAdminConstants.USER_SCOPE_EX_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.USER_WORK_GROUP_EX_CONTROLLER_NAME;
import static com.code.aon.ui.audit.controller.IAuditConstants.ACTION_DENIED_CONTROLLER_NAME;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.ActionDenied;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.config.UserScope;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.ui.admin.controller.AdminMainController;
import com.code.aon.ui.admin.controller.DomainApplicationUserController;
import com.code.aon.ui.admin.controller.DomainUserController;
import com.code.aon.ui.admin.controller.UserScopeController;
import com.code.aon.ui.admin.controller.UserWorkGroupController;
import com.code.aon.ui.audit.controller.ActionDeniedController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.db.Contact;
import com.code.aon.webmail.db.MailAccount;
import com.code.aon.webmail.db.Signature;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainUserControllerListener extends ControllerAdapter {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainUserControllerListener.class);

	private AdminMainController getAdmin() {
		return (AdminMainController) AonUtil.getRegisteredBean(ADMIN_CONTROLLER_NAME);
	}
	
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
		getAdmin().getLogger().domainUserAddded(user);
		update(duc);
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		Serializable id = ((User) event.getController().getTo()).getId();
		try {		
			DomainApplicationUserController dausc = (DomainApplicationUserController) AonUtil.getRegisteredBean(APPLICATION_USER_CONTROLLER_NAME);
			dausc.removeApplicationUsers( IEntityAlias.APPLICATION_USER_USER_ID, id );
			AdminMainController.removeLines(UserScope.class, IEntityAlias.USER_SCOPE_USER_ID, id);
			AdminMainController.removeLines(UserWorkGroup.class, IEntityAlias.USER_WORK_GROUP_USER_ID, id);
			AdminMainController.removeLines(ActionDenied.class, IEntityAlias.ACTION_DENIED_USER_ID, id);
			AdminMainController.removeLines(Contact.class, IEntityAlias.CONTACT_USER_ID, id);
			AdminMainController.removeLines(MailAccount.class, IEntityAlias.MAIL_ACCOUNT_USER_ID, id);
			AdminMainController.removeLines(Signature.class, IEntityAlias.SIGNATURE_USER_ID, id);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}	
	}	
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		DomainUserController duc = (DomainUserController) event.getController();
		User user = duc.getDomainUser();
		getAdmin().getLogger().domainUserdRemoved(user);
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
		ActionDeniedController denied = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
		denied.init(user);
	}	

	private void updateScopes( User user ) {
		UserScopeController usc = (UserScopeController) AonUtil.getRegisteredBean(USER_SCOPE_EX_CONTROLLER_NAME);
		usc.init(user);
	}	

	private void updateWorkGroups( User user ) {
		UserWorkGroupController uwgc = (UserWorkGroupController) AonUtil.getRegisteredBean(USER_WORK_GROUP_EX_CONTROLLER_NAME);
		uwgc.init(user);
	}	
	
}
