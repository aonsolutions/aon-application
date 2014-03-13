package com.code.aon.ui.warehouse.util;

import static com.code.aon.ui.common.ICommonMessages.WAREHOUSE_DELIVERY_EMAIL_BODY;
import static com.code.aon.ui.common.ICommonMessages.WAREHOUSE_DELIVERY_EMAIL_SUBJECT;

import java.io.IOException;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.report.ReportException;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.warehouse.Delivery;

public class WarehouseEmailUtil extends CompanyEmailUtil {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public void initMessageController( MessageController messageController, Delivery delivery, String reportKey ) throws ManagerBeanException, IOException, ReportException {
		String[] emails = getAdministrativeEmails( delivery.getCustomer().getRegistry() );
		initMessageController(messageController, emails, getEmailBody(delivery));
		messageController.setSubject( getEmailSubject(delivery) );
		messageController.addAttachment( getReport(delivery, reportKey) );
	}
	
	public String getEmailSubject( Delivery delivery ) {
		return AonUtil.getMessage(WAREHOUSE_DELIVERY_EMAIL_SUBJECT, delivery.getReferenceCode());
	}
	
	public String getEmailBody( Delivery delivery ) {
		return AonUtil.getMessage(WAREHOUSE_DELIVERY_EMAIL_BODY, delivery.getReferenceCode(), delivery.getIssueTime()); 
	}

}
