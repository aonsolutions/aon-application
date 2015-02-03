package com.code.aon.ui.admin.controller;

import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_CONFIG;

import java.io.Serializable;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.User;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.ContactDBController;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MailAccountDBController;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.code.aon.ui.webmail.controller.SignatureDBController;
import com.esferalia.aon.jooq.tables.records.UserRecord;
import com.esferalia.aon.occam.api.AONContext;

public class AdminMainController implements IAdminConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AdminMainController.class);
	
	private String _user;

	private String _password;
	
	private boolean termsOfServiceAccepted;
	
	private String advancedModeBackAction;
	
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
	
	public void onInitAdvancedMode(ActionEvent event) {
		this.advancedModeBackAction = AonUtil.getConfigurationController().getCurrentAction(); 
	}
	
	public String advancedModeBackAction() {
		return AonUtil.getRoleManager().isSysAdmin() ? advancedModeBackAction : null;
	}
	
	private UserRecord getAdminDomainUser() {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		AONContext ctx = AONContext.getAONContext(ds.getDomainNameURL(), ds.getDomainId());
		Condition expirationCondition = USER.PASSWORDEXPIRATION.isNull()
				.or(USER.PASSWORDEXPIRATION.gt(DSL.currentDate()));		
		UserRecord user = null;
		try {
			user = ctx.getDslContext()
				.select().from(USER).join(DOMAIN).onKey()
				.where(USER.ACTIVE.eq((byte)1)
						.and(expirationCondition)
						.and(USER.LOGIN.eq(getUser()))
						.and(DOMAIN.TYPE.eq((byte) DomainType.ADMIN.ordinal())) )
				.fetchAny().into(UserRecord.class);
		} catch ( Throwable th ) {
			LOGGER.debug( th.getMessage(), th );
		} finally {
			ctx.finalize();	
		}
		return user;
	}
	
	public void onAccept(ActionEvent event) {
		String crypted = AdminUtil.encodeSHA(_password);
		UserRecord user = getAdminDomainUser();
		if ( user != null ) {
			if ( StringUtils.equals(crypted, user.getPassword()) ) {
				AonUtil.getRoleManager().setSysAdmin();
			} else {
				AonUtil.addErrorMessageFromBundle(ICommonMessages.USER_PASSWORD_INVALID, _user);
			}			
		} else {
			AonUtil.addErrorMessageFromBundle(ICommonMessages.USER_INVALID, _user);
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