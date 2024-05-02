package net.aonsolutions.aon.api.servlet;

import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

@WebServlet(name = "MagicLinkServlet", urlPatterns = {"/ms/api/magicLink/*"})
public class MagicLinkServlet extends AonApiHttpServlet {

	private static final Logger LOGGER  = Logger.getLogger(MagicLinkServlet.class.getName());

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
		String email 	 = JsonUtils.getString(json, IJsonNames.EMAIL);
		String urlPortal = JsonUtils.optString(json, IJsonNames.URL);
		if(Utils.isEmail(email)) {
			Auth auth = AON_SOLUTIONS.getAuth(email);
			if(auth.isEmpty()) {
	             throw new AonApiException(AonApiError.NOT_EXIST_USER.getMessage());
			}
			Date expireDate = AonDateUtils.addDays(new Date(), 1);
			String token = AonToken.build(auth, expireDate);
			String magicLink = !AonStringUtils.isBlank(urlPortal) ? urlPortal + "?token=" + token : "https://"+ url + "?token=" + token; 
			sendGmail(auth, magicLink, expireDate);
		} else throw new AonApiException(AonApiError.NOT_VALID_EMAIL.getMessage());
	}
	
	public void sendGmail(Auth auth, String magicLink, Date expireDate) {
		SESMessage msg = new SESMessage()
			.setTo(auth.getEmail())
			.setSubject("MAGIC LINK - AON SOLUTIONS")
			.setBody(getContent(auth, magicLink, expireDate));
		SES.sendEmail(msg);
	}
	
	private String getContent(Auth auth, String magicLink, Date expireDate) {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.init();
        
        VelocityContext context = new VelocityContext();
        context.put("name", auth.getName());
        context.put("expireDate", AonDateUtils.format(expireDate, "dd/MM/yyyy HH:mm"));
        context.put("magicLink", magicLink);
        
        Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/magic_link.vm");
        
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        return writer.toString();
    }
	
}
