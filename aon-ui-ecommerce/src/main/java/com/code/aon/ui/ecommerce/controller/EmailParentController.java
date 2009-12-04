package com.code.aon.ui.ecommerce.controller;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import javax.faces.event.AbortProcessingException;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.webmail.AonFile;
import com.code.aon.webmail.EmailSender;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.WebmailUtil;

public class EmailParentController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailParentController.class.getName());
	
	public void email(String subject, String from, String to, String content) {
//		String domain = "localhost";
//		String login = "admin";
//		MailAccount mailAccount;
//		try {
//			mailAccount = WebmailUtil.getDefaultAccount(domain,login);
//		} catch (ManagerBeanException e) {
//			AonUtil.addErrorMessage( "El usuario " + login + " no tiene definida ninguna cuenta de correo" );
//			throw new AbortProcessingException( e.getMessage(), e);
//		}
//		try {
//			AonServer server = new AonServer(mailAccount);
//			server.connect();
//			String username = from;  
//			AonMessage aonMessage = server.createAonMessage(from, username);
//			InternetAddress iafrom = new InternetAddress(from, username);
//			aonMessage.setSender(iafrom);
//			aonMessage.setRecipientsTo(to);
//			aonMessage.setSubject(subject);
//			aonMessage.setContent(content.toString());
//			server.sendMessage(aonMessage);
//			server.disconnect();
//		} catch (Throwable e) {
//			LOGGER.error(e.getMessage(), e);
//			AonUtil.addErrorMessage( e.getMessage() );
//			throw new AbortProcessingException( e.getMessage(), e);
//		}
		
		
		
		EmailSender es;
		try {
			es = getEmailSender(from);
			es.connect();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendEmail(from, to, subject, content);
		
		
		
	}
	
	
	
	
	
	
	
	
	
	private EmailSender sender;
	private final String BUNDLE_KEY = "ecommerceBundle";
	private final String ECOMMERCE_SEND_EMAIL_ERROR = "ecommerce_send_email_error";
//	private final String ECOMMERCE_WITHOUT_EMAIL = "ecommerce_without_email";
	private final String ECOMMERCE_EMAIL_BODY_HEADER = "ecommerce_email_body_header";
	private final String ECOMMERCE_EMAIL_BODY = "ecommerce_email_body";
	private final String ECOMMERCE_EMAIL_BODY_FOOTER = "ecommerce_email_body_footer";
	
	
	public EmailSender getEmailSender(String username) throws UnsupportedEncodingException {
		if ( this.sender == null ) {
//			AuthPrincipal user = UserUtils.getInstance().getPrincipal();
//			MailAccount mailAccount = getDefaultMailAccount( user );
			String domain = "localhost";
			String login = "admin";
			MailAccount mailAccount;
			try {
				mailAccount = WebmailUtil.getDefaultAccount(domain,login);
			} catch (ManagerBeanException e) {
				AonUtil.addErrorMessage( "El usuario " + login + " no tiene definida ninguna cuenta de correo" );
				throw new AbortProcessingException( e.getMessage(), e);
			}
			
			
			
			if ( mailAccount != null ) {
//				Address from = new InternetAddress( mailAccount.getEmail(), getCompany().getName() );
				Address from = new InternetAddress(username, username);
				this.sender = new EmailSender( from, mailAccount );							
			} else {
				String text = AonUtil.getMessage(WebMailConstants.BUNDLE_NAME, WebMailConstants.NOT_MAIL_ACCOUNT); 
//				String message = MessageFormat.format(text, user.getShortName() );
				String message = MessageFormat.format(text, login );
				throw new AbortProcessingException( message );
			}
		}
		return this.sender;
	}
	
	public void sendEmail( String username, String to, String subject, String content) {
		
		try {
//			RegistryMedia email = invoice.getRegistry().getEmail();
//			if ( email == null) {
//				String text = AonUtil.getMessage(BUNDLE_KEY, ECOMMERCE_WITHOUT_EMAIL);
//				String message = MessageFormat.format(text, invoice.getReferenceCode(), invoice.getRegistryName() );				
//				AonUtil.addErrorMessage(message);				
//			} else {
//				Address[] recipients = getEmailAddresses(invoice, invoice.getRegistryName() );
				Address[] recipients = new Address[1];
				recipients[0] = new InternetAddress(to, to);
//				String subject = getEmailSubject(invoice);
				String bodyContent = getEmailBody(content);
//				AonFile file = getInvoiceFile(invoice);
				AonFile file = null;
//				AonFile xml = getInvoiceXml(invoice);
				AonFile xml = null;
//				if ( si != null ) {
//					getEmailSender(username).sendMessage(recipients, subject, content, MimeType.MIME_HTML, si, file, xml );
//				} else {
					getEmailSender(username).sendMessage(recipients, subject, content, MimeType.MIME_HTML );
//				}
//				file.getFile().delete();
//				xml.getFile().delete();
//			}
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			String text = AonUtil.getMessage(BUNDLE_KEY, ECOMMERCE_SEND_EMAIL_ERROR);
			String message = MessageFormat.format(text, username );				
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException( message );
		}
	}
	
//	public String getEmailSubject( Invoice invoice ) {
//		String key = invoice.isSigned() ? FINANCE_EINVOICE_EMAIL_SUBJECT : FINANCE_INVOICE_EMAIL_SUBJECT; 
//		String message = AonUtil.getMessage(BUNDLE_KEY, key);
//		return MessageFormat.format(message, invoice.getReferenceCode() );
//	}
	
	public String getEmailBody( String content ) throws UnsupportedEncodingException {
		StringBuffer body = new StringBuffer();
		body.append( "<html><head>" );
		body.append( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" );
		body.append( "</head><body>" );
		body.append(AonUtil.getMessage(BUNDLE_KEY, ECOMMERCE_EMAIL_BODY_HEADER) );
		String bodyPart = AonUtil.getMessage(BUNDLE_KEY, ECOMMERCE_EMAIL_BODY); 
//		body.append( MessageFormat.format(bodyPart, invoice.getReferenceCode(), invoice.getIssueDate()) );
		body.append( MessageFormat.format(bodyPart, content) );
		body.append(AonUtil.getMessage(BUNDLE_KEY, ECOMMERCE_EMAIL_BODY_FOOTER) );
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		Company company = companyController.obtainCompany();
		body.append( company.getName() ).append( "<br/>" );
		RegistryMedia phone = companyController.getPhone();
		if ( phone != null ) {
			String phoneLabel = AonUtil.getMessage("registryBundle", "registry_phone");
			body.append( StringEscapeUtils.escapeHtml(phoneLabel));
			body.append( ": " ).append( phone.getValue()).append( "<br/>" );
		}
		RegistryMedia fax = companyController.getFax();
		if ( fax != null ) {
			String faxLabel = AonUtil.getMessage("registryBundle", "registry_fax");
			body.append(faxLabel).append( ": " ).append( fax.getValue() ).append( "<br/>" );
		}
		RegistryMedia web = companyController.getWeb();
		if ( web != null ) {
			body.append( "<a href=\"" ).append( web.getValue() ).append( "\">").append( web.getValue() ).append("</a>" );
		}
		body.append( "</body></html>" );
		return body.toString();
	}
	
	
	
	
	
	
}