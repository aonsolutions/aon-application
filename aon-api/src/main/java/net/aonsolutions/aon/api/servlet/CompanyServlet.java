package net.aonsolutions.aon.api.servlet;
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
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.RegistryAddressJSON;
import com.esferalia.aon.occam.api.json.RegistryJSON;
import com.esferalia.aon.occam.api.json.RegistryMediaJSON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.Module;
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
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "AonCompanyServlet", urlPatterns = {"/ms/api/company/*"})
public class CompanyServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(CompanyServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API COMPANY SERVLET - GET METHOD");
		try {
			super.doGet(req, resp);

			switch (getPath()) {
			case "/":
				response(req, resp, getCompanies());
				break;
			case "/one":
				response(req, resp, getCompany());
				break;
			case "/app":
				response(req, resp, getDomainApps());
				break;
			case "/approles":
				response(req, resp, getDomainUserRoles());
				break;
			case "/notice":
				response(req, resp, getNotices());
				break;
			case "/media":
				response(req, resp, getMedia());
				break;
			case "/address":
				response(req, resp, getMainAddress());
				break;	
			case "/banks":
				response(req, resp, getBanks());
				break;	
			case "/header":
				response(req, resp, getHeaderInfo());
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
		LOGGER.info("AON API COMPANY SERVLET - POST METHOD");
		try {
			super.doPost(req, resp);

			switch (getPath()) {
			case "/app":
				response(req, resp, setDomainApp());
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API COMPANY SERVLET - POST METHOD");
		try {
			super.doPut(req, resp);
			switch (getPath()) {
			case "/":
				response(req, resp, saveCompany());
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONObject saveCompany() throws Exception {
		Company company = CompanyJSON.fromJSON(getData());
		if(company.getId() == null) {
			checkCompany(company);
			Domain domain = new Domain()
				.setName(company.getDocument() + "-" + getDomain().getName())
				.setDescription(company.getName())
				.setOwner(getDomain().getOwner())
				.setParentId(getDomain().getId())
				.setActive(true)
				.setDomainType(DomainType.ENTERPRISE)
				.setEnableHeredity(false)
				.setDomainManagement(false);
			Domain d = AON_SOLUTIONS.insertDomain(getDomain(), getUser(), domain, company);
			Company c = AON.getCompany(d.getName(), d.getId(), getUser().getLogin(), f -> f.getDomainProperty().eq(d.getId()));
			String mail = saveMedias(d, getUser(), c);
			saveAddress(d, getUser(), c);
			Auth auth = createAuth(c, mail);
			User user = createUser(d, auth, c);
			createUserScopes(domain, user);
			createUserRoles(d, user, false);
			return CompanyJSON.toJSON(c);
		} else {	
			Domain d = AON.getDomain(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> f.getIdProperty().eq(company.getDomain().getId()));
			saveMedias(d, getUser(), company);
			saveAddress(d, getUser(), company);
			AON.save(getDomain().getName(), getDomain().getId(), getUser().getLogin(), company);
		}

		return getData();
	}
	
	private void checkCompany(Company company) throws Exception {
		if(AonStringUtils.isBlank(company.getDocument())) {
			throw new Exception("El documento de la empresa está vacío.");
		}
		
		if(!AonDocumentUtil.isValid(company.getDocument())) {
			throw new Exception("El documento de la empresa no es válido");
		}
		
		if(AonStringUtils.isBlank(company.getName())) {
			throw new Exception("El nombre de la empresa está vacío");
		}
		Company c = AON.getCompany(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> f.getDocumentProperty().eq(company.getDocument()));
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
			auth = AON_SOLUTIONS.insertAuth(auth);
		} 
		return auth;
	}
	
	
	private User createUser(Domain domain, Auth auth, Company company) {
		User user = AON_SOLUTIONS.getUserUuid(domain, auth.getUuid());
		if(user.getId() == null) {
			String pass = Utils.createPasswordHash(auth.getEmail(), company.getDocument());
			user = new User()
				.setAuth(auth.getAuth())
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
	
	private void saveAddress(Domain domain, User user, Company company) {
		if(getData().opt(IJsonNames.ADDRESS) != null) {
			JSONObject address = getData().optJSONObject(IJsonNames.ADDRESS);
			RegistryAddress ra = RegistryAddressJSON.fromJSON(address);
			ra.setDomain(domain.getId());
			ra.setRegistry(company.getId());
			ra.setMain(true);
			AON.save(domain, user, ra);
		}
	}
	
	private String saveMedias(Domain domain, User user, Company company) {
		if(getData().opt(IJsonNames.MEDIA) != null) {
			JSONObject media = getData().optJSONObject(IJsonNames.MEDIA);
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
	
	private void createUserScopes(Domain domain, User user) {
		AON.getScopeStream(domain.getName(), domain.getId(), user.getLogin(), f -> 
			f.getDomainProperty().eq(domain.getId())
			.or(f.getDomainProperty().eq(domain.getParentId())))
		.forEach(s ->{
			AON.insertUserScope(getDomain().getName(), getDomain().getId(), getUser().getLogin(), new UserScope()
					.setDomain(domain.getId())
					.setScope(s.getId())
					.setUserId(user.getId()));
		});
	}
	
	private void createUserRoles(Domain domain, User user, Boolean bidoq) {
		createUserRole(domain, user, AonRole.ENTERPRISE);
		createUserRole(domain, user, AonRole.ACCOUNTING);
		createUserRole(domain, user, AonRole.FISCAL);
		createUserRole(domain, user, AonRole.PAYROLL);
		createUserRole(domain, user, AonRole.PAYROLL_PORTAL);
		createUserRole(domain, user, AonRole.COMUNICA);
		createUserRole(domain, user, AonRole.COMUNICA_PORTAL);
		createUserRole(domain, user, AonRole.DOCUMENTAL);
		createUserRole(domain, user, AonRole.DOCUMENTAL_PORTAL);
		createUserRole(domain, user, AonRole.TIMECONTROL);
		createUserRole(domain, user, AonRole.TIMECONTROL_PORTAL);
		createUserRole(domain, user, AonRole.INVOICE);
		createUserRole(domain, user, AonRole.INVOICE_PORTAL);
		createUserRole(domain, user, AonRole.MESSENGER);
		createUserRole(domain, user, AonRole.OCR);
		createUserRole(domain, user, AonRole.AIO);
		
		if(bidoq) {
			createUserRole(domain, user, AonRole.BIDOQ);
		}
		
	}
	
	private void createUserRole(Domain domain, User user, AonRole role) {
		UserAppRole uar = new UserAppRole()
				.setDomain(domain.getId())
				.setRole(role)
				.setUser(user.getId());
		uar = AON_SOLUTIONS.insertUserAppRole(getDomain().getName(), getDomain().getId(), user.getLogin(), uar);
	}
	
		
	private JSONArray getCompanies() {
		JSONArray jsArray = new JSONArray();
		if(getParams().opt("parent") != null && getParams().optBoolean("parent")) {
			AON.getCompanyStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> 
				f.getDomainParentProperty().eq(getDomain().getId())).forEach(c -> jsArray.put(CompanyJSON.toJSON(c)));
		} else {
			List<String> schemas = AONContext.getSchemas();
		
			for(String schema : schemas) {
				AON_SOLUTIONS.getCompanyStream(getToken(), schema, null, null)
				.sorted((o1, o2) -> o1.getCompany().getName().compareTo(o2.getCompany().getName())).forEach(
						ac -> jsArray.put(ac.toJSON()));
			}
		}
		return jsArray;
	}
	
	private JSONObject getCompany() {
		Company company =  getParams().opt("id") != null
			? AON.getCompany(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> 
				f.getIdProperty().eq(getParams().optInt("id"))) 
			:AON.getCompany(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> 
				f.getDomainProperty().eq(getDomain().getId()));
		JSONObject json = RegistryJSON.toJSON(company);
		
		RegistryAddress address = AON.getMain(getDomain(), getUser(), company.getId());
		json.put(IJsonNames.ADDRESS, RegistryAddressJSON.toJSON(address));
		
		JSONObject mJson = new JSONObject();
		RegistryMediaFilter filter  = f -> f.getRegistryProperty().eq(company.getId());
		AON.getStream(getDomain(), getUser(), filter)
			.forEach(media -> mJson.put(media.getMedia().name().toLowerCase(), RegistryMediaJSON.toJSON(media))); 
		json.put(IJsonNames.MEDIA, mJson);
		return json; 
	}
	
	private JSONObject getDomainUserRoles() {
		DomainUserRoles dur = SECURITY.getDomainUserRoles(getDomain(), getUser().getLogin(), getUser().getId());
		AonDomainUserRoles adur = new AonDomainUserRoles(dur);
		return adur.toJSON();
	}
	
	private JSONArray getDomainApps(){
		JSONArray array = new JSONArray();
		LinkedList<AonApp> list = new LinkedList<>();
		DomainUserRoles dur = SECURITY.getDomainUserRoles(getDomain(), getUser().getLogin(), getUser().getId());
		dur.getParentDomainApps().stream().forEach(app -> list.add(app));
		dur.getDomainApps().stream().forEach(app -> {
			if(!list.contains(app)) list.add(app);
		});
		
		list.stream().forEach(app -> {	
			array.put(app.name().toLowerCase());
		});
		if(array.isEmpty()) {
			return oldModules(getDomain());
		}
		return array;
	}
	
	private JSONObject getNotices() {
		return AON.getRawdocUserData(getDomain().getName(), getDomain().getId(), getUser().getLogin()).toJSON();
	}
	
	private JSONObject getMedia() {
		Integer registryId = null;
		if(getParams().opt("id") != null) registryId = getParams().optInt("id");
		if(registryId == null &&  getData().opt("company") != null) registryId = getParams().optInt("company");
		if(registryId == null) registryId = AON.getCompany(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> 
			f.getDomainProperty().eq(getDomain().getId())).getId();
		
		JSONObject json = new JSONObject();
		if(registryId != null) {
			Integer id = registryId;
			AON.getRMediaStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> 
				f.getRegistryProperty().eq(id))
			.forEach(rm -> {
				json.put(rm.getMedia().name().toLowerCase(), rm.getValue());
			});
		}
		return json;
	}
	
	private JSONObject getMainAddress() {
		Integer registryId = getData().opt("company") != null 
			? getData().optInt("company") 
			: AON.getCompany(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> 
					f.getDomainProperty().eq(getDomain().getId())).getId();
		
		RegistryAddress address = AON.getMain(getDomain(), getUser(), registryId);
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
			if(dapp.getApp() != null && dapp.getActive())
				array.put(dapp.getApp().name().toLowerCase());

		});			
		return array;
	}
	
	private JSONObject setDomainApp(){
		JSONArray array = getData().optJSONArray("apps");
		LinkedList<AonApp> apps = new LinkedList<>();
		for(int i = 0; i < array.length(); i++) {
			AonApp app = AonApp.safeValueOf(array.optString(i));
			if(app != null) {
				apps.add(AonApp.safeValueOf(array.optString(i)));
			}
		}
		LinkedList<DomainApp> activeDomainApps = new LinkedList<DomainApp>();
		
		for (AonApp aonApp : AonApp.values()) {
			DomainApp domainApp = AON_SOLUTIONS.getDomainApp(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> 
			f.getDomainProperty().eq(getDomain().getId()).and(f.getAppProperty().eq(aonApp.value()))).findFirst().orElse(new DomainApp());
			if(apps.contains(aonApp)) {
				 activeDomainApps.add(domainApp
						 .setDomain(getDomain().getId())
						 .setApp(aonApp)
						 .setActive(true));
			} else if(!domainApp.isEmpty()) {
				AON_SOLUTIONS.saveDomainApp(getDomain().getName(), getDomain().getId(), getUser().getLogin(), domainApp.setActive(false), true);
			}
		}

		for (DomainApp domainApp : activeDomainApps) {
			AON_SOLUTIONS.saveDomainApp(getDomain().getName(), getDomain().getId(), getUser().getLogin(), domainApp, true);
		}

		return new JSONObject();
	}
	
	private JSONArray getBanks() {
		JSONArray array = new JSONArray();
		Domain domain = getDomain();
		String login = getUser().getLogin();
		Company company = AON.getCompany(domain.getName(),domain.getId(), login, f-> f.getDomainProperty().eq(getDomain().getId()));
		
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
	
	private JSONObject getHeaderInfo() {
		DomainUserRoles dur = SECURITY.getDomainUserRoles(getDomain(), getUser().getLogin(), getUser().getId());
		JSONObject json = new JSONObject();
		if(dur.isEmployee()) {
			Company company = AON.getCompany(getDomain().getName(),getDomain().getId(), getUser().getLogin(), f -> f.getDomainProperty().eq(getDomain().getId()));
			RegistryMediaFilter filter  = f -> f.getRegistryProperty().eq(company.getId());
			AON.getStream(getDomain(), getUser(), filter)
				.forEach(media -> json.put(media.getMedia().name().toLowerCase(), media.getValue())); 
			json.put("name", company.getName());
			json.put("url", "http://" + company.getDomain().getName() + ":8080/aon-aio/aonDocuments/company.logo");
		} else if(getDomain().getParentId() != null) {
			Company company = AON.getCompany(getDomain().getName(),getDomain().getId(), getUser().getLogin(), f -> f.getDomainProperty().eq(getDomain().getParentId()));
			RegistryMediaFilter filter  = f -> f.getRegistryProperty().eq(company.getId());
			AON.getStream(getDomain(), getUser(), filter)
				.forEach(media -> json.put(media.getMedia().name().toLowerCase(), media.getValue())); 
			json.put("name", company.getName());
			json.put("logo", "http://" + company.getDomain().getName() + ":8080/aon-aio/aonDocuments/company.logo");
		}
		return json;
		
	}
	
}
