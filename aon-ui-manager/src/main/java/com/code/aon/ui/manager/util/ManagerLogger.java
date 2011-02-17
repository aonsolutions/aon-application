package com.code.aon.ui.manager.util;

import static com.code.aon.ui.manager.controller.IManagerConstants.BUNDLE_NAME;
import static com.code.aon.ui.manager.controller.IManagerConstants.NEED_MAIL_ACCOUNT;
import static com.code.aon.ui.manager.controller.IManagerConstants.WRONG_MAIL_ACCOUNT;

import java.util.Date;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.session.LoggedUser;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.manager.Domain;
import com.code.aon.manager.DomainApplication;
import com.code.aon.manager.DomainApplicationUser;
import com.code.aon.manager.DomainUser;
import com.code.aon.ui.manager.controller.IManagerConstants;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.EmailSender;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.WebmailUtil;

public class ManagerLogger {

	private final static Logger LOGGER = LoggerFactory.getLogger(ManagerLogger.class);
	
	private EmailSender sender;
	
	private AuthPrincipal loggedUser;
	
	private Address[] to;
	
	private boolean configured;
	
	public ManagerLogger( String toEmails ) {
		LoggedUser _loggedUser = (LoggedUser) AonUtil.getRegisteredBean(LoggedUser.LOGGED_USER);
		loggedUser = _loggedUser.getPrincipal();
		try {
			to = InternetAddress.parse(toEmails);
			MailAccount account = WebmailUtil.getDefaultAccount(loggedUser.getDomain(), loggedUser.getShortName());
			if ( account != null ) {
				Address from = InternetAddress.parse(account.getEmail())[0];
				this.sender = new EmailSender(from, account);			
				try {
					this.sender.connect();
					this.configured = true;
				} catch (MessagingException e) {
					AonUtil.addErrorMessageFromBundle(BUNDLE_NAME, WRONG_MAIL_ACCOUNT, e.getMessage());
				} finally {
					this.sender.disconnect();	
					if (! this.configured ) {
						this.sender = null;
					}
				}
			} else {
				AonUtil.addErrorMessageFromBundle(BUNDLE_NAME, NEED_MAIL_ACCOUNT, loggedUser.getShortName());				
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
				sender.connect();
				sender.sendMessage(to, subject, content);
				sender.disconnect();
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
		String subject = "NEW: Domain " + domain.getCommonName();
		sendEmail( subject, getContent() );
	}

	public void domainRemoved( Domain domain ) {
		String subject = "REMOVED: Domain " + domain.getCommonName();
		sendEmail( subject, getContent() );
	}	

	public void domainApplicationAddded( DomainApplication da ) {
		String subject = "NEW: Application " + da.getCommonName() + " in Domain " + da.getDomain();
		sendEmail( subject, getContent() );
	}

	public void domainApplicationRemoved( DomainApplication da ) {
		String subject = "REMOVED: Application " + da.getCommonName() + " in Domain " + da.getDomain();
		sendEmail( subject, getContent() );
	}	
	
	public void domainUserAddded( DomainUser user ) {
		String subject = "NEW: User " + user.getName() + " in Domain " + user.getDomain();
		sendEmail( subject, getContent() );
	}

	public void domainUserdRemoved( DomainUser user ) {
		String subject = "REMOVED: User " + user.getName() + " in Domain " + user.getDomain();
		sendEmail( subject, getContent() );
	}

	public void domainApplicationUserAddded( DomainApplicationUser user ) {
		String subject = "NEW: User " + user.getCommonName() + " in Application " + user.getAppplication() + " in Domain " + user.getDomain();
		sendEmail( subject, getContent() );
	}

	public void domainApplicationUserRemoved( DomainApplicationUser user ) {
		String subject = "REMOVED: User " + user.getCommonName() + " in Application " + user.getAppplication() + " in Domain " + user.getDomain();
		sendEmail( subject, getContent() );
	}	
	
	public void multiUser( Domain domain ) {
		String subject = "Domain " + domain.getCommonName() + " Multiuser: " + domain.getUserManagement();
		sendEmail( subject, getContent() );		
	}
	
	public void multiDomain( Domain domain ) {
		String subject = "Domain " + domain.getCommonName() + " MultiDomain: " + domain.getDomainManagement();
		sendEmail( subject, getContent() );		
	}

	public void documental( Domain domain ) {
		String subject = "Domain " + domain.getCommonName() + " Documental: " + domain.isDocumentManagement();
		sendEmail( subject, getContent() );		
	}
	
}
