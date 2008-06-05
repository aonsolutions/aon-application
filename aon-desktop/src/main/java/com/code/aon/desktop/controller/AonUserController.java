package com.code.aon.desktop.controller;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.time.DateUtils;

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

	private static final Logger LOGGER = Logger.getLogger(AonUserController.class.getName());
	
	private String domain;
	
	private boolean managerChangingPassword;
	
	private boolean showPasswordChangedWindow;
	
	private boolean accepted;
	
	public AonUserController() {
		AuthPrincipal user = UserUtils.getInstance().getPrincipal();
		domain = user.getDomain();
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

	private void updateExpirationTimestamp( String userName, boolean today ) {
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
			}
		} else {
			LOGGER.severe( "No existe en LDAP el usuario " + userName + " para el dominio " + domain );
		}
	}
	
	public void accept(ActionEvent event) {
		User user = (User) getTo();
		int status = user.getStatus();
		user.setStatus(status + 2);
		try {
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
	
}
