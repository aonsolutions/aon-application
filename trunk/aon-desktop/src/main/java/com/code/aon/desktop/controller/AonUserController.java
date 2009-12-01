package com.code.aon.desktop.controller;

import java.util.Date;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.naming.Name;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.desktop.IDesktopConstants;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.UserController;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

public class AonUserController extends UserController implements ILdapConstants, IAonObjectClasses, IDesktopConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(AonUserController.class);
	
	private AuthPrincipal principal;
	
	private String name;
	
	private String surname;

	private String alternativeEmail;

	private String cellular;

	private String domain;
	
	private boolean passwordExpired;
	
	private boolean contactsEnabled;
	
	private boolean managerChangingPassword;
	
	private boolean showPasswordChangedWindow;

	private boolean showUserChangedWindow;

	private boolean accepted;
	
	public AonUserController() {
		principal = UserUtils.getInstance().getPrincipal();
		domain = principal.getDomain();
		passwordExpired = calculatePasswordExpired();
		contactsEnabled = calculateContactsEnabled();
		onLoadCurrentUser( null );
	}

	public String getDomain() {
		return domain;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
	
	public boolean isManagerChangingPassword() {
		return managerChangingPassword;
	}
	
	public String returnAction() {
		return managerChangingPassword ? "user_form" : "home";
	}

	public void onLoadCurrentUser(ActionEvent event)  {
		try {
			this.managerChangingPassword = false;
			User user = UserUtils.getInstance().getLoggedUser();
			loadUser( user );
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

	public void onLoadUser(ActionEvent event)  {
		try {
			this.managerChangingPassword = true;
			IController userController = FormUtil.getController(ConfigConstants.USER);
			loadUser( (User) userController.getTo() );
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}
	
	private void loadUser( User user ) throws ManagerBeanException {
		setShowPasswordChangedWindow(false);
		setShowUserChangedWindow(false);
		getUserManager().findUser(user.getLogin());
		setUserTO(user);
	}
	
	private void changeDefaultMailAccountPassword( String userName, String newPassword ) {
		Name accountsDN = NameResolver.getUserDefaultAccount( domain, userName );
		BasicLdap ldap = new BasicLdap();
		if ( ldap.exists(accountsDN, MAIL_ACCOUNT ) ) {
			try {
				ldap.getLdapSession().replaceAttribute(accountsDN, USER_PASSWORD_ATTRIBUTE, newPassword);
			} catch (LdapException e) {
				AonUtil.addErrorMessage("Error cambiando la contraseña de la cuenta de correo por defecto del usuario " + userName );
			} finally {
				ldap.closeSession();
			}
		} else {
			AonUtil.addErrorMessage("No se ha encontrado la cuenta de correo por defecto del usuario " + userName );
		}
	}

	public void updateExpirationTimestamp( String userName, boolean expireToday ) {
		Name userDN = NameResolver.getUserDN( domain, userName );
		BasicLdap ldap = new BasicLdap();
		if ( ldap.exists(userDN, USER) ) {
			try {
				Date newDate = new Date();
				LdapSession session = ldap.getLdapSession();
				if ( ! expireToday ) {
					newDate = DateUtils.addDays(newDate, 180);
				}	
				session.replaceAttribute(userDN, PASSWORD_EXPIRATION_TIMESTAMP_ATTRIBUTE, newDate);
			} catch (LdapException e) {
				AonUtil.addErrorMessage("Error actualizando la fecha de expiración de la contraseña" );
			} finally {
				ldap.closeSession();
			}
		} else {
			LOGGER.error( "No existe en LDAP el usuario {} para el dominio {}", userName, domain );
		}
	}
	
	public void accept(ActionEvent event) {
		User user = (User) getTo();
		user.setName(this.name + " " + this.surname);
		try {
			super.accept(event);
			if (cellular != null) cellular = cellular.replace(" ", "");
			updateUserLdapProperties(user.getLogin(), name, surname, alternativeEmail, cellular);
			setShowUserChangedWindow(true);
		} catch (Exception e) {
			LOGGER.error( "Error cambiando datos del usuario.", e );
		}
	}

	public void acceptPassword(ActionEvent event) throws DeploymentException {
		User user = (User) getTo();
		int status = user.getStatus();
		user.setStatus(status + 2);
		if (!managerChangingPassword && (getUserManager().getPassword() == null || getUserManager().getPassword().equals("")  
				|| getUserManager().getNewPassword() == null || getUserManager().getNewPassword().equals("")
				|| getUserManager().getConfirmPassword() == null || getUserManager().getConfirmPassword().equals(""))) {
			String message = AonUtil.addErrorMessageFromBundle( "securityBundle", "aon_security_passwd_fill_error");
			throw new AbortProcessingException( message );
		}
		if (!getUserManager().areEqualPasswords()) {
			String message = AonUtil.addErrorMessageFromBundle( "securityBundle", "aon_security_new_passwd_error");
			throw new AbortProcessingException( message );
		}

		if ( managerChangingPassword ) {
			getUserManager().savePassword();
		} else {
			super.accept(event);
		}
		FacesContext ctx = FacesContext.getCurrentInstance();
		if ( ctx.getMaximumSeverity() == null ) {
			updateExpirationTimestamp( user.getLogin(), managerChangingPassword );
			changeDefaultMailAccountPassword( user.getLogin(), getUserManager().getPassword() );				
			AonDomainController domainController = (AonDomainController) FormUtil.getController(CURRENT_DOMAIN_CONTROLLER_NAME);
			domainController.flushAuthenticationCache( user.getLogin() );
			setShowPasswordChangedWindow(true);
		} else {
			throw new AbortProcessingException();
		}
	}

	public boolean isShowPasswordChangedWindow() {
		return showPasswordChangedWindow;
	}

	public void setShowPasswordChangedWindow(boolean showPasswordChangedWindow) {
		this.showPasswordChangedWindow = showPasswordChangedWindow;
	}

	public boolean isShowUserChangedWindow() {
		return showUserChangedWindow;
	}

	public void setShowUserChangedWindow(boolean showUserChangedWindow) {
		this.showUserChangedWindow = showUserChangedWindow;
	}
	
	public boolean isContactsEnabled() {
		return this.contactsEnabled;
	}
		
	public boolean calculateContactsEnabled() {		
		Name contactsDN = NameResolver.getUserAddressBookDN( domain, principal.getShortName() );
		BasicLdap ldap = new BasicLdap();
		boolean enabled = ldap.exists(contactsDN, ORGANIZATIONAL_UNIT);
		ldap.closeSession();
		return enabled;
	}
	
	public boolean isPasswordExpired() {
		return passwordExpired;
	}
	
	public boolean calculatePasswordExpired() {
		boolean passwordExpired = false;
		Name userDN = NameResolver.getUserDN( domain, principal.getShortName() );
		BasicLdap ldap = new BasicLdap();
		if ( ldap.exists(userDN, USER) ) {
			Entry userEntry = ldap.get(userDN, USER,PASSWORD_EXPIRATION_TIMESTAMP_ATTRIBUTE);
			if ( userEntry != null ) {
				if (userEntry.getSearchDN() == null) {
					Date expirationDate = userEntry.getAsDate(PASSWORD_EXPIRATION_TIMESTAMP_ATTRIBUTE);
					passwordExpired = new Date().after(expirationDate);
				}
			} else {
				AonUtil.addErrorMessage("Error calculando si la contraseña ha expirado" );
			}
		} else {
			LOGGER.error( "No existe en LDAP el usuario {} para el dominio {}", principal.getShortName(), domain );
		}
		return passwordExpired;
	}
	
	public String getUserName( String login ) {
		String name = login;
		BasicLdap ldap = new BasicLdap();
		Name dn = NameResolver.getUserDN(domain, login);
		Entry entry = ldap.get(dn, USER, COMMON_NAME_ATTRIBUTE, SURNAME_ATTRIBUTE);
		if ( entry != null ) {
	    	name = entry.getAsString(COMMON_NAME_ATTRIBUTE);
	    	if (entry.containsKey(SURNAME_ATTRIBUTE) ) {
	    		name += " " + entry.getAsString(SURNAME_ATTRIBUTE);
	    	}				
		} else {
			throw new AbortProcessingException( "Error getting name for " + login );
		}
		return name;
	}

	public void updateUserLdapProperties( String userName, String name, String surname, String mail, String mobile ) {
		Name userDN = NameResolver.getUserDN( domain, userName );
		BasicLdap ldap = new BasicLdap();
		if ( ldap.exists(userDN, USER) ) {
			try {
				LdapSession session = ldap.getLdapSession();
				session.replaceAttribute(userDN, COMMON_NAME_ATTRIBUTE, name);
				session.replaceAttribute(userDN, SURNAME_ATTRIBUTE, surname);

				String filter = NameResolver.getObjectClass(USER);
				Entry userEntry = session.get(userDN, filter, MAIL_ATTRIBUTE, MOBILE_ATTRIBUTE);
				String old_mail = null;
				if ( userEntry.containsKey(MAIL_ATTRIBUTE) ) {
					old_mail = userEntry.getAsString(MAIL_ATTRIBUTE);
				}
				String old_mobile = null;
				if ( userEntry.containsKey(MOBILE_ATTRIBUTE) ) {
					old_mobile = userEntry.getAsString(MOBILE_ATTRIBUTE);
				}
				session.updateAttribute(userDN, MAIL_ATTRIBUTE, old_mail, mail);
				session.updateAttribute(userDN, MOBILE_ATTRIBUTE, old_mobile, mobile);
			} catch (LdapException e) {
				e.printStackTrace();
				AonUtil.addErrorMessage("Error actualizando los datos de usuario." );
			} finally {
				ldap.closeSession();
			}
		} else {
			LOGGER.error( "No existe en LDAP el usuario {} para el dominio {}", userName, domain );
		}
	}

	public String getName() {
		User user = (User) getTo();
		String login = user.getLogin();
		BasicLdap ldap = new BasicLdap();
		Name dn = NameResolver.getUserDN(domain, login);
		Entry entry = ldap.get(dn, USER, COMMON_NAME_ATTRIBUTE);
		if ( entry != null ) {
	    	name = entry.getAsString(COMMON_NAME_ATTRIBUTE);
		} else {
			throw new AbortProcessingException( "Error getting name for " + login );
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		User user = (User) getTo();
		String login = user.getLogin();
		BasicLdap ldap = new BasicLdap();
		Name dn = NameResolver.getUserDN(domain, login);
		Entry entry = ldap.get(dn, USER, SURNAME_ATTRIBUTE);
		if ( entry != null ) {
	    	surname = entry.getAsString(SURNAME_ATTRIBUTE);
		} else {
			throw new AbortProcessingException( "Error getting surname for " + login );
		}
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getAlternativeEmail() {
		this.alternativeEmail = null;
		User user = (User) getTo();
		String login = user.getLogin();
		BasicLdap ldap = new BasicLdap();
		Name dn = NameResolver.getUserDN(domain, login);
		Entry entry = ldap.get(dn, USER, MAIL_ATTRIBUTE);
		if ( entry != null ) {
			if ( entry.containsKey(MAIL_ATTRIBUTE) ) {
				alternativeEmail = entry.getAsString(MAIL_ATTRIBUTE);	
			}
		} else {
			throw new AbortProcessingException( "Error getting email for " + login );
		}
		return alternativeEmail;
	}

	public void setAlternativeEmail(String alternativeEmail) {
		if (alternativeEmail.trim().equals("")) this.alternativeEmail = null;
		else this.alternativeEmail = alternativeEmail;
	}

	public String getCellular() {
		this.cellular = null;
		User user = (User) getTo();
		String login = user.getLogin();
		BasicLdap ldap = new BasicLdap();
		Name dn = NameResolver.getUserDN(domain, login);
		Entry entry = ldap.get(dn, USER, MOBILE_ATTRIBUTE);
		if ( entry != null ) {
			if ( entry.containsKey(MOBILE_ATTRIBUTE) ) {
				cellular = entry.getAsString(MOBILE_ATTRIBUTE);	
			}	
		} else {
			throw new AbortProcessingException( "Error getting cellular for " + login );
		}
		return cellular;
	}

	public void setCellular(String cellular) {
		if (cellular.trim().equals("")) this.cellular = null;
		else this.cellular = cellular;
	}

}
