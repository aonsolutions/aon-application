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
import java.util.Map;
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

import com.esferalia.aon.occam.api.model.MailAccount;
import com.google.api.client.util.Base64;

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
	
	public static List<MailAccount> getMailAccounts(Connection connection, Integer userId, Integer domainId) {
		return getMailAccountsDB(DSL.using(connection, getDefaultSettings()), userId, domainId);
	}
	
	public static String getPayrollEmailSendTo(Connection connection, Integer enterpriseID) {
		return getPayrollEmailSendToDB(DSL.using(connection, getDefaultSettings()), enterpriseID);
	}
	
	public static String getPayrollEmailBody(Connection connection, String completeURL) {
		return getPayrollEmailBodyDB(DSL.using(connection, getDefaultSettings()), completeURL);
	}
	
	public static String sendPayrollEmail(Connection connection, String from, String to, String cc, String cco, String bodyHTML) {
		return sendPayrollEmailDB(DSL.using(connection, getDefaultSettings()), from, to, cc, cco, bodyHTML);
	}
	
	public static String checkEmployeesEmails(Connection connection, ArrayList<Integer> salaryIds) {
		return checkEmployeesEmailsDB(DSL.using(connection, getDefaultSettings()), salaryIds);
	}
	
	public static String sendPayrollEmailToEmployees(Connection connection, String from, String cc, String cco,
			String bodyHTML, String completeURL) {
		return sendPayrollEmailToEmployeesDB(DSL.using(connection, getDefaultSettings()), from, cc, cco, bodyHTML, completeURL);
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------------------

	private static String sendPayrollEmailDB(DSLContext dslContext, String from, String to, String cc, String cco, String bodyHTML) {
		
		if(to.length() == 0){
			return "No existe destinatario al que enviar el email. Por favor inserte un destinatario.";
		}
		
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
			//message.setText(bodyHTML,"UTF-8", "text/html");
		    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		    if(null != cc)
		    	message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
		    if(null != cco)
		    	message.addRecipient(Message.RecipientType.BCC, new InternetAddress(cco));
		    
//		    transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
		    transport.sendMessage(message, message.getAllRecipients());
		    transport.close();	
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return "Email enviado correctamente.";
	    
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
	
	private static String getPayrollEmailSendToDB(DSLContext dslContext, Integer enterpriseID) {
		Record enterpriseDataRegistry = dslContext.select().from(ENTERPRISE_DATA)
				.where(ENTERPRISE_DATA.ENTERPRISE.eq(enterpriseID)
				.and(ENTERPRISE_DATA.NAME.eq("PAY_salarySending_email_PAY"))
				).fetchOne();
		
		return enterpriseDataRegistry.get(ENTERPRISE_DATA.EXPRESSION);
	}
	
	private static String getPayrollEmailBodyDB(DSLContext dslContext, String completeURL) {
		
		String paramsBase64 = completeURL.split("salary_exporter/")[1];
		
		String paramsStr = decode(paramsBase64.getBytes());
		Map<String, String> paramsMap = createParams(paramsStr);
		
		String html = "";
		
		html += "<div style=\"font-family: \"Lucida Sans Unicode\", \"Lucida Grande\", sans-serif;font-size: 12px;letter-spacing: 2px;word-spacing: 0px;color: #000000;font-weight: normal;text-decoration: none;font-style: normal;font-variant: normal;text-transform: none;\">";
		html += "<p>Estimado cliente:</p>";
		html += "<p>Le adjuntamos las n&oacute;minas de la empresa <a style=\"font-weight: bold;\">" + getEnterpriseName(dslContext, paramsMap) +"</a> que corresponden a los siguientes trabajadores:</p>";
		html += "<ul>";
		
		for(int i=0; i < Integer.parseInt(paramsMap.get("selectedSalaries")); i++) {
			html +=	getSalaryItem(dslContext, Integer.parseInt(paramsMap.get("salary"+i+"Id")));
		}

		html += "</ul>";
		html += "<p>Para descargar y visualizar el documento adjunto, por favor haga click en el siguiente enlace:</p>";
		
		html += "<div style=\"width:200px;border: 1px solid gray;text-align:center;\">";
		html +=	"<a type=\"button\" href=\" " + completeURL + " \" style=\"text-decoration:none;padding:5px;text-align:center;color: #153643;\">";
		html +=		"<img src=\"http://simpleicon.com/wp-content/uploads/cloud-download-2.png\" style=\"width:20px;vertical-align: middle;\" />";
		html +=		"<b style=\"color: black;padding-left: 4px;font-size: x-small;\">DESCARGAR NOMINAS</b>";
		html +=	"</a>";
		html += "</div>";

		html += "<p>Este archivo est&aacute; en formato PDF Adobe y se puede leer usando Acrobat Reader. Si no tiene instalado el Acrobat Reader pulse aqu&iacute; para conseguir su copia gratuita: http://get.adobe.com/es/reader. Para cualquier aclaraci&oacute;n sobre el documento adjunto p&oacute;ngase en contacto con nosotros.</p>";
		// html += "<p>AON SOLUTIONS, S.L.<br/> Tel&eacute;fono: 902121009<br/> Fax: 945121011<br/> <a style=\"text-decoration: none; color: black;\" href=\"https://www.aonsolutions.es\">www.aonsolutions.es</a></p>";
		html += getEnterpriseInfo(dslContext, Integer.parseInt(paramsMap.get("enterprise")));
		html += "</div>";
		
		return html;
	}
	
	private static String getEnterpriseInfo(DSLContext dslContext, Integer enterpriseId) {
		// <p>AON SOLUTIONS, S.L.<br/> Tel&eacute;fono: 902121009<br/> Fax: 945121011<br/> <a style=\"text-decoration: none; color: black;\" href=\"https://www.aonsolutions.es\">www.aonsolutions.es</a></p>
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

	private static String decode(byte[] value){
		String decode = "";
		decode = new String(Base64.decodeBase64(value));
		return decode;
	}
	
	private static String encode(byte[] value){
		String encode = "";
		encode = new String(Base64.encodeBase64(value));
		return encode;
	}
	
	private static Map<String, String> createParams(String paramsStr) {
		HashMap<String, String> paramsMap = new HashMap<String, String>();
		
		String params = paramsStr.substring(1);
		
		String[] paramsArr = params.split("&");
		for(int i=0; i < paramsArr.length; i++) {
			String key = paramsArr[i].split("=")[0];
			String value = paramsArr[i].split("=")[1];
			
			paramsMap.put(key, value);
		}
		
		return paramsMap;
	}
	
	private static String getSalaryItem(DSLContext dslContext, int salaryId) {
		
		Record salaryRecord = dslContext.select().from(SALARY)
				.where(SALARY.ID.eq(salaryId))
				.fetchOne();
		
		return "<li style=\"font-weight: bold;\">" + salaryRecord.get(SALARY.EMPLOYEE_NAME) + " ( " + dateFormatter.format(salaryRecord.get(SALARY.END_DATE)) + " )" + "</li>";
	}

	private static String getEnterpriseName(DSLContext dslContext, Map<String, String> paramsMap) {
		
		Record enterpriseRegistry = dslContext.select().from(REGISTRY)
				.where(REGISTRY.ID.eq(Integer.parseInt(paramsMap.get("enterprise"))))
				.fetchOne();
		
		return enterpriseRegistry.get(REGISTRY.NAME);
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

	private static String sendPayrollEmailToEmployeesDB(DSLContext dslContext, String from, String cc, String cco, String bodyHTML, String completeURL) {
		String baseURL = completeURL.split("salary_exporter/")[0] + "salary_exporter/";
		String paramsBase64 = completeURL.split("salary_exporter/")[1];
		
		String paramsStr = decode(paramsBase64.getBytes());
		Map<String, String> paramsMap = createParams(paramsStr);
		
		ArrayList<Integer> salaryIds = getSalaryIds(paramsMap);
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
				
				String parseHTMLBody = parseHTMLBody(bodyHTML, salariesRecords, paramsMap.get("enterprise"), baseURL, dslContext);
				
				if(parseHTMLBody.length() == 0)
					return "No se ha encontrado la variable [=NOMBRE_EMPLEADO], [=PERIODOS_NOMINA] y/o [=INFORMACION_EMPRESA]";
				
				sendPayrollEmailDB(dslContext, from, emailTo, cc, cco, parseHTMLBody);
				
				visitedContracts.add(contractId);
			}
		}
		
		
		return "Emails enviados correctamente.";
	}

	private static String parseHTMLBody(String bodyHTML, Result<Record> salariesRecords, String enterprise, String baseURL, DSLContext dslContext) {
		
		// GENERATE URL
		String params = "?type=salary&selectedSalaries=" + salariesRecords.size() + "&enterprise=" + enterprise;
		for(int i=0; i<salariesRecords.size(); i++) {
			params += "&salary"+i+"Id=" + salariesRecords.get(i).get(SALARY.ID);
		}
		params += "&name=salaries.pdf";
		
		String encodedParamas = encode(params.getBytes()); 
		String url = baseURL + encodedParamas;
		
		//GENERATE BODY HTML
		String[] aux = bodyHTML.split("\\[=NOMBRE_EMPLEADO\\]");
		
		if(aux.length != 2) 
			return "";
		
		String html = bodyHTML.split("\\[=NOMBRE_EMPLEADO\\]")[0] + salariesRecords.get(0).get(SALARY.EMPLOYEE_NAME) + bodyHTML.split("\\[=NOMBRE_EMPLEADO\\]")[1];
		
		if(html.split("<li>\\[=PERIODOS_NOMINA\\]</li>").length != 2) 
			return "";
		
		html = html.split("<li>\\[=PERIODOS_NOMINA\\]</li>")[0] + createPeriods(salariesRecords) + html.split("<li>\\[=PERIODOS_NOMINA\\]</li>")[1];
		
		html = html.split("URL_DOWNLOAD")[0] + url + html.split("URL_DOWNLOAD")[1];
		
		if(html.split("\\[=INFORMACION_EMPRESA\\]").length != 2)
			return "";
		
		html = html.split("\\[=INFORMACION_EMPRESA\\]")[0] + getEnterpriseInfo(dslContext, Integer.parseInt(enterprise));
		
		return html;
	}

	private static String createPeriods(Result<Record> salariesRecords) {
		String html = "";
		for(Record salaryRecord : salariesRecords) {
			html += "<li> N&oacute;mina del " + dateFormatter.format(salaryRecord.get(SALARY.END_DATE));
		}
		
		return html;
	}

	private static ArrayList<Integer> getSalaryIds(Map<String, String> paramsMap) {
		ArrayList<Integer> salaryIds = new ArrayList<Integer>();
		
		for(String key : paramsMap.keySet()) {
			if(key.contains("salary")) {
				salaryIds.add(Integer.parseInt(paramsMap.get(key)));
			}
		}
		
		return salaryIds;
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	// 											SERVI AGREEMENTS LOG MAILING
	// ----------------------------------------------------------------------------------------------------------------

	public static void sendAgreementLogMail(String body) {
		
//		SES.sendEmailWithBCC("no-reply@aon.solutions", "svaldepenas@aonsolutions.es", "s.valdepenas@gmail.com", "ServiConvenios Logs", body);
		SES.sendEmail("no-reply@aon.solutions", "svaldepenas@aonsolutions.es", "ServiConvenios Logs", body);
		
	}

}