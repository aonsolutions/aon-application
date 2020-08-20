package net.aonsolutions.aon.api.servlet;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
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
import com.esferalia.aon.occam.api.model.security.Auth;
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

		String[] pathInfo = req.getPathInfo()!= null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo().split("/") : null;
		
		String domainName = req.getHeader("domain_name");
		Integer domainId = !"null".equalsIgnoreCase(req.getHeader("domain_id")) && AonNumberUtils.toInteger(req.getHeader("domain_id")) != null 
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
				JSONObject json = getUserApps(domain, r);
				if(r.getAuth() != null) {
					Auth auth = AON_SOLUTIONS.getAuth(domainName, domainId, r.getAuth());
					if(auth.getEmail() == null) {
						auth = AON_SOLUTIONS.getAuth(r.getAuth());
					}
					json.put("id", r.getId());
					json.put("email", auth.getEmail());
					json.put("uuid", auth.getUuid());
					json.put("name", auth.getName() != null ? auth.getName() : r.getName());
					json.put("surname", auth.getSurname() != null ? auth.getSurname() : "");
					json.put("document", auth.getDocument() != null ? auth.getDocument() : "");
					json.put("phone", auth.getPhone() != null ? auth.getPhone() : "");
					jsArray.put(json);
				} else jsArray.put(userToJSON(r, json));
			});
			Utils.addCorsHeader(resp);
			Utils.giveBack(req, resp, jsArray, new JSONObject());
		}
	}

	private JSONObject getDomainUser(Domain domain, String token) {
		AonToken aonToken = SECURITY.getAonToken(token);
		User user = AON.getUser(domain.getName(), domain.getId(), "", f -> (f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId()))).and(f.getAuthProperty().eq(aonToken.getAuth())));
		return getUserApps(domain, user);
	}
	
	private JSONObject getUserApps(Domain domain, User user) {
		LinkedList<UserAppRole> roles = AON_SOLUTIONS.getUserAppRole(domain.getName(), domain.getId(), "", f -> f.getUserIdProperty().eq(user.getId())).collect(Collectors.toCollection(LinkedList::new));
		JSONObject userAppRoles = new JSONObject();
		Boolean admin = true;
		if(roles.stream().filter(f -> f.getApp() == null && AonRole.ADMIN.equals(f.getRole())).count() > 0) {
			admin = true;
			AON_SOLUTIONS.getDomainApp(domain.getName(), domain.getId(), "", f -> f.getDomainProperty().eq(domain.getId())).forEach(da -> {
				if(da.getActive()) {
					userAppRoles.put(da.getApp().name(), AonRole.ADMIN);
				}
			});
//			AonApp.aonValues().forEach(app -> userAppRoles.put(app.name(), AonRole.ADMIN));
		} else if(roles.stream().count() == 0) {
			User usr = AON.getUser(domain.getName(), user.getDomain(), user.getLogin());
			for(Integer i = 0; i < usr.getUserRoles().length; i++) {				
				com.esferalia.aon.occam.api.model.type.AonRole aonRole = usr.getUserRoles()[i];
				if(com.esferalia.aon.occam.api.model.type.AonRole.ACCOUNTING_MANAGER.equals(aonRole)) {
					userAppRoles.put(AonApp.ACCOUNTING.name(), AonRole.ADMIN);
					setUserAppRole(domain, user, AonApp.ACCOUNTING, AonRole.ADMIN);
				} else if(com.esferalia.aon.occam.api.model.type.AonRole.ACCOUNTING.equals(aonRole)) {
					userAppRoles.put(AonApp.ACCOUNTING.name(), AonRole.GUEST);
					setUserAppRole(domain, user, AonApp.ACCOUNTING, AonRole.GUEST);
				} else if(com.esferalia.aon.occam.api.model.type.AonRole.CALL_CENTER_MANAGER.equals(aonRole)) {
					userAppRoles.put(AonApp.HELPDESK.name(), AonRole.ADMIN);
					setUserAppRole(domain, user, AonApp.HELPDESK, AonRole.ADMIN);
				} else if(com.esferalia.aon.occam.api.model.type.AonRole.CALL_CENTER.equals(aonRole)) {
					userAppRoles.put(AonApp.HELPDESK.name(), AonRole.GUEST);
					setUserAppRole(domain, user, AonApp.HELPDESK, AonRole.GUEST);
				} else if(com.esferalia.aon.occam.api.model.type.AonRole.DOCUMENT_MANAGER.equals(aonRole)) {
					userAppRoles.put(AonApp.DOCUMENTAL.name(), AonRole.ADMIN);
					setUserAppRole(domain, user, AonApp.DOCUMENTAL, AonRole.ADMIN);
				} else if(com.esferalia.aon.occam.api.model.type.AonRole.DOCUMENT.equals(aonRole)) {
					userAppRoles.put(AonApp.DOCUMENTAL.name(), AonRole.GUEST);
					setUserAppRole(domain, user, AonApp.DOCUMENTAL, AonRole.GUEST);
				} else if(com.esferalia.aon.occam.api.model.type.AonRole.FISCAL.equals(aonRole)) {
					userAppRoles.put(AonApp.FISCAL.name(), AonRole.ADMIN);
					setUserAppRole(domain, user, AonApp.FISCAL, AonRole.ADMIN);
				} else if(com.esferalia.aon.occam.api.model.type.AonRole.PAYROLL.equals(aonRole)) {
					userAppRoles.put(AonApp.PAYROLL.name(), AonRole.ADMIN);
					setUserAppRole(domain, user, AonApp.PAYROLL, AonRole.ADMIN);
				} else if(com.esferalia.aon.occam.api.model.type.AonRole.FINANCE.equals(aonRole)) {
					userAppRoles.put(AonApp.INVOICE.name(), AonRole.ADMIN);
					setUserAppRole(domain, user, AonApp.INVOICE, AonRole.ADMIN);
				} else if(com.esferalia.aon.occam.api.model.type.AonRole.ADMIN.equals(aonRole)) {
					admin = true;
					setUserAppRole(domain, user, null, AonRole.ADMIN);
				}		
			}
		} else {
			roles.stream().forEach(uar -> userAppRoles.put(uar.getApp().name(), uar.getRole()));
		}
		JSONObject json = new JSONObject();
		json.put("id", user.getId());
		json.put("admin", admin);
		json.put("apps", userAppRoles);
		return json;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("USER SERVLET - POST METHOD");
		String[] pathInfo = req.getPathInfo()!= null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo().split("/") : null;

		String domainName = req.getHeader("domain_name");
		Integer domainId = !"null".equalsIgnoreCase(req.getHeader("domain_id")) && AonNumberUtils.toInteger(req.getHeader("domain_id")) != null 
				? AonNumberUtils.toInteger(req.getHeader("domain_id")) : 0;
		Domain domain = AON.getDomain(domainName, domainId, "", f -> f.getNameProperty().eq(domainName));
		
		JSONObject json = Utils.getRequestJSON(req);
		if(pathInfo != null) {
			if("app".equalsIgnoreCase(pathInfo[1])) {
				json = setUserAppRole(json);
			}
		} else {
			json = setUser(domain, json);
		}
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, json, new JSONObject());
	}
	
	private void setUserAppRole(Domain domain, User user, AonApp app, AonRole role){
		UserAppRole uar = AON_SOLUTIONS.getUserAppRole(domain.getName(), domain.getId(), "", f -> 
			f.getDomainProperty().eq(domain.getId())
				.and(f.getUserIdProperty().eq(user.getId()))
				.and((app != null ? f.getAppProperty().eq(app.value()): f.getAppProperty().eq((byte) -1))))
			.findFirst().orElse(new UserAppRole());
		if(uar.getId() == null) {
			uar = new UserAppRole();
			uar.setApp(app)
				.setDomain(domain.getId())
				.setRole(role)
				.setUser(user.getId());
			uar = AON_SOLUTIONS.insertUserAppRole(domain.getName(), domain.getId(), "", uar);
		} else {
			uar.setRole(role);
			AON_SOLUTIONS.updateUserAppRole(domain.getName(), domain.getId(), "", uar);
		}
	}
	
	private JSONObject setUserAppRole(JSONObject json){
		String domainName = json.optString("domain");
		String app = json.optString("app");
		String role = json.optString("role");
		Integer user = json.optInt("user");
		Boolean active = json.optBoolean("active"); 

		AonApp aonApp = AonApp.safeValueOf(app);
		AonRole aonRole = AonRole.safeValueOf(role);
		Domain domain = AON.getDomain(domainName, 0, "", f -> f.getNameProperty().eq(domainName));

		UserAppRole uar = AON_SOLUTIONS.getUserAppRole(domain.getName(), domain.getId(), "", f -> 
		f.getDomainProperty().eq(domain.getId())
			.and(f.getUserIdProperty().eq(user))
			.and((aonApp != null ? f.getAppProperty().eq(aonApp.value()): f.getAppProperty().isNull()))).findFirst().orElse(new UserAppRole());
		
		if(active) {
			if(uar.getId() == null) {
				uar = new UserAppRole();
				uar.setApp(aonApp)
					.setDomain(domain.getId())
					.setRole(aonRole)
					.setUser(user);
				uar = AON_SOLUTIONS.insertUserAppRole(domain.getName(), domain.getId(), "", uar);
			} else {
				uar.setRole(aonRole);
				AON_SOLUTIONS.updateUserAppRole(domain.getName(), domain.getId(), "", uar);
			}
		} else {
			if(uar.getId() != null) {
				Integer id = uar.getId();
				AON_SOLUTIONS.deleteUserAppRole(domain.getName(), domain.getId(), "", f -> f.getIdProperty().eq(id));
			}
		}

		return uar.toJSON();
	}
	
	private JSONObject setUser(Domain domain, JSONObject json) {
		JSONObject js = new JSONObject();
		String email = json.optString("email");
		String login = ramdonLogin();
		if(Utils.isEmail(email)) {
			Auth auth = AON_SOLUTIONS.getAuth(email);
			if(auth.getUuid() == null) {
				auth = createAuth(domain, json, login);
			} else updateAuth(auth, json);
			
			byte[] a = auth.getAuth();
			User user = AON.getUser(domain.getName(), domain.getId(), "", f -> f.getAuthProperty().eq(a));
			if((user == null || user.getId() == null) && json.opt("id") != null) {
				user = AON.getUser(domain.getName(), domain.getId(), "", f -> f.getIdProperty().eq(json.getInt("id")));
			} 
			
			if(auth.getAuth() != null) {
				if(user == null || user.getId() == null) {
					createUser(domain, json, login, auth.getAuth());
				} else AON_SOLUTIONS.assignAuthToUser(domain.getName(), domain.getId(), user, auth.getAuth());

				js.put("id", user.getId());
				js.put("email", auth.getEmail() != null ? auth.getEmail() : "");
				js.put("uuid", auth.getUuid());
				js.put("name", auth.getName() != null ? auth.getName() : user.getName());
				js.put("surname", auth.getSurname() != null ? auth.getSurname() : "");
				js.put("document", auth.getDocument() != null ? auth.getDocument() : "");
				js.put("phone", auth.getPhone() != null ? auth.getPhone() : "");

				JSONObject apps = getUserApps(domain, user);
				js.put("admin", apps.optBoolean("admin"));
				js.put("apps", apps.optJSONObject("apps"));
			}
		}
		return js;
	}
	
	private Auth createAuth(Domain domain, JSONObject json, String login) {
		String pass = Utils.createPasswordHash(json.optString("email"), login);
		Auth auth = new Auth()
			.setEmail(json.optString("email"))
			.setPassword(pass)
			.setName(json.optString("name"))
			.setSurname(json.optString("surname"))
			.setDocument(json.optString("document"))
			.setPhone(json.optString("phone"));
		auth = AON_SOLUTIONS.insertAuth(domain.getName(), domain.getId(), auth);
		return auth;
	}
	
	private Auth updateAuth(Auth auth, JSONObject json) {
		if(json.opt("document") != null) auth.setDocument(json.getString("document"));
		if(json.opt("phone") != null) auth.setPhone(json.getString("phone"));
		if(json.opt("name") != null) auth.setName(json.getString("name"));
		if(json.opt("surname") != null) auth.setSurname(json.getString("surname"));
		
		return AON_SOLUTIONS.updateAuth(auth);
	}
	
	private User createUser(Domain domain, JSONObject json, String login, byte[] auth) {
		User user = new User()
			.setAuth(auth)
			.setActive(true)
			.setDomain(domain.getId())
			.setLogin(login)
			.setName(json.opt("name") != null ? json.getString("name") : login)
			.setShared(json.optBoolean("shared"));
		return AON.insertUser(domain.getName(), domain.getId(), "", user);
	}
	
	private String ramdonLogin() {
		Random rnd = new Random();
		Integer i = rnd.nextInt(100000000-10000000+1)+10000000;
		return i.toString();	
	}
	
	private JSONObject userToJSON(User user, JSONObject json) {
		json.put("id", user.getId());
		json.put("name", user.getName());
		json.put("surname", "");
		if(json.opt("email") == null)
			json.put("email", "");
		return json;
	}	
	
}
