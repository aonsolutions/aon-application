package net.aonsolutions.aon.api.servlet;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.RawdocUserData;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.AonRole;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.api.model.security.UserToolbar;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

import solutions.aon.aws.SES;

@SuppressWarnings("serial")
@WebServlet(name = "AonUserServlet", urlPatterns = {"/ms/api/user/*"})
public class UserServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(UserServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON USER SERVLET - GET METHOD");
		try {
			super.doGet(req, resp);
		
			switch (getPath()) {
			case "/":
				response(req, resp, getDomainUsers());
				break;
			case "/app":
				response(req, resp, getDomainUserRoles());
				break;
			case "/notice":
				List<String> schemas = AONContext.getSchemas();
				RawdocUserData rawdocUserData = new RawdocUserData();
				for(String schema : schemas) {
					rawdocUserData.append(AON.getRawdocUserData(getToken(), schema));
				}
				response(req, resp, rawdocUserData.toJSON());
				break;
			case "/info":
				response(req, resp, getDomainUser());
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
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
			case "/":
				response(req, resp, setUser());
				break;
			case "/app":
				response(req, resp, setUserAppRole());
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("EXAMPLE SERVLET - POST METHOD");
		try {
			super.doDelete(req, resp);
			switch (getPath()) {
			case "/":
				deleteUser();
				response(req, resp);
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONArray getDomainUserRoles() {
		Integer userId = getParams().opt("user") != null ? getParams().optInt("user") : null;
		User user = new User();
		if(userId != null) {
			user = AON.getUser(getDomain().getName(), getDomain().getId(), "", f -> f.getIdProperty().eq(userId));
		} else {
			AonToken aonToken = SECURITY.getAonToken(getToken());
			user = AON.getUser(getDomain().getName(), getDomain().getId(), "", f -> 
				(f.getDomainProperty().eq(getDomain().getId()).or(f.getDomainProperty().eq(getDomain().getParentId())))
				.and(f.getAuthProperty().eq(aonToken.getAuth()).or(f.getLoginProperty().eq(aonToken.getUuid()))));
		}
		return getUserRoles(getDomain(), user);
	}
	
	private JSONArray getDomainUsers() {
		JSONArray jsArray = new JSONArray();
		Stream<User> users;
		if(!getDomain().isParent() && getParams().opt("filter") != null && getParams().optString("filter").equals("entorno")) {
			users = AON.getDomainUserStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> f.getDomainProperty().eq(getDomain().getParentId()));
		} else if(getParams().opt("filter") != null && getParams().optString("filter").equals("shared")) {
			users = AON.getDomainUserStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> f.getDomainProperty().eq(getDomain().getId())
					.and(f.getSharedProperty().eq((byte)1)));
		} else users = AON.getDomainUserStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> f.getDomainProperty().eq(getDomain().getId()));

		users.forEach(r -> {
			JSONObject json = new JSONObject();
			if(r.getAuth() != null) {
				Auth auth = AON_SOLUTIONS.getAuth(getDomain().getName(), getDomain().getId(), r.getAuth());
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
				json.put("roles", getUserRoles(getDomain(), r));
				
				jsArray.put(json);
			} else jsArray.put(userToJSON(r, json));
		});
		return jsArray;
	}
	
	private JSONObject getDomainUser() {
		Integer userId = getParams().opt("user") != null ? getParams().optInt("user") : null;
		User user = new User();
		if(userId != null) {
			user = AON.getUser(getDomain().getName(), getDomain().getId(), "", f -> f.getIdProperty().eq(userId));
		} else {
			AonToken aonToken = SECURITY.getAonToken(getToken());
			user = AON.getUser(getDomain().getName(), getDomain().getId(), "", f -> 
				(f.getDomainProperty().eq(getDomain().getId()).or(f.getDomainProperty().eq(getDomain().getParentId())))
				.and(f.getAuthProperty().eq(aonToken.getAuth()).or(f.getLoginProperty().eq(aonToken.getUuid()))));
		}
		JSONObject json = new JSONObject();
		json.put("id",user.getId());
		json.put("name", user.getName());
		json.put("login", user.getLogin());
		json.put("newAon", UserToolbar.AON_SOLUTIONS.equals(user.getToolbar()));
		
		JSONArray scopes = new JSONArray();
		AON.getUserScopeStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(), userId, null)
		.forEach(us ->{
			JSONObject scope = new JSONObject();
			scope.put("id", us.getId());
			scope.put("name", us.getDescription());
			scopes.put(scope);
		});
		json.put("scopes", scopes);
		
		JSONArray workgroups = new JSONArray();
		AON.getUserWorkgroupStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> f.getUserIdProperty().eq(userId))
		.forEach(wg ->{
			JSONObject workgroup = new JSONObject();
			workgroup.put("id", wg.getWorkgroup().getId());
			workgroup.put("name", wg.getWorkgroup().getDescription());
			workgroups.put(workgroup);
		});
		json.put("workgroups", workgroups);
		return json;
	}
	
	private JSONArray getUserRoles(Domain domain, User user) {
		LinkedList<UserAppRole> roles = AON_SOLUTIONS.getUserAppRole(domain.getName(), domain.getId(), "", f -> f.getUserIdProperty().eq(user.getId())).collect(Collectors.toCollection(LinkedList::new));
		JSONArray userAppRoles = new JSONArray();
		if(roles.stream().count() == 0) {
//			User usr = AON.getUser(domain.getName(), user.getDomain(), user.getLogin());
//			for(Integer i = 0; i < usr.getUserRoles().length; i++) {				
//				com.esferalia.aon.occam.api.model.type.AonRole aonRole = usr.getUserRoles()[i];
//				if(com.esferalia.aon.occam.api.model.type.AonRole.ACCOUNTING_MANAGER.equals(aonRole)) {
//					userAppRoles.put(AonRole.ACCOUNTING_MANAGER.name());
//					setUserAppRole(domain, user, AonApp.ACCOUNTING, AonRole.ACCOUNTING);
//				} else if(com.esferalia.aon.occam.api.model.type.AonRole.ACCOUNTING.equals(aonRole)) {
//					userAppRoles.put(AonRole.ACCOUNTING.name());
//					setUserAppRole(domain, user, AonApp.ACCOUNTING, AonRole.ACCOUNTING);
//				} else if(com.esferalia.aon.occam.api.model.type.AonRole.CALL_CENTER_MANAGER.equals(aonRole)) {
//					userAppRoles.put(AonRole.MESSENGER_MANAGER.name());
//					setUserAppRole(domain, user, AonApp.MESSENGER, AonRole.MESSENGER_MANAGER);
//				} else if(com.esferalia.aon.occam.api.model.type.AonRole.CALL_CENTER.equals(aonRole)) {
//					userAppRoles.put(AonRole.MESSENGER.name());
//					setUserAppRole(domain, user, AonApp.MESSENGER, AonRole.MESSENGER);
//				} else if(com.esferalia.aon.occam.api.model.type.AonRole.DOCUMENT_MANAGER.equals(aonRole)) {
//					userAppRoles.put(AonRole.DOCUMENTAL_MANAGER.name());
//					setUserAppRole(domain, user, AonApp.DOCUMENTAL, AonRole.DOCUMENTAL_MANAGER);
//				} else if(com.esferalia.aon.occam.api.model.type.AonRole.DOCUMENT.equals(aonRole)) {
//					userAppRoles.put(AonRole.DOCUMENTAL.name());
//					setUserAppRole(domain, user, AonApp.DOCUMENTAL, AonRole.DOCUMENTAL);
//				} else if(com.esferalia.aon.occam.api.model.type.AonRole.FISCAL.equals(aonRole)) {
//					userAppRoles.put(AonRole.FISCAL_MANAGER);
//					setUserAppRole(domain, user, AonApp.FISCAL, AonRole.FISCAL_MANAGER);
//				} else if(com.esferalia.aon.occam.api.model.type.AonRole.PAYROLL.equals(aonRole)) {
//					userAppRoles.put(AonRole.PAYROLL_MANAGER.name());
//					setUserAppRole(domain, user, AonApp.PAYROLL, AonRole.PAYROLL_MANAGER);
//				} else if(com.esferalia.aon.occam.api.model.type.AonRole.FINANCE.equals(aonRole)) {
//					userAppRoles.put(AonRole.INVOICE_MANAGER.name());
//					setUserAppRole(domain, user, AonApp.INVOICE, AonRole.INVOICE_MANAGER);
//				} else if(com.esferalia.aon.occam.api.model.type.AonRole.ADMIN.equals(aonRole)) {
//					userAppRoles.put(AonRole.ADMIN);
//					setUserAppRole(domain, user, null, AonRole.ADMIN);
//				}		
//			}
		} else {
			roles.stream().forEach(uar -> userAppRoles.put(uar.getRole().name()));
		}
		return userAppRoles;
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
			try {
				uar = AON_SOLUTIONS.insertUserAppRole(domain.getName(), domain.getId(), "", uar);
			} catch ( Exception e ) {
				
			}
		} else {
			uar.setRole(role);
			AON_SOLUTIONS.updateUserAppRole(domain.getName(), domain.getId(), "", uar);
		}
	}
	
	private JSONObject setUserAppRole(){
		JSONArray roles = getData().optJSONArray("roles");

		for (Integer i = 0; i < roles.length(); i++) {
			JSONObject object = roles.getJSONObject(i);
			String app = object.optString("app");
			Integer user = object.optInt("user");
			Boolean active = object.optBoolean("active"); 
			String role = object.optString("role");

			AonApp aonApp = AonApp.safeValueOf(app);
			
			AonRole aonRole = AonRole.safeValueOf(role);
			if(aonRole != null) {
				UserAppRole uar = AON_SOLUTIONS.getUserAppRole(getDomain().getName(), getDomain().getId(), "", f -> 
					f.getDomainProperty().eq(getDomain().getId())
					.and(f.getUserIdProperty().eq(user))
					.and(f.getRoleProperty().eq(aonRole.value())))
					.findFirst().orElse(new UserAppRole());
				
				if(active) {
					if(uar.getId() == null) {
						uar = new UserAppRole();
						uar.setApp(aonApp)
							.setDomain(getDomain().getId())
							.setRole(aonRole)
							.setUser(user);
						uar = AON_SOLUTIONS.insertUserAppRole(getDomain().getName(), getDomain().getId(), "", uar);
					}
					if(!getDomain().isParent() && AonApp.TIMECONTROL.equals(uar.getApp())) {
						TaskHolder th = AON.getTaskHolder(getDomain().getName(), getDomain().getId(), "", f -> f.getDomainProperty().eq(getDomain().getId()).and(f.getUserIdProperty().eq(user)));
						if(th == null || th.getId() == null) {
							User u = AON.getUser(getDomain().getName(), getDomain().getId(), "", f -> f.getIdProperty().eq(user));
							Auth a = AON_SOLUTIONS.getAuth(getDomain().getName(), getDomain().getId(), u.getAuth());

							Registry r = null;
							if(!AonStringUtils.isBlank(a.getDocument())) {
								r = AON.getRegistry(getDomain().getName(), getDomain().getId(), "", f -> 
								f.getDomainProperty().eq(getDomain().getId())
								.and(f.getDocumentProperty().eq(a.getDocument())));
							}
							if(r == null || r.getId() == null) {
								r = AON.save(getDomain().getName(), getDomain().getId(), "", new Registry()
										.setDocument(a.getDocument())
										.setName(a.getName()+ " "+ a.getSurname())
										.setAlias(a.getName())
										.setDomain(getDomain()));
							}
							th = new TaskHolder()
									.setRegistryData(r)
									.setActive(true)
									.setUserId(user);
							AON.insertTaskHolder(getDomain().getName(), getDomain().getId(), "", th);
						} else if(!th.isActive()) {
							th.setActive(true);
							AON.updateTaskHolder(getDomain().getName(), getDomain().getId(), "", th);
						}
					} 
				
					if(AonRole.EMPLOYEE.equals(uar.getRole()) || AonRole.ENTERPRISE.equals(uar.getRole())) {
						LinkedList<AonRole> list = AonRole.EMPLOYEE.equals(uar.getRole())
								? AonRole.getEmployeeRoles() : AonRole.getEnterpriseRoles();
							
						Byte[] arr = list.stream().map(r -> r.value()).toArray(Byte[]::new);
						AON_SOLUTIONS.deleteUserAppRole(getDomain().getName(), getDomain().getId(), "", f -> 
							f.getDomainProperty().eq(getDomain().getId())
							.and(f.getUserIdProperty().eq(user))
							.and(f.getRoleProperty().notIn(arr)));
					}	 
				} else {
					if(uar.getId() != null) {
						Integer id = uar.getId();
						AON_SOLUTIONS.deleteUserAppRole(getDomain().getName(), getDomain().getId(), "", f -> f.getIdProperty().eq(id));
					}
					
					if(AonApp.TIMECONTROL.equals(uar.getApp())) {
						TaskHolder th = AON.getTaskHolder(getDomain().getName(), getDomain().getId(), "", f -> f.getDomainProperty().eq(getDomain().getId()).and(f.getUserIdProperty().eq(user)));
						if(th != null && th.getId() != null) {
							th.setActive(false);
							AON.updateTaskHolder(getDomain().getName(), getDomain().getId(), "", th);
						}
					}
				}
			}
		}
		
		return new JSONObject();
	}
	
	private JSONObject setUser() throws Exception {
		JSONObject js = new JSONObject();
		String email = getData().optString("email");
		String login = ramdonLogin();
		if(Utils.isEmail(email)) {
			Auth auth = AON_SOLUTIONS.getAuth(email);
			if(auth.getUuid() == null) {
				auth = createAuth(getDomain(), getData(), login);
				sendAuthCreateInfoMail(email, login);
			} else updateAuth(auth, getData());
			
			byte[] a = auth.getAuth();

			User user = AON.getDomainUserStream(getDomain().getName(), getDomain().getId(), "", f -> 
					f.getAuthProperty().eq(a)).findFirst().orElse(new User());
					
			if((user == null || user.getId() == null) && getData().opt("id") != null) {
				user = AON.getUser(getDomain().getName(), getDomain().getId(), "", f -> f.getIdProperty().eq(getData().getInt("id")));
			} 
			if(user != null && user.getId() != null && !user.getId().equals(getData().optInt("id"))){
				throw new Exception("El mail introducido ya está asociado a otro usuario.");
			}
			
			if(user != null && user.getId() != null && !user.getId().equals(getData().getInt("id"))){
				throw new Exception("El mail pertenece a un usuario del entorno.");
			}
			if(auth.getAuth() != null) {
				if(user == null || user.getId() == null) {
					user = createUser(getDomain(), getData(), login, auth);
					
				} else AON_SOLUTIONS.assignAuthToUser(getDomain().getName(), getDomain().getId(), user, auth.getAuth());

				js.put("id", user.getId());
				js.put("email", auth.getEmail() != null ? auth.getEmail() : "");
				js.put("uuid", auth.getUuid());
				js.put("name", auth.getName() != null ? auth.getName() : user.getName());
				js.put("surname", auth.getSurname() != null ? auth.getSurname() : "");
				js.put("document", auth.getDocument() != null ? auth.getDocument() : "");
				js.put("phone", auth.getPhone() != null ? auth.getPhone() : "");
				js.put("roles", getUserRoles(getDomain(), user));
			}
		} else {
			throw new Exception("El email no es correcto.");
		}
		return js;
	}
	
	private void deleteUser() {
		if(getData().opt("user") != null) {
			Integer userId = getData().optInt("user");
			User user = AON.getUser(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> f.getIdProperty().eq(userId));
			SECURITY.delete(getDomain(), getUser().getLogin(), user);
		}
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
	
	private User createUser(Domain domain, JSONObject json, String login, Auth auth) {
		Company cp = AON.getCompany(getDomain().getName(), getDomain().getId(), login, f -> f.getDomainProperty().eq(getDomain().getId()));
		
		User user = new User()
			.setAuth(auth.getAuth())
			.setActive(true)
			.setDomain(domain.getId())
			.setLogin(login)
			.setName(json.opt("name") != null ? json.getString("name") : login)
			.setShared(json.optBoolean("shared"))
			.setEnterprise(cp.getId());
		
		if(json.opt("document") != null) {
			String document = json.optString("document");
			if(!AonStringUtils.isBlank(document) && AonDocumentUtil.isValid(document)) {
				Integer registryId = null;
				Optional<Person> p = AON.getPerson(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> 
					f.getDomainProperty().eq(domain.getId())
					.and(f.getDocumentProperty().eq(document)));
				if(p.isPresent() && p.get().getId() != null) {
					registryId = p.get().getId();
				}
				
				if(registryId == null) {
					Registry r = AON.getRegistry(getDomain().getName(), getDomain().getId(), "", f -> 
						f.getDomainProperty().eq(domain.getId())
						.and(f.getDocumentProperty().eq(document)));
					registryId = r.getId();
				}
				user.setRegistry(registryId);
			}
		}	
		user = AON.insertUser(domain.getName(), domain.getId(), "", user);
		AON.updateUserPassword(domain.getName(), domain.getId(),getUser().getLogin(), user.getId(), auth.getPassword());
		Scope s = getScope();
		if(s != null) {
			AON.insertUserScope(getDomain().getName(), getDomain().getId(), getUser().getLogin(), new UserScope()
					.setDomain(getDomain().getId())
					.setScope(s.getId())
					.setUserId(user.getId()));
		}
		
		ApplicationParameter a = AON.getApplicationParameter(getDomain().getName(), getDomain().getId(), getUser().getLogin(), AppParam.AON_PORTAL);
		ApplicationParameter appParam = new ApplicationParameter()
				.setDomain(getDomain().getId())
				.setValue("288")
				.setName(AppParam.AON_PORTAL.getValue());

		if(a == null && a.getId() == null)
			AON.insertApplicationParameter(getDomain().getName(), getDomain().getId(), getUser().getLogin(), appParam);
	
		AON_SOLUTIONS.saveUserFinancePortal(getDomain(), getUser().getLogin(), user.getId());		
		return user;
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
	
	private void sendAuthCreateInfoMail(String email, String password) {
		String from = "no-reply@aon.solutions"; //auth.getEmail();
		String to = email;
		String body = authCreateInfoContent(email, password);
		String subject = "NUEVO USUARIO | AON SOLUTIONS"; 
		SES.sendEmail(from, to, subject, body);
	}
	
	private String authCreateInfoContent(String email, String password) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
			
		VelocityContext context = new VelocityContext();
		context.put("email", email);
		context.put("password", password);
			
		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/auth_create_info.vm");
			
		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
	
	private Scope getScope() {
		Scope s = AON.getScopeStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(),
				f -> f.getDomainProperty().eq(getDomain().getId()).and(f.getDescriptionProperty().eq("GENERAL")))
				.findFirst().orElse(null);
		if(s == null && getDomain().getParentId() != null) {
			s =   AON.getScopeStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(),
					f -> f.getDomainProperty().eq(getDomain().getParentId()).and(f.getDescriptionProperty().eq("GENERAL")))
					.findFirst().orElse(null);
		}
		
		if(s== null){
			s = AON.getScopeStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(),
					f -> f.getDomainProperty().eq(getDomain().getId()))
					.findFirst().orElse(null);
		}
		if(s == null && getDomain().getParentId() != null) {
			s = AON.getScopeStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(),
					f -> f.getDomainProperty().eq(getDomain().getParentId()))
					.findFirst().orElse(null);
		}
		return s;
	}
	
}
