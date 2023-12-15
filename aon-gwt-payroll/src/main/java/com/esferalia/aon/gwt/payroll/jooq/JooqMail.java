package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;
import static com.esferalia.aon.jooq.tables.MailAccount.MAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.MailAccountType;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

public class JooqMail {

	private static Settings SETTINGS = null;
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	// ---------------------------------------------- Mail accounts
	
	public static List<MailAccount> getMailAccounts(Connection connection, Integer userId, Integer domainId, Integer parentDomainId) {
		return getMailAccountsDB(DSL.using(connection, getDefaultSettings()), userId, domainId, parentDomainId);
	}
	
	private static List<MailAccount> getMailAccountsDB(DSLContext dslContext, Integer userId, Integer domainId, Integer parentDomainId) {
		List<MailAccount> mailAccounts = new LinkedList<MailAccount>();
		List<String> visitedEmails = new LinkedList<String>();
		
		Record userRecord = dslContext.select().from(USER).where(USER.ID.eq(userId)).fetchOne();
		
		Integer userDomain = userRecord.get(USER.DOMAIN);
		
		Result<Record> mailAccountRecords = dslContext.select().from(MAIL_ACCOUNT)
				.where(MAIL_ACCOUNT.USER_ID.eq(userId).and(MAIL_ACCOUNT.DOMAIN.eq(domainId).or(MAIL_ACCOUNT.DOMAIN.eq(parentDomainId))))
				.or(MAIL_ACCOUNT.DOMAIN.eq(userDomain).and(MAIL_ACCOUNT.USER_ID.isNull()))
				.or(MAIL_ACCOUNT.DOMAIN.eq(domainId).and(MAIL_ACCOUNT.USER_ID.isNull()))
				.orderBy(MAIL_ACCOUNT.USER_ID.desc(), MAIL_ACCOUNT.DOMAIN.desc())
				.fetch();
		
		for(Record r : mailAccountRecords) {
			if(visitedEmails.contains(r.get(MAIL_ACCOUNT.EMAIL))) continue;
			
			MailAccount mailAccount = new MailAccount();
			
			mailAccount.setId(r.get(MAIL_ACCOUNT.ID));
			mailAccount.setName(r.get(MAIL_ACCOUNT.NAME));
			mailAccount.setEmail(r.get(MAIL_ACCOUNT.EMAIL));
			mailAccount.setReplytoMail(r.get(MAIL_ACCOUNT.REPLYTO_MAIL));
			mailAccount.setIncomingHost(r.get(MAIL_ACCOUNT.INCOMING_HOST));
			mailAccount.setProtocol(r.get(MAIL_ACCOUNT.PROTOCOL));
			mailAccount.setIncomingPort(r.get(MAIL_ACCOUNT.INCOMING_PORT));
			mailAccount.setIncomingSecurity(r.get(MAIL_ACCOUNT.INCOMING_SECURITY));
			mailAccount.setOutgoingVerification(r.get(MAIL_ACCOUNT.OUTGOING_VERIFICATION));
			mailAccount.setOutgoingHost(r.get(MAIL_ACCOUNT.OUTGOING_HOST));
			mailAccount.setOutgoingPort(r.get(MAIL_ACCOUNT.OUTGOING_PORT));
			mailAccount.setOutgoingSecurity(r.get(MAIL_ACCOUNT.OUTGOING_SECURITY));
			mailAccount.setMailUsername(r.get(MAIL_ACCOUNT.MAIL_USERNAME));
			mailAccount.setPassword(r.get(MAIL_ACCOUNT.PASSWORD));
			mailAccount.setDefaultAccount(r.get(MAIL_ACCOUNT.DEFAULT_ACCOUNT));
			mailAccount.setDraftFolder(r.get(MAIL_ACCOUNT.DRAFT_FOLDER));
			mailAccount.setSentFolder(r.get(MAIL_ACCOUNT.SENT_FOLDER));
			mailAccount.setTrashFolder(r.get(MAIL_ACCOUNT.TRASH_FOLDER));
			mailAccount.setSpamFolder(r.get(MAIL_ACCOUNT.SPAM_FOLDER));
			mailAccount.setDisplayName(r.get(MAIL_ACCOUNT.DISPLAY_NAME));
			mailAccount.setSignatureId(r.get(MAIL_ACCOUNT.SIGNATURE));
			mailAccount.setUserId(r.get(MAIL_ACCOUNT.USER_ID));
			mailAccount.setType(MailAccountType.safeValueOf(r.get(MAIL_ACCOUNT.TYPE)));
			
			mailAccounts.add(mailAccount);
			
			visitedEmails.add(r.get(MAIL_ACCOUNT.EMAIL));
		}
		
		return mailAccounts;
	}
	
	// ---------------------------------------------- SendTo
	
	public static String getPayrollEmailSendTo(Connection connection, Integer domainId) {
		return getPayrollEmailSendToDB(DSL.using(connection, getDefaultSettings()), domainId);
	}
	
	private static String getPayrollEmailSendToDB(DSLContext dslContext, Integer domainId) {
		String sendTo = "";
		
		Record enterpriseEmailRecord = dslContext.select().from(ENTERPRISE_DATA)
				.where(ENTERPRISE_DATA.ENTERPRISE.eq(
						dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE)
							.where(ENTERPRISE.DOMAIN.eq(domainId))
							.fetchOne(ENTERPRISE.REGISTRY)
				).and(ENTERPRISE_DATA.NAME.eq("PAY_salarySending_email_PAY")))
				.fetchOne();
		
		if(null != enterpriseEmailRecord)
			sendTo = enterpriseEmailRecord.get(ENTERPRISE_DATA.EXPRESSION);
		
		return sendTo;
	}
	
	// ---------------------------------------------- Email body
	
	public static String getPayrollEmailBody(Connection connection, Type type, HashMap<String, String> params) {
		return getPayrollEmailBodyDB(DSL.using(connection, getDefaultSettings()), type, params);
	}
	
	private static String getPayrollEmailBodyDB(DSLContext dslContext, Type type, HashMap<String, String> params) {	
		switch (type) {
		case ENTERPRISE:
			return getEnterpriseBody(dslContext, params);
		case EMPLOYEE:
			return getEmployeeBody();
		case ENTERPRISE_MANAGEMENT:
			return getEnterpriseManagementBody();
		default:
			return "";
		}	
	}
	
	private static String getEnterpriseBody(DSLContext dslContext, HashMap<String, String> params) {
		String html = "";
		
		html += "<div style=\"font-family: \"Arial\"; font-size: small; letter-spacing: 2px; word-spacing: 0px; color: #000000; font-weight: normal; text-decoration: none; font-style: normal; font-variant: normal; text-transform: none;\">";
		html += "<p>Estimado cliente:</p>";
		html += "<p>Le adjuntamos las n&oacute;minas de la empresa <a style=\"font-weight: bold;\">" + getEnterpriseName(dslContext, Integer.valueOf(params.get("enterprise"))) +"</a> que corresponden a los siguientes trabajadores:</p>";
		html += "<ul>";
		
		for(Entry<String, String> entry : params.entrySet()) {
			
			Pattern salaryIdPattern = Pattern.compile("^id\\d*$", Pattern.CASE_INSENSITIVE);
			Matcher matcher = salaryIdPattern.matcher(AonStringUtils.trimToEmpty(entry.getKey()));
			
			if(matcher.matches()) {				
				html +=	getSalaryItem(dslContext, Integer.parseInt(entry.getValue()));
			}
		}

		html += "</ul>";
		html += "<p>Para descargar y visualizar el documento adjunto, por favor haga click en el siguiente enlace:</p>";
		
		html += generateForm(params);

		html += "<p>Este archivo est&aacute; en formato PDF Adobe y se puede leer usando Acrobat Reader. Si no tiene instalado el Acrobat Reader pulse aqu&iacute; para conseguir su copia gratuita: http://get.adobe.com/es/reader. Para cualquier aclaraci&oacute;n sobre el documento adjunto p&oacute;ngase en contacto con nosotros.</p>";
		html += " <a style=\"font-weight: bold;\">" + getEnterpriseInfo(dslContext, Integer.parseInt(params.get("enterprise"))) + "</a>";
		html += "</div>";
		
		return html;
	}
	
	private static String getEmployeeBody() {
		String html = "";
		
		html += "<div style=\"font-family: \"Lucida Sans Unicode\", \"Lucida Grande\", sans-serif;font-size: 12px;letter-spacing: 2px;word-spacing: 0px;color: #000000;font-weight: normal;text-decoration: none;font-style: normal;font-variant: normal;text-transform: none;\">";
		html += 	"<p>Estimado/a <a style=\"font-weight: bold;\">NOMBRE_EMPLEADO</a> :</p>";
		html += 	"<p>Le adjuntamos la(s) n&oacute;mina(s) que corresponden a los siguientes periodos :</p>";
		html += 	"<ul>";
		
		html += 		"<li style=\"font-weight: bold;\">PERIODOS_NOMINA</li>";

		html += 	"</ul>";
		html += 	"<p>Para descargar y visualizar el documento adjunto, por favor haga click en el siguiente enlace:</p>";
		
		html += 	"<div id=\"form\">";
		html +=			"<a type=\"button\" href=\"URL_DOWNLOAD\" style=\"text-decoration:none;padding:5px;text-align:center;color: #153643;\">";
		html +=				"<img src=\"http://simpleicon.com/wp-content/uploads/cloud-download-2.png\" style=\"width:20px;vertical-align: middle;\" />";
		html +=				"<b style=\"color: black;padding-left: 4px;font-size: x-small;\">DESCARGAR NOMINAS</b>";
		html +=			"</a>";
		html += 	"</div>";

		html += 	"<p>Este archivo est&aacute; en formato PDF Adobe y se puede leer usando Acrobat Reader. Si no tiene instalado el Acrobat Reader pulse aqu&iacute; para conseguir su copia gratuita: http://get.adobe.com/es/reader. Para cualquier aclaraci&oacute;n sobre el documento adjunto p&oacute;ngase en contacto con nosotros.</p>";
		html += 	"<a style=\"font-weight: bold;\">INFORMACION_EMPRESA</a>";
		html += "</div>";
		
		return html;
	}
	
	private static String getEnterpriseManagementBody() {
		String html = "";
		
		html += "<div style=\"font-family: \"Lucida Sans Unicode\", \"Lucida Grande\", sans-serif;font-size: 12px;letter-spacing: 2px;word-spacing: 0px;color: #000000;font-weight: normal;text-decoration: none;font-style: normal;font-variant: normal;text-transform: none;\">";
		html += "<p>Estimado cliente:</p>";
		html += "<p>Le adjuntamos las n&oacute;minas de la empresa <a style=\"font-weight: bold;\">NOMBRE_EMPRESA</a> que corresponden a los siguientes trabajadores:</p>";
		html += 	"<ul>";
		
		html += 		"<li style=\"font-weight: bold;\">NOMINA_TRABAJDORES</li>";

		html += 	"</ul>";
		html += 	"<p>Para descargar y visualizar el documento adjunto, por favor haga click en el siguiente enlace:</p>";
		
		html += 	"<div id=\"form\">";
		html +=			"<a type=\"button\" href=\"URL_DOWNLOAD\" style=\"text-decoration:none;padding:5px;text-align:center;color: #153643;\">";
		html +=				"<img src=\"http://simpleicon.com/wp-content/uploads/cloud-download-2.png\" style=\"width:20px;vertical-align: middle;\" />";
		html +=				"<b style=\"color: black;padding-left: 4px;font-size: x-small;\">DESCARGAR NOMINAS</b>";
		html +=			"</a>";
		html += 	"</div>";

		html += 	"<p>Este archivo est&aacute; en formato PDF Adobe y se puede leer usando Acrobat Reader. Si no tiene instalado el Acrobat Reader pulse aqu&iacute; para conseguir su copia gratuita: http://get.adobe.com/es/reader. Para cualquier aclaraci&oacute;n sobre el documento adjunto p&oacute;ngase en contacto con nosotros.</p>";
		html += 	"<a style=\"font-weight: bold;\">INFORMACION_EMPRESA</a>";
		html += "</div>";
		
		return html;
	}
	
	
	// ---------------------------------------------- Check employees emails
	
	public static String checkEmployeesEmails(Connection connection, ArrayList<Integer> salaryIds) {
		return checkEmployeesEmailsDB(DSL.using(connection, getDefaultSettings()), salaryIds);
	}
	
	private static String checkEmployeesEmailsDB(DSLContext dslContext, ArrayList<Integer> salaryIds) {
		ArrayList<Integer> visitedContracts = new ArrayList<Integer>();
		String message = "";
		
		for(Integer salaryId : salaryIds) {
			Record salaryRecord = dslContext.select().from(SALARY)
					.where(SALARY.ID.eq(salaryId))
					.fetchOne();
			
			Integer contractId = null == salaryRecord ? null : salaryRecord.get(SALARY.CONTRACT);
			
			if(null != contractId && !visitedContracts.contains(contractId)) {
				Record rMediaEmailRecord = dslContext.select().from(RMEDIA)
						.where(RMEDIA.MEDIA.eq((byte)4))
						.and(RMEDIA.REGISTRY.eq(
								dslContext.select(CONTRACT.PERSON).from(CONTRACT)
									.where(CONTRACT.ID.eq(contractId))
								)
						).fetchOne();
				
				if(null == rMediaEmailRecord || rMediaEmailRecord.get(RMEDIA.VALUE).length() == 0) {
					message += "<p>" + salaryRecord.get(SALARY.EMPLOYEE_NAME) + " no tiene email definido, reviselo en su perfil. </p>";
				}
				
				visitedContracts.add(contractId);
			}
		}
		
		return message;
	}
	
	// ---------------------------------------------- Check enterprises emails
	
	public static String checkEnterprisesEmails(Connection connection, HashSet<Integer> enterpriseIds) {
		return checkEnterprisesEmailsDB(DSL.using(connection, getDefaultSettings()), enterpriseIds);
	}
	
	private static String checkEnterprisesEmailsDB(DSLContext dslContext, HashSet<Integer> enterpriseIds) {
		String message = "";
		
		for(Integer enterpriseId : enterpriseIds) {
			Record enterpriseEmailRecord = dslContext.select().from(ENTERPRISE_DATA)
					.where(ENTERPRISE_DATA.ENTERPRISE.eq(enterpriseId)
					.and(ENTERPRISE_DATA.NAME.eq("PAY_salarySending_email_PAY")))
					.fetchOne();
			
			Record enterpriseRecord = dslContext.select().from(REGISTRY)
					.where(REGISTRY.ID.eq(enterpriseId))
					.fetchOne();
			
			if(null == enterpriseEmailRecord || AonStringUtils.isBlank(enterpriseEmailRecord.get(ENTERPRISE_DATA.EXPRESSION)))
				message += "<p>" + enterpriseRecord.get(REGISTRY.NAME) + " no tiene email definido, reviselo en su perfil. </p>";
		}
		
		return message;
	}
	
	// ---------------------------------------------- Send Email
	
	public static String sendPayrollEmail(Connection connection, Integer domainId, Type type,  HashMap<String, String> params, String from, String to, String cc, String cco, String bodyHTML) throws IllegalArgumentException {
		return sendPayrollEmailDB(DSL.using(connection, getDefaultSettings()), domainId, type, params, from, to, cc, cco, bodyHTML);
	}
	
	private static String sendPayrollEmailDB(DSLContext dslContext, Integer domainId, Type type, HashMap<String, String> params, String from, String to, String cc, String cco, String bodyHTML) throws IllegalArgumentException {
		String message = "";
		String enterpriseName = dslContext.select(REGISTRY.NAME).from(REGISTRY)
				.where(REGISTRY.ID.eq(
						dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domainId)).fetchOne(ENTERPRISE.REGISTRY)
				)).fetchOne(REGISTRY.NAME);
		
		switch (type) {
		case EMPLOYEE:
			message = sendEmployeesEmail(dslContext, params, from, enterpriseName, to, cc, cco, bodyHTML);
			break;
		case ENTERPRISE:
			message = sendEmail(dslContext, from, enterpriseName, to, cc, cco, bodyHTML);
			break;
		case ENTERPRISE_MANAGEMENT:
			message = sendEnterpriseManagementEmail(dslContext, params, from, to, cc, cco, bodyHTML);
			break;
		default:
			break;
		}
		
		return message;
	}
	
	private static String sendEnterpriseManagementEmail(DSLContext dslContext, HashMap<String, String> params, String mailAccountId, String to, String cc, String cco, String bodyHTML) throws IllegalArgumentException {
		ArrayList<Integer> salaryIds = getSalaryIds(params);
		HashSet<Integer> enterpriseIds = getEnterpriseIds(params);
		
		for(Integer enterpriseId : enterpriseIds) {
			Integer domainId = dslContext.select(ENTERPRISE.DOMAIN).from(ENTERPRISE).where(ENTERPRISE.REGISTRY.eq(enterpriseId)).fetchOne(ENTERPRISE.DOMAIN);
			
			Record enterpriseEmailRecord = dslContext.select().from(ENTERPRISE_DATA)
					.where(ENTERPRISE_DATA.ENTERPRISE.eq(enterpriseId)
					.and(ENTERPRISE_DATA.NAME.eq("PAY_salarySending_email_PAY")))
					.fetchOne();
			
			Record enterpriseRecord = dslContext.select().from(REGISTRY)
					.where(REGISTRY.ID.eq(enterpriseId))
					.fetchOne();
			
			String emailTo = enterpriseEmailRecord.get(ENTERPRISE_DATA.EXPRESSION);
			String enterpriseName = enterpriseRecord.get(REGISTRY.NAME);
			
			Result<Record> salariesRecords = dslContext.select().from(SALARY)
					.where(SALARY.DOMAIN.eq(domainId))
					.and(SALARY.ID.in(salaryIds))
					.fetch();
			
			String parseHTMLBody = parseEnterpriseManagementHTMLBody(bodyHTML, salariesRecords, params, enterpriseId, enterpriseName, dslContext);
			
			if(parseHTMLBody.length() == 0)
				throw new IllegalArgumentException("No se ha encontrado la variable NOMBRE_EMPRESA, NOMINA_TRABAJDORES y/o INFORMACION_EMPRESA");
			
			sendEmail(dslContext, mailAccountId, enterpriseName, emailTo, cc, cco, parseHTMLBody);
		}
		
		return "Email(s) enviado(s) correctamente.";
	}

	private static String sendEmployeesEmail(DSLContext dslContext, HashMap<String, String> params, String mailAccountId, String enterpriseName, String to, String cc, String cco, String bodyHTML) throws IllegalArgumentException {
		ArrayList<Integer> salaryIds = getSalaryIds(params);
		ArrayList<Integer> visitedContracts = new ArrayList<Integer>();
		
		for(Integer salaryId : salaryIds) {
			Record salaryRecord = dslContext.select().from(SALARY)
					.where(SALARY.ID.eq(salaryId))
					.fetchOne();
			
			Integer contractId = salaryRecord.get(SALARY.CONTRACT);
			
			if(!visitedContracts.contains(contractId)) {
				Record rMediaEmailRecord = dslContext.select().from(RMEDIA)
						.where(RMEDIA.MEDIA.eq((byte)4))
						.and(RMEDIA.REGISTRY.eq(
								dslContext.select(CONTRACT.PERSON).from(CONTRACT)
									.where(CONTRACT.ID.eq(contractId))
								)
						).fetchOne();
				
				String emailTo = rMediaEmailRecord.get(RMEDIA.VALUE);
				
				Result<Record> salariesRecords = dslContext.select().from(SALARY).where(SALARY.CONTRACT.eq(contractId)).and(SALARY.ID.in(salaryIds)).fetch();
				
				String parseHTMLBody = parseHTMLBody(bodyHTML, salariesRecords, params, dslContext);
				
				if(parseHTMLBody.length() == 0)
					throw new IllegalArgumentException("No se ha encontrado la variable NOMBRE_EMPLEADO, PERIODOS_NOMINA y/o INFORMACION_EMPRESA");
				
				sendEmail(dslContext, mailAccountId, enterpriseName, emailTo, cc, cco, parseHTMLBody);
				
				visitedContracts.add(contractId);
			}
		}
		
		
		return "Email(s) enviado(s) correctamente.";
	}

	private static String sendEmail(DSLContext dslContext, String mailAccountId, String enterpriseName, String to, String cc, String cco, String bodyHTML) throws IllegalArgumentException {
		Record mailAccountRecord = dslContext.select().from(MAIL_ACCOUNT)
				.where(MAIL_ACCOUNT.ID.eq(Integer.parseInt(mailAccountId))).fetchOne();
		
		if(null != mailAccountRecord) {
			String from = mailAccountRecord.get(MAIL_ACCOUNT.EMAIL);
			
			SESMessage msg = new SESMessage()
					.setAlias(enterpriseName)
					.setReplyTo(from)
					.setTo(to)
					.setSubject("N\u00d3MINAS " + enterpriseName)
					.setBody(bodyHTML);
			if(AonStringUtils.isNotBlank(cc)) msg.setBcc(cc);
			SES.sendEmail(msg);
			
		} else {
			throw new IllegalArgumentException("No existe cuenta de correo desde la que enviar este mensaje.");
		}
	    
	    return "Email enviado correctamente.";
	}
	
	// ---------------------------------------------- Send Email (Auxiliar methods)
	
	private static String parseEnterpriseManagementHTMLBody(String bodyHTML, Result<Record> salariesRecords, HashMap<String, String> params, Integer enterpriseId, String enterpriseName, DSLContext dslContext) {
		
		// GENERATE URL
		params.put("enterprise", enterpriseId.toString());
		String formHTML = generateFormEmployee(params, salariesRecords);
		
		String enterpriseNames = "<a style=\"font-weight: bold;\">NOMBRE_EMPRESA</a>";
		String payrollPeriods = "<li style=\"font-weight: bold;\">NOMINA_TRABAJDORES</li>";
		String enterpriseInfo = "<a style=\"font-weight: bold;\">INFORMACION_EMPRESA</a>";
		
		//GENERATE BODY HTML
		String[] aux = bodyHTML.split(enterpriseNames);
		
		if(aux.length != 2) 
			return "";
		
		String html = bodyHTML.split(enterpriseNames)[0] +  "<a style=\"font-weight: bold;\">" + enterpriseName + "</a>" + bodyHTML.split(enterpriseNames)[1];
		
		if(html.split(payrollPeriods).length != 2) 
			return "";
		
		html = html.split(payrollPeriods)[0] + createPeriodsEnterprise(dslContext, salariesRecords) + html.split(payrollPeriods)[1];
		
		html = html.split("<div id=\"form\">")[0] + formHTML + html.split("<div id=\"form\">")[1].split("</div>")[1];
		
		html = html.split(enterpriseInfo)[0] +  "<a style=\"font-weight: bold;\">" + getEnterpriseInfo(dslContext, enterpriseId) + "</a>";
		
		return html;
	}
	
	private static String parseHTMLBody(String bodyHTML, Result<Record> salariesRecords, HashMap<String, String> params, DSLContext dslContext) {
		
		// GENERATE URL
		String formHTML = generateFormEmployee(params, salariesRecords);
		
		String employeeName = "<a style=\"font-weight: bold;\">NOMBRE_EMPLEADO</a>";
		String payrollPeriods = "<li style=\"font-weight: bold;\">PERIODOS_NOMINA</li>";
		String enterpriseInfo = "<a style=\"font-weight: bold;\">INFORMACION_EMPRESA</a>";
		
		//GENERATE BODY HTML
		String[] aux = bodyHTML.split(employeeName);
		
		if(aux.length != 2) 
			return "";
		
		String html = bodyHTML.split(employeeName)[0] +  "<a style=\"font-weight: bold;\">" + salariesRecords.get(0).get(SALARY.EMPLOYEE_NAME) + "</a>" + bodyHTML.split(employeeName)[1];
		
		if(html.split(payrollPeriods).length != 2) 
			return "";
		
		html = html.split(payrollPeriods)[0] + createPeriods(salariesRecords) + html.split(payrollPeriods)[1];
		
		html = html.split("<div id=\"form\">")[0] + formHTML + html.split("<div id=\"form\">")[1].split("</div>")[1];
		
		html = html.split(enterpriseInfo)[0] +  "<a style=\"font-weight: bold;\">" + getEnterpriseInfo(dslContext, Integer.parseInt(params.get("enterprise"))) + "</a>";
		
		return html;
	}
	
	private static String generateFormEmployee(HashMap<String, String> params, Result<Record> salariesRecords) {
		String html = "";
		
		html += "<div>";
		html += "<form method\"post\" action=\"" + params.get("url") + "\" target=\"_blank\">";
		
		for(Entry<String, String> entry : params.entrySet()) {
			if(AonStringUtils.equalsIgnoreCase(entry.getKey(), "url") || AonStringUtils.contains(entry.getKey(), "id"))
				continue;
			
			html += "<input type=\"hidden\" name=\"" + entry.getKey() + "\" value=\"" + entry.getValue() + "\">";
		}
		
		for(Record salaryRecord : salariesRecords)
			html += "<input type=\"hidden\" name=\"id\" value=\"" + salaryRecord.get(SALARY.ID) + "\">";
		
		html += "<button type=\"submit\" style=\"text-decoration:none;padding:5px;text-align:center;color: #153643;\">";
		html +=		"<img src=\"http://simpleicon.com/wp-content/uploads/cloud-download-2.png\" style=\"width:20px;vertical-align: middle;\" />";
		html +=		"<b style=\"color: black;padding-left: 4px;font-size: x-small;\">DESCARGAR NOMINAS</b>";
		html += "</button>";
		html += "</form>";
		html += "</div>";
		
		return html;
	}

	private static String generateForm(HashMap<String, String> params) {
		String html = "";
		
		html += "<div>";
		html += "<form method\"post\" action=\"" + params.get("url") + "\" target=\"_blank\">";
		
		for(Entry<String, String> entry : params.entrySet()) {
			if(AonStringUtils.equalsIgnoreCase(entry.getKey(), "url"))
				continue;
			
			Pattern salaryIdPattern = Pattern.compile("^id\\d*$", Pattern.CASE_INSENSITIVE);
			Matcher matcher = salaryIdPattern.matcher(AonStringUtils.trimToEmpty(entry.getKey()));
			
			if(matcher.matches())
				html += "<input type=\"hidden\" name=\"id\" value=\"" + entry.getValue() + "\">";
			else 
				html += "<input type=\"hidden\" name=\"" + entry.getKey() + "\" value=\"" + entry.getValue() + "\">";
		}
		
		html += "<button type=\"submit\" style=\"text-decoration:none;padding:5px;text-align:center;color: #153643;\">";
		html +=		"<img src=\"http://simpleicon.com/wp-content/uploads/cloud-download-2.png\" style=\"width:20px;vertical-align: middle;\" />";
		html +=		"<b style=\"color: black;padding-left: 4px;font-size: x-small;\">DESCARGAR NOMINAS</b>";
		html += "</button>";
		html += "</form>";
		html += "</div>";
		
		return html;
	}
	
	private static String createPeriods(Result<Record> salariesRecords) {
		String html = "";
		for(Record salaryRecord : salariesRecords) {
			html += "<li style=\"font-weight:bold;\"> N&oacute;mina del " + dateFormatter.format(salaryRecord.get(SALARY.END_DATE));
		}
		
		return html;
	}
	
	private static String createPeriodsEnterprise(DSLContext dslContext, Result<Record> salariesRecords) {
		String html = "";
		for(Record salaryRecord : salariesRecords) {
			html += getSalaryItem(dslContext, salaryRecord.get(SALARY.ID));
		}
		
		return html;
	}
	
	private static String getSalaryItem(DSLContext dslContext, int salaryId) {
		
		Record salaryRecord = dslContext.select().from(SALARY)
				.where(SALARY.ID.eq(salaryId))
				.fetchOne();
		
		return "<li style=\"font-weight: bold;\">" + salaryRecord.get(SALARY.EMPLOYEE_NAME) + " ( " + dateFormatter.format(salaryRecord.get(SALARY.END_DATE)) + " )" + "</li>";
	}

	private static String getEnterpriseInfo(DSLContext dslContext, Integer enterpriseId) {
		String enterpriseInfo = "";
		
		Record registryRecord = dslContext.select().from(REGISTRY)
				.where(REGISTRY.ID.eq(enterpriseId))
				.fetchOne();
		
		enterpriseInfo += "<p>" + registryRecord.get(REGISTRY.NAME);
		
		Result<Record> rmediaRecords = dslContext.select().from(RMEDIA)
				.where(RMEDIA.REGISTRY.eq(enterpriseId))
				.fetch();
		
		for(Record r : rmediaRecords) {
			switch (r.get(RMEDIA.MEDIA)) {
			case (byte)1:
				if(null != r.get(RMEDIA.VALUE) && r.get(RMEDIA.VALUE).length() != 0)
					enterpriseInfo += "<br/> Tel&eacute;fono: " + r.get(RMEDIA.VALUE);
				break;
			case (byte)4:
				if(null != r.get(RMEDIA.VALUE) && r.get(RMEDIA.VALUE).length() != 0)
					enterpriseInfo += "<br/> Email: " + r.get(RMEDIA.VALUE);
				break;
			case (byte)5:
				if(null != r.get(RMEDIA.VALUE) && r.get(RMEDIA.VALUE).length() != 0)
					enterpriseInfo += "<br/> <a style=\"text-decoration: none; color: black;\" href=\"" + r.get(RMEDIA.VALUE) + "\">" + r.get(RMEDIA.VALUE) + "</a>";
				break;
			default:
				break;
			}
		}
		
		enterpriseInfo += "</p>";
		
		return enterpriseInfo;
	}

	private static String getEnterpriseName(DSLContext dslContext, Integer enterpriseId) {
		
		Record enterpriseRegistry = dslContext.select().from(REGISTRY)
				.where(REGISTRY.ID.eq(enterpriseId))
				.fetchOne();
		
		return enterpriseRegistry.get(REGISTRY.NAME);
	}

	private static ArrayList<Integer> getSalaryIds(HashMap<String, String> params) {
		ArrayList<Integer> salaryIds = new ArrayList<Integer>();
		
		for( Entry<String, String> entry : params.entrySet()) {
			if(AonStringUtils.contains(entry.getKey(), "id"))
				salaryIds.add(Integer.valueOf(entry.getValue()));
		}
		
		return salaryIds;
	}
	
	private static HashSet<Integer> getEnterpriseIds(HashMap<String, String> params) {
		HashSet<Integer> enterpriseIds = new HashSet<Integer>();
		
		for( Entry<String, String> entry : params.entrySet()) {
			if(AonStringUtils.containsIgnoreCase(entry.getKey(), "enterpriseId"))
				enterpriseIds.add(Integer.valueOf(entry.getValue()));
		}
		
		return enterpriseIds;
	}
	
	// ---------------------------------------------- ServiAgreements mailing

	public static void sendAgreementLogMail(String body) {
		SESMessage msg = new SESMessage()
				.setTo("svaldepenas@aonsolutions.es")
				.setSubject("ServiConvenios Logs")
				.setBody(body);
		SES.sendEmail(msg);
	}

	public static void sendAttachEmail(String domainName, Integer domainId, String login, MailAccount emailFrom, String emailTo, List<String> ccTo, List<String> bccTo, String subject, String emailBody, List<Integer> attachIds) {
		List<File> files = createAttachFiles(domainName, domainId, login, attachIds);
		SESMessage msg = new SESMessage()
				.setAlias(emailFrom.getName())
				.setReplyTo(emailFrom.getEmail())
				.setTo(emailTo)
				.setCc(ccTo)
				.setBcc(bccTo)
				.setSubject(subject)
				.setBody(emailBody)
				.setFiles(files);
		SES.sendEmail(msg);
	}

	private static List<File> createAttachFiles(String domainName, Integer domainId, String login,
			List<Integer> attachIds) {
		List<File> files = new ArrayList<>();
		
		attachIds.forEach(attachId -> {
			try {
				Attach attach = AON.getAttach(domainName, domainId, login, f -> f.getIdProperty().eq(attachId), AttachType.CONTRACT, true);
				File file = File.createTempFile( attach.getDescription(), "." + attach.getMimeType().getExtension() );
				AonFileUtils.writeByteArrayToFile(file, attach.getData());
				files.add(file);
			} catch (Exception e) {
				// TODO: handle exception
			}
		});
		
		return files;
	}

}