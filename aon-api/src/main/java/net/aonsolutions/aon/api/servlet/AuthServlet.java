package net.aonsolutions.aon.api.servlet;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.TaskHolder;

import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonAuthServlet", urlPatterns = {"/ms/api/auth/*"})
public class AuthServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(AuthServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON AUTH SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req, resp);
			Auth auth = new Auth();
			if(api.getParams().opt("email") != null) {
				auth = AON_SOLUTIONS.getAuth(api.getParams().optString("email"));
			} else if(api.getParams().opt("task_holder") != null){
				TaskHolder th = AON.getTaskHolder(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f-> f.getIdProperty().eq(api.getParams().optInt("task_holder")));
				User user = AON.getUser(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(th.getUserId()));
				auth = AON_SOLUTIONS.getAuth(user.getAuth());	
			} else {
				AonToken aonToken = SECURITY.getAonToken(api.getToken());
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
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
			case "/password":
				response(req, resp, changePassword(api));
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
			
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONObject changePassword(AonApiData api) throws Exception {
		String oldPassword = api.getData().opt("oldPassword") != null ? api.getData().optString("oldPassword") : null;
		if(oldPassword == null) {
			throw new Exception("La contraseña introducida es incorrecta.");
		} 
		
		AonToken aonToken = SECURITY.getAonToken(api.getToken());
		Auth auth = AON_SOLUTIONS.getAuth(aonToken.getSchemaFirstDomain(), 0, aonToken.getAuth());
		auth.setSchema(aonToken.getSchema());
		String password = api.getData().optString("newPassword");
		String pass = Utils.createPasswordHash(auth.getEmail(), password);
		auth.setPassword(pass);
		AON_SOLUTIONS.updateAuthPassword(auth);
		return new JSONObject();
	}
}
