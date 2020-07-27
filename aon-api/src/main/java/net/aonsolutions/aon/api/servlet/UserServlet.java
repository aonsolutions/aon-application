package net.aonsolutions.aon.api.servlet;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.AonRole;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonNumberUtils;

@SuppressWarnings("serial")
@WebServlet(name = "AonUserServlet", urlPatterns = {"/ms/api/user/*"})
public class UserServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(UserServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON USER SERVLET - GET METHOD");
		String token = req.getHeader("session_id");

		Boolean next = false;
		String[] pathInfo = req.getPathInfo()!= null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo().split("/") : null;
		
		String domainName = req.getHeader("domain_name");
		Integer domainId = AonNumberUtils.toInteger(req.getHeader("domain_id")) != null 
				? AonNumberUtils.toInteger(req.getHeader("domain_id")) : 0;
		Domain domain = AON.getDomain(domainName, domainId, "", f -> f.getNameProperty().eq(domainName));
		
		if(pathInfo != null) {
			JSONObject json = getDomainUser(domain, token);
			Utils.addCorsHeader(resp);
			Utils.giveBack(req, resp, json, new JSONObject());
		} else {
			JSONArray jsArray = new JSONArray();
			AON.getUserStream(domain.getName(), domain.getId(), "", f -> f.getDomainProperty().eq(domain.getId()))
			.forEach(r -> {
				jsArray.put(userToJSON(r));
			});
			Utils.addCorsHeader(resp);
			Utils.giveBack(req, resp, jsArray, new JSONObject());
		}

	}

	private JSONObject getDomainUser(Domain domain, String token) {
		AonToken aonToken = SECURITY.getAonToken(token);
		User user = AON.getUser(domain.getName(), domain.getId(), "", f -> (f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId()))).and(f.getAuthProperty().eq(aonToken.getAuth())));
		LinkedList<UserAppRole> roles = AON_SOLUTIONS.getUserAppRole(domain.getName(), domain.getId(), "", f -> f.getUserIdProperty().eq(user.getId())).collect(Collectors.toCollection(LinkedList::new));
		JSONObject userAppRoles = new JSONObject();
		if(roles.stream().filter(f -> f.getApp() == null && AonRole.ADMIN.equals(f.getRole())).count() > 0) {
			AonApp.aonValues().forEach(app -> userAppRoles.put(app.name(), AonRole.ADMIN));
		} else {
			roles.stream().forEach(uar -> userAppRoles.put(uar.getApp().name(), uar.getRole()));
		}
		JSONObject json = new JSONObject();
		json.put("id", user.getId());
		json.put("apps", userAppRoles);
		return json;
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
