package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;
import static com.esferalia.aon.jooq.tables.MailAccount.MAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;

import java.sql.Connection;
import java.text.SimpleDateFormat;
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

public class JooqDomain {

	private static Settings SETTINGS = null;
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	public static List<MailAccount> getMailAccounts(Connection connection, Integer userId) {
		return getMailAccountsDB(DSL.using(connection, getDefaultSettings()), userId);
	}
	
	public static String getPayrollEmailSendTo(Connection connection, Integer enterpriseID) {
		return getPayrollEmailSendToDB(DSL.using(connection, getDefaultSettings()), enterpriseID);
	}
	
	public static String getPayrollEmailBody(Connection connection, String completeURL) {
		return getPayrollEmailBodyDB(DSL.using(connection, getDefaultSettings()), completeURL);
	}
	
	public static String sendPayrollEmail(Connection connection, String from, String to, String bodyHTML) {
		return sendPayrollEmailDB(DSL.using(connection, getDefaultSettings()), from, to, bodyHTML);
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------------------

	private static String sendPayrollEmailDB(DSLContext dslContext, String from, String to, String bodyHTML) {
		
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
			message.setContent(bodyHTML, "text/html");
		    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		    
		    transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
		    transport.close();	
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return "Email enviado correctamente.";
	    
	   
	}

	private static List<MailAccount> getMailAccountsDB(DSLContext dslContext, Integer userId) {
		List<MailAccount> mailAccounts = new LinkedList<MailAccount>();
		
		Result<Record> mailAccountRecords = dslContext.select().from(MAIL_ACCOUNT)
				.where(MAIL_ACCOUNT.USER_ID.eq(userId)).fetch();
		
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
		
		html += "<p>Estimado cliente:</p>";
		html += "<p>Le adjuntamos las n&oacute;minas de la empresa <a style=\"font-weight: bold;\">" + getEnterpriseName(dslContext, paramsMap) +"</a> que corresponden a los siguientes trabajadores:</p>";
		html += "<ul>";
		
		for(int i=0; i < Integer.parseInt(paramsMap.get("selectedSalaries")); i++) {
			html +=	getSalaryItem(dslContext, Integer.parseInt(paramsMap.get("salary"+i+"Id")));
		}

		html += "</ul>";
		html += "<p>Para descargar y visualizar el documento adjunto, por favor haga click en el siguiente enlace:</p>";
		
		html +=	"<a type=\"button\" href=\" " + completeURL + " \" style=\"border: 1px solid gray;text-decoration:none;padding:5px;text-align:center;color: #153643; font-family: Arial, sans-serif;\">";
		html +=		"<img src=\"http://simpleicon.com/wp-content/uploads/cloud-download-2.png\" style=\"width:20px;display: table-cell;vertical-align: middle;\" />";
		html +=		"<b style=\"color: black;padding-left: 4px;font-size: x-small;\">DESCARGAR NOMINAS</b>";
		html +=	"</a>";

		html += "<p>Este archivo est&aacute; en formato PDF Adobe y se puede leer usando Acrobat Reader. Si no tiene instalado el Acrobat Reader pulse aqu&iacute; para conseguir su copia gratuita: http://get.adobe.com/es/reader. Para cualquier aclaraci&oacute;n sobre el documento adjunto p&oacute;ngase en contacto con nosotros.</p>";
		html += "<p>AON SOLUTIONS, S.L.<br/> Tel&eacute;fono: 902121009<br/> Fax: 945121011<br/> <a style=\"text-decoration: none; color: black;\" href=\"www.aonsolutions.es\">www.aonsolutions.es</a></p>";
		
//		String html = "";
//		
//		html += 	"<table style=\"padding: 10px 0 20px 0;\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">";
//		html += 		"<tr>";
//		html +=				"<td align=\"center\" bgcolor=\"#70bbd9\" style=\"padding: 20px 0 20px 0;\">";
//		html +=					"<img src=\"https://static.comunicae.com/photos/notas/1042089/1357208241_aonsolutions-lgonubeconborde-alwayson.png\" alt=\"Creating Email Magic\" width=\"300\" style=\"display: block;\" />";
//		html +=				"</td>";
//		html +=			"</tr>";
//		html += 		"<tr>";
//		html +=				"<td bgcolor=\"#ffffff\" style=\"padding: 20px 30px 20px 30px;\">";
//		html +=					"<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">";
//		html +=						"<tr>";
//		html +=							"<td style=\"padding: 10px 0 10px 0;\" style=\"color: #153643; font-family: Arial, sans-serif; font-size: 16px; line-height: 20px;\">";
//		html +=								"Descargue las n" + String.valueOf("\u00F3") + "minas de la empresa: <a style=\"font-weight: bold;\">" + getEnterpriseName(dslContext, paramsMap) +"</a> a trav" + String.valueOf("\u00E9") + "s del siguiente enlace. <br> Las n" + String.valueOf("\u00F3") + "minas inclu" + String.valueOf("\u00ED") + "das corresponden a los siguientes trabajadores: ";
//		html +=							"</td>";
//		html +=						"</tr>";
//		html +=						"<tr>";
//		html +=							"<td style=\"padding: 10px 0 20px 0;\" style=\"color: #153643; font-family: Arial, sans-serif; font-size: 16px; line-height: 20px;\">";
//		html +=								"<ul>";
//		
//		for(int i=0; i < Integer.parseInt(paramsMap.get("selectedSalaries")); i++) {
//			html +=	getSalaryItem(dslContext, Integer.parseInt(paramsMap.get("salary"+i+"Id")));
//		}
//		
//		html +=								"</ul>";
//		html +=							"</td>";
//		html +=						"</tr>";
//		html +=						"<tr style=\"text-decoration: none; color: black;\">";
//		html +=							"<td style=\"height:25px;border: 2px solid gray;padding:5px;text-align:center;color: #153643; font-family: Arial, sans-serif;\">";
//		html +=								"<img src=\"http://simpleicon.com/wp-content/uploads/cloud-download-2.png\" style=\"width:20px;display: table-cell;vertical-align: middle;\" />";
//		html +=								"<a type=\"button\" href=\"" + completeURL + "\"  style=\"padding-left:10px;text-decoration: none; color: black;\"><b>DESCARGAR NOMINAS</b></a>";
//		html +=							"</td>";
//		html +=						"</tr>";
//		html +=						"<tr>";
//		html +=							"<td>";
//		html +=								"<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">";
//		html +=									"<tr>";
//		html +=										"<td width=\"260\" align=\"top\">";
//		html +=											"<table style=\"margin-top: 30px;\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">";
//		html +=												"<tr>";
//		html +=													"<td>";
//		html +=														"<img href=\"https://translogia.es/\" src=\"https://translogia.es/wp-content/uploads/2019/03/logoo-1-e1554365403288.png\" alt=\"\" width=\"100%\" height=\"140\" style=\"display: block;\" />";
//		html +=													"</td>";
//		html +=												"</tr>";
//		html +=												"<tr>";
//		html +=													"<td style=\"padding: 25px 0 0 0;\" style=\"color: #153643; font-family: Arial, sans-serif; font-size: 16px; line-height: 20px;\">";
//		html +=														"La herramienta que falta en tu despacho para conseguir el tiempo que necesitas TODO lo que necesitas para la automatizaci" + String.valueOf("\u00F3") + "n eficaz en la contabilizaci" + String.valueOf("\u00F3") + "n de FACTURAS, sin instalaciones y sin cambiar tu contabilidad.";
//		html +=													"</td>";
//		html +=												"</tr>";
//		html +=											"</table>";
//		html +=										"</td>";
//		html +=										"<td style=\"font-size: 0; line-height: 0;\" width=\"20\">";
//		html +=											"&nbsp;";
//		html +=										"</td>";
//		html +=										"<td width=\"260\" align=\"top\">";
//		html +=											"<table style=\"margin-top: 30px;\"  border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">";
//		html +=												"<tr>";
//		html +=													"<td>";
//		html +=														"<img href=\"https://translogia.es/gestion-en-la-nube/\" src=\"https://i0.wp.com/translogia.es/wp-content/uploads/2019/05/image-2019-05-15.jpg?resize=360%2C240&ssl=1\" alt=\"\" width=\"100%\" height=\"140\" style=\"display: block;\" />";
//		html +=													"</td>";
//		html +=												"</tr>";
//		html +=												"<tr>";
//		html +=													"<td style=\"padding: 25px 0 0 0;\" style=\"color: #153643; font-family: Arial, sans-serif; font-size: 16px; line-height: 20px;\">";
//		html +=														"La gesti" + String.valueOf("\u00F3") + "n en la nube, llamada tambi" + String.valueOf("\u00E9") + "n simplemente " + String.valueOf("\u00AB") + "la nube" + String.valueOf("\u00BB") + ", es cada vez m" + String.valueOf("\u00E1") + "s utilizada en todos los " + String.valueOf("\u00E1") + "mbitos, pero sobre todo destaca en el empresarial. " + String.valueOf("\u00BF") + "Por qu" + String.valueOf("\u00E9") + "? Muy f" + String.valueOf("\u00E1") + "cil, en este blog te lo contamos.";
//		html +=													"</td>";
//		html +=												"</tr>";
//		html +=											"</table>";
//		html +=										"</td>";
//		html +=									"</tr>";
//		html +=								"</table>";
//		html +=							"</td>";
//		html +=						"</tr>";
//		html +=					"</table>";
//		html +=				"</td>";
//		html +=			"</tr>";
//		html += 		"<tr>";
//		html +=				"<td bgcolor=\"#b7b7b7\" style=\"padding: 30px 30px 30px 30px;\">";
//		html +=					"<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">";
//		html +=						"<tr>";
//		html +=							"<td  style=\"color: #ffffff !important; text-docaration: none; font-family: Arial, sans-serif; font-size: 14px;\">";
//		html +=								 String.valueOf("\u00AE") + " AON SOLUTIONS S.L. 2018<br/>";
//		html +=								" 902 121 009 - 945 121 010<br/>";
//		html +=								" info@aonsolutions.es";
//		html +=							"</td>";
//		html +=							"<td align=\"right\">";
//		html +=								"<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">";
//		html +=									"<tr>";
//		html +=										"<td>";
//		html +=											"<a href=\"https://twitter.com/aonsolutions\">";
//		html +=												"<img src=\"http://www.bancoalimentoslpa.org/wp-content/uploads/2019/01/Twitter.ico\" alt=\"Twitter\" width=\"30px\" style=\"display: block;\" border=\"0\" />";
//		html +=											"</a>";
//		html +=										"</td>";
//		html +=										"<td style=\"font-size: 0; line-height: 0;\" width=\"20\">&nbsp;</td>";
//		html +=										"<td>";
//		html +=											"<a href=\"https://www.facebook.com/aonSolutions\">";
//		html +=												"<img src=\"https://i2.wp.com/alesteplaza.es/wp-content/uploads/2017/05/facebook.png?ssl=1\" alt=\"Facebook\" width=\"30px\" style=\"display: block;\" border=\"0\" />";
//		html +=											"</a>";
//		html +=										"</td>";
//		html +=									"</tr>";
//		html +=								"</table>";
//		html +=							"</td>";
//		html +=						"</tr>";
//		html +=					"</table>";
//		html +=				"</td>";
//		html +=			"</tr>";
//		html += 	"</table>";
		
		return html;
	}
	
	private static String decode(byte[] value){
		String decode = "";
		decode = new String(Base64.decodeBase64(value));
		return decode;
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



}