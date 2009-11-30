package com.code.aon.ui.company.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IHeaderObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.SingleCollectionProvider;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.report.ReportException;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.AonFile;
import com.code.aon.webmail.EmailSender;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.WebmailUtil;

public class CompanyEmailUtil implements ICompanyConstants {

	private final static String PDF_EXTENSION = "." + MimeType.MIME_PDF.getExtension();
	
	private EmailSender sender;
	
	private Company company;

	private MailAccount getDefaultMailAccount( AuthPrincipal user ) {		
		String domain = user.getDomain();
		String login = user.getShortName();
		MailAccount mailAccount;
		try {
			mailAccount = WebmailUtil.getDefaultAccount(domain,login);
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException( e.getMessage(), e);
		}
		return mailAccount;
	}
	
	public Company getCompany() {
		if (company == null) {
			CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			this.company = companyController.obtainCompany();
		}
		return company;
	}
	
	public EmailSender getEmailSender() throws UnsupportedEncodingException {
		if ( this.sender == null ) {
			AuthPrincipal user = UserUtils.getInstance().getPrincipal();
			MailAccount mailAccount = getDefaultMailAccount( user );
			if ( mailAccount != null ) {
				Address from = new InternetAddress( mailAccount.getEmail(), getCompany().getName() );
				this.sender = new EmailSender( from, mailAccount );							
			} else {
				String text = AonUtil.getMessage(WebMailConstants.BUNDLE_NAME, WebMailConstants.NOT_MAIL_ACCOUNT); 
				String message = MessageFormat.format(text, user.getShortName() );
				throw new AbortProcessingException( message );
			}
		}
		return this.sender;
	}
	
	public String[] getEmails( Registry registry ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteria = new Criteria();
		String type = bean.getFieldName( IRegistryAlias.REGISTRY_MEDIA_MEDIA_TYPE );
		criteria.addEqualExpression( type, MediaType.EMAIL );
		String registryField = bean.getFieldName( IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID );
		criteria.addEqualExpression( registryField, registry.getId() );		
		String administrative = bean.getFieldName( IRegistryAlias.REGISTRY_MEDIA_ADMINISTRATIVE );
		criteria.addEqualExpression( administrative, Boolean.TRUE );
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			String[] emails = new String[list.size()];
			for( int i = 0; i < list.size(); i++ ) {
				RegistryMedia rm = (RegistryMedia) list.get(i);
				emails[i] = rm.getValue();
			}
			return emails;
		}
		return null;
	}	

	protected void initMessageController( MessageController messageController, String[] emails, String body  ) throws ManagerBeanException {
		if (! ArrayUtils.isEmpty(emails) ) {
			messageController.setRecipientsTo( emails[0] );
			if ( emails.length > 1 ) { 
				String recipientsCc = StringUtils.join( emails, ',', 1, emails.length );
				messageController.setRecipientsCc( recipientsCc );
			}
		}
		messageController.setContent( getEmailContent(body) );
	}

	public String getEmailContent( String text ) {
		StringBuffer body = new StringBuffer();
		body.append( "<html><head>" );
		body.append( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" );
		body.append( "</head><body>" );
		
		body.append(AonUtil.getMessage(BUNDLE_NAME, COMPANY_EMAIL_BODY_HEADER) );
		body.append( text );
		body.append(AonUtil.getMessage(BUNDLE_NAME, COMPANY_EMAIL_BODY_FOOTER) );		

		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		body.append( getCompany().getName() ).append( "<br/>" );
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
		return body.toString();
	}
	
	private String getFileName( ITransferObject to, String report ) {
		String name = report;
		if ( IHeaderObject.class.isAssignableFrom(to.getClass()) ) {
			name = report + "_" + ((IHeaderObject)to).getReferenceCode().replace("/", "-");
		}
		return name + PDF_EXTENSION;
	}
	
	public AonFile getReport( ITransferObject to, String report ) throws IOException, ReportException {
		AonFile aonFile = new AonFile();
		File file = File.createTempFile( report, PDF_EXTENSION );
		aonFile.setFile( file );
		aonFile.setFileName( getFileName(to, report) );

		ReportManager reportManager = new ReportManager();
		reportManager.setCollectionProvider( new SingleCollectionProvider(to) );
		OutputStream out = new BufferedOutputStream( new FileOutputStream(file) );
		reportManager.execute( out, report );
		out.close();
		return aonFile;
	}		
	
}
