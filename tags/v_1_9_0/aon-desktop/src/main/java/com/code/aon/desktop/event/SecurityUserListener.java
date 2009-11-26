package com.code.aon.desktop.event;

import java.util.logging.Logger;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.bridge.plugin.UserManager;
import com.code.aon.config.User;
import com.code.aon.desktop.controller.AonDomainController;
import com.code.aon.desktop.controller.AonUserController;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ui.config.controller.UserController;
import com.code.aon.ui.config.event.UserSecurityActivationListener;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;

public class SecurityUserListener extends ControllerAdapter implements ILdapConstants, IAonObjectClasses {

	private static final Logger LOGGER = Logger.getLogger(SecurityUserListener.class.getName());
	
	private static final String ACTIVE_ATTRIBUTE = "active";
	
	private boolean active;
	
	private boolean disabled;

	private String domain;
	
	public SecurityUserListener() {
		AuthPrincipal user = UserUtils.getInstance().getPrincipal();
		domain = user.getDomain();
	}
	
	@Override
	public void setController(IController controller) {
		super.setController(controller);
		BasicController bean = (BasicController) controller;
		bean.addControllerListener(this);
		for( IControllerListener listener : bean.getListenerClasses() ) {
			if ( listener instanceof UserSecurityActivationListener ) {
				bean.removeControllerListener(listener);
				bean.addControllerListener(listener);
				break;
			}
		}
		retrieveProperties( (User) controller.getTo() );
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		retrieveProperties( (User) event.getController().getTo() );
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		retrieveProperties( (User) event.getController().getTo() );		
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		User user = (User) event.getController().getTo();
		user.setAvailable(this.active);
		user.setStatus(0);
		UserController uc = (UserController) event.getController();
		UserManager userManager = uc.getUserManager();
		userManager.setPassword( user.getLogin() );		
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		User user = (User) event.getController().getTo();
		setProperties( user, true );
		AonUserController userController = (AonUserController) AonUtil.getRegisteredBean("currentUser");
		userController.updateExpirationTimestamp( user.getLogin(), true );
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		setProperties( (User) event.getController().getTo(), false );
	}

	private void retrieveProperties( User user ) {
		if ( user.getId() == null ) {
			this.disabled = false;
			this.active = true;			
		} else {
			this.disabled = calculateDisabled(user);
			DistinguishedName userDN = AonDN.getUserDN( domain, user.getLogin() );
			BasicLdap ldap = new BasicLdap();
			if ( ldap.exists(userDN, USER) ) {
				try {
					LdapSession session = ldap.getLdapSession();
					String filter = LdapSession.getObjectClass(USER);
					Entry userEntry = session.get(userDN.toString(), filter, ACTIVE_ATTRIBUTE);
					active = userEntry.getAsBoolean(ACTIVE_ATTRIBUTE);
				} catch (LdapException e) {
					AonUtil.addErrorMessage( "Error obteniendo la propiedad active de " + user.getLogin() );
				} finally {
					ldap.closeSession();
				}
			} else {
				LOGGER.severe( "No existe en LDAP el usuario " + user.getLogin() + " para el dominio " + domain );
			}
		}
	}
	
	private void setProperties( User user, boolean updateId ) {
		DistinguishedName userDN = AonDN.getUserDN( domain, user.getLogin() );
		BasicLdap ldap = new BasicLdap();
		if ( ldap.exists(userDN, USER) ) {
			try {
				LdapSession session = ldap.getLdapSession();
				session.replaceAttribute(userDN, ACTIVE_ATTRIBUTE, active);
				if ( updateId ) {
					session.replaceAttribute(userDN, "uidNumber", user.getId().toString());	
				}
			} catch (LdapException e) {
				AonUtil.addErrorMessage( "Error obteniendo la propiedad active de " + user.getLogin() );
			} finally {
				ldap.closeSession();
			}
		} else {
			LOGGER.severe( "No existe en LDAP el usuario " + user.getLogin() + " para el dominio " + domain );
		}			
		try {
			AonDomainController domainController = (AonDomainController) AonUtil.getController("domain");
			domainController.flushAuthenticationCache( user.getLogin() );
		} catch (DeploymentException e) {
			LOGGER.severe( "Error refrescando la cache de autentificacion" );			
		}
	}
	
	private boolean calculateDisabled( User user ) {
		User loggedUser = UserUtils.getInstance().getLoggedUser();
		return ObjectUtils.equals(user.getId(), loggedUser.getId());
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isDisabled() {
		return disabled;
	}

}
