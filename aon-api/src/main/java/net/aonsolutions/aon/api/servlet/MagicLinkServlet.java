package net.aonsolutions.aon.api.servlet;

import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.Auth;

import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

@SuppressWarnings("serial")
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
		String email = JsonUtils.getString(json, IJsonNames.EMAIL);
		if(Utils.isEmail(email)) {
			Auth auth = AON_SOLUTIONS.getAuth(email);
			String token = AonToken.build(auth, null);
			String magicLink = "https://" + url + "?token=" + token; 
			sendGmail(email, magicLink);
		}	
	}
	
	public void sendGmail(String to, String magicLink) {
		SESMessage msg = new SESMessage()
			.setTo(to)
			.setSubject("MAGIC LINK - AON SOLUTIONS")
			.setBody(getContent(magicLink));
		SES.sendEmail(msg);
	}
	
	private String getContent(String magicLink) {
		String msg = "<div style='margin-left: -30px;'>"
				+"<div style='margin: 7px 15px 14px 30px;line-height: 18px;font-size: 13px;box-shadow: 0px 1px 2px rgba(0, 0, 0, 0.075);'>";
		msg = msg + "<a href='"+ magicLink +"'> MAGIC LINK</a>";

		msg = msg + "</div> </div>";
		return msg;
	}
}
