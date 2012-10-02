package com.code.aon.desktop.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
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
import com.code.aon.ldap.Scope;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class AonUserController implements ILdapConstants, IAonObjectClasses, IDesktopConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(AonUserController.class);
	
	private User user;
	
	private String name;
	
	private String surname;

	private String alternativeEmail;

	private String cellular;

	private String domain;
	
	private boolean passwordExpired;
	
	private boolean contactsEnabled;
	
	private boolean showPasswordChangedWindow;
	
	private String password;
	
	private String newPassword;
	
	private String confirmPassword;
	
	public AonUserController() {
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
		domain = principal.getDomain();
		passwordExpired = calculatePasswordExpired( principal.getShortName() );
		contactsEnabled = calculateContactsEnabled( principal.getShortName() );
		this.user = UserUtils.getInstance().getLoggedUser();
		setShowPasswordChangedWindow(false);
	}

	public String getDomain() {
		return domain;
	}
	
	public User getTo() {
		return user;
	}

	private void updatePassword( String userName, String newPassword ) {
		Name userDN = NameResolver.getUserDN( domain, userName );
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
			LOGGER.error( "No existe en LDAP el usuario {} para el dominio {}", userName, domain );
		}
	}
	
	private Entry getUserEntry() {
		BasicLdap ldap = new BasicLdap();
		Name dn = NameResolver.getUserDN(domain, user.getLogin());
		return ldap.get(dn, USER, COMMON_NAME_ATTRIBUTE, SURNAME_ATTRIBUTE, MAIL_ATTRIBUTE, MOBILE_ATTRIBUTE);
	}
	
	public void onInit(ActionEvent event) throws DeploymentException {
		setName(null);
		setSurname(null);
		setAlternativeEmail(null);
		setCellular(null);
		Entry entry = getUserEntry();
		if ( entry != null ) {
	    	setName( entry.getAsString(COMMON_NAME_ATTRIBUTE) );
	    	setSurname( entry.getAsString(SURNAME_ATTRIBUTE) );
			if ( entry.containsKey(MAIL_ATTRIBUTE) ) {
				setAlternativeEmail( entry.getAsString(MAIL_ATTRIBUTE) );	
			}	
			if ( entry.containsKey(MOBILE_ATTRIBUTE) ) {
				setCellular( entry.getAsString(MOBILE_ATTRIBUTE) );	
			}			
		} else {
			throw new AbortProcessingException( "Error getting name for " + user.getLogin() );
		}	
		setShowPasswordChangedWindow(false);
		setPassword(null);
		setNewPassword(null);
		setConfirmPassword(null);
	}
	
	public void accept(ActionEvent event) {
		user.setName(getName() + " " + getSurname());
		try {
			IManagerBean bean = BeanManager.getManagerBean(User.class);
			bean.update(user);
			updateUserLdapProperties();
		} catch (Exception e) {
			LOGGER.error( "Error cambiando datos del usuario.", e );
		}
	}

	private boolean isCorrectPassword() {
		Name userDN = NameResolver.getUserDN( domain, user.getLogin() );
		BasicLdap ldap = new BasicLdap();
		Entry entry = ldap.get(userDN, USER, USER_PASSWORD_ATTRIBUTE);
		if ( entry.containsKey(USER_PASSWORD_ATTRIBUTE) ) {
			byte[] value = entry.getAsByteArray(USER_PASSWORD_ATTRIBUTE);
			byte[] _password = BasicLdap.encodeSHA(this.password).getBytes();
			return Arrays.equals(value, _password);
		}
		return false;
	}
	
	public void acceptPassword(ActionEvent event) throws DeploymentException {
		if (! isCorrectPassword() ) {
			String message = AonUtil.addErrorMessageFromBundle( "securityBundle", "aon_security_passwd_error");
			throw new AbortProcessingException( message );			
		}
		if (! StringUtils.equals(newPassword, confirmPassword) ) {
			String message = AonUtil.addErrorMessageFromBundle( "securityBundle", "aon_security_new_passwd_error");
			throw new AbortProcessingException( message );
		}
		updatePassword( user.getLogin(), newPassword );			
		DomainController domainController = (DomainController) FormUtil.getController(DOMAIN_CONTROLLER_NAME);
		domainController.flushAuthenticationCache( user.getLogin() );
		setShowPasswordChangedWindow(true);
	}

	public boolean isShowPasswordChangedWindow() {
		return showPasswordChangedWindow;
	}

	public void setShowPasswordChangedWindow(boolean showPasswordChangedWindow) {
		this.showPasswordChangedWindow = showPasswordChangedWindow;
	}
	
	public boolean isContactsEnabled() {
		return this.contactsEnabled;
	}
		
	private boolean calculateContactsEnabled( String login ) {		
		Name contactsDN = NameResolver.getUserAddressBookDN( domain, login );
		BasicLdap ldap = new BasicLdap();
		boolean enabled = ldap.exists(contactsDN, ORGANIZATIONAL_UNIT);
		ldap.closeSession();
		return enabled;
	}
	
	public boolean isPasswordExpired() {
		return passwordExpired;
	}
	
	private boolean calculatePasswordExpired( String login ) {
		boolean passwordExpired = false;
		Name userDN = NameResolver.getUserDN( domain, login );
		BasicLdap ldap = new BasicLdap();
		if ( ldap.exists(userDN, USER) ) {
			Entry userEntry = ldap.get(userDN, USER,PASSWORD_EXPIRATION_TIMESTAMP_ATTRIBUTE);
			if ( userEntry != null ) {
				if ( (userEntry.getSearchDN() == null) && userEntry.containsKey(PASSWORD_EXPIRATION_TIMESTAMP_ATTRIBUTE) ) {
					Date expirationDate = userEntry.toDate(PASSWORD_EXPIRATION_TIMESTAMP_ATTRIBUTE);
					passwordExpired = new Date().after(expirationDate);
				}
			} else {
				AonUtil.addErrorMessage("Error calculando si la contraseña ha expirado" );
			}
		} else {
			LOGGER.error( "No existe en LDAP el usuario {} para el dominio {}", login, domain );
		}
		return passwordExpired;
	}

	private void updateUserLdapProperties() {
		Entry entry = getUserEntry();
		if ( entry != null ) {
			BasicLdap ldap = new BasicLdap();
			try {
				LdapSession session = ldap.getLdapSession();
				session.replaceAttribute(entry.getDN(), COMMON_NAME_ATTRIBUTE, getName());
				session.replaceAttribute(entry.getDN(), SURNAME_ATTRIBUTE, getSurname());
				
				String old_mail = null;
				if ( entry.containsKey(MAIL_ATTRIBUTE) ) {
					old_mail = entry.getAsString(MAIL_ATTRIBUTE);
				}
				String old_mobile = null;
				if ( entry.containsKey(MOBILE_ATTRIBUTE) ) {
					old_mobile = entry.getAsString(MOBILE_ATTRIBUTE);
				}
				session.updateAttribute(entry.getDN(), MAIL_ATTRIBUTE, old_mail, getAlternativeEmail());
				session.updateAttribute(entry.getDN(), MOBILE_ATTRIBUTE, old_mobile, getCellular());
			} catch (LdapException e) {
				AonUtil.addErrorMessage("Error actualizando los datos de usuario." );
			} finally {
				ldap.closeSession();
			}
		} else {
			LOGGER.error( "No existe en LDAP el usuario {} para el dominio {}", user.getLogin(), domain );
		}
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
		this.alternativeEmail = StringUtils.trimToNull(alternativeEmail);
	}

	public String getCellular() {
		return cellular;
	}

	public void setCellular(String cellular) {
		this.cellular = StringUtils.trimToNull(cellular);
	}

	private List<String> getUserApplications(String domainId, String userId) {
		List<String> applications = new ArrayList<String>();
		BasicLdap ldap = new BasicLdap();
		try {
			String objectClass = NameResolver.getObjectClass(DOMAIN_APPLICATION_USER);
			String cn = NameResolver.getCommonName(userId);
			String filter = NameResolver.getAndExpression(objectClass, cn);
			Name dn = NameResolver.getDomainApplicationsDN(domainId);
			List<Entry> list = ldap.getLdapSession().search( dn, filter, Scope.SUBTREE_SCOPE, COMMON_NAME_ATTRIBUTE);
			for( Entry entry : list ) {
				String application = NameResolver.getValue(entry.getDN(), 2);
				applications.add( application );
			}
		} catch ( Throwable th ) {
			LOGGER.error( th.getMessage(), th );
		} finally {
			ldap.closeSession();
		}
		return applications;
	}	
	
	public List<Entry> getUserApplications() {
		List<Entry> applications = new LinkedList<Entry>();
		BasicLdap ldap = new BasicLdap();
		for( String name : getUserApplications(domain, user.getLogin()) ) {
			Name dn = NameResolver.getApplicationDN(name);
			Entry application = ldap.get(dn, APPLICATION);
			if ( application != null ) {
				applications.add( application );	
			}
		}		
		return applications;		
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
