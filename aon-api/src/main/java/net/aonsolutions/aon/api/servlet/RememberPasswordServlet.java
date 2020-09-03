package net.aonsolutions.aon.api.servlet;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.code.aon.google.apis.GmailUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.services.gmail.Gmail;

@SuppressWarnings("serial")
@WebServlet(name = "RememberPasswordServlet", urlPatterns = {"/ms/api/remember/*"})
public class RememberPasswordServlet extends HttpServlet{
		
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject json = Utils.getRequestJSON(req);
		String email = json.getString("email");
		
		if(Utils.isEmail(email)) {
			Auth auth = new Auth();
	    	List<String> schemas = AONContext.getSchemas();
	    	for(String schema: schemas) {
	    		String domain = AONContext.getSchemaFirstDomain(schema);
	    		
	    		if(auth.getUuid() == null && !AonStringUtils.isBlank(domain)) {
    				auth = AON_SOLUTIONS.getAuth(domain, 0, email);
    				auth.setSchema(schema);
	    	    	if(auth.getUuid() != null) {
	    				String password = generatePassword();
	    	    		String pass = Utils.createPasswordHash(auth.getEmail(), password);
	    	    		auth.setPassword(pass);
	    	    		AON_SOLUTIONS.updateAuthPassword(auth);
	    	    		// SEND EMAIL
	    	    		sendGmail(domain, 0, "", email, password);
	    	    	} 
	    	    }	    		
	    	}
		}	
	}
	
	public void sendGmail(String domainName, Integer domainId, String login, String to, String password) {
		try {
			DomainGserviceaccount g = AON.getDomainGserviceaccount(domainName, domainId, "");		
			Gmail gmail = GmailUtils.serviceInitialize(g);
			MimeMessage email = createEmail(to, g.getGoogleAccount(), "RECORDAR CLAVE" , getContent(password), "AON SOLUTIONS | RECORDAR CLAVE");
			GmailUtils.sendMessage(gmail, "me", email); 
		} catch (MessagingException | IOException | GeneralSecurityException e) {
			e.printStackTrace();
		}
	}

	private String getContent(String password) {
		String msg = "<div style='margin-left: -30px;'>"
				+"<div style='margin: 7px 15px 14px 30px;line-height: 18px;font-size: 13px;box-shadow: 0px 1px 2px rgba(0, 0, 0, 0.075);'>";
		msg = msg + "<p> La nueva clave para acceder a Aon Solutions es <b>" + password + "</b></p>";

		msg = msg + "</div> </div>";
		return msg;
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
	
	private String generatePassword() {
		return com.code.aon.google.apis.Utils.PasswordGenerator.getPassword(
				com.code.aon.google.apis.Utils.PasswordGenerator.MINUSCULAS
				+ com.code.aon.google.apis.Utils.PasswordGenerator.MAYUSCULAS
				+ com.code.aon.google.apis.Utils.PasswordGenerator.NUMEROS, 10);
	}
	
}
