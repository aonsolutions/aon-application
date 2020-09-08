package net.aonsolutions.aon.api.servlet;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.Auth;

@SuppressWarnings("serial")
@WebServlet(name = "AonAuthServlet", urlPatterns = {"/ms/api/auth/*"})
public class AuthServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(AuthServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON AUTH SERVLET - GET METHOD");
		String token = req.getHeader("session_id");
		AonToken aonToken = SECURITY.getAonToken(token);
		Auth auth = AON_SOLUTIONS.getAuth(aonToken.getSchemaFirstDomain(), 0, aonToken.getAuth());
		
		JSONObject json = new JSONObject();
		json.put("email", auth.getEmail());
		json.put("uuid", auth.getUuid());
		json.put("name", auth.getName() != null ? auth.getName() : "");
		json.put("surname", auth.getSurname() != null ? auth.getSurname() : "");
		json.put("document", auth.getDocument() != null ? auth.getDocument() : "");
		json.put("phone", auth.getPhone() != null ? auth.getPhone() : "");
		
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, json, new JSONObject());
	}	
}
