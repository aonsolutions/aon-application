package com.code.aon.ui.finance.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.xml.sax.SAXException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.facturae.FacturaeWriter;
import com.code.aon.finance.Invoice;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.report.ReportException;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.webmail.AonFile;
import com.code.aon.webmail.EmailSender;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.SecurityInfo;
import com.code.aon.webmail.WebmailUtil;

public class EmailUtilController implements IFinanceMessages, IFinanceConstants {

	private static final Logger LOGGER = Logger.getLogger(EmailUtilController.class.getName());
	
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

	public String[] getEmails( Invoice invoice ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteria = new Criteria();
		String type = bean.getFieldName( IRegistryAlias.REGISTRY_MEDIA_MEDIA_TYPE );
		criteria.addEqualExpression( type, MediaType.EMAIL );
		String registry = bean.getFieldName( IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID );
		criteria.addEqualExpression( registry, invoice.getRegistry().getId() );		
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

	private Address[] getEmailAddresses( Invoice invoice, String name ) throws ManagerBeanException, UnsupportedEncodingException, AddressException {
		Address[] addresses = null;
		String[] emails = getEmails(invoice);
		if (! ArrayUtils.isEmpty(emails) ) {
			addresses = new Address[emails.length];
			for( int i = 0; i < emails.length; i++ ) {
				if ( i == 0 ) {
					addresses[i] = new InternetAddress( emails[i], name );
				} else {
					addresses[i] = new InternetAddress( emails[i] );	
				}
			} 
		}
		return addresses;
	}		
	public String getEmailSubject( Invoice invoice ) {
		String key = invoice.isSigned() ? FINANCE_EINVOICE_EMAIL_SUBJECT : FINANCE_INVOICE_EMAIL_SUBJECT; 
		String message = AonUtil.getMessage(BUNDLE_KEY, key);
		return MessageFormat.format(message, invoice.getReferenceCode() );
	}

	public String getEmailBody( Invoice invoice ) throws UnsupportedEncodingException {
		StringBuffer body = new StringBuffer();
		body.append( "<html><head>" );
		body.append( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" );
		body.append( "</head><body>" );
		body.append(AonUtil.getMessage(BUNDLE_KEY, FINANCE_INVOICE_EMAIL_BODY_HEADER) );
		String bodyPart = AonUtil.getMessage(BUNDLE_KEY, FINANCE_INVOICE_EMAIL_BODY); 
		body.append( MessageFormat.format(bodyPart, invoice.getReferenceCode(), invoice.getIssueDate()) );
		body.append(AonUtil.getMessage(BUNDLE_KEY, FINANCE_INVOICE_EMAIL_BODY_FOOTER) );
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
		return body.toString();
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

	public AonFile getInvoiceFile( Invoice invoice ) throws IOException, ReportException, ManagerBeanException {
		InvoiceController controller = (InvoiceController) AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
		byte[] data = controller.getInvoiceData(invoice);
		return getInvoiceFile(data, invoice);
	}
	
	public AonFile getInvoiceFile( byte[] data, Invoice invoice ) throws IOException, ReportException, ManagerBeanException {
		String fileName = "invoice_" + invoice.getSeries() + "-" + invoice.getNumber();
		File file = File.createTempFile( fileName, ".pdf" );
		FileUtils.writeByteArrayToFile(file, data);
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);	
		aonFile.setFileName( fileName + ".pdf" );
		return aonFile;
	}

	public AonFile getInvoiceXml( Invoice invoice ) throws IOException, SAXException {
		File file = File.createTempFile( "facturae", ".xsig" );
		FacturaeWriter fw = new FacturaeWriter( getCompany() );
		String filePath = file.getAbsolutePath();
		String fileName = FilenameUtils.getFullPath(filePath) + FilenameUtils.getBaseName(filePath);
		fw.serialize(invoice, fileName);
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);
		aonFile.setFileName( "facturae.xml" );
		return aonFile;
	}
	
	public void sendInvoice( Invoice invoice, SecurityInfo si ) {
		try {
			RegistryMedia email = invoice.getRegistry().getEmail();
			if ( email == null) {
				String text = AonUtil.getMessage(BUNDLE_KEY, FINANCE_INVOICE_WITHOUT_EMAIL);
				String message = MessageFormat.format(text, invoice.getReferenceCode(), invoice.getRegistryName() );				
				AonUtil.addErrorMessage(message);				
			} else {
				Address[] recipients = getEmailAddresses(invoice, invoice.getRegistryName() );
				String subject = getEmailSubject(invoice);
				String content = getEmailBody(invoice);
				AonFile file = getInvoiceFile(invoice);
				AonFile xml = getInvoiceXml(invoice);
				if ( si != null ) {
					getEmailSender().sendMessage(recipients, subject, content, MimeType.MIME_HTML, si, file, xml );
				} else {
					getEmailSender().sendMessage(recipients, subject, content, MimeType.MIME_HTML, file, xml );
				}
				file.getFile().delete();
				xml.getFile().delete();
			}
		} catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
			String text = AonUtil.getMessage(BUNDLE_KEY, FINANCE_INVOICE_SEND_EMAIL_ERROR);
			String message = MessageFormat.format(text, invoice.getReferenceCode(), invoice.getRegistryName() );				
			AonUtil.addErrorMessage(message);
		}
	}
	
}
