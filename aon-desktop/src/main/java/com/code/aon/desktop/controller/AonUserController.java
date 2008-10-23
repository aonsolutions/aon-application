package com.code.aon.desktop.controller;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.UserController;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

public class AonUserController extends UserController implements ILdapConstants, IAonObjectClasses {

	private static final String PASSWORD_EXPIRATION_TIMESTAMP = "passwordExpirationTimestamp";

	private static final String USER_ALTERNATIVE_EMAIL = "mail";

	private static final String USER_CELLULAR_NUMBER = "mobile";

	private static final Logger LOGGER = Logger.getLogger(AonUserController.class.getName());
	
	private String name;
	
	private String surname;

	private String alternativeEmail;

	private String cellular;

	private String domain;
	
	private boolean managerChangingPassword;
	
	private boolean showPasswordChangedWindow;

	private boolean showUserChangedWindow;

	private boolean accepted;
	
	public AonUserController() {
		AuthPrincipal user = UserUtils.getInstance().getPrincipal();
		domain = user.getDomain();
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
			IController userController = AonUtil.getController(ConfigConstants.USER);
			loadUser( (User) userController.getTo() );
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadUser( User user ) throws ManagerBeanException {
		setShowPasswordChangedWindow(false);
		setShowUserChangedWindow(false);
		getUserManager().findUser(user.getLogin());
		setUserTO(user);
	}
	
	private void changeDefaultMailAccountPassword( String userName, String newPassword ) {
		DistinguishedName accountsDN = AonDN.getUserDefaultAccount( domain, userName );
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

	public void updateExpirationTimestamp( String userName, boolean today ) {
		DistinguishedName userDN = AonDN.getUserDN( domain, userName );
		BasicLdap ldap = new BasicLdap();
		if ( ldap.exists(userDN, USER) ) {
			try {
				Date newDate = null;
				LdapSession session = ldap.getLdapSession();
				if ( today ) {
					newDate = new Date();
				} else {
					String filter = LdapSession.getObjectClass(USER);
					Entry user = session.get(userDN.toString(), filter, PASSWORD_EXPIRATION_TIMESTAMP);
					Date date = user.getAsDate(PASSWORD_EXPIRATION_TIMESTAMP);
					newDate = DateUtils.addDays(date, 180);
				}	
				session.replaceAttribute(userDN, PASSWORD_EXPIRATION_TIMESTAMP, newDate);
			} catch (LdapException e) {
				AonUtil.addErrorMessage("Error actualizando la fecha de expiración de la contraseña" );
			} finally {
				ldap.closeSession();
			}
		} else {
			LOGGER.severe( "No existe en LDAP el usuario " + userName + " para el dominio " + domain );
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
			LOGGER.log(Level.SEVERE, "Error cambiando datos del usuario.", e );
		}
	}

	public void acceptPassword(ActionEvent event) {
		User user = (User) getTo();
		int status = user.getStatus();
		user.setStatus(status + 2);
		try {
			if (!managerChangingPassword && (getUserManager().getPassword() == null || getUserManager().getPassword().equals("")  
					|| getUserManager().getNewPassword() == null || getUserManager().getNewPassword().equals("")
					|| getUserManager().getConfirmPassword() == null || getUserManager().getConfirmPassword().equals(""))) {
				AonUtil.addErrorMessage("Debe rellenar todos los campos de contraseña.");
				throw new AonException();
			}
			if (!getUserManager().areEqualPasswords()) {
				AonUtil.addErrorMessage("Las contraseña nueva no coincide con la confirmacion.");
				throw new AonException();
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
				AonDomainController domainController = (AonDomainController) AonUtil.getController("domain");
				domainController.flushAuthenticationCache( user.getLogin() );
				setShowPasswordChangedWindow(true);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error cambiando la contraseña.", e );
			user.setStatus(status);
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

	public boolean isPasswordExpired() {
		boolean passwordExpired = false;
		AuthPrincipal user = UserUtils.getInstance().getPrincipal();
		DistinguishedName userDN = AonDN.getUserDN( domain, user.getShortName() );
		BasicLdap ldap = new BasicLdap();
		if ( ldap.exists(userDN, USER) ) {
			try {
				LdapSession session = ldap.getLdapSession();
				String filter = LdapSession.getObjectClass(USER);
				Entry userEntry = session.get(userDN.toString(), filter, PASSWORD_EXPIRATION_TIMESTAMP);
				Date expirationDate = userEntry.getAsDate(PASSWORD_EXPIRATION_TIMESTAMP);
				passwordExpired = new Date().after(expirationDate);
			} catch (LdapException e) {
				AonUtil.addErrorMessage("Error actualizando la fecha de expiración de la contraseña" );
			} finally {
				ldap.closeSession();
			}
		} else {
			LOGGER.severe( "No existe en LDAP el usuario " + user.getShortName() + " para el dominio " + domain );
		}
		return passwordExpired;
	}
	
	public String getUserName( String login ) {
		String name = login;
		BasicLdap ldap = new BasicLdap();
		try {
			DistinguishedName dn = AonDN.getUserDN(domain, login);
			String filter = LdapSession.getObjectClass("aonUser");
			Entry entry = ldap.getLdapSession().get(dn.toString(), filter, COMMON_NAME_ATTRIBUTE, SURNAME_ATTRIBUTE);
			if ( entry != null ) {
		    	name = entry.getAsString(COMMON_NAME_ATTRIBUTE);
		    	if (entry.containsKey(SURNAME_ATTRIBUTE) ) {
		    		name += " " + entry.getAsString(SURNAME_ATTRIBUTE);
		    	}				
			}
		} catch ( LdapException e ) {
			throw new AbortProcessingException( "Error getting name for " + login + ". " + e.getMessage(), e );
		} finally {
			ldap.closeSession();
		}
		return name;
	}

	public void updateUserLdapProperties( String userName, String name, String surname, String mail, String mobile ) {
		DistinguishedName userDN = AonDN.getUserDN( domain, userName );
		BasicLdap ldap = new BasicLdap();
		if ( ldap.exists(userDN, USER) ) {
			try {
				LdapSession session = ldap.getLdapSession();
				session.replaceAttribute(userDN, COMMON_NAME_ATTRIBUTE, name);
				session.replaceAttribute(userDN, SURNAME_ATTRIBUTE, surname);

				String filter = LdapSession.getObjectClass(USER);
				Entry userEntry = session.get(userDN.toString(), filter, COMMON_NAME_ATTRIBUTE, USER_ALTERNATIVE_EMAIL, USER_CELLULAR_NUMBER);
				String old_mail = null;
				String old_mobile = null;
				try {
					old_mail = userEntry.getAsString(USER_ALTERNATIVE_EMAIL);
				}
				catch (NullPointerException npe) {}
				try {
					old_mobile = userEntry.getAsString(USER_CELLULAR_NUMBER);
				}
				catch (NullPointerException npe) {}
				
				session.updateAttribute(userDN, USER_ALTERNATIVE_EMAIL, old_mail, mail);
				session.updateAttribute(userDN, USER_CELLULAR_NUMBER, old_mobile, mobile);
			} catch (LdapException e) {
				e.printStackTrace();
				AonUtil.addErrorMessage("Error actualizando los datos de usuario." );
			} finally {
				ldap.closeSession();
			}
		} else {
			LOGGER.severe( "No existe en LDAP el usuario " + userName + " para el dominio " + domain );
		}
	}

	public String getName() {
		User user = (User) getTo();
		String login = user.getLogin();
		BasicLdap ldap = new BasicLdap();
		try {
			DistinguishedName dn = AonDN.getUserDN(domain, login);
			String filter = LdapSession.getObjectClass("aonUser");
			Entry entry = ldap.getLdapSession().get(dn.toString(), filter, COMMON_NAME_ATTRIBUTE);
			if ( entry != null ) {
		    	name = entry.getAsString(COMMON_NAME_ATTRIBUTE);
			}
		} catch ( LdapException e ) {
			throw new AbortProcessingException( "Error getting name for " + login + ". " + e.getMessage(), e );
		} finally {
			ldap.closeSession();
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
		try {
			DistinguishedName dn = AonDN.getUserDN(domain, login);
			String filter = LdapSession.getObjectClass("aonUser");
			Entry entry = ldap.getLdapSession().get(dn.toString(), filter, SURNAME_ATTRIBUTE);
			if ( entry != null ) {
		    	surname = entry.getAsString(SURNAME_ATTRIBUTE);
			}
		} catch ( LdapException e ) {
			throw new AbortProcessingException( "Error getting surname for " + login + ". " + e.getMessage(), e );
		} finally {
			ldap.closeSession();
		}
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getAlternativeEmail() {
		User user = (User) getTo();
		String login = user.getLogin();
		BasicLdap ldap = new BasicLdap();
		try {
			DistinguishedName dn = AonDN.getUserDN(domain, login);
			String filter = LdapSession.getObjectClass("aonUser");
			Entry entry = ldap.getLdapSession().get(dn.toString(), filter, USER_ALTERNATIVE_EMAIL);
			if ( entry != null ) {
				try {
					alternativeEmail = entry.getAsString(USER_ALTERNATIVE_EMAIL);
				}
				catch (NullPointerException npe) {}
			}
		} catch ( LdapException e ) {
			throw new AbortProcessingException( "Error getting email for " + login + ". " + e.getMessage(), e );
		} finally {
			ldap.closeSession();
		}
		return alternativeEmail;
	}

	public void setAlternativeEmail(String alternativeEmail) {
		if (alternativeEmail.trim().equals("")) this.alternativeEmail = null;
		else this.alternativeEmail = alternativeEmail;
	}

	public String getCellular() {
		User user = (User) getTo();
		String login = user.getLogin();
		BasicLdap ldap = new BasicLdap();
		try {
			DistinguishedName dn = AonDN.getUserDN(domain, login);
			String filter = LdapSession.getObjectClass("aonUser");
			Entry entry = ldap.getLdapSession().get(dn.toString(), filter, USER_CELLULAR_NUMBER);
			if ( entry != null ) {
				try {
					cellular = entry.getAsString(USER_CELLULAR_NUMBER);
				}
				catch (NullPointerException npe) {}
			}
		} catch ( LdapException e ) {
			throw new AbortProcessingException( "Error getting cellular for " + login + ". " + e.getMessage(), e );
		} finally {
			ldap.closeSession();
		}
		return cellular;
	}

	public void setCellular(String cellular) {
		if (cellular.trim().equals("")) this.cellular = null;
		else this.cellular = cellular;
	}

}
