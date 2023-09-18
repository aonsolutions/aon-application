package net.aonsolutions.aon.api.servlet;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

@SuppressWarnings("serial")
@WebServlet(name = "RememberPasswordServlet", urlPatterns = {"/ms/api/remember/*"})
public class RememberPasswordServlet extends AonApiHttpServlet {
		
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		JSONObject json = Utils.getRequestJSON(req);
		String email = json.getString("email");
		AonApiData api = initialize(req, false);
		if(Utils.isEmail(email)) {
			Auth auth = new Auth();
	    	List<String> schemas = AONContext.getSchemas();
	    	for(String schema: schemas) {
	    		String domain = AONContext.getSchemaFirstDomain(schema);
	    		
	    		if(auth.getUuid() == null && !AonStringUtils.isBlank(domain)) {
    				auth = AON_SOLUTIONS.getAuth(domain, 0, email);
    				auth.setSchema(schema);
	    	    	if(auth.getUuid() != null) {
	    				String password = Utils.generatePassword();
	    	    		String pass = Utils.createPasswordHash(auth.getEmail(), password);
	    	    		auth.setPassword(pass);
	    	    		AON_SOLUTIONS.updateAuthPassword(auth);
	    	    		// SEND EMAIL
	    	    		sendGmail(api, email, password);
	    	    	} 
	    	    }	    		
	    	}
		}	
	}
	
	public void sendGmail(AonApiData api, String to, String password) {
		SESMessage msg = new SESMessage()
			.setTo(to)
			.setSubject("RECORDAR CLAVE")
			.setBody(getContent(password));
		SES.sendEmail(msg);
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

    	email.addRecipient(jakarta.mail.Message.RecipientType.BCC, new InternetAddress(to));	

	    email.setSubject(subject);
	    email.setContent(bodyText, "text/html");
	    return email;
	}
	
}
