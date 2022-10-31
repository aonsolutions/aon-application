package net.aonsolutions.aon.api.servlet;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
import com.esferalia.aon.occam.api.json.AuthJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.UserJSON;
import com.esferalia.aon.occam.api.json.WorkgroupJSON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.Properties.UserProperties;
import com.esferalia.aon.occam.api.model.RawdocUserData;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.aonsolutions.AonRole;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.api.model.security.UserToolbar;
import com.esferalia.aon.occam.api.model.security.UserType;
import com.esferalia.aon.occam.api.model.security.UserWorkgroup;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

@SuppressWarnings("serial")
@WebServlet(name = "AonUserServlet", urlPatterns = {"/ms/api/user/*"})
public class UserServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(UserServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON USER SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req);
		
			switch (api.getPath()) {
			case "/":
				response(req, resp, getDomainUsers(api));
				break;
			case "/roles":
				response(req, resp, getUserRoles(api.getDomain(), JsonUtils.getInteger(api.getData(), IJsonNames.USER)));
				break;
			case "/list":
				response(req, resp, getUsers(api));
				break;
			case "/notice":
				List<String> schemas = AONContext.getSchemas();
				RawdocUserData rawdocUserData = new RawdocUserData();
				for(String schema : schemas) {
					rawdocUserData.append(AON.getRawdocUserData(api.getToken(), schema));
				}
				response(req, resp, rawdocUserData.toJSON());
				break;
			case "/info":
				response(req, resp, getDomainUser(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		try {
			AonApiData api = initialize(req);
			LOGGER.info("[POST] /user" + api.getPath());
			switch (api.getPath()) {
			case "/":
				response(req, resp, setUser(api));
				break;
			case "/email":
				response(req, resp, sendAuthInfoMail(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("EXAMPLE SERVLET - PUT METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/workgroup":
				response(req, resp, insertUserWorkgroup(api));
				break;
			case "/service":
				response(req, resp, saveServiceAccount(api));
				break;		
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("EXAMPLE SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				deleteUser(api);
				response(req, resp);
				break;
			case "/workgroup":
				response(req, resp, removeUserWorkgroup(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	

	private JSONObject saveServiceAccount(AonApiData api) {
		String name = JsonUtils.getString(api.getData(), IJsonNames.NAME);
		User user = new User()
				.setActive(true)
				.setType(UserType.SERVICE)
				.setDomain(api.getDomain().getId())
				.setLogin(name)
				.setName(name)
				.setToolbar(UserToolbar.AON_SOLUTIONS);
		AON.insertUser(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), user);
		
		return new JSONObject();
	}
	
	// USER WORKGROUP
	
	private JSONObject insertUserWorkgroup(AonApiData api) {
		Integer wId = api.getData().getInt(IJsonNames.WORKGROUP);
		Integer uId = api.getData().getInt(IJsonNames.USER);
		User user = AON.getUser(api.getDomain(), api.getUser().getLogin(), f-> f.getIdProperty().eq(uId))
			.addWorkgroup(new Workgroup().setId(wId).setDomain(api.getDomain().getId()));
		AON.saveUserWorkgroups(api.getDomain(), api.getUser().getLogin(), user);
		return new JSONObject();
	}
		
	private JSONObject removeUserWorkgroup(AonApiData api) {
		Integer wId = api.getData().getInt(IJsonNames.WORKGROUP);
		Integer uId = api.getData().getInt(IJsonNames.USER);
		User user = AON.getUser(api.getDomain(), api.getUser().getLogin(), f-> f.getIdProperty().eq(uId));
		AON.deleteUserWorkgroup(api.getDomain(), api.getUser().getLogin(), user, new Workgroup().setId(wId).setDomain(api.getDomain().getId()));
		return new JSONObject();
	}
	
	private JSONArray getUsers(AonApiData api) {
		Integer page = JsonUtils.getInteger(api.getData(), IJsonNames.PAGE);
		Integer perPage = JsonUtils.getInteger(api.getData(), IJsonNames.PER_PAGE);
		return UserJSON.toJSON(
			AON.getDomainUserStream(api.getDomain(), api.getUser(), page, perPage, f -> userFilter(api, f))
			.map(r -> !r.getAuth().isEmpty() && r.getAuth().getEmail() == null
				? r.setAuth(AON_SOLUTIONS.getAuth(r.getAuth().getAuth())) 
				: r)
			);
	}
	
	private JSONArray getDomainUsers(AonApiData api) {
		JSONArray jsArray = new JSONArray();
		AON.getDomainUserStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> userFilter(api, f))
		.map(user -> {
			user.setWorkgroups(AON.getUserWorkgroupStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getUserIdProperty().eq(user.getId()))
					.map(UserWorkgroup::getWorkgroup).collect(Collectors.toCollection(LinkedList::new)));
			return user;
		})
		.forEach(r -> {
			JSONObject json = new JSONObject();
			if(r.getAuth() != null) {
				Auth auth = AON_SOLUTIONS.getAuth(api.getDomain().getName(), api.getDomain().getId(), r.getAuth().getAuth());
				if(auth.getEmail() == null) {
					auth = AON_SOLUTIONS.getAuth(r.getAuth().getAuth());
				}
				json.put(IJsonNames.ID, r.getId());
				json.put(IJsonNames.EMAIL, auth.getEmail());
				json.put(IJsonNames.UUID, auth.getUuid());
				json.put(IJsonNames.NAME, auth.getName() != null ? auth.getName() : r.getName());
				json.put(IJsonNames.SURNAME, auth.getSurname() != null ? auth.getSurname() : "");
				json.put(IJsonNames.DOCUMENT, auth.getDocument() != null ? auth.getDocument() : "");
				json.put(IJsonNames.PHONE, auth.getPhone() != null ? auth.getPhone() : "");
				json.put(IJsonNames.ROLES, getUserRoles(api.getDomain(), r.getId()));
				json.put(IJsonNames.PORTAL, r.isPortal());
				json.put(IJsonNames.SHARED, r.isShared());
				json.put(IJsonNames.LOGIN, r.getLogin());
				json.put(IJsonNames.WORKGROUPS, WorkgroupJSON.toJSON(r.getWorkgroups()));
				jsArray.put(json);
			} else jsArray.put(userToJSON(r, json));
		});
		return jsArray;
	}
	
	private Filter userFilter(AonApiData api, UserProperties f) {
		JSONObject params = api.getData();
		
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		
		if(!api.getDomain().isParent() && params.opt("filter") != null 
				&& params.optString("filter").equals("entorno")) {
			filter = f.getDomainProperty().eq(api.getDomain().getParentId());
			if(api.getDomain().getScope() != null) {
				filter = filter.and(f.getScopeProperty().eq(api.getDomain().getScope()));
			}
		} else if(params.opt("filter") != null &&
				params.optString("filter").equals("shared")) {
			filter = f.getDomainProperty().eq(api.getDomain().getId())
					.and(f.getSharedProperty().eq((byte)1));
		} else if(params.opt("filter") != null &&
				params.optString("filter").equals("all")) {
			
			Filter all = f.getDomainProperty().eq(api.getDomain().getParentId());
			
			if(api.getDomain().getScope() != null) {
				all = all.and(f.getScopeProperty().eq(api.getDomain().getScope()));
			}
			
			filter = filter.or(all);
		}
		
		if(!AonStringUtils.isBlank(params.optString(IJsonNames.VALUE))) {
			String value = params.optString(IJsonNames.VALUE);
			Filter valueFilter = f.getLoginProperty().like("%" + value + "%")
					.or(f.getNameProperty().like("%" + value + "%"))
					.or(f.getAuthEmailProperty().like("%" + value + "%"))
					.or(f.getAuthNameProperty().like("%" + value + "%"))
					.or(f.getAuthDocumentProperty().like("%" + value + "%"));
			filter = filter.and(valueFilter);
		}
		
		if(params.opt(IJsonNames.TYPE) != null) {
			filter = filter.and(f.getTypeProperty().eq(UserType.safeValueOf(params.getString(IJsonNames.TYPE)).value()));
		}
		
		if(params.opt(IJsonNames.WORKGROUP) != null) {
			filter = filter.and(f.getWorkgroupProperty().eq(params.optInt(IJsonNames.WORKGROUP)));
		}
		
		if(params.opt(IJsonNames.ID) != null) {
			filter = filter.and(f.getIdProperty().eq(JsonUtils.getInteger(params, IJsonNames.ID)));
		}
		
		
		if(params.opt("task_holder_empty") != null) {
			Filter newFilter = f.getTaskHolderProperty().isNull()
			.or(f.getTaskHolderActiveProperty().eq((byte)0))
			.or(f.getTaskHolderDomainProperty().eq(api.getDomain().getParentId()));
			
			filter = filter.and(newFilter);
		}
		
		return filter;
	}
	
	
	private JSONObject getDomainUser(AonApiData api) {
		Integer userId = api.getData().opt("user") != null ? api.getData().optInt("user") : null;
		User user = new User();
		if(userId != null) {
			user = AON.getUser(api.getDomain().getName(), api.getDomain().getId(), "", f -> f.getIdProperty().eq(userId));
		} else {
			AonToken aonToken = SECURITY.getAonToken(api.getToken());
			user = AON.getUser(api.getDomain().getName(), api.getDomain().getId(), "", f -> 
				f.getDomainProperty().eq(api.getDomain().getId())
				.and(f.getAuthProperty().eq(aonToken.getAuth()).or(f.getLoginProperty().eq(aonToken.getUuid()))));
			if(user == null || user.getId() == null)
				user = AON.getUser(api.getDomain().getName(), api.getDomain().getId(), "", f -> 
					f.getDomainProperty().eq(api.getDomain().getParentId())
					.and(f.getAuthProperty().eq(aonToken.getAuth()).or(f.getLoginProperty().eq(aonToken.getUuid()))));
		}
		JSONObject json = new JSONObject();
		json.put("id", user.getId());
		json.put("name", user.getName());
		json.put("login", user.getLogin());
		json.put("newAon", UserToolbar.AON_SOLUTIONS.equals(user.getToolbar()));
		json.put("portal", user.isPortal());
		json.put("shared", user.isShared());
		
		JSONArray scopes = new JSONArray();
		AON.getUserScopeStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), userId, null)
		.forEach(us ->{
			JSONObject scope = new JSONObject();
			scope.put("id", us.getId());
			scope.put("name", us.getDescription());
			scopes.put(scope);
		});
		json.put("scopes", scopes);
		
		JSONArray workgroups = new JSONArray();
		AON.getUserWorkgroupStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getUserIdProperty().eq(userId))
		.forEach(wg ->{
			JSONObject workgroup = new JSONObject();
			workgroup.put("id", wg.getWorkgroup().getId());
			workgroup.put("name", wg.getWorkgroup().getDescription());
			workgroups.put(workgroup);
		});
		json.put("workgroups", workgroups);
		return json;
	}
	
	private JSONArray getUserRoles(Domain domain, Integer userId) {
		LinkedList<UserAppRole> roles = AON_SOLUTIONS.getUserAppRole(domain.getName(), domain.getId(), "", f -> f.getUserIdProperty().eq(userId)).collect(Collectors.toCollection(LinkedList::new));
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
	

//	
//	private void setUserAppRole(Domain domain, User user, AonApp app, AonRole role){
//		UserAppRole uar = AON_SOLUTIONS.getUserAppRole(domain.getName(), domain.getId(), "", f -> 
//			f.getDomainProperty().eq(domain.getId())
//				.and(f.getUserIdProperty().eq(user.getId()))
//				.and((app != null ? f.getAppProperty().eq(app.value()): f.getAppProperty().eq((byte) -1))))
//			.findFirst().orElse(new UserAppRole());
//		if(uar.getId() == null) {
//			uar = new UserAppRole();
//			uar.setApp(app)
//				.setDomain(domain.getId())
//				.setRole(role)
//				.setUser(user.getId());
//			try {
//				uar = AON_SOLUTIONS.insertUserAppRole(domain.getName(), domain.getId(), "", uar);
//			} catch ( Exception e ) {
//				
//			}
//		} else {
//			uar.setRole(role);
//			AON_SOLUTIONS.updateUserAppRole(domain.getName(), domain.getId(), "", uar);
//		}
//	}
//	
	
	private JSONObject setUserAppRole(AonApiData api, User user){
		Domain domain = api.getDomain();
		String login = api.getUser().getLogin();
		Integer userId = user != null && user.getId() != null 
				? user.getId() : JsonUtils.getInteger(api.getData(), IJsonNames.ID);

		LinkedList<AonRole> aRoles = AON_SOLUTIONS.getUserAppRole(domain.getName(), domain.getId(), "", f -> f.getUserIdProperty().eq(userId))
				.map(r -> r.getRole()).collect(Collectors.toCollection(LinkedList::new));
		
		JSONArray roles = JsonUtils.getJSONArray(api.getData(), "roles");
		LinkedList<AonRole> tRoles = new LinkedList<AonRole>();
		for(Integer i = 0; i < roles.length(); i++) {
			tRoles.add(AonRole.safeValueOf(roles.optString(i)));
		}

		AonRole.stream().forEach(role -> {	
			if(aRoles.contains(role) && !tRoles.contains(role)) {
				AON_SOLUTIONS.deleteUserAppRole(domain.getName(), domain.getId(), login, f -> 
					f.getDomainProperty().eq(domain.getId())
					.and(f.getUserIdProperty().eq(userId))
					.and(f.getRoleProperty().eq(role.value())));
			}
			if(!aRoles.contains(role) && tRoles.contains(role)) {
				AON_SOLUTIONS.insertUserAppRole(api.getDomain().getName(), api.getDomain().getId(), "", new UserAppRole()
						.setApp(null)
						.setDomain(api.getDomain().getId())
						.setRole(role)
						.setUser(userId));
			}
		});
		return new JSONObject();
	}
	
//	private JSONObject setUserAppRole3(AonApiData api){
//		JSONArray roles = JsonUtils.getJSONArray(api.getData(), "roles");
//		Boolean portal = false;
//		Integer userId = !roles.isEmpty() ? roles.getJSONObject(0).optInt("user") : null;
//		for (Integer i = 0; i < roles.length(); i++) {
//			JSONObject object = roles.getJSONObject(i);
//			String app = object.optString("app");
//			Integer user = object.optInt("user");
//			Boolean active = object.optBoolean("active"); 
//			String role = object.optString("role");
//
//			AonApp aonApp = AonApp.safeValueOf(app);
//			
//			AonRole aonRole = AonRole.safeValueOf(role);
//			if(aonRole != null) {
//				UserAppRole uar = AON_SOLUTIONS.getUserAppRole(api.getDomain().getName(), api.getDomain().getId(), "", f -> 
//					f.getDomainProperty().eq(api.getDomain().getId())
//					.and(f.getUserIdProperty().eq(user))
//					.and(f.getRoleProperty().eq(aonRole.value())))
//					.findFirst().orElse(new UserAppRole());
//				
//				if(active) {
//					if(uar.getId() == null) {
//						uar = new UserAppRole();
//						uar.setApp(aonApp)
//							.setDomain(api.getDomain().getId())
//							.setRole(aonRole)
//							.setUser(user);
//						uar = AON_SOLUTIONS.insertUserAppRole(api.getDomain().getName(), api.getDomain().getId(), "", uar);
//					}
//					if(!api.getDomain().isParent() && AonApp.TIMECONTROL.equals(uar.getApp())) {
//						TaskHolder th = AON.getTaskHolder(api.getDomain().getName(), api.getDomain().getId(), "", f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getUserIdProperty().eq(user)));
//						if(th == null || th.getId() == null) {
//							User u = AON.getUser(api.getDomain().getName(), api.getDomain().getId(), "", f -> f.getIdProperty().eq(user));
//							Auth a = AON_SOLUTIONS.getAuth(api.getDomain().getName(), api.getDomain().getId(), u.getAuth());
//
//							Registry r = null;
//							if(!AonStringUtils.isBlank(a.getDocument())) {
//								r = AON.getRegistry(api.getDomain().getName(), api.getDomain().getId(), "", f -> 
//								f.getDomainProperty().eq(api.getDomain().getId())
//								.and(f.getDocumentProperty().eq(a.getDocument())));
//							}
//							if(r == null || r.getId() == null) {
//								r = AON.save(api.getDomain().getName(), api.getDomain().getId(), "", new Registry()
//										.setDocument(a.getDocument())
//										.setName(a.getName()+ " "+ a.getSurname())
//										.setAlias(a.getName())
//										.setDomain(api.getDomain()));
//							}
//							Integer registryId = r.getId();
//							th = AON.getTaskHolder(api.getDomain().getName(), api.getDomain().getId(), "", f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getIdProperty().eq(registryId)));
//							if(th != null && th.getId() != null) {
//								th.setActive(true);
//							} else {
//								th = new TaskHolder().copy(r)
//									.setActive(true)
//									.setUserId(user);
//							}
//						} else if(!th.isActive()) {
//							th.setActive(true);
//						}
//						AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), th);
//					} 
//				
//					if(AonRole.EMPLOYEE.equals(uar.getRole()) || AonRole.ENTERPRISE.equals(uar.getRole())) {
//						portal = true;
//						LinkedList<AonRole> list = AonRole.EMPLOYEE.equals(uar.getRole())
//								? AonRole.getEmployeeRoles() : AonRole.getEnterpriseRoles();
//							
//						Byte[] arr = list.stream().map(r -> r.value()).toArray(Byte[]::new);
//						AON_SOLUTIONS.deleteUserAppRole(api.getDomain().getName(), api.getDomain().getId(), "", f -> 
//							f.getDomainProperty().eq(api.getDomain().getId())
//							.and(f.getUserIdProperty().eq(user))
//							.and(f.getRoleProperty().notIn(arr)));
//					}
//				} else {
//					if(uar.getId() != null) {
//						Integer id = uar.getId();
//						AON_SOLUTIONS.deleteUserAppRole(api.getDomain().getName(), api.getDomain().getId(), "", f -> f.getIdProperty().eq(id));
//					}
//					
//					if(AonApp.TIMECONTROL.equals(uar.getApp())) {
//						TaskHolder th = AON.getTaskHolder(api.getDomain().getName(), api.getDomain().getId(), "", f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getUserIdProperty().eq(user)));
//						if(th != null && th.getId() != null) {
//							th.setActive(false);
//							AON.updateTaskHolder(api.getDomain().getName(), api.getDomain().getId(), "", th);
//						}
//					}
//				}
//			}
//		}
//		
//		if(portal) {
//			User usr = AON.getUser(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(userId));
//			if(usr.getEnterprise() == null) {
//				Company cp = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(api.getDomain().getId()));
//				usr.setEnterprise(cp.getId());
//				AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), usr);
//			}
//		} else {
//			User usr = AON.getUser(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(userId));
//			if(usr.getEnterprise() != null) {
//				usr.setEnterprise(null);
//				AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), usr);
//			}
//		}
//		
//		return new JSONObject();
//	}
	
	private JSONObject saveTaskHolder(AonApiData api, User user) {
		Integer userId = user != null && user.getId() != null 
				? user.getId() : JsonUtils.getInteger(api.getData(), IJsonNames.ID);
		TaskHolder th = AON.getTaskHolder(api.getDomain().getName(), api.getDomain().getId(), "", f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getUserIdProperty().eq(userId)));
		if(th == null || th.getId() == null) {
			User u = AON.getUser(api.getDomain().getName(), api.getDomain().getId(), "", f -> f.getIdProperty().eq(userId));
			Auth a = AON_SOLUTIONS.getAuth(api.getDomain().getName(), api.getDomain().getId(), u.getAuth().getAuth());

			Registry r = null;
			if(!AonStringUtils.isBlank(a.getDocument())) {
				r = AON.getRegistry(api.getDomain().getName(), api.getDomain().getId(), "", f -> 
				f.getDomainProperty().eq(api.getDomain().getId())
				.and(f.getDocumentProperty().eq(a.getDocument())));
			}
			if(r == null || r.getId() == null) {
				r = AON.save(api.getDomain().getName(), api.getDomain().getId(), "", new Registry()
						.setDocument(a.getDocument())
						.setName(a.getName()+ " "+ a.getSurname())
						.setAlias(a.getName())
						.setDomain(api.getDomain()));
			}
			Integer registryId = r.getId();
			th = AON.getTaskHolder(api.getDomain().getName(), api.getDomain().getId(), "", f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getIdProperty().eq(registryId)));
			if(th != null && th.getId() != null) {
				th.setActive(true);
				if(th.getUserId() == null)  
					th.setUserId(userId);
			} else {
				th = new TaskHolder().copy(r)
					.setActive(true)
					.setUserId(userId);
			}
		} else if(!th.isActive()) {
			th.setActive(true);
		}
		th = AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), th);
		if(user != null && th != null && th.getId() != null) {
			user.setRegistry(new Registry().setId(th.getId()));
			user = AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), user);
		}
		return UserJSON.toJSON(user);
	}
	
//	private JSONObject setUserAppRole2(AonApiData api){
//		JSONArray roles = api.getData().optJSONArray("roles");
//		Boolean portal = false;
//		Integer userId = !roles.isEmpty() ? roles.getJSONObject(0).optInt("user") : null;
//		for (Integer i = 0; i < roles.length(); i++) {
//			JSONObject object = roles.getJSONObject(i);
//			String app = object.optString("app");
//			Integer user = object.optInt("user");
//			Boolean active = object.optBoolean("active"); 
//			String role = object.optString("role");
//
//			AonApp aonApp = AonApp.safeValueOf(app);
//			
//			AonRole aonRole = AonRole.safeValueOf(role);
//			if(aonRole != null) {
//				UserAppRole uar = AON_SOLUTIONS.getUserAppRole(api.getDomain().getName(), api.getDomain().getId(), "", f -> 
//					f.getDomainProperty().eq(api.getDomain().getId())
//					.and(f.getUserIdProperty().eq(user))
//					.and(f.getRoleProperty().eq(aonRole.value())))
//					.findFirst().orElse(new UserAppRole());
//				
//				if(active) {
//					if(uar.getId() == null) {
//						uar = new UserAppRole();
//						uar.setApp(aonApp)
//							.setDomain(api.getDomain().getId())
//							.setRole(aonRole)
//							.setUser(user);
//						uar = AON_SOLUTIONS.insertUserAppRole(api.getDomain().getName(), api.getDomain().getId(), "", uar);
//					}
//					if(!api.getDomain().isParent() && AonApp.TIMECONTROL.equals(uar.getApp())) {
//						TaskHolder th = AON.getTaskHolder(api.getDomain().getName(), api.getDomain().getId(), "", f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getUserIdProperty().eq(user)));
//						if(th == null || th.getId() == null) {
//							User u = AON.getUser(api.getDomain().getName(), api.getDomain().getId(), "", f -> f.getIdProperty().eq(user));
//							Auth a = AON_SOLUTIONS.getAuth(api.getDomain().getName(), api.getDomain().getId(), u.getAuth());
//
//							Registry r = null;
//							if(!AonStringUtils.isBlank(a.getDocument())) {
//								r = AON.getRegistry(api.getDomain().getName(), api.getDomain().getId(), "", f -> 
//								f.getDomainProperty().eq(api.getDomain().getId())
//								.and(f.getDocumentProperty().eq(a.getDocument())));
//							}
//							if(r == null || r.getId() == null) {
//								r = AON.save(api.getDomain().getName(), api.getDomain().getId(), "", new Registry()
//										.setDocument(a.getDocument())
//										.setName(a.getName()+ " "+ a.getSurname())
//										.setAlias(a.getName())
//										.setDomain(api.getDomain()));
//							}
//							Integer registryId = r.getId();
//							th = AON.getTaskHolder(api.getDomain().getName(), api.getDomain().getId(), "", f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getIdProperty().eq(registryId)));
//							if(th != null && th.getId() != null) {
//								th.setActive(true);
//							} else {
//								th = new TaskHolder().copy(r)
//									.setActive(true)
//									.setUserId(user);
//							}
//						} else if(!th.isActive()) {
//							th.setActive(true);
//						}
//						AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), th);
//					} 
//				
//					if(AonRole.EMPLOYEE.equals(uar.getRole()) || AonRole.ENTERPRISE.equals(uar.getRole())) {
//						portal = true;
//						LinkedList<AonRole> list = AonRole.EMPLOYEE.equals(uar.getRole())
//								? AonRole.getEmployeeRoles() : AonRole.getEnterpriseRoles();
//							
//						Byte[] arr = list.stream().map(r -> r.value()).toArray(Byte[]::new);
//						AON_SOLUTIONS.deleteUserAppRole(api.getDomain().getName(), api.getDomain().getId(), "", f -> 
//							f.getDomainProperty().eq(api.getDomain().getId())
//							.and(f.getUserIdProperty().eq(user))
//							.and(f.getRoleProperty().notIn(arr)));
//					}
//				} else {
//					if(uar.getId() != null) {
//						Integer id = uar.getId();
//						AON_SOLUTIONS.deleteUserAppRole(api.getDomain().getName(), api.getDomain().getId(), "", f -> f.getIdProperty().eq(id));
//					}
//					
//					if(AonApp.TIMECONTROL.equals(uar.getApp())) {
//						TaskHolder th = AON.getTaskHolder(api.getDomain().getName(), api.getDomain().getId(), "", f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getUserIdProperty().eq(user)));
//						if(th != null && th.getId() != null) {
//							th.setActive(false);
//							AON.updateTaskHolder(api.getDomain().getName(), api.getDomain().getId(), "", th);
//						}
//					}
//				}
//			}
//		}
//		
//		if(portal) {
//			User usr = AON.getUser(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(userId));
//			if(usr.getEnterprise() == null) {
//				Company cp = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(api.getDomain().getId()));
//				usr.setEnterprise(cp.getId());
//				AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), usr);
//			}
//		} else {
//			User usr = AON.getUser(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(userId));
//			if(usr.getEnterprise() != null) {
//				usr.setEnterprise(null);
//				AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), usr);
//			}
//		}
//		
//		return new JSONObject();
//	}
	
	private JSONObject setUser(AonApiData api) throws Exception {
		JSONObject js = new JSONObject();
		String email = api.getData().optString("email");

		if(Utils.isEmail(email)) {
			
			User usr = api.getData().opt("id") != null 
					? AON.getUser(api.getDomain().getName(), api.getDomain().getId(), "", f -> f.getIdProperty().eq(api.getData().getInt("id")))
					: new User();
			
			String login = ramdonLogin();
			String pass = null;
			if(usr != null && usr.getId() != null) {
				login = usr.getLogin();
//				pass = AON_SOLUTIONS.getUserPassword(getDomain().getName(), getDomain().getId(), usr.getId());
			}
			
			Auth  authx = AuthJSON.fromJSON(api.getData());
			if(!AonStringUtils.isBlank(authx.getDocument())) {
				for (Auth r : AON_SOLUTIONS.getAuths(f -> f.getDocumentProperty().eq(authx.getDocument()))) {
					User user = AON.getDomainUserStream(api.getDomain().getName(), api.getDomain().getId(), "", f -> 
					f.getAuthProperty().eq(r.getAuth())).findFirst().orElse(new User());
					
					if((user == null || user.getId() == null) && api.getData().opt("id") != null) {
						user = AON.getUser(api.getDomain().getName(), api.getDomain().getId(), "", f -> f.getIdProperty().eq(api.getData().getInt("id")));
					} 
					if(user != null && user.getId() != null && !user.getId().equals(api.getData().optInt("id"))){
						throw new Exception("El documento introducido ya está asociado a otro usuario.");
					}
				}
			}
			Auth auth = AON_SOLUTIONS.getAuth(email);
			if(auth.getUuid() == null) {
				auth = createAuth(api.getDomain(), api.getData(), login, pass);
//				sendAuthCreateInfoMail(email, login);
			} else updateAuth(auth, api.getData());
			
			byte[] a = auth.getAuth();

			User user = AON.getDomainUserStream(api.getDomain().getName(), api.getDomain().getId(), "", f -> 
					f.getAuthProperty().eq(a)).findFirst().orElse(new User());
					
			if((user == null || user.getId() == null) && api.getData().opt("id") != null) {
				user = AON.getUser(api.getDomain().getName(), api.getDomain().getId(), "", f -> f.getIdProperty().eq(api.getData().getInt("id")));
			} 
			if(user != null && user.getId() != null && !user.getId().equals(api.getData().optInt("id"))){
				throw new Exception("El mail introducido ya está asociado a otro usuario.");
			}
			
			if(auth.getAuth() != null) {
				if(user == null || user.getId() == null) {
					user = createUser(api, api.getDomain(), api.getData(), login, auth);
				} else {
					boolean portal = api.getData().optBoolean("portal");
					Company cp = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), login, f -> f.getDomainProperty().eq(api.getDomain().getId()));
					user.setEnterprise(portal ? cp.getId() : null);
					user.setAuth(auth);
					AON.save(api.getDomain().getName(), api.getDomain().getId(), login, user);
					AON_SOLUTIONS.assignAuthToUser(api.getDomain().getName(), api.getDomain().getId(), user, auth.getAuth());
				}
				
				setUserAppRole(api, user);
				if(api.getDomain().isChild() || api.getDomain().isStandalone()) {
					saveTaskHolder(api, user);
				}
				
				js.put("id", user.getId());
				js.put("email", auth.getEmail() != null ? auth.getEmail() : "");
				js.put("uuid", auth.getUuid());
				js.put("name", auth.getName() != null ? auth.getName() : user.getName());
				js.put("surname", auth.getSurname() != null ? auth.getSurname() : "");
				js.put("document", auth.getDocument() != null ? auth.getDocument() : "");
				js.put("phone", auth.getPhone() != null ? auth.getPhone() : "");
				js.put("roles", getUserRoles(api.getDomain(), user.getId()));
				js.put("portal", user.isPortal());
				js.put("shared", user.isShared());
				js.put("login", user.getLogin());
			}
		} else {
			throw new Exception("El email no es correcto.");
		}
		return js;
	}
	
	private void deleteUser(AonApiData api) {
		if(api.getData().opt("user") != null) {
			Integer userId = api.getData().optInt("user");
			User user = AON.getUser(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(userId));
			SECURITY.delete(api.getDomain(), api.getUser().getLogin(), user);
		}
	}
	
	private Auth createAuth(Domain domain, JSONObject json, String login, String pass) {
		if(pass == null) {
			pass = Utils.createPasswordHash(json.optString("email"), login);
		}
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
	
	private User createUser(AonApiData api, Domain domain, JSONObject json, String login, Auth auth) {
		Company cp = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), login, f -> f.getDomainProperty().eq(api.getDomain().getId()));
		
		User user = new User()
			.setAuth(auth)
			.setActive(true)
			.setDomain(domain.getId())
			.setLogin(login)
			.setName(json.opt("name") != null ? json.getString("name") : login)
			.setShared(json.optBoolean("shared"))
			.setEnterprise(cp.getId())
			.setToolbar(UserToolbar.GOOGLE);
		
		if(json.opt("document") != null) {
			String document = json.optString("document");
			if(!AonStringUtils.isBlank(document) && AonDocumentUtil.isValid(document)) {
				Integer registryId = null;
				Optional<Person> p = AON.getPerson(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
					f.getDomainProperty().eq(domain.getId())
					.and(f.getDocumentProperty().eq(document)));
				if(p.isPresent() && p.get().getId() != null) {
					registryId = p.get().getId();
				}
				
				if(registryId == null) {
					Registry r = AON.getRegistry(api.getDomain().getName(), api.getDomain().getId(), "", f -> 
						f.getDomainProperty().eq(domain.getId())
						.and(f.getDocumentProperty().eq(document)));
					registryId = r.getId();
				}
				user.setRegistry(new Registry().setId(registryId));
			}
		}	
		user = AON.save(domain.getName(), domain.getId(), "", user);
		AON.updateUserPassword(domain.getName(), domain.getId(), api.getUser().getLogin(), user.getId(), auth.getPassword());
		Scope s = getScope(api);
		if(s != null) {
			AON.insertUserScope(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), new UserScope()
					.setDomain(api.getDomain().getId())
					.setScope(s.getId())
					.setUserId(user.getId()));
		}
		if(api.getDomain().getScope() != null) {
			AON.insertUserScope(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), new UserScope()
					.setDomain(api.getDomain().getId())
					.setScope(api.getDomain().getScope())
					.setUserId(user.getId()));
		}
		
		ApplicationParameter a = AON.getApplicationParameter(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), AppParam.AON_PORTAL);
		ApplicationParameter appParam = new ApplicationParameter()
				.setDomain(api.getDomain().getId())
				.setValue("288")
				.setName(AppParam.AON_PORTAL.getValue());

		if(a == null || a.getId() == null)
			AON.insertApplicationParameter(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), appParam);
	
		AON_SOLUTIONS.saveUserFinancePortal(api.getDomain(), api.getUser().getLogin(), user.getId());		
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
		json.put("login", user.getLogin());
		return json;
	}	
	
	private void sendAuthCreateInfoMail(String email, String password, Company cp) {
		SESMessage msg = new SESMessage()
				.setTo(email)
				.setAlias(cp.getName())
				.setBody(authCreateInfoContent(email, password))
				.setSubject("NUEVO USUARIO | AON SOLUTIONS");
		SES.sendEmail(msg);
	}
	
	private JSONObject sendAuthInfoMail(AonApiData api) {
		Company cp = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
				f -> f.getDomainProperty().eq(api.getDomain().getId()));
		String email = api.getData().optString("email");
		Auth auth = AON_SOLUTIONS.getAuth(email);
		
		String password = Utils.generatePassword();
		String pass = Utils.createPasswordHash(auth.getEmail(), password);
		auth.setPassword(pass);
		AON_SOLUTIONS.updateAuthPassword(auth);
		
		sendAuthCreateInfoMail(email, password, cp);
		return new JSONObject();
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
	
	private Scope getScope(AonApiData api) {
		Scope s = AON.getScopeStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
				f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getDescriptionProperty().eq("GENERAL")))
				.findFirst().orElse(null);
		if(s == null && api.getDomain().getParentId() != null) {
			s =   AON.getScopeStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					f -> f.getDomainProperty().eq(api.getDomain().getParentId()).and(f.getDescriptionProperty().eq("GENERAL")))
					.findFirst().orElse(null);
		}
		
		if(s== null){
			s = AON.getScopeStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					f -> f.getDomainProperty().eq(api.getDomain().getId()))
					.findFirst().orElse(null);
		}
		if(s == null && api.getDomain().getParentId() != null) {
			s = AON.getScopeStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					f -> f.getDomainProperty().eq(api.getDomain().getParentId()))
					.findFirst().orElse(null);
		}
		return s;
	}
	
}
