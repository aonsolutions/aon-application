package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_CONFIG;

import java.io.File;
import java.util.Properties;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.common.util.PropertiesUtil;
import com.code.aon.config.User;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.ContactDBController;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MailAccountDBController;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.code.aon.ui.webmail.controller.SignatureDBController;

public class AdminMainController implements IAdminConstants {
	
	public static final String PROPERTIES_PATH = "/com/code/aon/ui/admin/";
	
	private static final String DEFAULT_PROPERTIES = PROPERTIES_PATH + "default.config.properties";
	
	private static final File MANAGER_PROPERTIES = new File( "/home/COMMON-RESOURCES/aon-admin/config.properties" );

	private String _user;

	private String _password;
	
	private Properties properties;
	
	private boolean termsOfServiceAccepted;
	
	public AdminMainController() {
		this.properties = PropertiesUtil.getProperties(MANAGER_PROPERTIES, DEFAULT_PROPERTIES);
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public String getUser() {
		return _user;
	}

	public void setUser(String user) {
		this._user = user;
	}

	public String getPasswd() {
		return _password;
	}

	public void setPasswd(String passwd) {
		this._password = passwd;
	}
	
	public boolean isTermsOfServiceAccepted() {
		return termsOfServiceAccepted;
	}

	public void setTermsOfServiceAccepted(boolean termsOfServiceAccepted) {
		this.termsOfServiceAccepted = termsOfServiceAccepted;
	}	

	public void resetTermsOfServiceAccepted() {
		termsOfServiceAccepted = AonUtil.getRoleManager().isSysAdmin();
	}
	
	public void onAccept(ActionEvent event) {
		String crypted = AdminUtil.encodeSHA(_password);
		String amUser = getProperties().getProperty(ADVANCED_MODE_USER); 
		String amPassword = getProperties().getProperty(ADVANCED_MODE_PASSWORD);
		if (amUser.equals(_user) && amPassword.equals(crypted)) {
			AonUtil.getRoleManager().setSysAdmin();
		} else {
			AonUtil.addErrorMessageFromBundle(ICommonMessages.USER_PASSWORD_INVALID, _user);
		}
		_user = null;
		_password = null;
	}
	
	private void initSignature( User user ) throws ManagerBeanException {
		SignatureDBController signature = (SignatureDBController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_SIGNATURE_DB);
		signature.updateUser(user);
		signature.onSearch(null);
		MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
		String title = (user == null) ? AonUtil.getMessage(ICommonMessages.SIGNATURE_ENTERPRISE_TITLE) : null;
		mailConfig.setSignatureTitle(title);
	}

	private void initMailAccount( User user ) throws ManagerBeanException {
		MailAccountDBController account = (MailAccountDBController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MAIL_ACCOUNT_DB);
		account.updateUser(user);
		account.onSearch(null);		
		MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
		String title = (user == null) ? AonUtil.getMessage(ICommonMessages.MAIL_ACCOUNT_ENTERPRISE_TITLE) : null;
		mailConfig.setMailAccountTitle(title);
		mailConfig.setSkipDefaultAccountColumn(true);
	}

	private void initContact( User user ) throws ManagerBeanException {
		ContactDBController contact = (ContactDBController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_CONTACT_DB);
		contact.updateUser(user);
		contact.onSearch(null);
	}
	
	public void onInitMailAccount( ActionEvent event ) throws ManagerBeanException {
		initMailAccount(null);
	}

	public void onInitSignature( ActionEvent event ) throws ManagerBeanException {
		initSignature(null);
	}
	
	public void initWebmail( User user ) throws ManagerBeanException {
		initSignature(user);
		initMailAccount(user);
		initContact(user);
	}
	
}