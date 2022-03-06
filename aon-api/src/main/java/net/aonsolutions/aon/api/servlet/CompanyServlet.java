package net.aonsolutions.aon.api.servlet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.RegistryAddressJSON;
import com.esferalia.aon.occam.api.json.RegistryMediaJSON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.Module;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.AonDomainUserRoles;
import com.esferalia.aon.occam.api.model.aonsolutions.AonRole;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.api.model.security.UserToolbar;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.registry.RegistryAdditionalInfo;
import net.aonsolutions.aon.api.servlet.registry.RegistryServlet;

@SuppressWarnings("serial")
@WebServlet(name = "AonCompanyServlet", urlPatterns = {"/ms/api/company/*"})
public class CompanyServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(CompanyServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API COMPANY SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req, resp);
			
			switch (api.getPath()) {
			case "/":
				response(req, resp, getCompanies(api));
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
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API COMPANY SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req, resp);
			
			switch (api.getPath()) {
			case "/app":
				response(req, resp, setDomainApp(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API COMPANY SERVLET - PUT METHOD");
		try {
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
			case "/":
				response(req, resp, saveCompany(api));
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
			String mail = AON.getRMedia(domain.getName(), domain.getId(), "", f -> f.getRegistryProperty().eq(c.getId()).and(f.getMediaProperty().eq(MediaType.EMAIL.value()))).getValue();
			Auth auth = createAuth(c, mail);
			User user = createUser(domain, auth, c);
			createUserScopes(api, domain, user);
			createUserRoles(api, domain, user, false);
			
			RegistryAddress address = AON.getMain(domain, user, c.getId());
			Workplace workplace = new Workplace()
					.setActive((byte) 1)
					.setAddress(address.getId())
					.setDescription("PRINCIPAL")
					.setDomain(domain.getId())
					.setEnterprise(c.getId());
			workplace = AON.saveWorkplace(domain, user, workplace);
			return CompanyJSON.toJSON(c);
		} else {	
			RegistryServlet.saveRegistry(api);
		}

		return api.getData();
	}
	
	private void checkCompany(AonApiData api, Company company) throws Exception {
		if(AonStringUtils.isBlank(company.getDocument())) {
			throw new Exception("El documento de la empresa está vacío.");
		}
		
		if(!AonDocumentUtil.isValid(company.getDocument())) {
			throw new Exception("El documento de la empresa no es válido");
		}
		
		if(AonStringUtils.isBlank(company.getName())) {
			throw new Exception("El nombre de la empresa está vacío");
		}
		Company c = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getDocumentProperty().eq(company.getDocument()));
		if(c.getId() != null) {
			throw new Exception("Ya existe una empresa con el mismo documento");
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
	
	private void saveAddress(AonApiData api, Domain domain, User user, Company company) {
		if(api.getData().opt(IJsonNames.ADDRESS) != null) {
			JSONObject address = api.getData().optJSONObject(IJsonNames.ADDRESS);
			RegistryAddress ra = RegistryAddressJSON.fromJSON(address);
			ra.setDomain(domain.getId());
			ra.setRegistry(company.getId());
			ra.setMain(true);
			AON.save(domain, user, ra);
		}
	}
	
	private String saveMedias(AonApiData api, Domain domain, User user, Company company) {
		if(api.getData().opt(IJsonNames.MEDIA) != null) {
			JSONObject media = api.getData().optJSONObject(IJsonNames.MEDIA);
			saveMedia(domain, user, company, media, IJsonNames.PHONE, MediaType.FIXED_PHONE);
			saveMedia(domain, user, company, media, IJsonNames.FAX, MediaType.FAX);
			RegistryMedia emailMedia = saveMedia(domain, user, company, media, IJsonNames.EMAIL, MediaType.EMAIL);
			saveMedia(domain, user, company, media, IJsonNames.WEB, MediaType.WEB);
			
			return AonStringUtils.isBlank(emailMedia.getValue()) ? null : emailMedia.getValue();
		}
		return null;
	}
	
	private RegistryMedia saveMedia(Domain domain, User user, Company company, JSONObject media, String name, MediaType type) {
		RegistryMedia rmedia = new RegistryMedia();
		if(media.opt(name) != null) {
			rmedia = RegistryMediaJSON.fromJSON(media.optJSONObject(name));
			if(rmedia.getId() == null) {
				rmedia.setDomain(domain.getId())
				.setRegistry(company.getId())
				.setMedia(type)
				.setAdministrative(true)
				.setCommercial(true)
				.setTechnical(true);
			}
			if(rmedia.getRegistry() == null) rmedia.setRegistry(company.getId());
			return AON.save(domain.getName(), domain.getId(), user.getLogin(), rmedia);
		}
		return rmedia;
	}
	
	private void createUserScopes(AonApiData api, Domain domain, User user) {
		AON.getScopeStream(domain.getName(), domain.getId(), user.getLogin(), f -> 
			f.getDomainProperty().eq(domain.getId())
			.or(f.getDomainProperty().eq(domain.getParentId())))
		.forEach(s ->{
			AON.insertUserScope(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), new UserScope()
					.setDomain(domain.getId())
					.setScope(s.getId())
					.setUserId(user.getId()));
		});
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
		UserAppRole uar = new UserAppRole()
				.setDomain(domain.getId())
				.setRole(role)
				.setUser(user.getId());
		AON_SOLUTIONS.insertUserAppRole(api.getDomain().getName(), api.getDomain().getId(), user.getLogin(), uar);
	}
	
		
	private JSONArray getCompanies(AonApiData api) {
		JSONArray jsArray = new JSONArray();
		if(api.getParams().opt("parent") != null && api.getParams().optBoolean("parent")) {
			AON.getCompanyStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
				f.getDomainParentProperty().eq(api.getDomain().getId()),
				api.getParams().optInt(IJsonNames.PAGE), api.getParams().optInt(IJsonNames.PER_PAGE))
			.forEach(c -> jsArray.put(CompanyJSON.toJSON(c)));
		} else {
			List<String> schemas = AONContext.getSchemas();
		
			for(String schema : schemas) {
				LinkedList<Integer> ds = new LinkedList<Integer>();
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
	
	private JSONObject getCompany(AonApiData api) {
		Company company =  api.getParams().opt("id") != null
			? AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
				f.getIdProperty().eq(api.getParams().optInt("id"))) 
			: AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
				f.getDomainProperty().eq(api.getDomain().getId()));
		JSONObject json = CompanyJSON.toJSON(company);
		
		LinkedList<RegistryAdditionalInfo> list = new LinkedList<>();
		list.add(RegistryAdditionalInfo.ADDRESS);
		list.add(RegistryAdditionalInfo.MEDIA);

		return RegistryServlet.getRegistryAdditionalInfo(json, api, api.getParams(), company.getId(), list);
	}
	
	private JSONObject getDomainUserRoles(AonApiData api) {
		DomainUserRoles dur = SECURITY.getDomainUserRoles(api.getDomain(), api.getUser().getLogin(), api.getUser().getId());
		AonDomainUserRoles adur = new AonDomainUserRoles(dur);
		return adur.toJSON();
	}
	
	private JSONArray getDomainApps(AonApiData api){
		JSONArray array = new JSONArray();
		LinkedList<AonApp> list = new LinkedList<>();
		DomainUserRoles dur = SECURITY.getDomainUserRoles(api.getDomain(), api.getUser().getLogin(), api.getUser().getId());
		dur.getParentDomainApps().stream().forEach(app -> list.add(app));
		dur.getDomainApps().stream().forEach(app -> {
			if(!list.contains(app)) list.add(app);
		});
		
		list.stream().forEach(app -> {	
			array.put(app.name().toLowerCase());
		});
		if(array.isEmpty()) {
			return oldModules(api.getDomain());
		}
		return array;
	}
	
	private JSONObject getNotices(AonApiData api) {
		JSONObject jsonG = AON.getRawdocUserData(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin()).toJSON();
		HashMap<Byte, Integer> map = AON_SOLUTIONS.getTaskStatusCount(api.getDomain(), api.getUser(), f-> f.getDomainProperty().eq(api.getDomain().getId()));
		JSONObject request = new JSONObject();
		map.forEach((k,v)->{
			request.put(TaskStatus.safeValueOf(k).getName(), v);
		});
		jsonG.put("solicitudes",request);
		return jsonG;
	}
	
	private JSONObject getMedia(AonApiData api) {
		Integer registryId = null;
		if(api.getParams().opt("id") != null) registryId = api.getParams().optInt("id");
		if(registryId == null && api.getData().opt("company") != null) registryId = api.getParams().optInt("company");
		if(registryId == null) registryId = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
			f.getDomainProperty().eq(api.getDomain().getId())).getId();
		
		JSONObject json = new JSONObject();
		if(registryId != null) {
			Integer id = registryId;
			AON.getRMediaStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
				f.getRegistryProperty().eq(id))
			.forEach(rm -> {
				json.put(rm.getMedia().name().toLowerCase(), rm.getValue());
			});
		}
		return json;
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
			} else if(Module.COMUNICA.equals(r)) {
				
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
	
	private JSONObject setDomainApp(AonApiData api){
		JSONArray array = api.getData().optJSONArray("apps");
		Integer users = JsonUtils.getInteger(api.getData(), "users");
		if(users != null) {
			AON_SOLUTIONS.saveDomainMaxDefinedUser(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), users);
		}
		LinkedList<AonApp> apps = new LinkedList<>();
		for(int i = 0; i < array.length(); i++) {
			AonApp app = AonApp.safeValueOf(array.optString(i));
			if(app != null) {
				apps.add(AonApp.safeValueOf(array.optString(i)));
			}
		}
		LinkedList<DomainApp> activeDomainApps = new LinkedList<>();
		
		for (AonApp aonApp : AonApp.values()) {
			DomainApp domainApp = AON_SOLUTIONS.getDomainApp(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
			f.getDomainProperty().eq(api.getDomain().getId()).and(f.getAppProperty().eq(aonApp.value()))).findFirst().orElse(new DomainApp());
			if(apps.contains(aonApp)) {
				 activeDomainApps.add(domainApp
						 .setDomain(api.getDomain().getId())
						 .setApp(aonApp)
						 .setActive(true));
			} else if(!domainApp.isEmpty()) {
				AON_SOLUTIONS.saveDomainApp(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), domainApp.setActive(false), true);
			}
		}

		for (DomainApp domainApp : activeDomainApps) {
			AON_SOLUTIONS.saveDomainApp(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), domainApp, true);
		}

		return new JSONObject();
	}
	
	private JSONArray getBanks(AonApiData api) {
		JSONArray array = new JSONArray();
		Domain domain = api.getDomain();
		String login = api.getUser().getLogin();
		Company company = AON.getCompany(domain.getName(),domain.getId(), login, 
				f-> f.getDomainProperty().eq(api.getDomain().getId()));
		
		LinkedList<RegistryBank> registryBanks = AON.getRegistryBanks(domain.getName(), domain.getId(), login, company.getId());
		
		registryBanks.stream().forEach(rb->{
			JSONObject json = new JSONObject();
			json.put("id", rb.getId());
			json.put("bankAccount", rb.getBankAccount());
			json.put("bic", rb.getBic());
			json.put("suffix", rb.getSuffix());
			json.put("alias", rb.getAlias());
			json.put("account", rb.getAccount());
			json.put("accountCode", rb.getAccountCode());
			json.put("accountDescription", rb.getAccountDescription());
			array.put(json);
		});
		
		return array;
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
	
}
