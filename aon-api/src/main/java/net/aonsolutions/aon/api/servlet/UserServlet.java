package net.aonsolutions.aon.api.servlet;
import static com.esferalia.aon.occam.api.model.attachment.AttachType.REGISTRY;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.LOGO;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.SIGNATURE;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.json.AuthJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.RegistryJSON;
import com.esferalia.aon.occam.api.json.UserJSON;
import com.esferalia.aon.occam.api.json.WorkgroupJSON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.InvoiceUserData;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.Properties.UserProperties;
import com.esferalia.aon.occam.api.model.RawdocUserData;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.aonsolutions.AonDomainUserRoles;
import com.esferalia.aon.occam.api.model.aonsolutions.AonRole;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.api.model.security.UserToolbar;
import com.esferalia.aon.occam.api.model.security.UserType;
import com.esferalia.aon.occam.api.model.security.UserWorkgroup;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.RawdocNature;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AuthDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryOldDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO;
import com.esferalia.aon.occam.impl.jooq.dao.UserDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

@SuppressWarnings("serial")
@WebServlet(name = "AonUserServlet", urlPatterns = {"/ms/api/user/*"})
public class UserServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(UserServlet.class.getName());
	private static final String AON_LOGO = "https://aon.solutions/assets/aon-logo.png";
	private static final String AON_FROM = "booking@aon.solutions";
	private static final String AON_REPLY_TO = "asignacion@aonsolutions.es";
	private static final String AON_ALIAS = "AON SOLUTIONS S.L.";
	private static final String AON_SUBJECT = "USUARIO | AON SOLUTIONS";
	
	private static final String AON_BOOKING_BCCC = "booking@aonsolutions.es";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
		
			switch (api.getPath()) {
			case "/":
				response(req, resp, getDomainUsers(api));
				break;
			case "/roles":
				response(req, resp, getUserRoles(api.getDomain(), JsonUtils.getInteger(api.getData(), IJsonNames.USER)));
				break;
			case "/approles":
				response(req, resp, getUserAppRoles(api.getDomain(), JsonUtils.getInteger(api.getData(), IJsonNames.USER)));
				break;
			case "/list":
				response(req, resp, getUsers(api));
				break;
			case "/notice":
				List<String> schemas = AONContext.getSchemas(api.getDomain().getName());
				RawdocUserData rawdocUserData = new RawdocUserData();
				for(String schema : schemas) {
					rawdocUserData.append(AON.getRawdocUserData(api.getToken(), schema));
				}
				InvoiceUserData invoiceUserData = new InvoiceUserData();
				for(String schema : schemas) {
					invoiceUserData.append(AON.getInvoiceUserData(api.getToken(), schema));
				}
				
				JSONObject noticeJson = rawdocUserData.toJSON();

				JSONObject invoiceJson = noticeJson.optJSONObject(RawdocNature.INVOICE.name().toLowerCase());
				if ( invoiceJson == null ) {
					invoiceJson = new JSONObject(); 
					noticeJson.put (RawdocNature.INVOICE.name().toLowerCase(), invoiceJson);
				}
				invoiceUserData.toJSON().toMap().forEach( invoiceJson::put);

				response(req, resp, noticeJson);
				
				break;
			case "/info":
				response(req, resp, getDomainUser(api));
				break;
				
			case "/email":
				response(req, resp, getUserEmail(api));
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
			case "/bookingUser":
				response(req, resp, bookingUser(api));
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
			case "/auth":
				response(req, resp, setUserAuth(api));
				break;
			case "/workgroup":
				response(req, resp, insertUserWorkgroup(api));
				break;
			case "/service":
				response(req, resp, saveServiceAccount(api));
				break;
			case "/scope":
				response(req, resp, addUserScope(api));
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
			case "/scope":
				response(req, resp, removeUserScope(api));
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
	
	// USER AUTH

	private JSONObject setUserAuth(AonApiData api) {
		Integer userId = api.getData().getInt(IJsonNames.USER);
		String email = api.getData().getString(IJsonNames.EMAIL);
		Auth auth = AON_SOLUTIONS.getAuth(api.getDomain().getName(), api.getDomain().getId(), email);
		User user = AON.getUser(api.getDomain(), api.getUser().getLogin(), f-> f.getIdProperty().eq(userId));
		user.setAuth(auth);
		user = AON.saveUser(api.getDomain(), api.getUser().getLogin(), user);
		return UserJSON.toJSON(user);
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
	
	private JSONObject addUserScope(AonApiData api) {
		JSONArray scopes = JsonUtils.getJSONArray(api.getData(), IJsonNames.SCOPES);
		List<Integer> list = new ArrayList<>();
        for (int i = 0; i < scopes.length(); i++) {
            list.add(scopes.getInt(i));
        }

		Integer userId = JsonUtils.getInteger(api.getData(), IJsonNames.USER);
		AON.addUserScope(api.getOccam(), userId, list);

		return new JSONObject();
	}
	
	private JSONObject removeUserScope(AonApiData api) {
		Integer scopeId = JsonUtils.getInteger(api.getData(), IJsonNames.SCOPE);
		Integer userId = JsonUtils.getInteger(api.getData(), IJsonNames.USER);
		boolean all = JsonUtils.getboolean(api.getData(), "all");

		if(all && scopeId == null) {
			AON.deleteUserScope(api.getDomain().getName(), api.getDomain().getId(),
					api.getUser().getLogin(), f -> f.getUserIdProperty().eq(userId));
		} else {
			AON.deleteUserScope(api.getDomain().getName(), api.getDomain().getId(),
				api.getUser().getLogin(), userId, scopeId);
		}
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
			.or(f.getTaskHolderDomainProperty().eq(api.getDomain().getParentId()).and(f.getTaskHolderActiveProperty().eq((byte)0)));
			
			filter = filter.and(newFilter);
		}
		
		return filter;
	}
	
	
	private JSONObject getDomainUser(AonApiData api) {
		Integer userId = api.getData().opt("user") != null ? api.getData().optInt("user",0) : null;
		User user = new User();
		if(userId != null && userId != 0 ) {
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
		json.put("registry", RegistryJSON.toJSON(user.getRegistry()));
		
		JSONArray scopes = new JSONArray();
		AON.getUserScopeStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), user.getId(), null)
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
		roles.stream().forEach(uar -> userAppRoles.put(uar.getRole().name()));
		return userAppRoles;
	}
	
	private JSONObject getUserAppRoles(Domain domain, Integer userId) {
		DomainUserRoles dur = SECURITY.getDomainUserRoles(domain, "", userId);
		AonDomainUserRoles adur = new AonDomainUserRoles(dur);
		return adur.toJSON();
	}
	
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
	
	private JSONObject setUserAppRole(CloseableAONContext ctx, Domain domain, User userServlet, JSONObject data, User user){
		String login = userServlet.getLogin();
		Integer userId = user != null && user.getId() != null ? user.getId() : JsonUtils.getInteger(data, IJsonNames.ID);

		LinkedList<AonRole> aRoles = AON_SOLUTIONS.getUserAppRole(domain.getName(), domain.getId(), "", f -> f.getUserIdProperty().eq(userId))
				.map(r -> r.getRole()).collect(Collectors.toCollection(LinkedList::new));
		
		JSONArray roles = JsonUtils.getJSONArray(data, "roles");
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
				SecurityDAO.insertUserAppRole(ctx, new UserAppRole()
						.setApp(null)
						.setDomain(domain.getId())
						.setRole(role)
						.setUser(userId));
			}
		});
		return new JSONObject();
	}
	
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
	
	private JSONObject saveTaskHolder(CloseableAONContext ctx, Domain domain, User userServlet, JSONObject data, User user) {
		Integer userId = user != null && user.getId() != null  ? user.getId() : JsonUtils.getInteger(data, IJsonNames.ID);
		
		TaskHolder th = TaskHolderDAO.get(ctx, f -> f.getDomainProperty().eq(domain.getId()).and(f.getUserIdProperty().eq(userId)), new Options());
		if(th == null || th.getId() == null) {
			User u = UserDAO.get(ctx,  f -> f.getIdProperty().eq(userId), new Options());
			Auth a = AuthDAO.getAuth(ctx, u.getAuth().getAuth());

			Registry r = null;
			if(!AonStringUtils.isBlank(a.getDocument())) {
				r =  RegistryOldDAO.getRegistry(ctx, f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getDocumentProperty().eq(a.getDocument())));
			}
			if(r == null || r.getId() == null) {
				r = RegistryDAO.save(ctx,  new Registry()
						.setDocument(a.getDocument())
						.setName(a.getName()+ " "+ a.getSurname())
						.setAlias(a.getName())
						.setDomain(domain));
			}
			Integer registryId = r.getId();
			th = TaskHolderDAO.get(ctx, f -> f.getDomainProperty().eq(domain.getId()).and(f.getIdProperty().eq(registryId)), new Options());
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
		
		th = TaskHolderDAO.save(ctx, th);
		if(user != null && th != null && th.getId() != null) {
			user.setRegistry(new Registry().setId(th.getId()));
			user = UserDAO.save(ctx, user);
		}
		return UserJSON.toJSON(user);
	}
	
	
	private JSONObject bookingUser(AonApiData api) throws Exception {
		setUser(api);
		
		try (CloseableAONContext ctx = AONContext.getAONContext(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin())) {
					
			ctx.transaction(t -> {
				
//				setBookingUser(ctx, api.getDomain(), api.getUser(), api.getData(), AuthJSON.fromJSON(api.getData()));
				
				Domain currentDomain =  DomainDAO.getDomain(ctx, api.getDomain().getId());
				Integer maxDefinedUsers = currentDomain.getMaxDefinedUsers();
				maxDefinedUsers++;
				SecurityDAO.saveDomainMaxDefinedUser(ctx, maxDefinedUsers);
			});
		}
		
		return null;
	}
	
	private JSONObject setBookingUser(CloseableAONContext ctx, Domain domain, User userServlet, JSONObject data, Auth  authx) throws Exception {
		JSONObject js = new JSONObject();
		String email = data.optString("email");
		
		if(Utils.isEmail(email)) {
			
			Object userIdObj = data.opt("id");
			Object activeObj = data.opt("active");
			Boolean active = data.optBoolean("active");
			boolean portal = data.optBoolean("portal");
			
			User usr = userIdObj != null 
					? UserDAO.get(ctx, f -> f.getIdProperty().eq(data.optInt("id")), new Options())
					: new User();
			
			if(activeObj != null) {
				usr.setActive(active);
				UserDAO.save(ctx, usr);
			}
			
			String login = ramdonLogin();
			String pass = null;
			
			if(usr != null && usr.getId() != null)
				login = usr.getLogin();
			
			if(!AonStringUtils.isBlank(authx.getDocument())) {
				for (Auth r : AON_SOLUTIONS.getAuths(f -> f.getDocumentProperty().eq(authx.getDocument()))) {
					
					User user = SecurityDAO.getDomainUserStream(ctx, f -> f.getAuthProperty().eq(r.getAuth())).findFirst().orElse(new User());
					
					if((user == null || user.getId() == null) && userIdObj != null) {
						user = UserDAO.get(ctx, f -> f.getIdProperty().eq(data.optInt("id")), new Options());
					} 
					
					if(user != null && user.getId() != null && !user.getId().equals(data.optInt("id"))){
						throw new Exception("El documento introducido ya está asociado a otro usuario.");
					}
				}
			}
			
			Auth auth = AON_SOLUTIONS.getAuth(email);
			
			if(auth.getUuid() == null)
				auth = createAuth(domain, data, login, pass);
			else 
				updateAuth(auth, data);
			
			byte[] a = auth.getAuth();
			
			User user = SecurityDAO.getDomainUserStream(ctx, f -> f.getAuthProperty().eq(a)).findFirst().orElse(new User());
					
			if((user == null || user.getId() == null) && userIdObj != null) {
				user = UserDAO.get(ctx, f -> f.getIdProperty().eq(data.optInt("id")), new Options());
			} 
			if(user != null && user.getId() != null && !user.getId().equals(data.optInt("id"))){
				throw new Exception("El mail introducido ya está asociado a otro usuario.");
			}
			
			if(auth.getAuth() != null) {
				if(user == null || user.getId() == null) {
					user = createUser(ctx, domain, userServlet, data, login, auth);
				} else {
					
					Company cp = CompanyDAO.getStream(ctx, f -> f.getDomainProperty().eq(domain.getId())).findFirst().orElse(new Company());
					user.setEnterprise(portal ? cp.getId() : null);
					user.setAuth(auth);
					SecurityDAO.save(ctx, user);
					SecurityDAO.assignAuthToUser(ctx, user, auth.getAuth());
				}
				
				setUserAppRole(ctx, domain, userServlet, data, user);
				if(domain.isChild() || domain.isStandalone()) {
					saveTaskHolder(ctx, domain, userServlet, data, user);
				}
				
				Integer userId = user.getId();
				
				return UserJSON.toJSON(
					SecurityDAO.getDomainUserStream(ctx, f -> f.getIdProperty().eq(userId))
						.map(r -> 
								!r.getAuth().isEmpty() && r.getAuth().getEmail() == null
									? r.setAuth(AON_SOLUTIONS.getAuth(r.getAuth().getAuth()))
									: r
						).findAny().get()
				);
			}
		} else {
			throw new Exception("El email no es correcto.");
		}
		return js;
	}
	
	private JSONObject setUser(AonApiData api) throws Exception {
		JSONObject js = new JSONObject();
		String email = api.getData().optString("email");

		if(Utils.isEmail(email)) {
			
			User usr = api.getData().opt("id") != null 
					? AON.getUser(api.getDomain().getName(), api.getDomain().getId(), "", f -> f.getIdProperty().eq(api.getData().getInt("id")))
					: new User();
			
			if(api.getData().opt("active") != null) {
				usr.setActive(api.getData().optBoolean("active"));
				AON.saveUser(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), usr);
			}
			
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
				
				// Return user better
//				js.put("id", user.getId());
//				js.put("email", auth.getEmail() != null ? auth.getEmail() : "");
//				js.put("uuid", auth.getUuid());
//				js.put("name", auth.getName() != null ? auth.getName() : user.getName());
//				js.put("surname", auth.getSurname() != null ? auth.getSurname() : "");
//				js.put("document", auth.getDocument() != null ? auth.getDocument() : "");
//				js.put("phone", auth.getPhone() != null ? auth.getPhone() : "");
//				js.put("roles", getUserRoles(api.getDomain(), user.getId()));
//				js.put("portal", user.isPortal());
//				js.put("shared", user.isShared());
//				js.put("login", user.getLogin());
				
				Integer userId = user.getId();
				
				return UserJSON.toJSON(
					AON.getDomainUserStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getName(), f -> f.getIdProperty().eq(userId))
						.map(r -> !r.getAuth().isEmpty() && r.getAuth().getEmail() == null
						? r.setAuth(AON_SOLUTIONS.getAuth(r.getAuth().getAuth())) 
						: r)
						.findAny().get()
				);
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
	
	private User createUser(CloseableAONContext ctx, Domain domain, User userServlet, JSONObject json, String login, Auth auth) {
		Company cp = CompanyDAO.getCompanyStream(ctx, f -> f.getDomainProperty().eq(domain.getId())).findFirst().orElse(new Company());
		
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
				Optional<Person> p =  RegistryOldDAO.getPersonStream(ctx, 
						f -> f.getDomainProperty().eq(domain.getId())
							.and(f.getDocumentProperty().eq(document))
						).findFirst();
				
				if(p.isPresent() && p.get().getId() != null)
					registryId = p.get().getId();
				
				if(registryId == null) {
					Registry r = RegistryOldDAO.getRegistry(ctx, f -> 
						f.getDomainProperty().eq(domain.getId())
						.and(f.getDocumentProperty().eq(document)));
					registryId = r.getId();
				}
				
				user.setRegistry(new Registry().setId(registryId));
			}
		}	
		user = AON.save(domain.getName(), domain.getId(), "", user);
		AON.updateUserPassword(domain.getName(), domain.getId(), userServlet.getLogin(), user.getId(), auth.getPassword());
		
		Scope s = getScope(ctx, domain);
		
		if(s != null) {
			SecurityDAO.insertUserScope(ctx,  new UserScope()
					.setDomain(domain.getId())
					.setScope(s.getId())
					.setUserId(user.getId()));
		}
		
		if(domain.getScope() != null) {
			SecurityDAO.insertUserScope(ctx,  new UserScope()
					.setDomain(domain.getId())
					.setScope(domain.getScope())
					.setUserId(user.getId()));
		}
		
		ApplicationParameter a = AppParamDAO.fetchOne(ctx, AppParam.AON_PORTAL.getValue());
		ApplicationParameter appParam = new ApplicationParameter()
				.setDomain(domain.getId())
				.setValue("288")
				.setName(AppParam.AON_PORTAL.getValue());

		if(a == null || a.getId() == null)
			AppParamDAO.insertApplicationParameter(ctx, appParam);
	
		SecurityDAO.saveUserFinancePortal(ctx, user.getId());
		
		return user;
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
	
	private String sendAuthCreateInfoMail(AonApiData api, Auth auth, String email, String password, Company cp) {
		Domain parent = api.getDur().getDomain().isParent()
			? api.getDur().getDomain() : api.getDur().getParentDomain(); 
		
		String logoUrl = AON_LOGO;
		String from = AON_FROM;
		String replyTo = AON_REPLY_TO;
		String alias = AON_ALIAS;
		String subject = AON_SUBJECT;
		
		if(api.getDur().hasCustomView() || api.getDur().hasParentCustomView()) {
			logoUrl = getLogoUrl(api);
			from = getFromMessage(api);		
			replyTo = getReplayTo(api);
			alias = formatUT8B(cp.getName());
			subject = formatUT8B("USUARIO | " + cp.getName().toUpperCase());
		}
		
		User user = AON.getUser(api.getDomain(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getAuthProperty().eq(auth.getAuth())));

		SESMessage msg = new SESMessage()
			.setAlias(alias)
			.setFrom(from)
			.setReplyTo(replyTo)
			.setTo(email)
			.setSubject(subject)
			.setBcc(AON_BOOKING_BCCC)
			.setBody(authCreateInfoContent(api, user, auth.getFullname(), email, password, replyTo, logoUrl, parent));

		SES.sendEmail(msg);
		
		return from;
	}

	private static String getFromMessage(AonApiData api) {
//		Domain parentDomain = api.getDur().getDomain().isParent()  ? api.getDur().getDomain() : api.getDur().getParentDomain();
		
		String from = AON_FROM;

		// TODO: vista personalizada / email verificada, averiguar como se verifica
//		if(api.getDur().hasCustomView() || api.getDur().hasParentCustomView()) {
//			RegistryMedia emailMedia = AON.getRegistryMedia(api.getDomain(), api.getUser(),
//					f -> f.getDomainProperty().eq(parentDomain.getId()).and(f.getMediaProperty().eq((byte) 4)));
//			
//			if (null != emailMedia && AonStringUtils.isNotBlank(emailMedia.getValue()))
//				from = emailMedia.getValue();
//			
//		}
		
		return from;
	}
	
	private static String getReplayTo(AonApiData api) {
		Domain parentDomain = api.getDur().getDomain().isParent() ? api.getDur().getDomain() : api.getDur().getParentDomain();
		
		String from = null;
		
		RegistryMedia emailMedia = AON.getRegistryMedia(api.getDomain(), api.getUser(),
				f -> f.getDomainProperty().eq(parentDomain.getId()).and(f.getMediaProperty().eq((byte) 4)));
		
		if (null != emailMedia && AonStringUtils.isNotBlank(emailMedia.getValue()))
			from = emailMedia.getValue();
		
//		if(AonStringUtils.isBlank(from)) {
//			from = parentDomain.getOwner();
//		}
		
		if(AonStringUtils.isBlank(from)) {
			from = AON_REPLY_TO;
		}
		
		return from;
	}
	
    /**
     * Returns the parameter formated to UTF8 & Base64
     */
    private static String formatUT8B(final String text) {
    	if(AonStringUtils.isBlank(text)) return text;
        return "=?UTF-8?B?" + Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8)) + "?=" ;
    }
	
	private JSONObject sendAuthInfoMail(AonApiData api) {
		try {
			Company cp = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
					f -> f.getDomainProperty().eq(api.getDomain().getId()));
			
			String email = api.getData().optString("email");
			Auth auth = AON_SOLUTIONS.getAuth(email);
			
			String password = Utils.generatePassword();
			String pass = Utils.createPasswordHash(auth.getEmail(), password);
			auth.setPassword(pass);
			AON_SOLUTIONS.updateAuthPassword(auth);
			
			String fromEmail = sendAuthCreateInfoMail(api, auth, email, password, cp);
			return new JSONObject().put("fromEmail", fromEmail);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AonCoreException(e.getMessage());
		}
	}
	
	private String authCreateInfoContent(AonApiData api, User user, String fullName, String email, String password, String from, String logo, Domain parentDomain) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
		
		String url = "https://" + api.getDomain().getName() + "/";
		
		if(api.getDur().hasCustomView() || api.getDur().hasParentCustomView()) {
			url = null != parentDomain && AonStringUtils.isNotBlank(parentDomain.getName()) ? "https://" + parentDomain.getName() + "/" : "https://" + api.getDomain().getName() + "/";;
		}
		
//		if(user != null && user.isPortal()) {
//			url = "https://" + api.getDomain().getName() + "/";
//			
//			if(AonStringUtils.equalsIgnoreCase(parentDomain.getName(), "app.leevy.es"))
//				url = "https://leevy.aon.solutions";
//			else if(AonStringUtils.equalsIgnoreCase(parentDomain.getName(), "infoautonomos.aonsolutions.net"))
//				url = "https://infoautonomos.aon.solutions";
//		} else {
//			url = "https://" + api.getDomain().getName() + "/";
//			
//			if(AonStringUtils.equalsIgnoreCase(parentDomain.getName(), "infoautonomos.aonsolutions.net"))
//				url = "https://infoautonomos.aon.solutions";
//		}
		
		VelocityContext context = new VelocityContext();
		context.put("logo", logo);
		context.put("parentName", parentDomain.getDescription());
		context.put("enterpriseUrl", url);
		context.put("enterpriseName", api.getDomain().getDescription());
		context.put("fullName", fullName);
		context.put("email", email);
		context.put("password", password);
		context.put("contact", AonStringUtils.isBlank(from) ? "booking@aonsolutions.es" : from);
			
		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/auth_create_info.vm");
			
		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
	
	private Scope getScope(AonApiData api) {
		Scope s = AON.getScopeStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
				f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getDescriptionProperty().eq("GENERAL")))
				.findFirst().orElse(null);
		if(s== null){
			s = AON.getScopeStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getDescriptionProperty().eq("EMPRESA")))
					.findFirst().orElse(null);
		}
		
		if(s== null){
			s = AON.getScopeStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					f -> f.getDomainProperty().eq(api.getDomain().getId()))
					.findFirst().orElse(null);
		}
		
		return s;
	}
	
	private Scope getScope(CloseableAONContext ctx, Domain domain) {
		Scope s =  SecurityDAO.getScopeStream(ctx, f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getDescriptionProperty().eq("GENERAL"))
			).findFirst().orElse(null);
		
		if(s == null && domain.getParentId() != null) {
			s = SecurityDAO.getScopeStream(ctx, f -> 
					f.getDomainProperty().eq(domain.getParentId())
					.and(f.getDescriptionProperty().eq("GENERAL"))
				).findFirst().orElse(null);
		}
		
		if(s== null){
			s = SecurityDAO.getScopeStream(ctx, f -> 
					f.getDomainProperty().eq(domain.getId())
				).findFirst().orElse(null);
		}
		
		if(s == null && domain.getParentId() != null) {
			s = SecurityDAO.getScopeStream(ctx, f -> 
					f.getDomainProperty().eq(domain.getParentId())
				).findFirst().orElse(null);
		}
		return s;
	}

	private static String getLogoUrl(AonApiData api) {
		String logoUrl = AON_LOGO;
		Domain parentDomain = api.getDur().getDomain().isParent() ? api.getDur().getDomain() : api.getDur().getParentDomain();
		
		try (CloseableAONContext aonContext = AONContext.getAONContext(parentDomain.getName(), api.getUser().getLogin())) {
			Company company = AON.getCompany(parentDomain, api.getUser(), f -> f.getDomainProperty().eq(parentDomain.getId()));

			Attach attach = getLogoAttach(aonContext, company.getId());

			String str = "domain=" + attach.getDomain().getId() + "&id=" + attach.getId() + "&attach_type=registry";
			String result = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));

			Domain attachDomain = DomainDAO.getDomain(aonContext, attach.getDomain().getId());
			logoUrl = "https://" + parentDomain.getName() + "/ms/download_attachment/" + attachDomain.getName() + "/" + attach.getCreationUser() + "/"
					+ result;
			
//			logoUrl = (isLocal ? "http" : "https") + "://" + parentDomain.getName() + (isLocal ? ":8080" : "")
//					+ "/ms/download_attachment/" + attachDomain.getName() + "/" + attach.getCreationUser() + "/"
//					+ result;
		} catch (Exception e) {
			e.printStackTrace();
			logoUrl = AON_LOGO;
		}

		return logoUrl;
	}

	private static Attach getLogoAttach(AONContext aonContext, Integer enterpriseId) {
		Attach attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
				f -> f.getTypeProperty().eq(SIGNATURE.value()).and(f.getAttachModuleProperty().eq(enterpriseId)),
				REGISTRY);

		if (attach1 == null || attach1.getData() == null)
			attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
					f -> f.getTypeProperty().eq(LOGO.value()).and(f.getAttachModuleProperty().eq(enterpriseId)),
					REGISTRY);

		return attach1;
	}
	
	private static JSONObject getUserEmail(AonApiData api) {
		String login = JsonUtils.getString(api.getData(), "userLogin");
		
		Integer[] domains = api.getDomain().getParentId() != null
				? new Integer[] {api.getDomain().getId(), api.getDomain().getParentId()}
				: new Integer[] {api.getDomain().getId()};
		
		User user = AON.getUser(api.getDomain(), api.getUser().getLogin(), f -> 
			f.getDomainProperty().in(domains).and(
			f.getLoginProperty().eq(login)));	
		if(user != null && user.getAuth() != null && AonStringUtils.isNotBlank(user.getAuth().getEmail())) {
			return new JSONObject().put("email", user.getAuth().getEmail());			
		} else if(user != null && user.getId() != null) {
			MailAccount mailAccount = AON.getMailAccount(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					f -> f.getUserIdProperty().eq(user.getId()).and(f.getDomainProperty().eq(user.getDomain().getId())));
			return new JSONObject().put("email", mailAccount.getEmail());
		}
		return new JSONObject();
	}
	
}
