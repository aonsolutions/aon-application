package com.code.aon.ui.ecommerce.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import javax.faces.event.AbortProcessingException;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IAttachment;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.sign.controller.SignerController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.webmail.AonFile;
import com.code.aon.webmail.EmailSender;
import com.code.aon.webmail.MailAccount;

public class EmailParentController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EmailParentController.class.getName());

	public void email(String subject, String from, String to, String content) {
		EmailSender es;
		try {
			es = getEmailSender(from);
			es.connect();
		} catch (UnsupportedEncodingException e) {
			// nada
		} catch (MessagingException e) {
			// nada
		}
		sendEmail(from, to, subject, content);
	}

	private EmailSender sender;
	private final String BUNDLE_KEY = "ecommerceBundle";
	private final String ECOMMERCE_SEND_EMAIL_ERROR = "ecommerce_send_email_error";

	// private final String ECOMMERCE_WITHOUT_EMAIL = "ecommerce_without_email";
	// private final String ECOMMERCE_EMAIL_BODY_HEADER =
	// "ecommerce_email_body_header";
	// private final String ECOMMERCE_EMAIL_BODY = "ecommerce_email_body";
	// private final String ECOMMERCE_EMAIL_BODY_FOOTER =
	// "ecommerce_email_body_footer";

	public EmailSender getEmailSender(String username)
			throws UnsupportedEncodingException {
		if (this.sender == null) {
			// String domain = "localhost";
			String login = "admin";
			MailAccount mailAccount = getDefaultMailAccount();

			if (mailAccount != null) {
				Address from = new InternetAddress(username, username);
				this.sender = new EmailSender(from, mailAccount);
			} else {
				String text = AonUtil.getMessage(WebMailConstants.BUNDLE_NAME,
						WebMailConstants.NOT_MAIL_ACCOUNT);
				String message = MessageFormat.format(text, login);
				throw new AbortProcessingException(message);
			}
		}
		return this.sender;
	}

	private MailAccount getDefaultMailAccount() {
		MailAccount mailAccount = new MailAccount();
		mailAccount.setHost("mail.esferalia.com");
		mailAccount.setEmail("eagirrezabal@esferalia.com");
		mailAccount.setOutgoingHost("mail.esferalia.com");
		mailAccount.setOutgoingPort(25);
		mailAccount.setOutgoingSsl(false);
		mailAccount.setOutgoingVerification(true);
		mailAccount.setProtocol("imap");
		mailAccount.setMailUsername("test@esferalia.com");
		mailAccount.setPasswordString("test");
		return mailAccount;
	}

	public void sendEmail(String username, String to, String subject,
			String content) {

		try {
			Address[] recipients = new Address[1];
			recipients[0] = new InternetAddress(to, to);
			// String subject = getEmailSubject(invoice);
			String bodyContent = getEmailBody(content);
			SignerController sc = (SignerController) AonUtil
					.getRegisteredBean(IECommerceConstants.OFFER_SIGNER_CONTROLLER);
			CartOfferController coc = (CartOfferController) AonUtil
					.getRegisteredBean(IECommerceConstants.OFFER_CONTROLLER);
			if (coc.getOffer() != null) {
				IAttachment attach = sc.getReport(coc.getOffer());
				getEmailSender(username).sendMessage(recipients, subject,
						bodyContent, MimeType.MIME_HTML, getOfferFile(attach));
			} else {
				getEmailSender(username).sendMessage(recipients, subject,
						bodyContent, MimeType.MIME_HTML);
			}
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			String text = AonUtil.getMessage(BUNDLE_KEY,
					ECOMMERCE_SEND_EMAIL_ERROR);
			String message = MessageFormat.format(text, username);
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
		}
	}

	public AonFile getOfferFile(IAttachment attach) throws IOException {
		String fileName = "presupuesto";
		File file = File.createTempFile(fileName, ".pdf");
		FileUtils.writeByteArrayToFile(file, attach.getData());
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);
		aonFile.setFileName(fileName + ".pdf");
		return aonFile;
	}

	// public String getEmailSubject( Invoice invoice ) {
	// String key = invoice.isSigned() ? FINANCE_EINVOICE_EMAIL_SUBJECT :
	// FINANCE_INVOICE_EMAIL_SUBJECT;
	// String message = AonUtil.getMessage(BUNDLE_KEY, key);
	// return MessageFormat.format(message, invoice.getReferenceCode() );
	// }

	public String getEmailBody(String text) throws UnsupportedEncodingException {
		StringBuffer body = new StringBuffer();
		body.append("<html><head>");
		body
				.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		body.append("</head><body>");

		body.append(AonUtil.getMessage(ICompanyConstants.BUNDLE_NAME,
				ICompanyConstants.COMPANY_EMAIL_BODY_HEADER));
		body.append(text);
		body.append(AonUtil.getMessage(ICompanyConstants.BUNDLE_NAME,
				ICompanyConstants.COMPANY_EMAIL_BODY_FOOTER));

		CompanyController companyController = (CompanyController) AonUtil
				.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		body.append(getCompany().getName()).append("<br/>");
		RegistryMedia phone = companyController.getPhone();
		if (phone != null) {
			String phoneLabel = AonUtil.getMessage("registryBundle",
					"registry_phone");
			body.append(StringEscapeUtils.escapeHtml(phoneLabel));
			body.append(": ").append(phone.getValue()).append("<br/>");
		}
		RegistryMedia fax = companyController.getFax();
		if (fax != null) {
			String faxLabel = AonUtil.getMessage("registryBundle",
					"registry_fax");
			body.append(faxLabel).append(": ").append(fax.getValue()).append(
					"<br/>");
		}
		RegistryMedia web = companyController.getWeb();
		if (web != null) {
			body.append("<a href=\"").append(web.getValue()).append("\">")
					.append(web.getValue()).append("</a>");
		}
		body.append("</body></html>");
		return body.toString();
	}

	public Company getCompany() {
		CompanyController companyController = (CompanyController) AonUtil
				.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		return companyController.obtainCompany();
	}
}