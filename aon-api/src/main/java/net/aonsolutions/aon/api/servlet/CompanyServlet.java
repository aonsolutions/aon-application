package net.aonsolutions.aon.api.servlet;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

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
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.DomainType;
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
			case "/schemas":
				response(req, resp, getCompaniesBySchemas(api));
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
		if(company.getId() == null) {
			company.setLegalPerson(AonDocumentUtil.isValidCIF(company.getDocument()));
			checkCompany(api, company);
			
			Domain parent = api.getDomain().isParent() ? 
					api.getDomain() : 
					AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f->f.getIdProperty().eq(api.getDomain().getParentId()));
					
			String domainName = company.getDocument() + "-" + parent.getName();
			Domain d = new Domain()
				.setName(domainName.toLowerCase())
				.setDescription(company.getName())
				.setOwner(api.getDomain().getOwner())
				.setParentId(parent.getId())
				.setActive(true)
				.setDomainType(DomainType.ENTERPRISE)
				.setEnableHeredity(true)
				.setDomainManagement(false);
			
			Domain domain = AON_SOLUTIONS.insertDomain(api.getDomain(), api.getUser(), d, company);
			Company c = AON.getCompany(domain.getName(), domain.getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(domain.getId()));
			RegistryServlet.saveRegistryAdditionalInfo(api, c.getId(), domain.getId());
			
			RegistryAddress raddress = AON.getRegistryAddress(domain, new User(), f-> f.getDomainProperty().eq(domain.getId()).and(f.getTypeProperty().eq((byte)0)));
			
			if(raddress!=null && raddress.getId()!=null) {
				
				Workplace workplace = new Workplace()
					.setActive(true)
					.setDescription("PRINCIPAL")
					.setDomain(domain.getId())
					.setEnterprise(c.getId())
					.setAddress(raddress.getId());
				
				AON.saveWorkplace(domain, new User(), workplace);
			}
			
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

	private JSONArray getCompanies(AonApiData api) {
		if((JsonUtils.has(api.getData(), IJsonNames.PARENT) && JsonUtils.getboolean(api.getData(), IJsonNames.PARENT))
				|| JsonUtils.has(api.getData(), IJsonNames.DOCUMENT) || JsonUtils.has(api.getData(), IJsonNames.PARENT_ID)) {
			
			if(!api.getData().isNull(IJsonNames.PAGE)) {
				return CompanyJSON.toJSON(
					AON.getCompanyStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> companyFilter(api, f),
					api.getData().optInt(IJsonNames.PAGE), api.getData().optInt(IJsonNames.PER_PAGE))
				);
			} else {
				return CompanyJSON.toJSON(
					AON.getCompanyStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> companyFilter(api, f))
				);
			}
			
		} else {
			
			JSONArray jsArray = new JSONArray();
			
			List<String> schemas = AONContext.getSchemas();
		
			for(String schema : schemas) {
				LinkedList<Integer> ds = new LinkedList<>();	
				AON_SOLUTIONS.getCompanyStream(api.getToken(), schema, null, null)
				.sorted((o1, o2) -> o1.getCompany().getName().compareTo(o2.getCompany().getName()))
				.forEach(
					ac -> {
					if(!ds.contains(ac.getDomain().getId())){
						jsArray.put(ac.toJSON());
						ds.add(ac.getDomain().getId());
					}
				});
			}
			
			return jsArray;
		}
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
	
	
	private Object getCompaniesBySchemas(AonApiData api) {
		JSONObject params = api.getData();
		
		int page = params.optInt(IJsonNames.PAGE)!=0 ? params.optInt(IJsonNames.PAGE) : 1;
		int perPage = params.optInt(IJsonNames.PER_PAGE)!=0 ? params.optInt(IJsonNames.PER_PAGE) : 20;
		
		JSONArray jsArray = new JSONArray();
		
		AON_SOLUTIONS.getCompanyBySchemaStream(api.getToken(), f-> companyFilter(api, f) , page, perPage)
		.stream()
		.sorted((o1, o2) -> o1.getCompany().getName().compareTo(o2.getCompany().getName()))
		.forEach(ac -> 
			jsArray.put(ac.toJSON())
		);
		
		
		return jsArray;
	}
	
	private Filter companyFilter(AonApiData api, CompanyProperties f) {
		JSONObject params = api.getData();
		Filter filter = f.getIdProperty().isNotNull();

		if(JsonUtils.has(params, IJsonNames.PARENT) && JsonUtils.getboolean(params, IJsonNames.PARENT)) {
			filter = f.getDomainParentProperty().eq(api.getDomain().getId());
		}

		if(JsonUtils.has(params, IJsonNames.DOCUMENT) && AonStringUtils.isNotBlank(JsonUtils.getString(params, IJsonNames.DOCUMENT))) {
			filter =  filter.and(f.getDocumentProperty().eq(JsonUtils.getString(params, IJsonNames.DOCUMENT))) ;
		}
		
		if(!params.isNull(IJsonNames.PARENT_ID)) {
			filter =  filter.and(f.getDomainParentProperty().eq(params.optInt(IJsonNames.PARENT_ID)));
		}
		
		if(!params.isNull(IJsonNames.ACTIVE)) {
			int active = params.optBoolean(IJsonNames.ACTIVE) ? 1 : 0;
			filter = filter.and(f.getActiveProperty().eq((byte)active));
		}
		
		if(!params.isNull(IJsonNames.DOMAIN_ACTIVE)) {
			int active = params.optBoolean(IJsonNames.DOMAIN_ACTIVE) ? 1 : 0;
			filter = filter.and(f.getDomainActiveProperty().eq((byte)active));
		}
		
		if(!params.isNull(IJsonNames.SHARED)) {
			int shared = params.optBoolean(IJsonNames.SHARED) ? 1 : 0;
			filter = filter.and(f.getUserSharedProperty().eq((byte)shared));
		}
		
		if(!params.isNull("domainIds")) {
		    Integer[] domainIds = Stream.of(params.optString("domainIds").split(",")).map(Integer::parseInt).toArray(Integer[]::new); 
			filter = filter.and(f.getDomainProperty().in(domainIds));
		}
		
		if(!params.isNull(IJsonNames.TYPE)) {
			DomainType type = DomainType.safeValueOf(params.optString(IJsonNames.TYPE));
			
			filter = filter.and(f.getDomainTypeProperty().eq(type.value()));
			
			if(type.equals(DomainType.CONSULTANCY)) {
				filter = filter.and(f.getDomainParentProperty().isNull());
			}
		}
		
		if(!params.isNull(IJsonNames.VALUE)) {
			String value = params.optString(IJsonNames.VALUE);
			filter = filter.and(
				f.getNameProperty().like("%" + value + "%")
				.or(f.getDocumentProperty().like("%" + value + "%"))
				.or(f.getAliasProperty().like("%" + value + "%"))
			);
		}
		
		return filter;
	}
	
	private JSONObject getCompany(AonApiData api) {
		Company company =  api.getData().opt(IJsonNames.ID) != null
			? AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
				f.getIdProperty().eq(api.getData().optInt(IJsonNames.ID))) 
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
	
	public static <T> List<T> getPage(List<T> sourceList, int page, int pageSize) {
	    if(pageSize <= 0 || page <= 0) {
	        throw new IllegalArgumentException("invalid page size: " + pageSize);
	    }
	    
	    int fromIndex = (page - 1) * pageSize;
	    if(sourceList == null || sourceList.size() <= fromIndex){
	        return Collections.emptyList();
	    }
	    
	    // toIndex exclusive
	    return sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size()));
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
