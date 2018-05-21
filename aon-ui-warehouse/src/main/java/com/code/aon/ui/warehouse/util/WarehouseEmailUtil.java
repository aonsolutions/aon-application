package com.code.aon.ui.warehouse.util;

import static com.code.aon.ui.common.ICommonMessages.WAREHOUSE_DELIVERY_EMAIL_BODY;
import static com.code.aon.ui.common.ICommonMessages.WAREHOUSE_DELIVERY_EMAIL_SUBJECT;

import static com.code.aon.ui.common.ICommonMessages.WAREHOUSE_INCOME_EMAIL_BODY;
import static com.code.aon.ui.common.ICommonMessages.WAREHOUSE_INCOME_EMAIL_SUBJECT;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.enumeration.MailProcessType;
import com.code.aon.report.ReportException;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.Income;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailTemplate;

public class WarehouseEmailUtil extends CompanyEmailUtil {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	// -------------------- DELIVERY EMAIL UTILS
	
	public void initMessageController( MessageController messageController, Delivery delivery, String reportKey ) throws ManagerBeanException, IOException, ReportException {
		String[] emails = getAdministrativeEmails(delivery.getCustomer().getRegistry());
		initMessageController(messageController, emails);
		messageController.setGenericMessage(false);
		messageController.setTemplates(getTemplates(MailProcessType.DELIVERY));
		if(!messageController.initMessageController(getDomain(delivery.getDomain()), "", getMap(delivery), "AON_MAIL_PROCESS_"+ MailProcessType.DELIVERY.ordinal() + "_1")) {
			initMessageController(messageController, emails, getEmailBody(delivery));
		}
		if(messageController.getSubject() == null || messageController.getSubject().isEmpty()) {
			messageController.setSubject(getEmailSubject(delivery));
		}
		messageController.addAttachment(getReport(delivery, reportKey));
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
	
	private Map<String,String> getMap(Delivery delivery) {
		Map<String,String> map = new HashMap<String,String>();		
		
		map.put("comentarios", delivery.getComments());
		map.put("comments", delivery.getComments());
		
		map.put("nombre_cliente", delivery.getCustomer().getRegistry().getName());
		map.put("customer_name", delivery.getCustomer().getRegistry().getName());
		
		map.put("documento_cliente", delivery.getCustomer().getRegistry().getDocument());
		map.put("customer_document", delivery.getCustomer().getRegistry().getDocument());

		map.put("estado", delivery.getStatus().getName(AonUtil.getCurrentLocale()));
		map.put("status", delivery.getStatus().getName(AonUtil.getCurrentLocale()));

		map.put("referencia", delivery.getReferenceCode());
		map.put("reference", delivery.getReferenceCode());
		
		map.put("numero_rastreo", delivery.getTrackingNumber());
		map.put("tracking_number", delivery.getTrackingNumber());
		
		map.put("serie", delivery.getSeries());
		
		map.put("numero", Integer.toString(delivery.getNumber()));
		map.put("number", Integer.toString(delivery.getNumber()));
				
		return map;
	}
	
	public String getEmailSubject(Delivery delivery) {
		return AonUtil.getMessage(WAREHOUSE_DELIVERY_EMAIL_SUBJECT, delivery.getReferenceCode());
	}
	
	public String getEmailBody(Delivery delivery) {
		return AonUtil.getMessage(WAREHOUSE_DELIVERY_EMAIL_BODY, delivery.getReferenceCode(), delivery.getIssueTime()); 
	}
	
	// -------------------- INCOME EMAIL UTILS
	
	public void initMessageController( MessageController messageController, Income income, String reportKey ) throws ManagerBeanException, IOException, ReportException {
		String[] emails = getAdministrativeEmails(income.getSupplier().getRegistry() );
		initMessageController(messageController, emails);
		messageController.setGenericMessage(false);
		messageController.setTemplates(getTemplates(MailProcessType.DELIVERY));
		if(!messageController.initMessageController(getDomain(income.getDomain()), "", getMap(income), "AON_MAIL_PROCESS_" + MailProcessType.DELIVERY.ordinal() + "_1")) {
			initMessageController(messageController, emails, getEmailBody(income));
		}
		if(messageController.getSubject() == null || messageController.getSubject().isEmpty()) {
			messageController.setSubject(getEmailSubject(income));
		}
		messageController.addAttachment( getReport(income, reportKey) );
	}
	
	private Map<String,String> getMap(Income income) {
		Map<String,String> map = new HashMap<String,String>();		
		
		map.put("comentarios", income.getComments());
		map.put("comments", income.getComments());
		
		map.put("nombre_proveedor", income.getSupplier().getRegistry().getName());
		map.put("supplier_name", income.getSupplier().getRegistry().getName());
		
		map.put("documento_proveedor", income.getSupplier().getRegistry().getDocument());
		map.put("supplier_document", income.getSupplier().getRegistry().getDocument());

		map.put("estado", income.getStatus().getName(AonUtil.getCurrentLocale()));
		map.put("status", income.getStatus().getName(AonUtil.getCurrentLocale()));

		map.put("referencia", income.getReferenceCode());
		map.put("reference", income.getReferenceCode());
		
		map.put("serie", income.getSeries());
		
		map.put("numero", Integer.toString(income.getNumber()));
		map.put("number", Integer.toString(income.getNumber()));
		
		return map;
	}
	
	public String getEmailSubject(Income income) {
		return AonUtil.getMessage(WAREHOUSE_INCOME_EMAIL_SUBJECT, income.getReferenceCode());
	}
	
	public String getEmailBody(Income income) {
		return AonUtil.getMessage(WAREHOUSE_INCOME_EMAIL_BODY, income.getReferenceCode(), income.getIssueTime()); 
	}

	private Domain getDomain(Integer domainId) {
		return AON.getDomain(AonUtil.getDomainName(), domainId, "");
	}
}
