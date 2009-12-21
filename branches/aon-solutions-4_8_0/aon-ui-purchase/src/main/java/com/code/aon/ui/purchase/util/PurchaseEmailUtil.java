package com.code.aon.ui.purchase.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.purchase.Purchase;
import com.code.aon.report.ReportException;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.purchase.IPurchaseMessages;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;

public class PurchaseEmailUtil extends CompanyEmailUtil implements IPurchaseMessages {

	private static final String REPORT_KEY = "purchase";

	public void initMessageController( MessageController messageController, Purchase purchase ) throws ManagerBeanException, IOException, ReportException {
		String[] emails = getEmails( purchase.getSupplier().getRegistry() );
		initMessageController(messageController, emails, getEmailBody(purchase));
		messageController.setSubject( getEmailSubject(purchase) );
		messageController.addAttachment( getReport(purchase, REPORT_KEY) );
	}
	
	public String getEmailSubject( Purchase purchase ) {
		String message = AonUtil.getMessage(BUNDLE_KEY, PURCHASE_EMAIL_SUBJECT);
		return MessageFormat.format(message, purchase.getReferenceCode() );
	}
	
	public String getEmailBody( Purchase purchase ) throws UnsupportedEncodingException {
		String bodyMessage = AonUtil.getMessage(BUNDLE_KEY, PURCHASE_EMAIL_BODY); 
		return MessageFormat.format(bodyMessage, purchase.getReferenceCode(), purchase.getIssueDate() );
	}

}
