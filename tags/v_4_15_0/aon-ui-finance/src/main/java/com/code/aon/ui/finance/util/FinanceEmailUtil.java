package com.code.aon.ui.finance.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IAttachment;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.facturae.FacturaeWriter;
import com.code.aon.finance.Invoice;
import com.code.aon.report.ReportException;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.AonFile;
import com.code.aon.webmail.SecurityInfo;

public class FinanceEmailUtil extends CompanyEmailUtil implements IFinanceMessages, IFinanceConstants {

	private static final Logger LOGGER = LoggerFactory.getLogger(FinanceEmailUtil.class.getName());
	
	private Address[] getEmailAddresses( String[] emails, String name ) throws UnsupportedEncodingException, AddressException {
		Address[] addresses = new Address[emails.length];
		for( int i = 0; i < emails.length; i++ ) {
			if ( i == 0 ) {
				addresses[i] = new InternetAddress( emails[i], name );
			} else {
				addresses[i] = new InternetAddress( emails[i] );	
			}
		} 
		return addresses;
	}		
	
	public void initMessageController( MessageController messageController, Invoice invoice, IAttachment attach, boolean facturae ) throws ManagerBeanException, IOException{
		String[] emails = getEmails(invoice.getRegistry());
		initMessageController(messageController, emails, getEmailBody(invoice));
		messageController.setSubject( getEmailSubject(invoice) );
		messageController.addAttachment( getInvoiceFile(attach, invoice) );
		if ( facturae ) {
			messageController.addAttachment( getInvoiceXml(invoice) );	
		}
	}	
	
	public String getEmailSubject( Invoice invoice ) {
		String key = invoice.isSigned() ? FINANCE_EINVOICE_EMAIL_SUBJECT : FINANCE_INVOICE_EMAIL_SUBJECT; 
		String message = AonUtil.getMessage(BUNDLE_KEY, key);
		return MessageFormat.format(message, invoice.getReferenceCode() );
	}

	public String getEmailBody( Invoice invoice )  {
		String bodyMessage = AonUtil.getMessage(BUNDLE_KEY, FINANCE_INVOICE_EMAIL_BODY); 
		return MessageFormat.format(bodyMessage, invoice.getReferenceCode(), invoice.getIssueDate());
	}

	private AonFile getInvoiceFile( Invoice invoice ) throws IOException, ReportException, ManagerBeanException {
		InvoiceController controller = (InvoiceController) AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
		IAttachment attach = controller.getInvoiceData(invoice);
		return getInvoiceFile(attach, invoice);
	}
	
	public AonFile getInvoiceFile( IAttachment attach, Invoice invoice ) throws IOException {
		String fileName = attach.getDescription();
		if ( StringUtils.isEmpty(fileName) ) {
			InvoiceController controller = (InvoiceController) AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
			fileName = controller.getDescription( invoice );
		}
		File file = File.createTempFile( fileName, ".pdf" );
		FileUtils.writeByteArrayToFile(file, attach.getData());
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);	
		aonFile.setFileName( fileName + ".pdf" );
		return aonFile;
	}

	public AonFile getInvoiceXml( Invoice invoice ) throws IOException {
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
			String[] emails = getEmails(invoice.getRegistry());
			if ( ArrayUtils.isEmpty(emails) ) {
				String text = AonUtil.getMessage(BUNDLE_KEY, FINANCE_INVOICE_WITHOUT_EMAIL);
				String message = MessageFormat.format(text, invoice.getReferenceCode(), invoice.getRegistryName() );				
				AonUtil.addErrorMessage(message);				
			} else {
				Address[] recipients = getEmailAddresses(emails, invoice.getRegistryName() );
				String subject = getEmailSubject(invoice);
				String content = getEmailContent( getEmailBody(invoice) );
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
			LOGGER.error(th.getMessage(), th);
			String text = AonUtil.getMessage(BUNDLE_KEY, FINANCE_INVOICE_SEND_EMAIL_ERROR);
			String message = MessageFormat.format(text, invoice.getReferenceCode(), invoice.getRegistryName() );				
			AonUtil.addErrorMessage(message);
		}
	}
	
}
