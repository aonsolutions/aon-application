package com.code.aon.ui.admin.controller;

import static com.code.aon.bridge.controller.ISecurityBridgeConstants.USER_PASSWORD_INVALID;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_CONTACT_DB;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_ACCOUNT_DB;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_CONFIG;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_SIGNATURE_DB;

import java.io.File;
import java.io.Serializable;
import java.util.Properties;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.common.util.PropertiesUtil;
import com.code.aon.config.User;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.admin.util.ManagerLogger;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.ContactDBController;
import com.code.aon.ui.webmail.controller.MailAccountDBController;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.code.aon.ui.webmail.controller.SignatureDBController;

public class AdminMainController implements IAdminConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AdminMainController.class);
	
	public static final String PROPERTIES_PATH = "/com/code/aon/ui/admin/";
	
	private static final String DEFAULT_PROPERTIES = PROPERTIES_PATH + "default.config.properties";
	
	private static final File MANAGER_PROPERTIES = new File( "/home/COMMON-RESOURCES/aon-admin/config.properties" );

	private String _user;

	private String _password;
	
	private Properties properties;
	
	private ManagerLogger logger;
	
	private boolean termsOfServiceAccepted;
	
	public AdminMainController() {
		this.properties = PropertiesUtil.getProperties(MANAGER_PROPERTIES, DEFAULT_PROPERTIES);
		this.logger = new ManagerLogger( this.properties.getProperty(NOTIFICATION_EMAIL) );
	}
	
	public void onInit( ActionEvent event ) {
		initDomain(event);
		if ( AonUtil.getRoleManager().isSysAdmin() ) {
			initSysAdmin(event);			
		}
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public ManagerLogger getLogger() {
		return logger;
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
			initSysAdmin(event);
		} else {
			String message = AonUtil.getMessage(BUNDLE_NAME, USER_PASSWORD_INVALID, _user);
			AonUtil.addErrorMessage(message);
		}
		_user = null;
		_password = null;
	}
	
	private void initSysAdmin( ActionEvent event ) {
		AonUtil.getRoleManager().setSysAdmin();
		MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
		mailConfig.setSystemAccountEditable(true);
	}

	private void initDomain( ActionEvent event ) {
		DomainController controller = (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
		try {
			if ( controller.getTo() == null ) {
				controller.select(event, DomainManager.getCurrentDomain());	
			}
			controller.initApplicationInfos();
			controller.initOEM();
			controller.updateDocumental();
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}				
	}

	private void initSignature( User user ) throws ManagerBeanException {
		SignatureDBController signature = (SignatureDBController) AonUtil.getRegisteredBean(BEAN_SIGNATURE_DB);
		signature.updateUser(user);
		signature.onSearch(null);
		MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
		String title = (user == null) ? AonUtil.getMessage(BUNDLE_NAME, SIGNATURE_ENTERPRISE_TITLE) : null;
		mailConfig.setSignatureTitle(title);
	}

	private void initMailAccount( User user ) throws ManagerBeanException {
		MailAccountDBController account = (MailAccountDBController) AonUtil.getRegisteredBean(BEAN_MAIL_ACCOUNT_DB);
		account.updateUser(user);
		account.onSearch(null);		
		MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
		String title = (user == null) ? AonUtil.getMessage(BUNDLE_NAME, MAIL_ACCOUNT_ENTERPRISE_TITLE) : null;
		mailConfig.setMailAccountTitle(title);
		mailConfig.setSkipDefaultAccountColumn(true);
	}

	private void initContact( User user ) throws ManagerBeanException {
		ContactDBController contact = (ContactDBController) AonUtil.getRegisteredBean(BEAN_CONTACT_DB);
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

	public static void removeLines( Class<? extends ITransferObject> _class, Serializable id, String ... aliases ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(_class);
		Criteria criteria = new Criteria();
		Expression exp = null;
		for( String alias : aliases ) {
			if ( exp == null ) {
				exp = ExpressionUtilities.getEqualExpression(bean.getFieldName(alias), id);
			} else {
				Expression exp1 = ExpressionUtilities.getEqualExpression(bean.getFieldName(alias), id);
				exp = ExpressionUtilities.getOrExpression(exp, exp1);
			}
		}
		criteria.addExpression(exp);
		for( ITransferObject to : bean.getList(criteria) ) {
			bean.remove(to);
		}	
	}
	
}