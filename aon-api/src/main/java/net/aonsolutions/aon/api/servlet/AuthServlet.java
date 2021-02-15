package net.aonsolutions.aon.api.servlet;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.Auth;

@SuppressWarnings("serial")
@WebServlet(name = "AonAuthServlet", urlPatterns = {"/ms/api/auth/*"})
public class AuthServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(AuthServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON AUTH SERVLET - GET METHOD");
		try {
			super.doGet(req, resp);
			Auth auth = new Auth();
			if(getParams().opt("email") != null) {
				auth = AON_SOLUTIONS.getAuth(getParams().optString("email"));
			} else {
				AonToken aonToken = SECURITY.getAonToken(getToken());
				auth = AON_SOLUTIONS.getAuth(aonToken.getSchemaFirstDomain(), 0, aonToken.getAuth());
			}
			
			JSONObject json = new JSONObject();
			json.put("email", auth.getEmail());
			json.put("uuid", auth.getUuid());
			json.put("name", auth.getName() != null ? auth.getName() : "");
			json.put("surname", auth.getSurname() != null ? auth.getSurname() : "");
			json.put("document", auth.getDocument() != null ? auth.getDocument() : "");
			json.put("phone", auth.getPhone() != null ? auth.getPhone() : "");
			
			response(req, resp, json);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("EXAMPLE SERVLET - POST METHOD");
		try {
			super.doPost(req, resp);
			switch (getPath()) {
			case "/password":
				response(req, resp, changePassword());
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
			
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONObject changePassword() throws Exception {
		String oldPassword = getData().opt("oldPassword") != null ? getData().optString("oldPassword") : null;
		if(oldPassword == null) {
			throw new Exception("La contraseña introducida es incorrecta.");
		} 
		
		AonToken aonToken = SECURITY.getAonToken(getToken());
		Auth auth = AON_SOLUTIONS.getAuth(aonToken.getSchemaFirstDomain(), 0, aonToken.getAuth());
		auth.setSchema(aonToken.getSchema());
		String password = getData().optString("newPassword");
		String pass = Utils.createPasswordHash(auth.getEmail(), password);
		auth.setPassword(pass);
		AON_SOLUTIONS.updateAuthPassword(auth);
		return new JSONObject();
	}
}
