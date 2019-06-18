package com.code.aon.ui.sales.util;

import static com.code.aon.ui.common.ICommonMessages.SALES_EMAIL_BODY;
import static com.code.aon.ui.common.ICommonMessages.SALES_EMAIL_SUBJECT;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.report.ReportException;
import com.code.aon.sales.Sales;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.sales.ISalesMessages;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.MailProcessType;
import com.esferalia.aon.watson.server.AonDateUtils;

public class SalesEmailUtil extends CompanyEmailUtil implements ISalesMessages {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final String REPORT_KEY = "sales";

	public void initMessageController( MessageController messageController, Sales sales ) throws ManagerBeanException, IOException, ReportException {
		String[] emails = getAdministrativeEmails( sales.getCustomer().getRegistry() );
		initMessageController(messageController, emails);
		messageController.setGenericMessage(false);
		messageController.setMailProccessType(MailProcessType.ORDER);
		if(!messageController.initMessageController(getDomain(sales.getDomain()), "", getMap(sales), "AON_MAIL_PROCESS_"+ MailProcessType.ORDER.ordinal() + "_1")) {
			initMessageController(messageController, emails, getEmailBody(sales));
		}
		if(messageController.getSubject() == null || messageController.getSubject().isEmpty()) {
			messageController.setSubject(getEmailSubject(sales));
		}		
		
		messageController.addAttachment( getReport(sales, REPORT_KEY) );
	}
	
	private Map<String,String> getMap(Sales sales) {
		Map<String,String> map = new HashMap<String,String>();		
		map.put("comentarios", sales.getComments());
		map.put("comments", sales.getComments());
		
		map.put("fecha", AonDateUtils.simpleFormat(sales.getDate()));
		map.put("date", AonDateUtils.simpleFormat(sales.getDate()));
		
		map.put("fecha_entrega", AonDateUtils.simpleFormat(sales.getDeliveryDate()));
		map.put("delivery_date", AonDateUtils.simpleFormat(sales.getDeliveryDate()));

		map.put("fecha_pedido", AonDateUtils.simpleFormat(sales.getIssueDate()));
		map.put("issue_date", AonDateUtils.simpleFormat(sales.getIssueDate()));
		
		map.put("serie", sales.getSeries());
		
		map.put("numero", Integer.toString(sales.getNumber()));
		map.put("number", Integer.toString(sales.getNumber()));

		map.put("estado", sales.getStatus().getName(AonUtil.getCurrentLocale()));
		map.put("status", sales.getStatus().getName(AonUtil.getCurrentLocale()));
			
		map.put("nombre_cliente", sales.getCustomer().getRegistry().getName());
		map.put("customer_name", sales.getCustomer().getRegistry().getName());
		
		map.put("documento_cliente", sales.getCustomer().getRegistry().getDocument());
		map.put("customer_document", sales.getCustomer().getRegistry().getDocument());
		
		map.put("referencia", sales.getReferenceCode());
		map.put("reference", sales.getReferenceCode());
		return map;
	}
	
	public String getEmailSubject( Sales sales ) {
		String message = AonUtil.getMessage(SALES_EMAIL_SUBJECT);
		return MessageFormat.format(message, sales.getReferenceCode() );
	}
	
	public String getEmailBody( Sales sales ) throws UnsupportedEncodingException {
		String bodyMessage = AonUtil.getMessage(SALES_EMAIL_BODY); 
		return MessageFormat.format(bodyMessage, sales.getReferenceCode(), sales.getIssueDate() );
	}

	private Domain getDomain(Integer domainId) {
		return AON.getDomain(AonUtil.getDomainName(), domainId, "");
	}
}
