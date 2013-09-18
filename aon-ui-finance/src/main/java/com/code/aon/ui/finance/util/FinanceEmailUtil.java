package com.code.aon.ui.finance.util;

import static com.code.aon.ui.common.ICommonMessages.FACTURAE_ERROR;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_BUNDLE;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_EINVOICE_EMAIL_SUBJECT;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_EMAIL_BODY;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_EMAIL_SUBJECT;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_SEND_EMAIL;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_SEND_EMAIL_ERROR;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_WITHOUT_EMAIL;

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

import com.code.aon.common.AonException;
import com.code.aon.common.IAttachment;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.facturae.FacturaeWriter;
import com.code.aon.finance.Invoice;
import com.code.aon.report.ReportException;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.bean.AonMessage;

public class FinanceEmailUtil extends CompanyEmailUtil implements IFinanceConstants {

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
		String[] emails = getAdministrativeEmails(invoice.getRegistry());
		initMessageController(messageController, emails, getEmailBody(invoice));
		messageController.setSubject( getEmailSubject(invoice) );
		messageController.addAttachment( getInvoiceFile(attach, invoice) );
		if ( facturae ) {
			AonFile xml = getInvoiceXml(invoice);
			if ( xml != null ) {
				messageController.addAttachment(xml);	
			}	
		}
	}	
	
	public String getEmailSubject( Invoice invoice ) {
		String key = invoice.isSigned() ? FINANCE_EINVOICE_EMAIL_SUBJECT : FINANCE_INVOICE_EMAIL_SUBJECT; 
		String message = AonUtil.getMessage(FINANCE_BUNDLE, key);
		return formatEmailSubject(invoice, message);
	}

	private String formatEmailSubject( Invoice invoice, String message ) {
		return MessageFormat.format(message, invoice.getReferenceCode() );
	}
	
	public String getEmailSubject() {
		return AonUtil.getMessage(FINANCE_BUNDLE, FINANCE_INVOICE_EMAIL_SUBJECT);
	}

	public String getEmailBody( Invoice invoice )  {
		String message = AonUtil.getMessage(FINANCE_BUNDLE, FINANCE_INVOICE_EMAIL_BODY); 
		return formatEmailBody(invoice, message);
	}
	
	public String getEmailBody()  {
		return AonUtil.getMessage(FINANCE_BUNDLE, FINANCE_INVOICE_EMAIL_BODY); 
	}

	private String formatEmailBody( Invoice invoice, String message )  {
		return MessageFormat.format(message, invoice.getReferenceCode(), invoice.getIssueDate());
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
		aonFile.setMimeType(MimeType.MIME_PDF);
		return aonFile;
	}

	public AonFile getInvoiceXml( Invoice invoice ) throws IOException {
		File file = File.createTempFile( "facturae", FacturaeWriter.FACTURAE_EXTENSION );
		FacturaeWriter fw = new FacturaeWriter(AonUtil.getCurrentLocale());
		String filePath = file.getAbsolutePath();
		String fileName = FilenameUtils.getFullPath(filePath) + FilenameUtils.getBaseName(filePath);
		AonFile aonFile = null;
		try {
			fw.serialize(invoice, fileName);
			aonFile = new AonFile();
			aonFile.setFile(file);
			aonFile.setFileName( "facturae.xml" );
			aonFile.setMimeType(MimeType.MIME_XML);
		} catch (AonException e) {
			LOGGER.error( e.getMessage(), e );
			AonUtil.addErrorMessageFromBundle(FINANCE_BUNDLE, FACTURAE_ERROR, invoice.getReferenceCode());			
			FileUtils.deleteQuietly(file);
		}
		return aonFile;
	}
	
	public void sendInvoice( Invoice invoice, String subject, String content, boolean saveSent ) {
		LogPanelController logger = LogPanelController.getInstance();
		AonFile file = null;
		AonFile xml = null;
		try {
			String[] emails = getAdministrativeEmails(invoice.getRegistry());
			if ( ArrayUtils.isEmpty(emails) ) {
				String text = AonUtil.getMessage(FINANCE_BUNDLE, FINANCE_INVOICE_WITHOUT_EMAIL);
				String message = MessageFormat.format(text, invoice.getReferenceCode(), invoice.getRegistryName() );				
				logger.error( message );				
			} else {
				Address[] recipients = getEmailAddresses(emails, invoice.getRegistryName() );
				String _subject = formatEmailSubject(invoice, subject);
				String _content = formatEmailBody(invoice, content );
				file = getInvoiceFile(invoice);
				xml = getInvoiceXml(invoice);
				AonMessage aonMessage = getEmailSender().sendMessage(recipients, _subject, _content, MimeType.MIME_HTML, file, xml );
				if ( saveSent ) {
					getEmailSender().storeMessage(aonMessage);
				}
				String text = AonUtil.getMessage(FINANCE_BUNDLE, FINANCE_INVOICE_SEND_EMAIL);
				String message = MessageFormat.format(text, invoice.getReferenceCode(), invoice.getRegistryName(), ArrayUtils.toString(emails) );
				logger.info( message );
			}
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			String text = AonUtil.getMessage(FINANCE_BUNDLE, FINANCE_INVOICE_SEND_EMAIL_ERROR);
			String message = MessageFormat.format(text, invoice.getReferenceCode(), invoice.getRegistryName() );
			logger.error( message );
		} finally {
			if ( file != null ) {
				FileUtils.deleteQuietly(file.getFile());	
			}
			if ( xml != null ) {
				FileUtils.deleteQuietly(xml.getFile());	
			}
		}
	}
	
}
