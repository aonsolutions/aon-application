package com.code.aon.ui.finance.util;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_EINVOICE_EMAIL_SUBJECT;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_EMAIL_BODY;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_EMAIL_SUBJECT;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_SEND_EMAIL;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_SEND_EMAIL_ERROR;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_WITHOUT_EMAIL;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.IAttachment;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.facturae.FACeUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.report.ReportException;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.finance.SddMandateObject;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.bean.AonMessage;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.api.services.drive.Drive;

public class FinanceEmailUtil extends CompanyEmailUtil implements IFinanceConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(FinanceEmailUtil.class.getName());
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	
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
		if(attach!=null){
			initMessageController(messageController, emails, getEmailBody(invoice));
		} else {
			Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap();
			String host = map.get("host"), referer = map.get("referer");
			String remain_url = "domain="+host.replaceAll(":8080", "")+"&login="+AonUtil.getRemoteUser()+"&invoice="+invoice.getId();
			remain_url = referer+"sid/"+Base64.getEncoder().encodeToString(remain_url.getBytes(StandardCharsets.UTF_8));
			initMessageController(messageController, emails, getEmailBody(invoice, remain_url));
		}
		messageController.setSubject( getEmailSubject(invoice) );
		if ( attach != null ) {
			messageController.addAttachment( getInvoiceFile(attach, invoice) );	
		}
		if ( facturae ) {
			AonFile xml = getInvoiceXml(invoice);
			if ( xml != null ) {
				messageController.addAttachment(xml);	
			}	
		}
	}	
	
	public String getEmailSubject( Invoice invoice ) {
		String key = invoice.isSigned() ? FINANCE_EINVOICE_EMAIL_SUBJECT : FINANCE_INVOICE_EMAIL_SUBJECT; 
		String message = AonUtil.getMessage(key);
		return formatEmailSubject(invoice, message);
	}

	private String formatEmailSubject( Invoice invoice, String message ) {
		return MessageFormat.format(message, invoice.getReferenceCode() );
	}
	
	public String getEmailSubject() {
		return AonUtil.getMessage(FINANCE_INVOICE_EMAIL_SUBJECT);
	}

	public String getEmailBody( Invoice invoice )  {
		String message = AonUtil.getMessage(FINANCE_INVOICE_EMAIL_BODY); 
		return formatEmailBody(invoice, message);
	}
	
	public String getEmailBody( Invoice invoice, String url)  {
		StringBuilder sb = new StringBuilder();
		sb.append("Puede descargar la factura ");
		sb.append("<a href='").append(url).append("' target='_blank'>");
		sb.append(invoice.getReferenceCode());
		sb.append("</a>");
		sb.append(" emitida el ");
		sb.append(new SimpleDateFormat("dd/MM/yyyy").format(invoice.getIssueDate()));
		sb.append(" a su nombre.<br/>");
		return sb.toString();
	}
	
	public String getEmailBody()  {
		return AonUtil.getMessage(FINANCE_INVOICE_EMAIL_BODY); 
	}

	private String formatEmailBody( Invoice invoice, String message )  {
		return MessageFormat.format(message, invoice.getReferenceCode(), invoice.getIssueDate());
	}
	
	private AonFile getInvoiceFile( Invoice invoice ) throws IOException, ReportException, ManagerBeanException {
		InvoiceController controller = (InvoiceController) AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
		IAttachment attach = controller.getInvoiceData(invoice);
		return getInvoiceFile(attach, invoice);
	}
	
	private AonFile getInvoiceFile( IAttachment attach, Invoice invoice ) throws IOException {
		return getInvoiceFile(attach, invoice, null, null);
	}

	private AonFile getInvoiceFile( IAttachment attach, Invoice invoice, String fileName, String extension ) throws IOException {
		String _fileName = fileName;
		if ( StringUtils.isEmpty(_fileName) ) {
			if ( StringUtils.isEmpty(attach.getDescription()) ) {
				InvoiceController controller = (InvoiceController) AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
				_fileName = controller.getDescription( invoice );
			} else {
				_fileName = attach.getDescription();
			}
		}
		String _extension = extension;
		if ( StringUtils.isEmpty(_extension) ) {
			_extension = attach.getMimeType().getExtension();	
		}
		File file = File.createTempFile( _fileName, "." + _extension );
		if (attach.getData() != null) {
			FileUtils.writeByteArrayToFile(file, attach.getData());
		} else if (StringUtils.isNotBlank(attach.getDriveId())) {
			writeAttachDataToFile(attach, file);
		} else {
			throw new AonCoreException("No se ha podido generar el Documento de Factura.");
		}

		AonFile aonFile = new AonFile();
		aonFile.setFile(file);	
		aonFile.setFileName( _fileName + "." + _extension );
		aonFile.setMimeType(attach.getMimeType());
		return aonFile;
	}

	private void writeAttachDataToFile(IAttachment attach, File file) throws IOException {
		try {
			Domain domain = new Domain().setName(AonUtil.getDomainName()).setId(attach.getDomain());
			User user = new User().setLogin(AonUtil.getRemoteUser() != null ? AonUtil.getRemoteUser() : "");
			DomainGserviceaccount serviceAccount = DBConsults.getServiceAccount(domain, user);
			Drive drive = DriveUtils.serviceInitialize(serviceAccount);
			InputStream input = DriveUtils.downloadFile(drive, DriveUtils.getFile(drive, domain, user, attach.getDriveId(), attach.getId()));
			copyInputStreamToFile(input, file);
		} catch (GeneralSecurityException ex) {
			throw new AonCoreException(ex.getMessage());
		}
	}

    private long copyInputStreamToFile(InputStream input, File file) throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int bytes = 0;

		FileOutputStream output = new FileOutputStream(file);
		while (-1 != (bytes = input.read(buffer))) {
			output.write(buffer, 0, bytes);
			count += bytes;
		}
		output.close();
		return count;
    }

	@SuppressWarnings("unchecked")
	public AonFile getSddMandateReport(SddMandateObject sddMandateObject) throws ReportException, IOException {
		AonFile aonFile = new AonFile();
		File file = File.createTempFile( "ssdMandate-temp", "." + MimeType.MIME_PDF.getExtension() );
		aonFile.setFile( file );
		aonFile.setFileName( "Domiciliacion-Bancaria-SEPA" + "." + MimeType.MIME_PDF.getExtension() );
		aonFile.setMimeType(MimeType.MIME_PDF);

		ReportManager reportManager = new ReportManager();
		reportManager.setCollectionProvider( sddMandateObject );
		OutputStream out = new BufferedOutputStream( new FileOutputStream(file) );
		reportManager.execute( out, REPORT_TEMPLATE_SDD_MANDATE );
		out.close();
		return aonFile;
	}

	private AonFile getInvoiceXml( Invoice invoice ) throws IOException, ManagerBeanException {
		InvoiceController controller = (InvoiceController) AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
		IAttachment attach = controller.getInvoiceFacturae(invoice);
		String extension = FACeUtil.isDefined(invoice) ? MimeType.MIME_XSIG.getExtension() : MimeType.MIME_XML.getExtension();
		return getInvoiceFile(attach, invoice, "facturae", extension);
	}
	
	public void sendInvoice( int index, Invoice invoice, String subject, String content, boolean saveSent ) {
		LogPanelController logger = LogPanelController.getInstance();
		AonFile file = null;
		AonFile xml = null;
		try {
			String[] emails = getAdministrativeEmails(invoice.getRegistry());
			if ( ArrayUtils.isEmpty(emails) ) {
				String text = AonUtil.getMessage(FINANCE_INVOICE_WITHOUT_EMAIL);
				String message = MessageFormat.format(text, invoice.getReferenceCode(), invoice.getRegistryName() );				
				logger.error( message );				
			} else {
				Address[] recipients = getEmailAddresses(emails, invoice.getRegistryName() );
				String _subject = formatEmailSubject(invoice, subject);
				String _content = formatEmailBody(invoice, content );
				file = getInvoiceFile(invoice);
				if ( InvoiceController.isIncludeFacturae(invoice) ) {
					xml = getInvoiceXml(invoice);	
				}
				AonMessage aonMessage = getEmailSender().sendMessage(recipients, _subject, _content, MimeType.MIME_HTML, file, xml );
				if ( saveSent ) {
					getEmailSender().storeMessage(aonMessage);
				}
				String text = AonUtil.getMessage(FINANCE_INVOICE_SEND_EMAIL);
				String message = MessageFormat.format(text, invoice.getReferenceCode(), invoice.getRegistryName(), ArrayUtils.toString(emails), index );
				logger.info( message );
			}
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			String errorMessage = th.getMessage();
			if ( (th.getCause() != null) && (th instanceof WebmailException) ) {
				errorMessage = th.getCause().getMessage();
			}
			String text = AonUtil.getMessage(FINANCE_INVOICE_SEND_EMAIL_ERROR );
			String message = MessageFormat.format(text, invoice.getReferenceCode(), invoice.getRegistryName(), errorMessage, index );
			logger.error( message );
		} finally {
			if ( file != null ) {
				file.clean();
			}
			if ( xml != null ) {
				FileUtils.deleteQuietly(xml.getFile());	
			}
		}
	}
	
}
