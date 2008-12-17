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
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;

public class SecurityUserListener extends ControllerAdapter implements ILdapConstants, IAonObjectClasses {

	private static final Logger LOGGER = Logger.getLogger(SecurityUserListener.class.getName());

	private static final String USER_UID_NUMBER_ATTRIBUTE = "uidNumber";

	private static final String ACTIVE_ATTRIBUTE = "active";

	private static final String USER_ALTERNATIVE_EMAIL = "mail";

	private static final String USER_CELLULAR_NUMBER = "mobile";

	private boolean showUserNotExistsWindow = false;
	
	private boolean active;

	private String name;

	private String surname;

	private String alternativeEmail;

	private String cellular;

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
		user.setName(this.name + " " + this.surname);
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
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		User user = (User) event.getController().getTo();
		user.setAvailable(this.active);
		user.setName(this.name + " " + this.surname);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		setProperties( (User) event.getController().getTo(), false );
	}

	private void retrieveProperties( User user ) {
		this.name = null;
		this.surname = null;
		this.cellular = null;
		this.alternativeEmail = null;
		if ( user.getId() == null ) {
			this.disabled = false;
			this.active = true;
		} else {
			this.disabled = calculateDisabled(user);
			DistinguishedName userDN = AonDN.getUserDN( domain, user.getLogin() );
			BasicLdap ldap = new BasicLdap();
			if ( ldap.exists(userDN, USER) ) {
				this.showUserNotExistsWindow = false;
				try {
					LdapSession session = ldap.getLdapSession();
					String filter = LdapSession.getObjectClass(USER);
					Entry userEntry = session.get(userDN.toString(), filter, 
							COMMON_NAME_ATTRIBUTE, SURNAME_ATTRIBUTE, USER_ALTERNATIVE_EMAIL, USER_CELLULAR_NUMBER, ACTIVE_ATTRIBUTE);
					name = userEntry.getAsString(COMMON_NAME_ATTRIBUTE);
					surname = userEntry.getAsString(SURNAME_ATTRIBUTE);
					active = userEntry.getAsBoolean(ACTIVE_ATTRIBUTE);
					try {
						alternativeEmail = userEntry.getAsString(USER_ALTERNATIVE_EMAIL);
					}catch (NullPointerException npe) {}
					try {
						cellular = userEntry.getAsString(USER_CELLULAR_NUMBER);
					}catch (NullPointerException npe) {}
				} catch (LdapException e) {
					AonUtil.addErrorMessage( "Error obteniendo una propiedad de " + user.getLogin() );
				} finally {
					ldap.closeSession();
				}
			} else {
				this.showUserNotExistsWindow = true;
				//AonUtil.addErrorMessage("El usuario no existe en el sistema.\nPongase en contacto con el Administrador.");
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
				session.replaceAttribute(userDN, COMMON_NAME_ATTRIBUTE, name);
				session.replaceAttribute(userDN, SURNAME_ATTRIBUTE, surname);
				session.replaceAttribute(userDN, ACTIVE_ATTRIBUTE, active);
				session.replaceAttribute(userDN, USER_UID_NUMBER_ATTRIBUTE, user.getId().toString());

				String filter = LdapSession.getObjectClass(USER);
				Entry u = session.get(userDN.toString(), filter, COMMON_NAME_ATTRIBUTE, USER_ALTERNATIVE_EMAIL, USER_CELLULAR_NUMBER);
				String old_mail = null;
				String old_cellular = null;
				try {
					old_mail = u.getAsString(USER_ALTERNATIVE_EMAIL);
					old_cellular = u.getAsString(USER_CELLULAR_NUMBER);
				}
				catch (NullPointerException npe) {
				}
				
				session.updateAttribute(userDN, USER_ALTERNATIVE_EMAIL, old_mail, alternativeEmail);
				session.updateAttribute(userDN, USER_CELLULAR_NUMBER, old_cellular, cellular);

//				if ( updateId ) {
//					session.replaceAttribute(userDN, "uidNumber", user.getId().toString());	
//				}
			} catch (LdapException e) {
				AonUtil.addErrorMessage( "Error obteniendo las propiedades del usuario " + user.getLogin() );
			} finally {
				ldap.closeSession();
			}
		} else {
			AonUtil.addErrorMessage("El usuario no existe en el sistema.\nPongase en contacto con su administrador.");
			LOGGER.severe( "No existe en LDAP el usuario " + user.getLogin() + " para el dominio " + domain );
		}			
		try {
			AonDomainController domainController = (AonDomainController) FormUtil.getController("domain");
			domainController.flushAuthenticationCache( user.getLogin() );
		} catch (DeploymentException e) {
			LOGGER.severe( "Error refrescando la cache de autentificacion" );			
		}
	}
	
	private boolean calculateDisabled( User user ) {
		User loggedUser = UserUtils.getInstance().getLoggedUser();
		return ObjectUtils.equals(user.getId(), loggedUser.getId());
	}

	public boolean isShowUserNotExistsWindow() {
		return showUserNotExistsWindow;
	}

	public void setShowUserNotExistsWindow(boolean showUserNotExistsWindow ) {
		this.showUserNotExistsWindow = showUserNotExistsWindow;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getAlternativeEmail() {
		return alternativeEmail;
	}

	public void setAlternativeEmail(String alternativeEmail) {
		if (alternativeEmail.trim().equals("")) this.alternativeEmail = null;
		else this.alternativeEmail = alternativeEmail;
	}

	public String getCellular() {
		return cellular;
	}

	public void setCellular(String cellular) {
		if (cellular.trim().equals("")) this.cellular = null;
		else this.cellular = cellular;
	}

}
