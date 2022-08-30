package net.aonsolutions.aon.api.servlet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.json.CompanyJSON;
import com.esferalia.aon.occam.api.json.EnterpriseActivityJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.RegistryAddressJSON;
import com.esferalia.aon.occam.api.json.RegistryBankJSON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Module;
import com.esferalia.aon.occam.api.model.Properties.CompanyProperties;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.AonDomainUserRoles;
import com.esferalia.aon.occam.api.model.aonsolutions.AonRole;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.api.model.security.UserToolbar;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.booking.BookingUtils;
import net.aonsolutions.aon.api.servlet.registry.RegistryAdditionalInfo;
import net.aonsolutions.aon.api.servlet.registry.RegistryServlet;
import net.aonsolutions.aon.api.servlet.task.TaskFilter;

@SuppressWarnings("serial")
@WebServlet(name = "AonCompanyServlet", urlPatterns = {"/ms/api/company/*"})
public class CompanyServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(CompanyServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API COMPANY SERVLET - GET METHOD");
		get(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API COMPANY SERVLET - POST METHOD");
		get(req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API COMPANY SERVLET - PUT METHOD");
		put(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		try {
			AonApiData api = initialize(req);
			
			switch (api.getPath()) {
			case "/":
				response(req, resp, getCompanies(api));
				break;
			case "/domain":
				response(req, resp, getDomainCompanies(api));
				break;
			case "/one":
				response(req, resp, getCompany(api));
				break;
			case "/app":
				response(req, resp, getDomainApps(api));
				break;
			case "/approles":
				response(req, resp, getDomainUserRoles(api));
				break;
			case "/notice":
				response(req, resp, getNotices(api));
				break;
			case "/media":
				response(req, resp, getMedia(api));
				break;
			case "/address":
				response(req, resp, getMainAddress(api));
				break;	
			case "/banks":
				response(req, resp, getBanks(api));
				break;	
			case "/header":
				response(req, resp, getHeaderInfo(api));
				break;	
			case "/activities":
				response(req, resp, getActivities(api));
				break;	
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}	
	}
	
	private void put(HttpServletRequest req, HttpServletResponse resp) {
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, saveCompany(api));
				break;
			case "/booking":
				response(req, resp, saveBooking(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONObject saveCompany(AonApiData api) throws Exception {
		Company company = CompanyJSON.fromJSON(api.getData());
		checkCompany(api, company);
		company.setLegalPerson(AonDocumentUtil.isValidCIF(company.getDocument()));
		if(company.getId() == null) {
			String domainName = company.getDocument() + "-" + api.getDomain().getName();
			Domain d = new Domain()
				.setName(domainName.toLowerCase())
				.setDescription(company.getName())
				.setOwner(api.getDomain().getOwner())
				.setParentId(api.getDomain().getId())
				.setActive(true)
				.setDomainType(DomainType.ENTERPRISE)
				.setEnableHeredity(true)
				.setDomainManagement(false);
			Domain domain = AON_SOLUTIONS.insertDomain(api.getDomain(), api.getUser(), d, company);
			Company c = AON.getCompany(domain.getName(), domain.getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(domain.getId()));
			RegistryServlet.saveRegistryAdditionalInfo(api, c.getId(), c.getDomain().getId());
			String mail = AON.getRegistryMedia(domain, api.getUser(), f -> f.getRegistryProperty().eq(c.getId()).and(f.getMediaProperty().eq(MediaType.EMAIL.value()))).getValue();
			Auth auth = createAuth(c, mail);
			User user = createUser(domain, auth, c);
			createUserScopes(api, domain, user);
			createUserRoles(api, domain, user, false);
			
			RegistryAddress address = AON.getMain(domain, user, c.getId());
			Workplace workplace = new Workplace()
					.setActive(true)
					.setAddress(address.getId())
					.setDescription("PRINCIPAL")
					.setDomain(domain.getId())
					.setEnterprise(c.getId());
			AON.saveWorkplace(domain, user, workplace);
			return CompanyJSON.toJSON(c);
		} else {	
			RegistryServlet.saveRegistry(api);
		}

		return api.getData();
	}
	
	private void checkCompany(AonApiData api, Company company) throws AonApiException {
		if(AonStringUtils.isBlank(company.getDocument())) {
			throw new AonApiException("El documento de la empresa está vacío.");
		}
		
		if(!AonDocumentUtil.isValid(company.getDocument())) {
			throw new AonApiException("El documento de la empresa no es válido");
		}
		
		if(AonStringUtils.isBlank(company.getName())) {
			throw new AonApiException("El nombre de la empresa está vacío");
		}
		Company c = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getDocumentProperty().eq(company.getDocument()));
		if(c.getId() != null) {
			throw new AonApiException("Ya existe una empresa con el mismo documento");
		}
	}
	
	private Auth createAuth(Company company, String mail) {
		mail = mail != null && Utils.isEmail(mail) 
			? mail : company.getDocument() + "@aon.solutions";
		Auth auth = AON_SOLUTIONS.getAuth(mail);
		if(auth.getUuid() == null) {
    		String pass = Utils.createPasswordHash(mail, company.getDocument());
			auth = new Auth()
					.setDocument(company.getDocument())
					.setEmail(mail)
					.setName(company.getName())
					.setPassword(pass);
			auth = AON_SOLUTIONS.insertAuth(company.getDomain().getName(), company.getDomain().getId(), auth);
		} 
		return auth;
	}
	
	
	private User createUser(Domain domain, Auth auth, Company company) {
		User user = AON_SOLUTIONS.getUserUuid(domain, auth.getUuid());
		if(user.getId() == null) {
			String pass = Utils.createPasswordHash(auth.getEmail(), company.getDocument());
			user = new User()
				.setAuth(auth)
				.setActive(true)
				.setDomain(domain.getId())
				.setLogin(company.getDocument())
				.setName(company.getName())
				.setEnterprise(company.getId())
				.setRegistry(company.getId())
				.setShared(false)
				.setToolbar(UserToolbar.GOOGLE);
			user = AON.save(domain.getName(), domain.getId(), "", user);
			AON.updateUserPassword(domain.getName(), domain.getId(), "", user.getId(), pass);
		}
		return user;
	}
	
	private void createUserScopes(AonApiData api, Domain domain, User user) {
		AON.getScopeStream(domain.getName(), domain.getId(), user.getLogin(), f -> 
			f.getDomainProperty().eq(domain.getId())
			.or(f.getDomainProperty().eq(domain.getParentId())))
		.forEach(s ->
			AON.insertUserScope(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), new UserScope()
					.setDomain(domain.getId())
					.setScope(s.getId())
					.setUserId(user.getId()))
		);
	}
	
	private void createUserRoles(AonApiData api, Domain domain, User user, boolean bidoq) {
		createUserRole(api, domain, user, AonRole.ENTERPRISE);
		createUserRole(api, domain, user, AonRole.ACCOUNTING);
		createUserRole(api, domain, user, AonRole.FISCAL);
		createUserRole(api, domain, user, AonRole.PAYROLL);
		createUserRole(api, domain, user, AonRole.PAYROLL_PORTAL);
		createUserRole(api, domain, user, AonRole.COMUNICA);
		createUserRole(api, domain, user, AonRole.COMUNICA_PORTAL);
		createUserRole(api, domain, user, AonRole.DOCUMENTAL);
		createUserRole(api, domain, user, AonRole.DOCUMENTAL_PORTAL);
		createUserRole(api, domain, user, AonRole.TIMECONTROL);
		createUserRole(api, domain, user, AonRole.TIMECONTROL_PORTAL);
		createUserRole(api, domain, user, AonRole.INVOICE);
		createUserRole(api, domain, user, AonRole.INVOICE_PORTAL);
		createUserRole(api, domain, user, AonRole.MESSENGER);
		createUserRole(api, domain, user, AonRole.OCR);
		createUserRole(api, domain, user, AonRole.AIO);
		
		if(bidoq) createUserRole(api, domain, user, AonRole.BIDOQ);
	}
	
	private void createUserRole(AonApiData api, Domain domain, User user, AonRole role) {
		AON_SOLUTIONS.insertUserAppRole(api.getDomain().getName(), api.getDomain().getId(), user.getLogin(), new UserAppRole()
				.setDomain(domain.getId())
				.setRole(role)
				.setUser(user.getId()));
	}
	
	private JSONArray getCompanies(AonApiData api) {
		JSONArray jsArray = new JSONArray();
		if((JsonUtils.has(api.getData(), IJsonNames.PARENT) && JsonUtils.getboolean(api.getData(), IJsonNames.PARENT))
				|| JsonUtils.has(api.getData(), IJsonNames.DOCUMENT)) {
			AON.getCompanyStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> companyFilter(api, f),
				api.getData().optInt(IJsonNames.PAGE), api.getData().optInt(IJsonNames.PER_PAGE))
			.forEach(c -> jsArray.put(CompanyJSON.toJSON(c)));
		} else {
			List<String> schemas = AONContext.getSchemas();
		
			for(String schema : schemas) {
				LinkedList<Integer> ds = new LinkedList<>();	
				AON_SOLUTIONS.getCompanyStream(api.getToken(), schema, null, null)
				.sorted((o1, o2) -> o1.getCompany().getName().compareTo(o2.getCompany().getName())).forEach(
						ac -> {
							if(!ds.contains(ac.getDomain().getId())){
								jsArray.put(ac.toJSON());
								ds.add(ac.getDomain().getId());
							}
						});
			}
		}
		return jsArray;
	}
	
	private JSONArray getDomainCompanies(AonApiData api) {
		JSONArray jsArray = new JSONArray();

		List<String> schemas = AONContext.getSchemas();
		
		schemas.stream().forEach(schema -> {
			String domain = AONContext.getSchemaFirstDomain(schema);
			AON.getCompanyStream(domain, 0, "", f -> companyFilter(api, f))
				.forEach(c -> jsArray.put(CompanyJSON.toJSON(c).put(IJsonNames.SCHEMA, schema)));			
		});

		return jsArray;
	}
	
	
	
	private Filter companyFilter(AonApiData api, CompanyProperties f) {
		JSONObject json = api.getData();
		Filter filter = null;

		if(JsonUtils.has(json, IJsonNames.PARENT) && JsonUtils.getboolean(json, IJsonNames.PARENT)) {
			filter = f.getDomainParentProperty().eq(api.getDomain().getId());
		}

		if(JsonUtils.has(json, IJsonNames.DOCUMENT) && AonStringUtils.isNotBlank(JsonUtils.getString(json, IJsonNames.DOCUMENT))) {
			Filter aux = f.getDocumentProperty().eq(JsonUtils.getString(json, IJsonNames.DOCUMENT));
			filter = filter != null ? filter.and(aux) : aux;
		}
		
		return filter;
	}
	
	
	private JSONObject getCompany(AonApiData api) {
		Company company =  api.getData().opt("id") != null
			? AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
				f.getIdProperty().eq(api.getData().optInt("id"))) 
			: AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
				f.getDomainProperty().eq(api.getDomain().getId()));
		JSONObject json = CompanyJSON.toJSON(company);
		
		LinkedList<RegistryAdditionalInfo> list = new LinkedList<>();
		list.add(RegistryAdditionalInfo.ADDRESS);
		list.add(RegistryAdditionalInfo.MEDIA);

		return RegistryServlet.getRegistryAdditionalInfo(json, api, api.getData(), company.getId(), list);
	}
	
	private JSONObject getDomainUserRoles(AonApiData api) {
		DomainUserRoles dur = SECURITY.getDomainUserRoles(api.getDomain(), api.getUser().getLogin(), api.getUser().getId());
		AonDomainUserRoles adur = new AonDomainUserRoles(dur);
		return adur.toJSON();
	}
	
	private JSONArray getDomainApps(AonApiData api){
		JSONArray array = new JSONArray();
		
		DomainUserRoles dur = SECURITY.getDomainUserRoles(api.getDomain(), api.getUser().getLogin(), api.getUser().getId());
		
		dur.getParentDomainApps().stream()
			.forEach(app -> array.put(app.name().toLowerCase()));
		
		dur.getDomainApps().stream()
			.filter(f -> !dur.getParentDomainApps().contains(f))
			.forEach(app -> array.put(app.name().toLowerCase()));
		
		if(array.isEmpty()) {
			return oldModules(api.getDomain());
		}
		return array;
	}
	
	private JSONObject getNotices(AonApiData api) {
		JSONObject jsonG = AON.getRawdocUserData(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin()).toJSON();
		
		taskCount(api, jsonG);
		
		return jsonG;
	}
	
	private void taskCount(AonApiData api, JSONObject jsonG) {
		try {			
		
			Map<String, Integer> counts = AON_SOLUTIONS.getTaskCount(api.getDomain(), api.getUser(), 
				f -> TaskFilter.taskSenderCount(api, api.getDomain(), f, new Customer()), 
				f -> TaskFilter.taskReceiverCount(api, api.getDomain(), f, new Customer())
			);
			JSONObject request = new JSONObject();
			counts.keySet().stream().forEach(k-> request.put(k, counts.get(k)) );
			
			jsonG.put("solicitudes",request);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private JSONObject getMedia(AonApiData api) {
		Integer registryId = getRegistryId(api);
		JSONObject json = new JSONObject();
		if(registryId != null) {
			AON.getRegistryMediaStream(api.getDomain(), api.getUser(), 
				f -> f.getRegistryProperty().eq(registryId))
			.forEach(rm -> json.put(rm.getMedia().name().toLowerCase(), rm.getValue()));
		}
		return json;
	}
	
	private Integer getRegistryId(AonApiData api) {
		Integer registryId = JsonUtils.getInteger(api.getData(), IJsonNames.ID);
		if(registryId == null) registryId = JsonUtils.getInteger(api.getData(), IJsonNames.COMPANY);
		if(registryId == null) registryId = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
			f.getDomainProperty().eq(api.getDomain().getId())).getId();
		return registryId;
	}
	
	private JSONObject getMainAddress(AonApiData api) {
		Integer registryId = api.getData().opt("company") != null 
			? api.getData().optInt("company") 
			: AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
					f.getDomainProperty().eq(api.getDomain().getId())).getId();
		
		RegistryAddress address = AON.getMain(api.getDomain(), api.getUser(), registryId);
		return RegistryAddressJSON.toJSON(address);
	}
	
	private JSONArray oldModules(Domain domain) {
		JSONArray array = new JSONArray();
		AON.getDomainModules(domain.getName(), domain.getId(), "").forEach(r -> {
			DomainApp dapp = new DomainApp()
					.setDomain(domain.getId())
					.setActive(true);
			if(Module.ACCOUNTING.equals(r)) {
				dapp.setApp(AonApp.ACCOUNTING);
				AON_SOLUTIONS.saveDomainApp(domain.getName(), domain.getId(), "", dapp, false);
			} else if(Module.AON_FINANCE.equals(r)) {
				dapp.setApp(AonApp.INVOICE);
				AON_SOLUTIONS.saveDomainApp(domain.getName(), domain.getId(), "", dapp, false);
			} else if(Module.CALL_CENTER.equals(r)) {
				dapp.setApp(AonApp.MESSENGER);
				AON_SOLUTIONS.saveDomainApp(domain.getName(), domain.getId(), "", dapp, false);
			} else if(Module.DOCUMENT.equals(r)) {
				dapp.setApp(AonApp.DOCUMENTAL);
				AON_SOLUTIONS.saveDomainApp(domain.getName(), domain.getId(), "", dapp, false);
			} else if(Module.FISCAL.equals(r)) {
				dapp.setApp(AonApp.FISCAL);
				AON_SOLUTIONS.saveDomainApp(domain.getName(), domain.getId(), "", dapp, false);
			} else if(Module.PAYROLL.equals(r)) {
				dapp.setApp(AonApp.PAYROLL);
				AON_SOLUTIONS.saveDomainApp(domain.getName(), domain.getId(), "", dapp, false);
			}
			if(dapp.getApp() != null && dapp.isActive())
				array.put(dapp.getApp().name().toLowerCase());

		});			
		return array;
	}
	
	private JSONObject saveBooking(AonApiData api){
		Booking oldBooking = AON.getBooking(api.getDomain(), api.getUser());
		Booking newBooking = new Booking()
			.setDomain(api.getDomain())
			.setCompany(oldBooking.getCompany())
			.setApps(safeValueOf(JsonUtils.getJSONArray(api.getData(), IJsonNames.APPS)))
			.setNumberOfUsers(JsonUtils.getInteger(api.getData(), IJsonNames.USERS))
			.setPayer("");
		
		AON.saveBooking(api.getDomain(), api.getUser(), newBooking);
		BookingUtils.getInstance().sendMail(api.getDomain(), api.getUser(), oldBooking, newBooking);
		return new JSONObject();
	}
	
	private JSONArray getBanks(AonApiData api) {
		Company company = AON.getCompany(api.getDomain(), api.getUser(), 
				f-> f.getDomainProperty().eq(api.getDomain().getId()));

		return RegistryBankJSON.toJSON(AON.getRegistryBanks(
			api.getDomain(), api.getUser(), company.getId()));
	}
	
	private JSONObject getHeaderInfo(AonApiData api) {
		DomainUserRoles dur = SECURITY.getDomainUserRoles(api.getDomain(), api.getUser().getLogin(), api.getUser().getId());
		JSONObject json = new JSONObject();
		if(dur.isEmployee()) {
			Company company = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
					f -> f.getDomainProperty().eq(api.getDomain().getId()));
			RegistryMediaFilter filter  = f -> f.getRegistryProperty().eq(company.getId());
			AON.getStream(api.getDomain(), api.getUser(), filter)
				.forEach(media -> json.put(media.getMedia().name().toLowerCase(), media.getValue())); 
			json.put("name", company.getName());
			json.put("logo", "https://" + company.getDomain().getName() + "/aonDocuments/company.logo");
		} else if(api.getDomain().getParentId() != null) {
			Company company = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					f -> f.getDomainProperty().eq(api.getDomain().getParentId()));
			RegistryMediaFilter filter  = f -> f.getRegistryProperty().eq(company.getId());
			AON.getStream(api.getDomain(), api.getUser(), filter)
				.forEach(media -> json.put(media.getMedia().name().toLowerCase(), media.getValue())); 
			json.put("name", company.getName());
			json.put("logo", "https://" + company.getDomain().getName() + "/aonDocuments/company.logo");
		}
		return json;	
	}
	
	private JSONArray getActivities(AonApiData api) {
		JSONArray array = new JSONArray();
		AON.getEnterpriseActivities(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin())
			.forEach(ea -> array.put(EnterpriseActivityJSON.toJSON(ea))); 
		return array;		
	}
	
	public static List<AonApp> safeValueOf(JSONArray array){
		LinkedList<AonApp> apps = new LinkedList<>();
		for(int i = 0; i < array.length(); i++) {
			AonApp app = AonApp.safeValueOf(array.optString(i));
			if(app != null) {
				apps.add(AonApp.safeValueOf(array.optString(i)));
			}
		}
		return apps;
	}
}
