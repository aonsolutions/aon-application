package com.code.aon.ui.manager.controller;

import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_UNIT;
import static com.code.aon.ldap.ILdapConstants.USER_PASSWORD_ATTRIBUTE;

import java.security.MessageDigest;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.jaas.auth.util.Util;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.DomainUser;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.LdapBasicController;

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

	public void createUserWebmailDefaultData( DomainUser user ) {
		BasicLdap ldap = new BasicLdap();
		String domain = NameResolver.getValue(user.getId(), 2);
		Name addressBookDN = NameResolver.getUserAddressBookDN(domain, user.getUid());
		if (! ldap.exists(addressBookDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(addressBookDN);
		}
		Name accountsDN = NameResolver.getUserAccountsDN(domain, user.getUid());
		if (! ldap.exists(accountsDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(accountsDN);
		}
		Name signaturesDN = NameResolver.getUserSignaturesDN(domain, user.getUid());
		if (! ldap.exists(signaturesDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(signaturesDN);
		}
	}
	
	public void onShowChangePasswordWindow( ActionEvent event ) {
		setShowChangePasswordWindow(true);
		setNewPassword(null);
		setConfirmPassword(null);
	}

	public void onChangePassword( ActionEvent event ) {
		if (! StringUtils.equals(newPassword, confirmPassword)) {
			String message = AonUtil.addErrorMessageFromBundle( BUNDLE_NAME, NEW_PASSWORD_ERROR );
			throw new AbortProcessingException( message );
		}		
		DomainUser user = (DomainUser) getTo();
		BasicLdap ldap = new BasicLdap();
		try {
	        byte[] hash = MessageDigest.getInstance("SHA").digest(newPassword.getBytes());
	        String passwordHash = "{SHA}" + Util.encodeBase64(hash);		
			ldap.getLdapSession().replaceAttribute(user.getId(), USER_PASSWORD_ATTRIBUTE, passwordHash);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage("Error cambiando la contraseña" );
		} finally {
			ldap.closeSession();
		}
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
		String domain = NameResolver.getValue(user.getId(), 2);
		Name dn = NameResolver.getDomainApplicationUserDN(domain, "aon-webmail", user.getUid());
		BasicLdap ldap = new BasicLdap();
		return ldap.exists(dn, IAonObjectClasses.DOMAIN_APPLICATION_USER);
	}
	
	public boolean isWebmail() {
		return webmail;
	}

	public void setWebmail(boolean webmail) {
		this.webmail = webmail;
	}	
	
}
