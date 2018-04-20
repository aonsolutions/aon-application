package com.code.aon.ui.finance.util;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_EINVOICE_EMAIL_SUBJECT;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_EMAIL_BODY;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_EMAIL_SUBJECT;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_SEND_EMAIL;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_SEND_EMAIL_ERROR;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_WITHOUT_EMAIL;
import static com.code.aon.ui.common.ICommonMessages.WAREHOUSE_DELIVERY_EMAIL_BODY;
import static com.code.aon.ui.common.ICommonMessages.WAREHOUSE_DELIVERY_EMAIL_SUBJECT;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.HashMap;
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
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.marketing.enumeration.MailProcessType;
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
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

public class FinanceEmailUtil extends CompanyEmailUtil implements IFinanceConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
	
	public void initMessageController(MessageController messageController, Finance finance) throws ManagerBeanException{
		String[] emails = getAdministrativeEmails(finance.getRegistry());
		initMessageController(messageController, emails);
		
		if(!messageController.initMessageController(getDomain(finance.getDomain()), "", getMap(finance), "AON_MAIL_PROCESS" + MailProcessType.FINANCE.ordinal())) {
			initMessageController(messageController, emails, getEmailBody(finance));
		}
		messageController.setSubject(getEmailSubject(finance));
	}
	
	private Map<String,String> getMap(Finance finance) {
		Map<String,String> map = new HashMap<String,String>();		
		map.put("importe", Double.toString(finance.getAmount()));
		map.put("amount", Double.toString(finance.getAmount()));
		
		map.put("importe_total", Double.toString(finance.getTotalAmount()));
		map.put("total_amount", Double.toString(finance.getTotalAmount()));
		
		map.put("concepto", finance.getConcept());
		map.put("concept", finance.getConcept());
		
		map.put("nombre_acreedor", finance.getRegistryName());
		map.put("creditor_name", finance.getRegistryName());
		
		map.put("documento_acreedor", finance.getRegistryDocument());
		map.put("creditor_document", finance.getRegistryDocument());

		map.put("nombre_deudor", finance.getRegistryName());
		map.put("debtor_name", finance.getRegistryName());

		map.put("documento_deudor", finance.getRegistryDocument());
		map.put("debtor_document", finance.getRegistryDocument());

		map.put("iban", finance.getBankAccount().getIban());

		map.put("gastos", Double.toString(finance.getExpenses()));
		map.put("expenses", Double.toString(finance.getExpenses()));

		return map;
	}
	
	public String getEmailSubject(Finance finance) {
		return AonUtil.getMessage(WAREHOUSE_DELIVERY_EMAIL_SUBJECT, finance.getReferenceCode());
	}
	
	public String getEmailBody(Finance finance) {
		return AonUtil.getMessage(WAREHOUSE_DELIVERY_EMAIL_BODY, finance.getReferenceCode(), finance.getDueDate()); 
	}
	
	public void initMessageController(MessageController messageController, Invoice invoice, IAttachment attach, boolean facturae) throws ManagerBeanException, IOException{
		String[] emails = getAdministrativeEmails(invoice.getRegistry());
		initMessageController(messageController, emails);

		if(!messageController.initMessageController(getDomain(invoice.getDomain()), "", getMap(invoice), "AON_MAIL_PROCESS" + MailProcessType.INVOICE.ordinal())) {
			if(attach!=null){
				initMessageController(messageController, emails, getEmailBody(invoice));
			} else {
				Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap();
				String host = map.get("host"), referer = map.get("referer");
				String remain_url = "domain="+host.replaceAll(":8080", "")+"&login="+AonUtil.getRemoteUser()+"&invoice="+invoice.getId();
				remain_url = referer+"sid/"+Base64.getEncoder().encodeToString(remain_url.getBytes(StandardCharsets.UTF_8));
				initMessageController(messageController, emails, getEmailBody(invoice, remain_url));
			}
		}
		
		messageController.setSubject(getEmailSubject(invoice));
		
		if (attach != null) {
			messageController.addAttachment(getInvoiceFile(attach, invoice));	
		}
		if(facturae) {
			AonFile xml = getInvoiceXml(invoice);
			if (xml != null) {
				messageController.addAttachment(xml);	
			}	
		}
	}	
	
	private Domain getDomain(Integer domainId) {
		return AON.getDomain(AonUtil.getDomainName(), domainId, "");
	}
	
	private Map<String,String> getMap(Invoice invoice) {
		Map<String,String> map = new HashMap<String,String>();		

		map.put("serie", invoice.getSeries());

		map.put("numero", Integer.toString(invoice.getNumber()));
		map.put("number", Integer.toString(invoice.getNumber()));
		
		map.put("referencia", invoice.getReferenceCode());
		map.put("reference", invoice.getReferenceCode());
		
		map.put("documento_cliente", invoice.getRegistryDocument());
		map.put("customer_document", invoice.getRegistryDocument());
		
		map.put("nombre_cliente", invoice.getRegistryName());
		map.put("customer_name", invoice.getRegistryName());
		
		map.put("documento_proveedor", invoice.getRegistryDocument());
		map.put("supplier_document", invoice.getRegistryDocument());
		
		map.put("nombre_proveedor", invoice.getRegistryName());
		map.put("supplier_name", invoice.getRegistryName());
		
		map.put("documento_acreedor", invoice.getRegistryDocument());
		map.put("creditor_document", invoice.getRegistryDocument());
		
		map.put("nombre_acreedor", invoice.getRegistryName());
		map.put("creditor_name", invoice.getRegistryName());
		
		map.put("fecha", AonDateUtils.simpleFormat(invoice.getDate()));
		map.put("date", AonDateUtils.simpleFormat(invoice.getDate()));
		
		map.put("fecha_iva", AonDateUtils.simpleFormat(invoice.getTaxDate()));
		map.put("tax_date", AonDateUtils.simpleFormat(invoice.getTaxDate()));
		
		map.put("total", Double.toString(invoice.getTotal()));
		
		return map;
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
		if(attach.getData() == null && attach.getDriveId() != null) {
			DomainGserviceaccount serviceAccount = AON.getDomainGserviceaccount(AonUtil.getDomainName()
					, attach.getDomain(), AonUtil.getRemoteUser() != null ? AonUtil.getRemoteUser() : "");
			Drive drive = AonDrive.getInstace().serviceInitialize(serviceAccount);
			attach.setData(AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId()));
		}
		if (attach.getData() != null) {
			AonFileUtils.writeByteArrayToFile(file, attach.getData());
		} else {
			throw new AonCoreException("No se ha podido generar el Documento de Factura.");
		}

		AonFile aonFile = new AonFile();
		aonFile.setFile(file);	
		aonFile.setFileName( _fileName + "." + _extension );
		aonFile.setMimeType(attach.getMimeType());
		return aonFile;
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
