package net.aonsolutions.aon.api.servlet;


import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.code.aon.google.apis.GmailUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.api.services.gmail.Gmail;


public class EmailVerify implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void sendGmail(Domain domain, User user, String email) {
		try {
			DomainGserviceaccount g = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), "");		
			Gmail gmail = GmailUtils.serviceInitialize(g);
			MimeMessage mail = createEmail(email, g.getGoogleAccount(), "VERIFICAR EMAIL" , getContent(domain.getName(), domain.getId(), user, email), "AON SOLUTIONS | VERIFICAR EMAIL");
			GmailUtils.sendMessage(gmail, "me", mail); 
		} catch (MessagingException | IOException | GeneralSecurityException e) {
			e.printStackTrace();
		}
	}
	
	private static String getContent(String domainName, Integer domainId, User user, String to) {
		Domain userDomain = AON.getDomain(domainName, domainId, user.getLogin());
		String msg = "<div style='margin-left: -30px;'>"
				+"<div style='margin: 7px 15px 14px 30px;line-height: 18px;font-size: 13px;box-shadow: 0px 1px 2px rgba(0, 0, 0, 0.075);'>";
		msg = msg + "<p> El usuario <b>"+ (user.getName() != null ? user.getName() : user.getLogin()) +"</b> de la empresa <b>" + userDomain.getDescription() +
			"</b> quiere verificar su cuenta. </p><p> Pulsa en este "+ "<a href=\""+ getUrl(domainName, domainId, user, to) +"\" > enlace </a> para verificar su cuenta.</p>";
		msg = msg + "<br>"
				+ "<a href=\""+ getUrl(domainName, domainId, user, to) +"\" style=\"text-decoration: none;cursor:pointer;color:#fff;\">"
					+ "<div style=\"color:#fff;background-color:#4d90fe;padding: 15px;font-weight: bold;width: 120px;\">"
						+ "Verificar Email"
					+ "</div>"
				+ "</a>";
		msg = msg + "</div> </div>";
		return msg;
	}
	
	public static String getUrl(String domainName, Integer domainId, User user, String to){	
		String str ="domain=" + domainName + "&email=" + to + "&user=" + user.getId();
		String base64 = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
		domainName = "udapa.aonsolutions.net";
		return "https://" + domainName + "/ms/api/verify/" + base64 ;
	}
	
	public static MimeMessage createEmail(String to, String from, String subject,
			  String bodyText, String fromName) throws MessagingException {
	    Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);

	    MimeMessage email = new MimeMessage(session);
	    try {
			email.setFrom(new 	InternetAddress(from, fromName));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

    	email.addRecipient(javax.mail.Message.RecipientType.BCC, new InternetAddress(to));	

	    email.setSubject(subject);
	    email.setContent(bodyText, "text/html");
	    return email;
	}
}