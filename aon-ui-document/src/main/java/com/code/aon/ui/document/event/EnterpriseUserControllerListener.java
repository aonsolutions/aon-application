package com.code.aon.ui.document.event;

import static com.code.aon.ui.company.controller.ICompanyConstants.ENTERPRISE_CONTROLLER_NAME;
import static com.code.aon.ui.document.controller.IDocumentConstants.MANAGER_CONTROLLER_NAME;

import java.io.Serializable;
import java.util.List;

import com.code.aon.audit.ActionDenied;
import com.code.aon.audit.ActionEntry;
import com.code.aon.audit.ActionFavorite;
import com.code.aon.audit.Session;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.company.EnterpriseUser;
import com.code.aon.config.Scope;
import com.code.aon.config.User;
import com.code.aon.config.UserScope;
import com.code.aon.document.AlfrescoUserManager;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.document.controller.ManagerController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * Listener added to the EnterpriseController
 * 
 */
public class EnterpriseUserControllerListener extends ControllerAdapter {

	private AlfrescoUserManager getUserManager() {
		ManagerController mc = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
		return mc.getUserManager();
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseUser user = (EnterpriseUser) event.getController().getTo();
		user.setActive(true);
		EnterpriseController ec = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
		user.setEnterprise( ec.getEnterprise() );
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseUser user = (EnterpriseUser) event.getController().getTo();
		try {
			user.setPassword(user.getLogin());
			getUserManager().createUser(user);
		} catch (DAOException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseUser eUser = (EnterpriseUser) event.getController().getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(UserScope.class);
			UserScope us = new UserScope();
			us.setUser(getUser(eUser));
			us.setScope(getScope());
			bean.insert(us);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);			
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseUser user = (EnterpriseUser) event.getController().getTo();
		try {
			getUserManager().updateUser(user);
		} catch (DAOException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseUser user = (EnterpriseUser) event.getController().getTo();
		try {
			deleteEntries(UserScope.class, IEntityAlias.USER_SCOPE_USER_ID, user.getId());
			deleteEntries(ActionDenied.class, IEntityAlias.ACTION_DENIED_USER_ID, user.getId());
			deleteEntries(ActionFavorite.class, IEntityAlias.ACTION_FAVORITE_USER_ID, user.getId());
			deleteEntries(ActionEntry.class, IEntityAlias.ACTION_ENTRY_SESSION_USER_ID, user.getId());
			deleteEntries(Session.class, IEntityAlias.SESSION_USER_ID, user.getId());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
		try {
			getUserManager().deleteUser(user.getLogin());
		} catch (DAOException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}
	
	private Scope getScope() throws ManagerBeanException {
		Scope scope = null;
		IManagerBean bean = BeanManager.getManagerBean(Scope.class);
		List<ITransferObject> list = bean.getList(null);
		if (! list.isEmpty()) {
			scope = (Scope) list.get(0);
		} else {
			scope = new Scope();
			scope.setDescription("GENERAL");
			bean.insert(scope);
		}
		return scope;
	}

	private User getUser( EnterpriseUser eu) throws ManagerBeanException {
		User user = new User();
		user.setId(eu.getId());
		user.setActive(eu.isActive());
		user.setLogin(eu.getLogin());
		user.setName(eu.getName());
		user.setPassword(eu.getPassword());
		if ( eu.getEnterprise() != null ) {
			user.setEnterprise(eu.getEnterprise().getId());
		}
		if ( eu.getRegistry() != null ) {
			user.setRegistry(eu.getRegistry().getId());	
		}
		return user;
	}
	
	private void deleteEntries( Class<? extends ITransferObject> _class, String field, Serializable id ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(_class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(field), id);
		for( ITransferObject to : bean.getList(criteria) ) {
			bean.remove(to);
		}
	}

}
