package net.aonsolutions.aon.api.servlet;

import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Logger;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.watson.server.AonDateUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

@WebServlet(name = "MagicLinkServlet", urlPatterns = {"/ms/api/magicLink/*"})
public class MagicLinkServlet extends AonApiHttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER  = Logger.getLogger(MagicLinkServlet.class.getName());
	
	private static final String AON_LOGO = "https://aon.solutions/assets/aon-logo.png";
	private static final String AON_FROM = "booking@aon.solutions";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		JSONObject json = getParamsJSON(req);
		get(req, resp, json);

	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		JSONObject json = getRequestJSON(req);
		get(req, resp, json);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp, JSONObject json) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			magicLink(json, req.getServerName());
			response(req, resp);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}	
	
	private void magicLink(JSONObject json, String url) {
		JSONObject data = JsonUtils.getJSONObject(json, "data");
		String email = JsonUtils.getString(data, IJsonNames.EMAIL);
		String urlNew = JsonUtils.getString(data, IJsonNames.URL);
		if(Utils.isEmail(email)) {
			Auth auth = AON_SOLUTIONS.getAuth(email);
			
			if(auth.isEmpty()) throw new AonApiException(AonApiError.NOT_EXIST_USER.getMessage() + " Compruebe el email.");
			
			Date expireDate = AonDateUtils.addDays(new Date(), 1);
			String token = AonToken.build(auth, expireDate);
			String magicLink = urlNew + "?token=" + token; 
//			String magicLink = "https://" + url + "?token=" + token; 
			
			sendGmail(auth, magicLink, expireDate);
				
		} else throw new AonApiException(AonApiError.NOT_VALID_EMAIL.getMessage() + " Compruebe el email.");
	}
	
	public void sendGmail(Auth auth, String magicLink, Date expireDate) {
		SESMessage msg = new SESMessage()
			.setTo(auth.getEmail())
			.setFrom(AON_FROM)
			.setSubject("SOLICITUD ACCESO | " + "AON SOLUTIONS")
			.setBody(getContent(auth, magicLink, expireDate));
		SES.sendEmail(msg);
	}
	
	private String getContent(Auth auth, String magicLink, Date expireDate) {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.init();
        
        VelocityContext context = new VelocityContext();
        context.put("logo", AON_LOGO);
		context.put("parentName", "AON SOLUTIONS");
		context.put("name", auth.getName());
        context.put("expireDate", AonDateUtils.format(expireDate, "dd/MM/yyyy HH:mm"));
        context.put("magicLink", magicLink);
		context.put("contact", "booking@aonsolutions.es");
        
        Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/magic_link.vm");
        
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        return writer.toString();
    }
}
