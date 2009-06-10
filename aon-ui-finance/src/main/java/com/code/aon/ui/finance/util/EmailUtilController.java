package com.code.aon.ui.finance.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.finance.Invoice;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.report.ReportException;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.controller.InvoicePrintController;
import com.code.aon.ui.finance.controller.SaleInvoiceController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.AonFile;
import com.code.aon.webmail.EmailSender;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.WebmailUtil;

public class EmailUtilController implements ICollectionProvider, IFinanceMessages, IFinanceConstants {

	private static final String SALE_INVOICE_REPORT = "saleInvoice";
	
	private static final String SALE_EINVOICE_REPORT = "saleEInvoice";

	private static final Logger LOGGER = Logger.getLogger(InvoicePrintController.class.getName());
	
	private EmailSender sender;
	
	private Company company;
	
	private Invoice currentInvoice;
	
	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		try {
			return getCollection(false);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")	
	public Collection getCollection(boolean forceRefresh)
			throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		List<ITransferObject> l = new LinkedList<ITransferObject>();
		l.add( bean.get(getCurrentInvoice().getId()) );
		return l;
	}
	
	public Invoice getCurrentInvoice() {
		return currentInvoice;
	}

	public void setCurrentInvoice(Invoice currentInvoice) {
		this.currentInvoice = currentInvoice;
	}

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

	public String getEmailSubject( Invoice invoice, boolean eInvoice ) {
		String key = eInvoice ? FINANCE_EINVOICE_EMAIL_SUBJECT : FINANCE_INVOICE_EMAIL_SUBJECT; 
		String message = AonUtil.getMessage(BUNDLE_KEY, key);
		return MessageFormat.format(message, invoice.getReferenceCode() );
	}

	public String getEmailBody( Invoice invoice ) {
		StringBuffer body = new StringBuffer();
		body.append( "<html><body>" );
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
			body.append(phoneLabel).append( ": " ).append( phone.getValue() ).append( "<br/>" );
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
	
	public EmailSender getEmailSender() throws UnsupportedEncodingException {
		if ( this.sender == null ) {
			AuthPrincipal user = UserUtils.getInstance().getPrincipal();
			MailAccount mailAccount = getDefaultMailAccount( user );
			if ( mailAccount != null ) {
				Address from = new InternetAddress( mailAccount.getEmail(), getCompany().getName() );
				this.sender = new EmailSender( from, mailAccount );							
			} else {
				String text = AonUtil.getMessage(BUNDLE_KEY, FINANCE_NOT_MAIL_ACCOUNT); 
				String message = MessageFormat.format(text, user.getShortName() );
				throw new AbortProcessingException( message );
			}
		}
		return this.sender;
	}
	
	public AonFile getInvoiceFile( String fileName, boolean eInvoice ) throws IOException, ReportException {
		ReportManager report = new ReportManager();
		report.setCollectionProvider(this);
		String reporkey = eInvoice ? SALE_EINVOICE_REPORT : SALE_INVOICE_REPORT;
		File file = File.createTempFile( reporkey, ".pdf" );
		report.execute( file, reporkey);
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);
		aonFile.setFileName( fileName );
		return aonFile;
	}
	
	public AonFile getDigitalCertificate() throws ManagerBeanException, IOException {
		SaleInvoiceController invoiceController = (SaleInvoiceController) AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
		RegistryAttachment ra = invoiceController.getDigitalCertificate();
		File file = File.createTempFile( ra.getDescription(), ".cer" );
		FileUtils.writeByteArrayToFile(file, ra.getData());
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);
		aonFile.setFileName( ra.getDescription() );
		return aonFile;
	}	

	private AonFile getInvoiceXml() throws IOException, SAXException {
		SaleInvoiceController invoiceController = (SaleInvoiceController) AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);		
		File file = File.createTempFile( "facturae", ".xml" );
		Writer writer = new FileWriter( file );
		invoiceController.writeInvoiceXml( writer, currentInvoice.getId());
		writer.close();
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);
		aonFile.setFileName( "facturae.xml" );
		return aonFile;
	}
	
	public void sendInvoice( Invoice invoice, boolean eInvoice ) {
		try {
			RegistryMedia email = invoice.getRegistry().getEmail();
			if ( email == null) {
				String text = AonUtil.getMessage(BUNDLE_KEY, FINANCE_INVOICE_WITHOUT_EMAIL);
				String message = MessageFormat.format(text, invoice.getReferenceCode(), invoice.getRegistryName() );				
				AonUtil.addErrorMessage(message);				
			} else {
				Address to = new InternetAddress( email.getValue(), invoice.getRegistryName() );
				String subject = getEmailSubject(invoice, eInvoice);
				String content = getEmailBody(invoice);
				String name = "invoice_" + invoice.getSeries() + "-" + invoice.getNumber() + ".pdf";
				setCurrentInvoice(invoice);
				AonFile file = getInvoiceFile(name, eInvoice);
				if ( eInvoice ) {
					AonFile dc = getDigitalCertificate();
					AonFile xml = getInvoiceXml();
					getEmailSender().sendMessage(to, subject, content, MimeType.MIME_HTML, file, dc, xml );
					dc.getFile().delete();
					xml.getFile().delete();
				} else {
					getEmailSender().sendMessage(to, subject, content, MimeType.MIME_HTML, file);
				}
				file.getFile().delete();
			}
		} catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
			String text = AonUtil.getMessage(BUNDLE_KEY, FINANCE_INVOICE_SEND_EMAIL_ERROR);
			String message = MessageFormat.format(text, invoice.getReferenceCode(), invoice.getRegistryName() );				
			AonUtil.addErrorMessage(message);
		}
	}
	
}
