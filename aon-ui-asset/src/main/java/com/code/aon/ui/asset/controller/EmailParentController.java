package com.code.aon.ui.asset.controller;

import static com.code.aon.ui.common.ICommonMessages.COMPANY_EMAIL_BODY_FOOTER;
import static com.code.aon.ui.common.ICommonMessages.COMPANY_EMAIL_BODY_HEADER;
import static com.code.aon.ui.common.ICommonMessages.FAX;
import static com.code.aon.ui.common.ICommonMessages.NOT_MAIL_ACCOUNT;
import static com.code.aon.ui.common.ICommonMessages.PHONE;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import javax.faces.event.AbortProcessingException;
import jakarta.mail.Address;
import jakarta.mail.internet.InternetAddress;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.EmailSender;
import com.code.aon.webmail.IMailAccount;
import com.code.aon.webmail.db.MailAccount;
import com.code.aon.webmail.enumeration.ConnectionSecurity;

public class EmailParentController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailParentController.class.getName());
	private EmailSender sender;
	private final String SEND_EMAIL_ERROR = "error al enviar el email";
	
	public void email(String subject, String from, String to, String content) {
		EmailSender es;
		try {
			es = getEmailSender(from);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendEmail(from, to, subject, content);
	}
	
	public EmailSender getEmailSender(String username) throws UnsupportedEncodingException {
		if ( this.sender == null ) {
//			String domain = "localhost";
			String login = "admin";
			IMailAccount mailAccount = getDefaultMailAccount();
			
			if ( mailAccount != null ) {
				Address from = new InternetAddress(username, username);
				this.sender = new EmailSender( from, mailAccount );							
			} else {
				String text = AonUtil.getMessage(NOT_MAIL_ACCOUNT); 
				String message = MessageFormat.format(text, login );
				throw new AbortProcessingException( message );
			}
		}
		return this.sender;
	}
	
	private IMailAccount getDefaultMailAccount() {
		MailAccount	mailAccount=new MailAccount();
		mailAccount.setIncomingHost("mail.esferalia.com");
		mailAccount.setEmail("eagirrezabal@esferalia.com");
		mailAccount.setOutgoingHost("mail.esferalia.com");
		mailAccount.setOutgoingPort(25);
		mailAccount.setOutgoingSecurity(ConnectionSecurity.NONE);
		mailAccount.setOutgoingVerification(true);
		mailAccount.setProtocol("imap");
		mailAccount.setMailUsername("test@esferalia.com");
		mailAccount.setPasswordString("test");
		return mailAccount;		
	}

	public void sendEmail( String username, String to, String subject, String content) {
		
		try {
			Address[] recipients = new Address[1];
			recipients[0] = new InternetAddress(to, to);
			String bodyContent = getEmailBody(content);
			getEmailSender(username).sendMessage(recipients, subject, bodyContent, MimeType.MIME_HTML);
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			AonUtil.addErrorMessage(SEND_EMAIL_ERROR);
			throw new AbortProcessingException( SEND_EMAIL_ERROR );
		}
	}
	
	public String getEmailBody( String text ) throws UnsupportedEncodingException {
		StringBuffer body = new StringBuffer();
		body.append( "<html><head>" );
		body.append( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" );
		body.append( "</head><body>" );
		
		body.append(AonUtil.getMessage(COMPANY_EMAIL_BODY_HEADER) );
		body.append( text );
		body.append(AonUtil.getMessage(COMPANY_EMAIL_BODY_FOOTER) );		

		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		body.append( getCompany().getName() ).append( "<br/>" );
		RegistryMedia phone = companyController.getPhone();
		if ( phone != null ) {
			String phoneLabel = AonUtil.getMessage(PHONE);
			body.append( StringEscapeUtils.escapeHtml(phoneLabel));
			body.append( ": " ).append( phone.getValue()).append( "<br/>" );
		}
		RegistryMedia fax = companyController.getFax();
		if ( fax != null ) {
			String faxLabel = AonUtil.getMessage(FAX);
			body.append(faxLabel).append( ": " ).append( fax.getValue() ).append( "<br/>" );
		}
		RegistryMedia web = companyController.getWeb();
		if ( web != null ) {
			body.append( "<a href=\"" ).append( web.getValue() ).append( "\">").append( web.getValue() ).append("</a>" );
		}
		return body.toString();
	}
	
	public Company getCompany() {
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		return companyController.obtainCompany();
	}
	
	
	
}