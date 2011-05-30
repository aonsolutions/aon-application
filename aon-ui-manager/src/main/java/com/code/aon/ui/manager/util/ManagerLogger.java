package com.code.aon.ui.manager.util;

import static com.code.aon.ui.manager.controller.IManagerConstants.BUNDLE_NAME;
import static com.code.aon.ui.manager.controller.IManagerConstants.LOGGER_SCRIPT;
import static com.code.aon.ui.manager.controller.IManagerConstants.MANAGER_CONTROLLER_NAME;
import static com.code.aon.ui.manager.controller.IManagerConstants.NEED_MAIL_ACCOUNT;
import static com.code.aon.ui.manager.controller.IManagerConstants.WRONG_MAIL_ACCOUNT;

import java.text.SimpleDateFormat;
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
import com.code.aon.ui.manager.controller.DomainController;
import com.code.aon.ui.manager.controller.IManagerConstants;
import com.code.aon.ui.manager.controller.ManagerController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.EmailSender;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.WebmailUtil;

public class ManagerLogger {

	private final static Logger LOGGER = LoggerFactory.getLogger(ManagerLogger.class);
	
	private final static String APPLICATION_TYPE = "application";
	private final static String USER_TYPE = "user";
	private final static String APPLICATION_USER_TYPE = "appUser";
	private final static String CAPACITY_TYPE = "capacity";
	private final static String DOMAIN_TYPE = "domain";
	
	private final static String ADD_ACTION = "add";
	private final static String DELETE_ACTION = "del";
	
	private final static String MULTIUSER_CAPACITY = "Multiuser";
	private final static String MULTIDOMAIN_CAPACITY = "MultiDomain";
	private final static String DOCUMENTAL_CAPACITY = "Documental";
	private final static String STORAGE_CAPACITY = "Storage";
	
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
				String fullContent = subject + SystemUtils.LINE_SEPARATOR + content;
				sender.sendMessage(to, subject, fullContent);
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
		executeLogScript(DOMAIN_TYPE, domain.getCommonName(), ADD_ACTION, domain);
	}

	public void domainRemoved( Domain domain ) {
		String subject = "REMOVED: Domain " + domain.getCommonName();
		sendEmail( subject, getContent() );
		executeLogScript(DOMAIN_TYPE, domain.getCommonName(), DELETE_ACTION, domain);
	}	

	public void domainApplicationAddded( DomainApplication da ) {
		String subject = "NEW: Application " + da.getCommonName() + " in Domain " + da.getDomain();
		sendEmail( subject, getContent() );
		executeLogScript(APPLICATION_TYPE, da.getCommonName(), ADD_ACTION);
	}

	public void domainApplicationRemoved( DomainApplication da ) {
		String subject = "REMOVED: Application " + da.getCommonName() + " in Domain " + da.getDomain();
		sendEmail( subject, getContent() );
		executeLogScript(APPLICATION_TYPE, da.getCommonName(), DELETE_ACTION);
	}	
	
	public void domainUserAddded( DomainUser user ) {
		String subject = "NEW: User " + user.getName() + " in Domain " + user.getDomain();
		sendEmail( subject, getContent() );
		executeLogScript(USER_TYPE, user.getName(), ADD_ACTION);
	}

	public void domainUserdRemoved( DomainUser user ) {
		String subject = "REMOVED: User " + user.getName() + " in Domain " + user.getDomain();
		sendEmail( subject, getContent() );
		executeLogScript(USER_TYPE, user.getName(), DELETE_ACTION);
	}

	public void domainApplicationUserAddded( DomainApplicationUser user ) {
		String subject = "NEW: User " + user.getCommonName() + " in Application " + user.getAppplication() + " in Domain " + user.getDomain();
		sendEmail( subject, getContent() );
		String value = user.getAppplication() + "/" + user.getCommonName();
		executeLogScript(APPLICATION_USER_TYPE, value, ADD_ACTION);
	}

	public void domainApplicationUserRemoved( DomainApplicationUser user ) {
		String subject = "REMOVED: User " + user.getCommonName() + " in Application " + user.getAppplication() + " in Domain " + user.getDomain();
		sendEmail( subject, getContent() );
		String value = user.getAppplication() + "/" + user.getCommonName();
		executeLogScript(APPLICATION_USER_TYPE, value, DELETE_ACTION);
	}	
	
	public void multiUser( Domain domain ) {
		String subject = "Domain " + domain.getCommonName() + " Multiuser: " + domain.getUserManagement();
		sendEmail( subject, getContent() );
		executeLogScript(CAPACITY_TYPE, MULTIUSER_CAPACITY, domain.getUserManagement() ? ADD_ACTION : DELETE_ACTION);
	}
	
	public void multiDomain( Domain domain ) {
		String subject = "Domain " + domain.getCommonName() + " MultiDomain: " + domain.getDomainManagement();
		sendEmail( subject, getContent() );		
		executeLogScript(CAPACITY_TYPE, MULTIDOMAIN_CAPACITY, domain.getDomainManagement() ? ADD_ACTION : DELETE_ACTION);
	}

	public void documental( Domain domain ) {
		String subject = "Domain " + domain.getCommonName() + " Documental: " + domain.isDocumentManagement();
		sendEmail( subject, getContent() );		
		executeLogScript(CAPACITY_TYPE, DOCUMENTAL_CAPACITY, domain.isDocumentManagement() ? ADD_ACTION : DELETE_ACTION);		
	}

	public void maxTotalDocumentSizeChanged( Domain domain, Integer previous, Integer current ) {
		String subject = "Domain " + domain.getCommonName() + " Documental Storage Changed from " + previous + "MB to " + current + "MB";
		sendEmail( subject, getContent() );		
		executeLogScript(CAPACITY_TYPE, STORAGE_CAPACITY, current.toString());
	}
	
	private void executeLogScript( String type, String name, String action ) {
		DomainController dc = (DomainController) AonUtil.getRegisteredBean(IManagerConstants.DOMAIN_CONTROLLER_NAME);
		executeLogScript(type, name, action, dc.getDomain());
	}
	
	private void executeLogScript( String type, String name, String action, Domain domain) {
		ManagerController manager = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
		String script = manager.getProperties().getProperty(LOGGER_SCRIPT);
		String date = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT).format(new Date());
		String companyId = "" + (domain.getDataBaseId() != null ? domain.getDataBaseId() : -1 );
		String[] commandLine = new String[] {script, type, name, action, date,
				loggedUser.getShortName(), loggedUser.getDomain(), companyId};
		manager.execute( commandLine );		
	}
	
}