package com.code.aon.ui.sales.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.report.ReportException;
import com.code.aon.sales.Sales;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.sales.ISalesMessages;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;

public class SalesEmailUtil extends CompanyEmailUtil implements ISalesMessages {

	private static final String REPORT_KEY = "sales";

	public void initMessageController( MessageController messageController, Sales sales ) throws ManagerBeanException, IOException, ReportException {
		String[] emails = getEmails( sales.getCustomer().getRegistry() );
		initMessageController(messageController, emails, getEmailBody(sales));
		messageController.setSubject( getEmailSubject(sales) );
		messageController.addAttachment( getReport(sales, REPORT_KEY) );
	}
	
	public String getEmailSubject( Sales sales ) {
		String message = AonUtil.getMessage(BUNDLE_KEY, SALES_EMAIL_SUBJECT);
		return MessageFormat.format(message, sales.getReferenceCode() );
	}
	
	public String getEmailBody( Sales sales ) throws UnsupportedEncodingException {
		String bodyMessage = AonUtil.getMessage(BUNDLE_KEY, SALES_EMAIL_BODY); 
		return MessageFormat.format(bodyMessage, sales.getReferenceCode(), sales.getIssueDate() );
	}

}
