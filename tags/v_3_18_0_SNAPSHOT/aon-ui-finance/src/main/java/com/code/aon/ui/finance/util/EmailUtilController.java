package com.code.aon.ui.finance.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
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
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.xml.sax.SAXException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.finance.Invoice;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.report.ReportException;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.controller.InvoicePrintController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.AonFile;
import com.code.aon.webmail.EmailSender;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.WebmailUtil;

public class EmailUtilController implements ICollectionProvider, IFinanceMessages, IFinanceConstants {

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

	public String getEmailSubject( Invoice invoice ) {
		String message = AonUtil.getMessage(BUNDLE_KEY, FINANCE_INVOICE_EMAIL_SUBJECT);
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
	
	public AonFile getInvoiceFile( String reporkey, String fileName ) throws IOException, ReportException {
		ReportManager report = new ReportManager();
		report.setCollectionProvider(this);
		File file = File.createTempFile( reporkey, ".pdf" );
		report.execute( file, reporkey);
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);
		aonFile.setFileName( fileName );
		return aonFile;
	}
	
	public boolean hasDigitalCertificate() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		String type = bean.getFieldName( IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE );
		criteria.addEqualExpression( type, RegistryAttachmentType.DIGITAL_CERTIFICATE );
		String registry = bean.getFieldName( IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID );
		criteria.addEqualExpression( registry, company.getId() );
		int count = bean.getCount(criteria);
		return count > 0;
	}

	public AonFile getDigitalCertificate() throws ManagerBeanException, IOException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		String type = bean.getFieldName( IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE );
		criteria.addEqualExpression( type, RegistryAttachmentType.DIGITAL_CERTIFICATE );
		String registry = bean.getFieldName( IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID );
		criteria.addEqualExpression( registry, company.getId() );		
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			RegistryAttachment ra = (RegistryAttachment) list.get(0);
			File file = File.createTempFile( ra.getDescription(), ".cer" );
			FileUtils.writeByteArrayToFile(file, ra.getData());
			AonFile aonFile = new AonFile();
			aonFile.setFile(file);
			aonFile.setFileName( ra.getDescription() );
			return aonFile;
		}
		return null;
	}
	
    private XMLWriter createWriter( File file ) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();   
        format.setEncoding( "UTF-8" );
        BufferedWriter out = new BufferedWriter( new FileWriter(file) );
        XMLWriter writer = new XMLWriter( out, format );
        writer.setMaximumAllowedCharacter(0x7F);
        return writer;
    }
	
	private void writeInvoiceXml( File file, String entityName, Serializable id ) throws IOException, SAXException {
		String factoryName = HibernateUtil.getSessionFactoryName(entityName);
		Session session = HibernateUtil.getSession(factoryName);
		Session dom4jSession = session.getSession(EntityMode.DOM4J);
		Object object = dom4jSession.get(entityName, id);
		
		XMLWriter writer = createWriter(file);
        Element root = new DefaultElement( "root" );
        writer.startDocument();
        writer.writeOpen( root );
        writer.write( object );
        writer.writeClose( root );
        writer.endDocument();
        writer.close();        
        
        dom4jSession.close();
        HibernateUtil.closeSession( factoryName );
	}
	
	public AonFile getInvoiceXml() throws IOException, SAXException {
		File file = File.createTempFile( "facturae", ".xml" );
		writeInvoiceXml(file, Invoice.class.getName(), currentInvoice.getId());
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);
		aonFile.setFileName( "facturae.xml" );
		return aonFile;
	}
	
	public void sendInvoice( Invoice invoice ) {
		try {
			RegistryMedia email = invoice.getRegistry().getEmail();
			if ( email == null) {
				String text = AonUtil.getMessage(BUNDLE_KEY, FINANCE_INVOICE_WITHOUT_EMAIL);
				String message = MessageFormat.format(text, invoice.getReferenceCode(), invoice.getRegistryName() );				
				AonUtil.addErrorMessage(message);				
			} else {
				Address to = new InternetAddress( email.getValue(), invoice.getRegistryName() );
				String subject = getEmailSubject(invoice);
				String content = getEmailBody(invoice);
				String name = "invoice_" + invoice.getSeries() + "-" + invoice.getNumber() + ".pdf";
				setCurrentInvoice(invoice);
				AonFile file = getInvoiceFile("saleInvoice", name);
				if ( hasDigitalCertificate() ) {
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
