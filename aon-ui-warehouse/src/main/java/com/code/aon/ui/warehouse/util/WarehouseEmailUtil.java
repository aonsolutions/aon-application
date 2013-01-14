package com.code.aon.ui.warehouse.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.report.ReportException;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.IWarehouseMessages;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.warehouse.Delivery;

public class WarehouseEmailUtil extends CompanyEmailUtil implements IWarehouseMessages {
	
	public void initMessageController( MessageController messageController, Delivery delivery, String reportKey ) throws ManagerBeanException, IOException, ReportException {
		String[] emails = getAdministrativeEmails( delivery.getCustomer().getRegistry() );
		initMessageController(messageController, emails, getEmailBody(delivery));
		messageController.setSubject( getEmailSubject(delivery) );
		messageController.addAttachment( getReport(delivery, reportKey) );
	}
	
	public String getEmailSubject( Delivery delivery ) {
		String message = AonUtil.getMessage(BUNDLE_KEY, WAREHOUSE_DELIVERY_EMAIL_SUBJECT);
		return MessageFormat.format(message, delivery.getReferenceCode() );
	}
	
	public String getEmailBody( Delivery delivery ) throws UnsupportedEncodingException {
		String bodyMessage = AonUtil.getMessage(BUNDLE_KEY, WAREHOUSE_DELIVERY_EMAIL_BODY); 
		return MessageFormat.format(bodyMessage, delivery.getReferenceCode(), delivery.getIssueTime() );
	}

}
