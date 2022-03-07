package com.code.aon.aio.controller;


import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Properties;

import javax.faces.event.ActionEvent;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.code.aon.AonVersion;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.google.apis.GmailUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.api.services.gmail.Gmail;


public class EmailVerifyController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public final static String CONTROLLER_NAME = "emailVerify";
	
	private String email;
	private Boolean verified;
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void onVerify(ActionEvent event) {
		sendGmail();
	}	
	
	public void sendGmail() {
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		Integer userId = AonUtil.getAuthPrincipal().getUserId();
		User user = AON.getUser(domainId, domainName, "", userId);
		
		String to = getEmail();
		try {
			DomainGserviceaccount g = AON.getDomainGserviceaccount(domainName, domainId, user.getLogin());		
			Gmail gmail = GmailUtils.serviceInitialize(g);
			MimeMessage email = createEmail(to, g.getGoogleAccount(), "VERIFICAR EMAIL" , getContent(domainName, domainId, user, to), "AON SOLUTIONS | VERIFICAR EMAIL");
			GmailUtils.sendMessage(gmail, "me", email); 
		} catch (MessagingException | IOException | GeneralSecurityException e) {
			e.printStackTrace();
		}
	}
	
	private String getContent(String domainName, Integer domainId, User user, String to) {
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
	
	public String getUrl(String domainName, Integer domainId, User user, String to){	
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
	
	public boolean isVerified() {
		if ( verified == null ) {
			String domainName = AonUtil.getDomainName();
			Integer domainId = DomainManager.getCurrentDomain();
			Integer userId = AonUtil.getAuthPrincipal().getUserId();
			User user = AON.getUser(domainId, domainName, "", userId);
			this.verified = user.hasAuth();
		}
		return verified;
	}
}