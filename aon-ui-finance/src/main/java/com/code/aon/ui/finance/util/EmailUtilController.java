package com.code.aon.ui.finance.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import javax.faces.event.AbortProcessingException;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringEscapeUtils;

import com.code.aon.bridge.session.LoggedUser;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.finance.Invoice;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.AonFile;
import com.code.aon.webmail.EmailSender;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.WebmailUtil;

public class EmailUtilController implements IFinanceMessages {
	
	private EmailSender sender;
	
	private Company company;
	
	private MailAccount getDefaultMailAccount() {
		AuthPrincipal user = UserUtils.getInstance().getPrincipal();		
		String domain = user.getDomain();
		String login = user.getShortName();
		MailAccount mailAccount;
		try {
			mailAccount = WebmailUtil.getDefaultAccount(domain,login);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage( "El usuario " + login + " no tiene definida ninguna cuenta de correo" );
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
		return MessageFormat.format(message, getCompany().getName(), invoice.getReferenceCode() );
	}

	public String getEmailBody() {
		StringBuffer body = new StringBuffer();
		body.append( "<html><body>" );
		body.append(AonUtil.getMessage(BUNDLE_KEY, FINANCE_INVOICE_EMAIL_BODY) );
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		Company company = companyController.obtainCompany();
		body.append( company.getName() ).append( "<br/>" );
		RegistryMedia phone = companyController.getPhone();
		if ( phone != null ) {
			body.append( "Telefono: " ).append( phone.getValue() ).append( "<br/>" );
		}
		RegistryMedia fax = companyController.getFax();
		if ( fax != null ) {
			body.append( "Fax: " ).append( fax.getValue() ).append( "<br/>" );
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
			MailAccount mailAccount = getDefaultMailAccount();
			Address from = new InternetAddress( mailAccount.getEmail(), getCompany().getName() );
			this.sender = new EmailSender( from, mailAccount );			
		}
		return this.sender;
	}
	
	public AonFile getInvoiceFile( Invoice invoice ) throws IOException, ReportException, DAOException {
		ReportManager report = new ReportManager();
		report.setOutputFormat(OutputFormat.PDF);
		report.setReportKey("saleInvoice");
		File file = File.createTempFile( "invoice", ".pdf" );
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		report.execute(out);
		out.close();
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);
		aonFile.setFileName( "invoice.pdf" );
		return aonFile;
	}
	
	public void sendInvoice( Invoice invoice ) throws WebmailException, MessagingException, ManagerBeanException, ReportException, IOException, DAOException {
		Address to = new InternetAddress( invoice.getRegistry().getEmail().getValue(), invoice.getRegistryName() );
		String subject = getEmailSubject(invoice);
		String content = getEmailBody();
		AonFile file = getInvoiceFile(invoice);
		getEmailSender().sendMessage(to, subject, content, MimeType.MIME_HTML, file);		
		file.getFile().delete();
	}
	
}
