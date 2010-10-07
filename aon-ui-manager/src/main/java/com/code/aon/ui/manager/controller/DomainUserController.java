package com.code.aon.ui.manager.controller;

import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_UNIT;
import static com.code.aon.ldap.ILdapConstants.USER_PASSWORD_ATTRIBUTE;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_EMAIL;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_HOST;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_INCOMING_HOST;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_INCOMING_PORT;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_INCOMING_SSL;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_MAIL_USERNAME;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_OUTGOING_HOST;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_OUTGOING_PORT;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_OUTGOING_SSL;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_OUTGOING_VERIFICATION;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_PROTOCOL;
import static com.code.aon.webmail.dao.IWebMailAlias.SIGNATURE_SIGNATURE;

import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.naming.Name;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.jaas.auth.util.Util;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.DomainApplicationUser;
import com.code.aon.manager.DomainUser;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.LdapBasicController;
import com.code.aon.ui.webmail.controller.MailAccountController;
import com.code.aon.ui.webmail.controller.SignatureController;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.Signature;

public class DomainUserController extends LdapBasicController implements IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainUserController.class);
	
	private boolean showChangePasswordWindow;
	
	private String newPassword;
	
	private String confirmPassword;
	
	private boolean webmail;
	
	private String selectedTab;
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}	

	@SuppressWarnings("unchecked")
	public List<DomainUser> getUsers() throws ManagerBeanException {
		return (List) getModel().getWrappedData();
	}	
	
	public DomainUser getDomainUser() {
		return (DomainUser) getTo();
	}	
	
	@Override
	public void updateBaseDN(Name parent) {
		String domain = NameResolver.getValue(parent, 0);
		Name baseDN = NameResolver.getUsersDN(domain);
		getLdapDAO().setBaseDN(baseDN);
	}

	private Signature addDefaultSignature( DomainUser user ) throws ManagerBeanException {
		Signature signature = new Signature();
		signature.setName( user.getDomain() );
		ManagerController manager = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
		String text = manager.getProperties().getProperty(SIGNATURE_SIGNATURE);
		String content = MessageFormat.format( text, user.getFullName() );	
		signature.setSignature(content);
		SignatureController controller = (SignatureController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_SIGNATURE);
		controller.updateBaseDN(user.getId());
		controller.getManagerBean().insert( signature );
		return signature;
	}

	private void addDefaultMailAccount( DomainUser user, Signature signature ) throws ManagerBeanException {
		MailAccount account = new MailAccount();
		account.setName(NameResolver.DEFAULT_MAIL_ACCOUNT_NAME);
		account.setPasswordString(user.getUid());
		account.setSignature(signature);
		ManagerController manager = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
		Properties properties = manager.getProperties();
		String email = MessageFormat.format( properties.getProperty(MAIL_ACCOUNT_EMAIL), user.getUid(), user.getDomain() );
		account.setEmail(email);
		String mailUsername = MessageFormat.format( properties.getProperty(MAIL_ACCOUNT_MAIL_USERNAME), user.getUid(), user.getDomain() );
		account.setMailUsername(mailUsername);
		String host = MessageFormat.format( properties.getProperty(MAIL_ACCOUNT_HOST), user.getDomain() );
		account.setHost(host);		
		account.setProtocol( properties.getProperty(MAIL_ACCOUNT_PROTOCOL) );
		String incomingHost = MessageFormat.format( properties.getProperty(MAIL_ACCOUNT_INCOMING_HOST), user.getDomain() );
		account.setIncomingHost(incomingHost);
		int incomingPort = NumberUtils.toInt(properties.getProperty(MAIL_ACCOUNT_INCOMING_PORT));
		account.setIncomingPort(incomingPort);
		boolean incomingSsl = BooleanUtils.toBoolean(properties.getProperty(MAIL_ACCOUNT_INCOMING_SSL));
		account.setIncomingSsl(incomingSsl);
		String outgoingHost = MessageFormat.format( properties.getProperty(MAIL_ACCOUNT_OUTGOING_HOST), user.getDomain() );
		account.setOutgoingHost(outgoingHost);
		int outgoingPort = NumberUtils.toInt(properties.getProperty(MAIL_ACCOUNT_OUTGOING_PORT));
		account.setOutgoingPort(outgoingPort);
		boolean outgoingSsl = BooleanUtils.toBoolean(properties.getProperty(MAIL_ACCOUNT_OUTGOING_SSL));
		account.setOutgoingSsl(outgoingSsl);
		boolean outgoingVerification = BooleanUtils.toBoolean(properties.getProperty(MAIL_ACCOUNT_OUTGOING_VERIFICATION));
		account.setOutgoingVerification(outgoingVerification);
		MailAccountController controller = (MailAccountController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MAIL_ACCOUNT);
		controller.updateBaseDN(user.getId());
		controller.getManagerBean().insert( account );				
	}
	
	public void createUserWebmailDefaultData( DomainUser user ) throws ManagerBeanException {
		BasicLdap ldap = new BasicLdap();
		String domain = user.getDomain();
		Name addressBookDN = NameResolver.getUserAddressBookDN(domain, user.getUid());
		if (! ldap.exists(addressBookDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(addressBookDN);
		}
		Name signaturesDN = NameResolver.getUserSignaturesDN(domain, user.getUid());
		if (! ldap.exists(signaturesDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(signaturesDN);
		}
		Signature signature = addDefaultSignature(user);
		Name accountsDN = NameResolver.getUserAccountsDN(domain, user.getUid());
		if (! ldap.exists(accountsDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(accountsDN);
		}
		addDefaultMailAccount(user, signature);
	}
	
	public void onShowChangePasswordWindow( ActionEvent event ) {
		setShowChangePasswordWindow(true);
		setNewPassword(null);
		setConfirmPassword(null);
	}

	private String getSHAPassword( String value ) {
		String shaPassword = null;
		try {
			byte[] hash = MessageDigest.getInstance("SHA").digest(value.getBytes());
			shaPassword = "{SHA}" + Util.encodeBase64(hash);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}        		
		return shaPassword;
	}
	
	public void onChangePassword( ActionEvent event ) {
		if (! StringUtils.equals(newPassword, confirmPassword)) {
			String message = AonUtil.addErrorMessageFromBundle( BUNDLE_NAME, NEW_PASSWORD_ERROR );
			throw new AbortProcessingException( message );
		}		
		DomainUser user = getDomainUser();
		BasicLdap ldap = new BasicLdap();
		try {
	        String passwordHash = getSHAPassword(newPassword);
			ldap.getLdapSession().replaceAttribute(user.getId(), USER_PASSWORD_ATTRIBUTE, passwordHash);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("Error cambiando la contraseña" );
		} finally {
			ldap.closeSession();
		}
	}
	
	public void onResetPassword( ActionEvent event ) {
		DomainUser user = getDomainUser();
		user.setPasswordExpirationTimestamp( DateUtils.addDays(new Date(), -1) );
		String newPassword = getSHAPassword(user.getUid());
		user.setPasswordString( newPassword );
	}	
	
	public boolean isShowChangePasswordWindow() {
		return showChangePasswordWindow;
	}

	public void setShowChangePasswordWindow(boolean showChangePasswordWindow) {
		this.showChangePasswordWindow = showChangePasswordWindow;
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

	public boolean hasWebmail( DomainUser user ) {
		String domain = user.getDomain();
		Name dn = NameResolver.getDomainApplicationUserDN(domain, AON_WEBMAIL, user.getUid());
		BasicLdap ldap = new BasicLdap();
		return ldap.exists(dn, IAonObjectClasses.DOMAIN_APPLICATION_USER);
	}
	
	public boolean isWebmail() {
		return webmail;
	}

	public void setWebmail(boolean webmail) {
		this.webmail = webmail;
	}	

	public void registerUserInApplication( DomainUser user, String application, String profile ) throws ManagerBeanException {
		BasicLdap ldap = new BasicLdap();
		Name applicationDN = NameResolver.getApplicationDN(application);
		if (! ldap.exists(applicationDN, IAonObjectClasses.APPLICATION) ) {
			LOGGER.error( "Application doesn't exist: " + applicationDN );
			return;
		}
		Name profileDN = NameResolver.getApplicationProfileDN(application, profile);
		if (! ldap.exists(profileDN, IAonObjectClasses.PROFILE) ) {
			LOGGER.error( "Profile doesn't exist: " + profileDN );
			return;
		}
		DomainApplicationUser dau = new DomainApplicationUser();
		dau.setCommonName( user.getUid() );
		List<Name> profiles = new LinkedList<Name>();
		profiles.add( profileDN );
		dau.setProfiles( profiles );
		Name domainApplicationDN = NameResolver.getDomainApplicationDN(user.getDomain(), application);
		DomainApplicationUserController dauc = (DomainApplicationUserController) AonUtil.getRegisteredBean(DOMAIN_APPLICATION_USER_CONTROLLER_NAME);
		dauc.updateBaseDN(domainApplicationDN);
		dauc.getManagerBean().insert( dau );
	}
	
}
