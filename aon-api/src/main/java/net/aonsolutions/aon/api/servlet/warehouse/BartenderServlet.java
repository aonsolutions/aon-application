package net.aonsolutions.aon.api.servlet.warehouse;

import java.io.File;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;
import net.aonsolutions.aon.api.servlet.ExampleServlet;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

@SuppressWarnings("serial")
@WebServlet(name = "BartenderServlet", urlPatterns = {"/ms/api/bartender/*"})
public class BartenderServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(ExampleServlet.class.getName());
	
	public static final String BARTENDER = "/";
	public static final String EMAIL = "/email";
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		post(req, resp);
	}
	
	private void post(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(BARTENDER, BartenderServlet::getAction)
				.addRoute(EMAIL, BartenderServlet::sendEmail)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONObject getAction(AonApiData api) {
		return new JSONObject();
	}
	
	private static JSONObject sendEmail(AonApiData api) {
		String email = "bartender.paturpat@gmail.com"; // JsonUtils.getString(api.getData(), IJsonNames.EMAIL);
		File file = BartenderFile.generate(api.getData());

		SESMessage ses = new SESMessage()
				.setAlias("AON | BARTENDER")
				.setSubject("IMPRESIÓN_ETIQUETAS")
				.setBody("")
				.setTo(email)
				.setFile(file);
		
		SES.sendEmailWithAttachment(ses);
		return new JSONObject();
	}
	
	
	
}
