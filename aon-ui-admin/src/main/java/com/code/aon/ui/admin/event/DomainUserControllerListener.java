package com.code.aon.ui.admin.event;

import static com.code.aon.ui.admin.controller.IAdminConstants.ADMIN_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.GENERAL_SCOPE;
import static com.code.aon.ui.admin.controller.IAdminConstants.USER_SCOPE_EX_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.USER_WORK_GROUP_EX_CONTROLLER_NAME;
import static com.code.aon.ui.audit.controller.IAuditConstants.ACTION_DENIED_CONTROLLER_NAME;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.admin.ApplicationUserProfile;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationUser;
import com.code.aon.config.User;
import com.code.aon.config.UserScope;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.admin.controller.AdminMainController;
import com.code.aon.ui.admin.controller.DomainUserController;
import com.code.aon.ui.admin.controller.UserScopeController;
import com.code.aon.ui.admin.controller.UserWorkGroupController;
import com.code.aon.ui.audit.controller.ActionDeniedController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
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
		User user = (User) event.getController().getTo();
		try {		
			removeApplicationUsers( user );
			removeDependencies(UserScope.class, IEntityAlias.USER_SCOPE_USER_ID, user.getId());
			removeDependencies(UserWorkGroup.class, IEntityAlias.USER_WORK_GROUP_USER_ID, user.getId());
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

	private void removeApplicationUsers( User user ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ApplicationUser.class);
		IManagerBean aupBean = BeanManager.getManagerBean(ApplicationUserProfile.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_USER_USER_ID), user.getId());
		for( ITransferObject to : bean.getList(criteria) ) {
			ApplicationUser appUser = (ApplicationUser) to;
			Criteria aupCriteria = new Criteria();
			aupCriteria.addEqualExpression(aupBean.getFieldName(IEntityAlias.APPLICATION_USER_PROFILE_APPLICATION_USER_ID), appUser.getId());
			for( ITransferObject aup : aupBean.getList(aupCriteria) ) {
				aupBean.remove(aup);
			}
			bean.remove(appUser);
		}	
	}

	private void removeDependencies( Class<? extends ITransferObject> _class, String alias, Serializable id ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(_class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(alias), id);
		for( ITransferObject to : bean.getList(criteria) ) {
			bean.remove(to);
		}	
	}	
}
