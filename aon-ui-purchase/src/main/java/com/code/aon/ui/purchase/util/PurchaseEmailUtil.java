package com.code.aon.ui.purchase.util;

import static com.code.aon.ui.common.ICommonMessages.PURCHASE_EMAIL_BODY;
import static com.code.aon.ui.common.ICommonMessages.PURCHASE_EMAIL_BODY_HEADER;
import static com.code.aon.ui.common.ICommonMessages.PURCHASE_EMAIL_SUBJECT;
import static com.code.aon.ui.common.ICommonMessages.PURCHASE_SEND_EMAIL;
import static com.code.aon.ui.common.ICommonMessages.PURCHASE_SEND_EMAIL_ERROR;
import static com.code.aon.ui.common.ICommonMessages.PURCHASE_WITHOUT_EMAIL;
import static com.code.aon.ui.common.ICommonMessages.SALES_PURCHASE_REFERENCE;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.marketing.enumeration.MailProcessType;
import com.code.aon.purchase.Purchase;
import com.code.aon.report.ReportException;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.purchase.controller.IPurchaseConstants;
import com.code.aon.ui.purchase.controller.PurchaseReportManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.bean.AonMessage;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailTemplate;
import com.esferalia.aon.watson.server.AonDateUtils;

public class PurchaseEmailUtil extends CompanyEmailUtil implements IPurchaseConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseEmailUtil.class.getName());

	private static final String REPORT_KEY = "purchaseForm";

	public void initMessageController( MessageController messageController ) throws ManagerBeanException, IOException, ReportException {
		initMessageController(messageController, null);
	}

	public void initMessageController( MessageController messageController, Purchase purchase, List<String> moreRecipients ) throws ManagerBeanException, IOException, ReportException {
		String[] emails = getEmails( purchase, moreRecipients );
		initMessageController(messageController, emails);
		messageController.setGenericMessage(false);
		messageController.setTemplates(getTemplates(MailProcessType.ORDER));
		if(!messageController.initMessageController(getDomain(purchase.getDomain()), "", getMap(purchase), "AON_MAIL_PROCESS_" + MailProcessType.ORDER.ordinal()+ "_1")) {
			messageController.updateMessageBody( getEmailContent(getEmailBody(purchase), AonUtil.getMessage(PURCHASE_EMAIL_BODY_HEADER)) );
		}
		if(messageController.getSubject() == null || messageController.getSubject().isEmpty()) {
			messageController.setSubject(getEmailSubject(purchase));
		}

		PurchaseReportManager purchaseReportManager = (PurchaseReportManager) AonUtil.getRegisteredBean(PURCHASE_REPORT_CONTROLLER_NAME);
		purchaseReportManager.setValued(purchase.getSupplier().isPurchaseValuated());
		messageController.addAttachment( getReport(purchase, REPORT_KEY) );
	}
	
	private LinkedList<SelectItem> getTemplates(MailProcessType type) {
		LinkedList<SelectItem> templates = new LinkedList<>();
		AON.getApplicationParameterStream(AonUtil.getDomainName(), getCompany().getDomain(), "", f -> 
			f.getDomainProperty().eq(getCompany().getDomain())
			.and(f.getNameProperty().like("AON_MAIL_PROCESS_" + type.ordinal() + "%"))).forEach(ap -> {
				String[] ids = StringUtils.split(ap.getValue());
				MailTemplate mt = AON.getMailTemplate(AonUtil.getDomainName(), ap.getDomain(), "", f-> 
					f.getIdProperty().eq(Integer.parseInt(ids[1])));
				templates.add(new SelectItem(mt.getId(), mt.getName()));
			});
		
		return templates;
	}
	
	private Map<String,String> getMap(Purchase purchase) {
		Map<String,String> map = new HashMap<String,String>();		
		
		map.put("comentarios", purchase.getComments());
		map.put("comments", purchase.getComments());
		
		map.put("fecha", AonDateUtils.simpleFormat(purchase.getDate()));
		map.put("date", AonDateUtils.simpleFormat(purchase.getDate()));
		
		map.put("fecha_entrega", AonDateUtils.simpleFormat(purchase.getDeliveryDate()));
		map.put("delivery_date", AonDateUtils.simpleFormat(purchase.getDeliveryDate()));

		map.put("fecha_pedido", AonDateUtils.simpleFormat(purchase.getIssueDate()));
		map.put("issue_date", AonDateUtils.simpleFormat(purchase.getIssueDate()));
		
		map.put("serie", purchase.getSeries());
		
		map.put("numero", Integer.toString(purchase.getNumber()));
		map.put("number", Integer.toString(purchase.getNumber()));

		map.put("estado", purchase.getStatus().getName(AonUtil.getCurrentLocale()));
		map.put("status", purchase.getStatus().getName(AonUtil.getCurrentLocale()));
			
		map.put("nombre_proveedor", purchase.getSupplier().getRegistry().getName());
		map.put("supplier_name", purchase.getSupplier().getRegistry().getName());
		
		map.put("documento_proveedor", purchase.getSupplier().getRegistry().getDocument());
		map.put("supplier_document", purchase.getSupplier().getRegistry().getDocument());
		
		map.put("referencia", purchase.getReferenceCode());
		map.put("reference", purchase.getReferenceCode());
		
		return map;
	}
	
	private Domain getDomain(Integer domainId) {
		return AON.getDomain(AonUtil.getDomainName(), domainId, "");
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
	private String[] getEmails( Purchase purchase, List<String> moreRecipients ) throws ManagerBeanException {
		List<String> emails = new LinkedList<String>();
		String[] emailArray = getAdministrativeEmails(purchase.getSupplier().getRegistry());
		if (! ArrayUtils.isEmpty(emailArray) ) {
			for( String email : emailArray ) {
				if ( EmailValidator.getInstance().isValid(email) ) {
					emails.add(email);
				}
			}
		}
		for( String email: moreRecipients ) {
			if ( EmailValidator.getInstance().isValid(email) ) {
				emails.add(email);
			}
		}
		return emails.toArray(new String[emails.size()]);
	}
	
	public String getEmailSubject( Purchase purchase ) {
		String message = AonUtil.getMessage(PURCHASE_EMAIL_SUBJECT);
		return formatEmailSubject( purchase, message );
	}
	
	public String getEmailBody( Purchase purchase ) throws UnsupportedEncodingException {
		String bodyMessage = AonUtil.getMessage(PURCHASE_EMAIL_BODY); 
		return MessageFormat.format(bodyMessage, purchase.getReferenceCode(), purchase.getIssueDate() );
	}
	
	private String formatEmailSubject( Purchase purchase, String message ) {
		String msgContent = purchase.getReferenceCode();
		if( StringUtils.isNotBlank(purchase.getPurchaseReference()) ){
			msgContent += " ("+AonUtil.getMessage(SALES_PURCHASE_REFERENCE)+": ";
			msgContent += purchase.getPurchaseReference()+")";
		}
		return MessageFormat.format(message, msgContent);
	}
	
	private String formatEmailBody(  Purchase purchase, String message )  {
		return MessageFormat.format(message, purchase.getReferenceCode(), purchase.getIssueDate());
	}
	
	public String getEmailSubject() {
		return AonUtil.getMessage(PURCHASE_EMAIL_SUBJECT);
	}
	
	public String getEmailBody()  {
		return AonUtil.getMessage(PURCHASE_EMAIL_BODY); 
	}
	
	public void sendPurchase( int index, Purchase purchase, List<String> moreRecipients, String recipientsCc, String recipientsBcc, String subject, String content ) {
		LogPanelController logger = LogPanelController.getInstance();
		AonFile file = null;
		AonFile xml = null;
		try {
			String[] emails = getEmails(purchase, moreRecipients);
			if ( ArrayUtils.isEmpty(emails) ) {
				String text = AonUtil.getMessage(PURCHASE_WITHOUT_EMAIL);
				String message = MessageFormat.format(text, purchase.getReferenceCode(), purchase.getSupplier().getRegistry().getFullName() );				
				logger.error( message );				
			} else {
				Address[] recipients = getEmailAddresses(emails, purchase.getSupplier().getRegistry().getFullName() );
				String _subject = formatEmailSubject(purchase, subject);
				String _content = formatEmailBody(purchase, content );
				PurchaseReportManager puerchaseReport = (PurchaseReportManager) AonUtil.getRegisteredBean(IPurchaseConstants.PURCHASE_REPORT_CONTROLLER_NAME);
				puerchaseReport.setValued(purchase.getSupplier().isPurchaseValuated());
				file = getReport(purchase, REPORT_KEY);
				
				AonMessage aonMessage = getEmailSender().createMessage(recipients, _subject);
				getEmailSender().addMessageContent(aonMessage, _content, MimeType.MIME_HTML, file);
				aonMessage.setRecipientsCc(recipientsCc);
				aonMessage.setRecipientsBcc(recipientsBcc);
				getEmailSender().sendMessage(aonMessage);
				
				String text = AonUtil.getMessage(PURCHASE_SEND_EMAIL);
				String message = MessageFormat.format(text, purchase.getReferenceCode(), purchase.getSupplier().getRegistry().getFullName(), ArrayUtils.toString(emails), index );
				logger.info( message );
			}
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			String errorMessage = th.getMessage();
			if ( (th.getCause() != null) && (th instanceof WebmailException) ) {
				errorMessage = th.getCause().getMessage();
			}
			String text = AonUtil.getMessage(PURCHASE_SEND_EMAIL_ERROR);
			String message = MessageFormat.format(text, purchase.getReferenceCode(), index, errorMessage );
			logger.error( message + "<br />" + th.getMessage() + "<br />" + th.getCause() );
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
