package com.code.aon.ui.config.controller;

import static com.code.aon.bridge.controller.ISecurityBridgeConstants.BUNDLE_NAME;
import static com.code.aon.bridge.controller.ISecurityBridgeConstants.NEW_PASSWORD_ERROR;
import static com.code.aon.bridge.controller.ISecurityBridgeConstants.PASSWORD_ERROR;
import static com.code.aon.ldap.IAonObjectClasses.MAIL_ACCOUNT;
import static com.code.aon.ldap.IAonObjectClasses.USER;
import static com.code.aon.ldap.ILdapConstants.PASSWORD_EXPIRATION_TIMESTAMP_ATTRIBUTE;
import static com.code.aon.ldap.ILdapConstants.USER_PASSWORD_ATTRIBUTE;

import java.util.Arrays;
import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.plugin.UserManager;
import com.code.aon.bridge.plugin.Utils;
import com.code.aon.config.User;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;

public class ChangePasswordController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ChangePasswordController.class);
	
	private String user;
	
	private String domain;
	
	private User to;
	
	private boolean showPasswordChangedWindow;
	
	private String password;
	
	private String newPassword;
	
	private String confirmPassword;
	
	public ChangePasswordController() {
		AuthPrincipal principal = UserUtils.getInstance().getPrincipal();
		this.user = principal.getShortName();
		this.domain = principal.getDomain();
		this.to = UserUtils.getInstance().getLoggedUser();
	}
	
	public User getTo() {
		return to;
	}
	
	public void onInit(ActionEvent event) throws DeploymentException {
		setShowPasswordChangedWindow(false);
		setPassword(null);
		setNewPassword(null);
		setConfirmPassword(null);
	}	

	private void changeDefaultMailAccountPassword( String newPassword ) {
		Name accountsDN = NameResolver.getUserDefaultAccount( this.domain, this.user );
		BasicLdap ldap = new BasicLdap();
		if ( ldap.exists(accountsDN, MAIL_ACCOUNT ) ) {
			try {
				ldap.getLdapSession().replaceAttribute(accountsDN, USER_PASSWORD_ATTRIBUTE, newPassword);
			} catch (LdapException e) {
				AonUtil.addErrorMessage("Error cambiando la contraseña de la cuenta de correo por defecto del usuario " + user );
			} finally {
				ldap.closeSession();
			}
		} else {
			AonUtil.addErrorMessage("No se ha encontrado la cuenta de correo por defecto del usuario " + user );
		}
	}

	private void updatePassword( String newPassword ) {
		Name userDN = NameResolver.getUserDN( this.domain, this.user );
		BasicLdap ldap = new BasicLdap();
		if ( ldap.exists(userDN, USER) ) {
			try {
				Date newDate = DateUtils.addDays(new Date(), 180);
				LdapSession session = ldap.getLdapSession();
				session.replaceAttribute(userDN, PASSWORD_EXPIRATION_TIMESTAMP_ATTRIBUTE, newDate);
				String encodedPassword = BasicLdap.encodeSHA(newPassword);
				session.replaceAttribute(userDN, USER_PASSWORD_ATTRIBUTE, encodedPassword);
			} catch (LdapException e) {
				AonUtil.addErrorMessage("Error actualizando la fecha de expiración de la contraseña" );
			} finally {
				ldap.closeSession();
			}
		} else {
			LOGGER.error( "No existe en LDAP el usuario {} para el dominio {}", user, domain );
		}
	}

	private boolean isCorrectPassword() {
		Name userDN = NameResolver.getUserDN( domain, user );
		BasicLdap ldap = new BasicLdap();
		Entry entry = ldap.get(userDN, USER, USER_PASSWORD_ATTRIBUTE);
		if ( entry.containsKey(USER_PASSWORD_ATTRIBUTE) ) {
			byte[] value = entry.getAsByteArray(USER_PASSWORD_ATTRIBUTE);
			byte[] _password = BasicLdap.encodeSHA(this.password).getBytes();
			return Arrays.equals(value, _password);
		}
		return false;
	}
	
	public void flushAuthenticationCache( String userName ) throws DeploymentException {
		IConsoleAdmin console = Utils.getSecurityConsole();
		AuthPrincipal principal = null;
		if ( userName != null ) {
			String name = UserUtils.getInstance().getPrincipal().getName();
			principal = new AuthPrincipal( userName + name.substring(name.indexOf('@')));			
		}
		console.flushAuthenticationCache(UserManager.LDAP_SECURITY_DOMAIN, principal);
	}
	
	public void acceptPassword(ActionEvent event) throws DeploymentException {
		if (! isCorrectPassword() ) {
			String message = AonUtil.addErrorMessageFromBundle( BUNDLE_NAME, PASSWORD_ERROR);
			throw new AbortProcessingException( message );			
		}
		if (! StringUtils.equals(newPassword, confirmPassword) ) {
			String message = AonUtil.addErrorMessageFromBundle( BUNDLE_NAME, NEW_PASSWORD_ERROR);
			throw new AbortProcessingException( message );
		}
		updatePassword( newPassword );
		changeDefaultMailAccountPassword( newPassword );				
		flushAuthenticationCache( user );
		setShowPasswordChangedWindow(true);
	}

	public boolean isShowPasswordChangedWindow() {
		return showPasswordChangedWindow;
	}

	public void setShowPasswordChangedWindow(boolean showPasswordChangedWindow) {
		this.showPasswordChangedWindow = showPasswordChangedWindow;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
}
