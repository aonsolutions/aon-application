package com.code.aon.ui.purchase.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.purchase.Purchase;
import com.code.aon.report.ReportException;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.purchase.IPurchaseMessages;
import com.code.aon.ui.purchase.controller.IPurchaseConstants;
import com.code.aon.ui.purchase.controller.PurchaseController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;

public class PurchaseEmailUtil extends CompanyEmailUtil implements IPurchaseMessages, IPurchaseConstants {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseEmailUtil.class.getName());

	private static final String REPORT_KEY = "purchaseForm";

	public void initMessageController( MessageController messageController, Purchase purchase ) throws ManagerBeanException, IOException, ReportException {
		String[] emails = getEmails( purchase.getSupplier().getRegistry() );
		initMessageController(messageController, emails, getEmailBody(purchase));
		messageController.setSubject( getEmailSubject(purchase) );
		messageController.addAttachment( getReport(purchase, REPORT_KEY) );
	}
	
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
	
	public String getEmailSubject( Purchase purchase ) {
		String message = AonUtil.getMessage(BUNDLE_KEY, PURCHASE_EMAIL_SUBJECT);
		return MessageFormat.format(message, purchase.getReferenceCode() );
	}
	
	public String getEmailBody( Purchase purchase ) throws UnsupportedEncodingException {
		String bodyMessage = AonUtil.getMessage(BUNDLE_KEY, PURCHASE_EMAIL_BODY); 
		return MessageFormat.format(bodyMessage, purchase.getReferenceCode(), purchase.getIssueDate() );
	}
	
	private String formatEmailSubject( Purchase purchase, String message ) {
		return MessageFormat.format(message, purchase.getReferenceCode() );
	}
	
	private String formatEmailBody(  Purchase purchase, String message )  {
		return MessageFormat.format(message, purchase.getReferenceCode(), purchase.getIssueDate());
	}
	
	public String getEmailSubject() {
		return AonUtil.getMessage(BUNDLE_KEY, PURCHASE_EMAIL_SUBJECT);
	}
	
	public String getEmailBody()  {
		return AonUtil.getMessage(BUNDLE_KEY, PURCHASE_EMAIL_BODY); 
	}
	
	public void sendPurchase( Purchase purchase, String subject, String content ) {
		LogPanelController logger = LogPanelController.getInstance();
		AonFile file = null;
		AonFile xml = null;
		try {
			String[] emails = getEmails(purchase.getSupplier().getRegistry());
			if ( ArrayUtils.isEmpty(emails) ) {
				String text = AonUtil.getMessage(BUNDLE_KEY, PURCHASE_WITHOUT_EMAIL);
				String message = MessageFormat.format(text, purchase.getReferenceCode(), purchase.getSupplier().getRegistry().getFullName() );				
				logger.error( message );				
			} else {
				Address[] recipients = getEmailAddresses(emails, purchase.getSupplier().getRegistry().getFullName() );
				String _subject = formatEmailSubject(purchase, subject);
				String _content = formatEmailBody(purchase, content );
				file = getPurchaseFile(purchase);
				getEmailSender().sendMessage(recipients, _subject, _content, MimeType.MIME_HTML, file );
				String text = AonUtil.getMessage(BUNDLE_KEY, PURCHASE_SEND_EMAIL);
				String message = MessageFormat.format(text, purchase.getReferenceCode(), purchase.getSupplier().getRegistry().getFullName(), ArrayUtils.toString(emails) );
				logger.info( message );
			}
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			String text = AonUtil.getMessage(BUNDLE_KEY, PURCHASE_SEND_EMAIL_ERROR);
			String message = MessageFormat.format(text, purchase.getSeries(), purchase.getNumber() );
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
	
	private AonFile getPurchaseFile( Purchase purchase ) throws IOException, ReportException, ManagerBeanException {
		PurchaseController controller = (PurchaseController) AonUtil.getRegisteredBean(PURCHASE_CONTROLLER_NAME);
		return getPurchaseFile(controller.getPurchaseData(purchase), purchase);
	}
	
	public AonFile getPurchaseFile( byte[] data, Purchase purchase ) throws IOException {
		PurchaseController controller = (PurchaseController) AonUtil.getRegisteredBean(PURCHASE_CONTROLLER_NAME);
		String fileName = controller.getDescription( purchase );
		
		File file = File.createTempFile( fileName, ".pdf" );
		FileUtils.writeByteArrayToFile(file, data);
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);	
		aonFile.setFileName( fileName + ".pdf" );
		aonFile.setMimeType(MimeType.MIME_PDF);
		return aonFile;
	}

}
