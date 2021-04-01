package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;
import static com.esferalia.aon.jooq.tables.MailAccount.MAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.watson.util.AonStringUtils;

import solutions.aon.aws.SES;

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
	
	public static List<MailAccount> getMailAccounts(Connection connection, Integer userId, Integer domainId) {
		return getMailAccountsDB(DSL.using(connection, getDefaultSettings()), userId, domainId);
	}
	
	private static List<MailAccount> getMailAccountsDB(DSLContext dslContext, Integer userId, Integer domainId) {
		List<MailAccount> mailAccounts = new LinkedList<MailAccount>();
		
		Record userRecord = dslContext.select().from(USER).where(USER.ID.eq(userId)).fetchOne();
		
		Integer userDomain = userRecord.get(USER.DOMAIN);
		
		Result<Record> mailAccountRecords = dslContext.select().from(MAIL_ACCOUNT)
				.where(MAIL_ACCOUNT.USER_ID.eq(userId))
				.or(MAIL_ACCOUNT.DOMAIN.eq(userDomain).and(MAIL_ACCOUNT.USER_ID.isNull()))
				.or(MAIL_ACCOUNT.DOMAIN.eq(domainId).and(MAIL_ACCOUNT.USER_ID.isNull()))
				.fetch();
		
		for(Record r : mailAccountRecords) {
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
			mailAccount.setType(r.get(MAIL_ACCOUNT.TYPE));
			
			mailAccounts.add(mailAccount);
		}
		
		return mailAccounts;
	}
	
	// ---------------------------------------------- SendTo
	
	public static String getPayrollEmailSendTo(Connection connection, Type type, Integer enterpriseID) {
		return getPayrollEmailSendToDB(DSL.using(connection, getDefaultSettings()), type, enterpriseID);
	}
	
	private static String getPayrollEmailSendToDB(DSLContext dslContext, Type type, Integer enterpriseID) {
		String sendTo = "";
		
		Record enterpriseEmailRecord = dslContext.select().from(ENTERPRISE_DATA)
				.where(ENTERPRISE_DATA.ENTERPRISE.eq(enterpriseID)
				.and(ENTERPRISE_DATA.NAME.eq("PAY_salarySending_email_PAY"))
				).fetchOne();
		
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
			if(AonStringUtils.containsIgnoreCase(entry.getKey(), "id"))
				html +=	getSalaryItem(dslContext, Integer.parseInt(entry.getValue()));
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
			
			Integer contractId = salaryRecord.get(SALARY.CONTRACT);
			
			if(!visitedContracts.contains(contractId)) {
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
	
	// ---------------------------------------------- Send Email
	
	public static String sendPayrollEmail(Connection connection, Type type,  HashMap<String, String> params, String from, String to, String cc, String cco, String bodyHTML) {
		return sendPayrollEmailDB(DSL.using(connection, getDefaultSettings()), type, params, from, to, cc, cco, bodyHTML);
	}
	
	private static String sendPayrollEmailDB(DSLContext dslContext, Type type, HashMap<String, String> params, String from, String to, String cc, String cco, String bodyHTML) {
		String message = "";
		
		switch (type) {
		case EMPLOYEE:
			message = sendEmployeesEmail(dslContext, type, params, from, to, cc, cco, bodyHTML);
			break;
		case ENTERPRISE:
			message = sendEmail(dslContext, from, to, cc, cco, bodyHTML);
			break;
		default:
			break;
		}
		
		return message;
	}

	private static String sendEmployeesEmail(DSLContext dslContext, Type type, HashMap<String, String> params, String from, String to, String cc, String cco, String bodyHTML) {
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
					return "No se ha encontrado la variable NOMBRE_EMPLEADO, PERIODOS_NOMINA y/o INFORMACION_EMPRESA";
				
				sendEmail(dslContext, from, emailTo, cc, cco, parseHTMLBody);
				
				visitedContracts.add(contractId);
			}
		}
		
		
		return "Email(s) enviado(s) correctamente.";
	}

	private static String sendEmail(DSLContext dslContext, String from, String to, String cc, String cco, String bodyHTML) {
		Record mailAccountRecord = dslContext.select().from(MAIL_ACCOUNT)
				.where(MAIL_ACCOUNT.ID.eq(Integer.parseInt(from))).fetchOne();
		
		String smtp = "smtp";
		String port = null;
		String host = null;
		String user =  null;
		String password = null;
		
		if(null != mailAccountRecord) {
			port =  mailAccountRecord.get(MAIL_ACCOUNT.OUTGOING_PORT).toString();
			host = mailAccountRecord.get(MAIL_ACCOUNT.OUTGOING_HOST);
			user = mailAccountRecord.get(MAIL_ACCOUNT.MAIL_USERNAME);
			password = mailAccountRecord.get(MAIL_ACCOUNT.PASSWORD);
		} else {
			return "No existe cuenta de correo desde la que enviar este mensaje.";
		}
		
		// Send Email
		Properties props = new Properties();

	    props.setProperty("mail.transport.protocol", smtp);
	    props.setProperty("mail.smtp.port", port);
	    props.setProperty("mail.host", host);
	    props.setProperty("mail.user", user);
	    props.setProperty("mail.password", password);
	    props.setProperty("mail.smtp.starttls.enable", "true");
	   
	    try {
	    	Session session = Session.getInstance(props, null);
	 	    Transport transport = session.getTransport("smtp");
			transport.connect(host, user, password);
			MimeMessage message = new MimeMessage(session);
			message.setSubject("N" + String.valueOf("\u00F3") + "minas");
			message.setContent(bodyHTML, "text/html; charset=UTF-8");
		    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		    if(null != cc)
		    	message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
		    if(null != cco)
		    	message.addRecipient(Message.RecipientType.BCC, new InternetAddress(cco));
		    
		    transport.sendMessage(message, message.getAllRecipients());
		    transport.close();	
		} catch (MessagingException e) {}
	    
	    return "Email enviado correctamente.";
	}
	
	// ---------------------------------------------- Send Email (Auxiliar methods)
	
	private static String parseHTMLBody(String bodyHTML, Result<Record> salariesRecords, HashMap<String, String> params, DSLContext dslContext) {
		
		// GENERATE URL
		String formHTML = generateForm(params);
		
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

	private static String generateForm(HashMap<String, String> params) {
		String html = "";
		
		html += "<div>";
		html += "<form method\"post\" action=\"" + params.get("url") + "\" target=\"_blank\">";
		
		for(Entry<String, String> entry : params.entrySet()) {
			if(AonStringUtils.equalsIgnoreCase(entry.getKey(), "url"))
				continue;
			
			if(AonStringUtils.containsIgnoreCase(entry.getKey(), "id"))
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
			if(AonStringUtils.containsIgnoreCase(entry.getKey(), "id"))
				salaryIds.add(Integer.valueOf(entry.getValue()));
		}
		
		return salaryIds;
	}
	
	// ---------------------------------------------- ServiAgreements mailing

	public static void sendAgreementLogMail(String body) {
//		SES.sendEmailWithBCC("no-reply@aon.solutions", "svaldepenas@aonsolutions.es", "s.valdepenas@gmail.com", "ServiConvenios Logs", body);
		SES.sendEmail("no-reply@aon.solutions", "svaldepenas@aonsolutions.es", "ServiConvenios Logs", body);
		
	}

}