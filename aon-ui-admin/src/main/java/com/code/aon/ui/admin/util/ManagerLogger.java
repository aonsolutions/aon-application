package com.code.aon.ui.admin.util;

import static com.code.aon.ui.admin.controller.IAdminConstants.BUNDLE_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.WRONG_MAIL_ACCOUNT;
import static com.code.aon.ui.common.ICommonConstants.LOGGED_USER_CONTROLLER_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_CONFIG;

import java.util.Date;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationUser;
import com.code.aon.config.Domain;
import com.code.aon.config.DomainApplication;
import com.code.aon.config.User;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.common.controller.LoggedUser;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.code.aon.webmail.EmailSender;
import com.code.aon.webmail.IMailAccount;
import com.code.aon.webmail.bean.AonServer;

public class ManagerLogger {

	private final static Logger LOGGER = LoggerFactory.getLogger(ManagerLogger.class);
	
	private EmailSender sender;
	
	private AuthPrincipal loggedUser;
	
	private Address[] to;
	
	private boolean configured;
	
	private IMailAccount getMailAccount() throws ManagerBeanException {
		MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
		IMailAccount account = mailConfig.getDefaultMailAccount();
		if (account!=null) {
			if ( AonServer.test(account, false, true) ) {
				return account;
			} else {
				AonUtil.addErrorMessageFromBundle(BUNDLE_NAME, WRONG_MAIL_ACCOUNT, account.getName());
			}
		}
		return null;
	}
	
	public ManagerLogger( String toEmails ) {
		LoggedUser lu = (LoggedUser) AonUtil.getRegisteredBean(LOGGED_USER_CONTROLLER_NAME);
		loggedUser = lu.getPrincipal();
		try {
			to = InternetAddress.parse(toEmails);
			IMailAccount account = getMailAccount();
			if ( account != null ) {
				Address from = InternetAddress.parse(account.getEmail())[0];
				this.sender = new EmailSender(from, account);
				this.configured = true;				
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e );
		} catch (AddressException e) {
			LOGGER.error(e.getMessage(), e );
		}
	}
	
	public boolean isConfigured() {
		return this.configured;
	}
	
	private void sendEmail( String subject, String content ) {
		if ( isConfigured() ) {
			try {		
				String fullContent = subject + SystemUtils.LINE_SEPARATOR + content;
				sender.sendMessage(to, subject, fullContent);
			} catch (Throwable e) {
				LOGGER.error(e.getMessage(), e );
			}
		}
	}
	
	private String getContent() {
		StringBuffer sb = new StringBuffer();
		sb.append( "Date: ").append( new Date() ).append( SystemUtils.LINE_SEPARATOR );
		sb.append( "User: ").append( loggedUser ).append( SystemUtils.LINE_SEPARATOR );
		return sb.toString();
	}
	
	public void domainAddded( Domain domain ) {
		String subject = "NEW: Domain " + domain.getName();
		sendEmail( subject, getContent() );
	}

	public void domainRemoved( Domain domain ) {
		String subject = "REMOVED: Domain " + domain.getName();
		sendEmail( subject, getContent() );
	}	

	public void domainApplicationAddded( DomainApplication da ) {
		String subject = "NEW: Application " + da.getApplication().getName() + " in Domain " + da.getDomain();
		sendEmail( subject, getContent() );
	}

	public void domainApplicationRemoved( DomainApplication da ) {
		String subject = "REMOVED: Application " + da.getApplication().getName() + " in Domain " + da.getDomain();
		sendEmail( subject, getContent() );
	}	
	
	public void domainUserAddded( User user ) {
		String subject = "NEW: User " + user.getName() + " in Domain " + user.getDomain();
		sendEmail( subject, getContent() );
	}

	public void domainUserdRemoved( User user ) {
		String subject = "REMOVED: User " + user.getName() + " in Domain " + user.getDomain();
		sendEmail( subject, getContent() );
	}

	public void domainApplicationUserAddded( ApplicationUser user ) {
		String application = user.getDomainApplication().getApplication().getName();
		String subject = "NEW: User " + user.getUser().getLogin() + " in Application " + application + " in Domain " + user.getUser().getDomain();
		sendEmail( subject, getContent() );
	}

	public void domainApplicationUserRemoved( ApplicationUser user ) {
		String application = user.getDomainApplication().getApplication().getName();
		String subject = "REMOVED: User " + user.getUser().getLogin() + " in Application " + application + " in Domain " + user.getUser().getDomain();
		sendEmail( subject, getContent() );
	}	
	
	public void multiUser( Domain domain ) {
		String subject = "Domain " + domain.getName() + " Multiuser: " + domain.isUserManagement();
		sendEmail( subject, getContent() );
	}
	
	public void multiDomain( Domain domain ) {
		String subject = "Domain " + domain.getName() + " MultiDomain: " + domain.isDomainManagement();
		sendEmail( subject, getContent() );		
	}

	public void documental( Domain domain ) {
		String subject = "Domain " + domain.getName() + " Documental: " + domain.isDocumentManagement();
		sendEmail( subject, getContent() );		
	}

	public void maxTotalDocumentSizeChanged( Domain domain, Integer previous, Integer current ) {
		String subject = "Domain " + domain.getName() + " Documental Storage Changed from " + previous + "MB to " + current + "MB";
		sendEmail( subject, getContent() );		
	}
	
}