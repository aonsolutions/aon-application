package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.ADMIN_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.BUNDLE_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.MAIL_ACCOUNT_ENTERPRISE_TITLE;
import static com.code.aon.ui.admin.controller.IAdminConstants.SIGNATURE_ENTERPRISE_TITLE;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_CONTACT_DB;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_ACCOUNT_DB;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_CONFIG;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_SIGNATURE_DB;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Domain;
import com.code.aon.config.User;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.ContactDBController;
import com.code.aon.ui.webmail.controller.MailAccountDBController;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.code.aon.ui.webmail.controller.SignatureDBController;

public class DomainController extends BasicController {

	public final static int DEFAULT_MAX_DOCUMENT_SIZE = 1;
	
	public final static int DEFAULT_MAX_TOTAL_DOCUMENT_SIZE = 100;	
	
	private AdminMainController getAdmin() {
		return (AdminMainController) AonUtil.getRegisteredBean(ADMIN_CONTROLLER_NAME);
	}
	
	public Domain getDomain() {
		return (Domain) getTo();
	}	
	
	public Domain getParentDomain() {
		Domain parent = getDomain().getParent();
		if ( (parent != null) && (parent.getId() != null) ) {
			return parent;
		}		
		return null;
	}
	
	public boolean isShowDomainSubDomainSuffix() {
		if ( getDomain().getType() != DomainType.ENTERPRISE ) {
			if ( getDomain().isDomainManagement() || getAdmin().isSysAdmin() ) {
				return true;
			}
		}
		return false;
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
	
}