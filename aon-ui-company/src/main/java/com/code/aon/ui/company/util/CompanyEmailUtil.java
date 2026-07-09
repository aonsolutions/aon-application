package com.code.aon.ui.company.util;

import static com.code.aon.ui.common.ICommonMessages.COMPANY_EMAIL_BODY_FOOTER;
import static com.code.aon.ui.common.ICommonMessages.COMPANY_EMAIL_BODY_HEADER;
import static com.code.aon.ui.common.ICommonMessages.PHONE;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_CONFIG;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.event.AbortProcessingException;
import jakarta.mail.Address;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeUtility;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IHeaderObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.SingleCollectionProvider;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.company.Company;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.report.ReportException;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.EmailSender;
import com.code.aon.webmail.IMailAccount;
import com.esferalia.aon.entity.IEntityAlias;

public class CompanyEmailUtil implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static String PDF_EXTENSION = "." + MimeType.MIME_PDF.getExtension();
	
	private EmailSender sender;
	
	private Company company;
	
	public Company getCompany() {
		if (company == null) {
			CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			this.company = companyController.obtainCompany();
		}
		return company;
	}
	
	public EmailSender getEmailSender() throws UnsupportedEncodingException {
		if ( this.sender == null ) {
			MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
			IMailAccount mailAccount = mailConfig.getDefaultMailAccount(false);			
			if ( mailAccount != null ) {
				changeMailAccount(mailAccount);
			} else {
				AuthPrincipal user = AonUtil.getAuthPrincipal();
				String text = AonUtil.getMessage(ICommonMessages.NOT_MAIL_ACCOUNT); 
				String message = MessageFormat.format(text, user.getShortName() );
				throw new AbortProcessingException( message );
			}
		}
		return this.sender;
	}
	
    private String getPersonal( IMailAccount account ) throws UnsupportedEncodingException {
    	String personal = null;
    	if ( ! StringUtils.isEmpty(account.getDisplayName()) ) {
    		personal = account.getDisplayName();
    	} else {
        	personal = getCompany().getName();    	    		
    	}
    	return MimeUtility.encodeText(personal);
    }	
	
	public void changeMailAccount(IMailAccount mailAccount) throws UnsupportedEncodingException {
		Address from = new InternetAddress( mailAccount.getEmail(), getPersonal(mailAccount) );
		this.sender = new EmailSender( from, mailAccount );							
	}
	
	public static String[] getAdministrativeEmails( Registry registry ) throws ManagerBeanException {
		return getEmails(registry, true, false);
	}

	public static String[] getCommercialEmails( Registry registry ) throws ManagerBeanException {
		return getEmails(registry, false, true);
	}

	public static String[] getEmails( Registry registry ) throws ManagerBeanException {
		return getEmails(registry, false, false);
	}
	
	private static String[] getEmails( Registry registry, boolean administrative, boolean commercial ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteria = new Criteria();
		String type = bean.getFieldName( IEntityAlias.REGISTRY_MEDIA_MEDIA_TYPE );
		criteria.addEqualExpression( type, MediaType.EMAIL );
		String registryField = bean.getFieldName( IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID );
		criteria.addEqualExpression( registryField, registry.getId() );		
		if ( administrative ) {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_MEDIA_ADMINISTRATIVE), Boolean.TRUE);			
		}
		if ( commercial ) {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_MEDIA_COMMERCIAL), Boolean.TRUE);			
		}
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			Set<String> emails = new TreeSet<String>();
			for( int i = 0; i < list.size(); i++ ) {
				RegistryMedia rm = (RegistryMedia) list.get(i);
				emails.add( rm.getValue() );
			}
			return emails.toArray(new String[emails.size()]);
		}
		return null;
	}	
	
	public static void initMessageController( MessageController messageController, String[] emails  )  {
		if (! ArrayUtils.isEmpty(emails) ) {
			messageController.setRecipientsTo( emails[0] );
			if ( emails.length > 1 ) { 
				String recipientsCc = StringUtils.join( emails, ',', 1, emails.length );
				messageController.setRecipientsCc( recipientsCc );
			}
		}
	}

	protected void initMessageController( MessageController messageController, String[] emails, String body  )  {
		initMessageController(messageController, emails);
		messageController.updateMessageBody( getEmailContent(body) );
	}

	public String getEmailContent( String text ) {
		return getEmailContent( text, null );
	}
	
	public String getEmailContent( String text, String bodyHeader ) {
		StringBuffer body = new StringBuffer();
		body.append( "<html><head>" );
		body.append( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" );
		body.append( "</head><body>" );
		
		body.append(StringUtils.isEmpty(bodyHeader)?AonUtil.getMessage(COMPANY_EMAIL_BODY_HEADER):bodyHeader );
		body.append( text ).append( "<br/>" );
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
			String faxLabel = AonUtil.getMessage(ICommonMessages.FAX);
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
		aonFile.setMimeType(MimeType.MIME_PDF);

		ReportManager reportManager = new ReportManager();
		reportManager.setCollectionProvider( new SingleCollectionProvider(to) );
		OutputStream out = new BufferedOutputStream( new FileOutputStream(file) );
		reportManager.execute( out, report );
		out.close();
		return aonFile;
	}		
	
	public void setNumberOfMessagesPerTransport(int numberOfMessagesPerTransport) {
		if ( sender != null ) {
			sender.setNumberOfMessagesPerTransport(numberOfMessagesPerTransport);
		}
	}
	
	
	public void close() {
		if ( sender != null ) {
			sender.close();
			sender.setNumberOfMessagesPerTransport(1);
		}
	}
	
}
