package net.aonsolutions.aon.api.servlet;
import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;

@SuppressWarnings("serial")
@WebServlet(name = "AonUserServlet", urlPatterns = {"/ms/api/user/*"})
public class UserServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(UserServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON USER SERVLET - GET METHOD");
		String token = req.getHeader("session_id");
		JSONArray jsArray = new JSONArray();
		Boolean next = false;
		
		String domainName = "udapa.aibanez.net";// req.getHeader("domain_name");
		Integer domainId = 3049; // AonNumberUtils.toInteger(req.getHeader("domain_id")) != null 
//				? AonNumberUtils.toInteger(req.getHeader("domain_id")) : 0;
		Domain domain = AON.getDomain(domainName, domainId, "", f -> f.getNameProperty().eq(domainName));
		AONContext.getAONContext(domainName, domainId, "");

		AON.getUserStream(domain.getName(), domain.getId(), "", f -> f.getDomainProperty().eq(domain.getId()))
		.forEach(r -> {
			jsArray.put(userToJSON(r));
		});

		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, jsArray, new JSONObject());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("USER SERVLET - POST METHOD");
	}
	
	private JSONObject userToJSON(User user) {
		JSONObject json = new JSONObject();
		json.put("id", user.getId());
		json.put("name", user.getName());
		json.put("surname", "");
		json.put("email", "email");
		return json;
	}
}
