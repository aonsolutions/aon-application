package net.aonsolutions.aon.api.servlet;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.CONSOLE;
import com.esferalia.aon.occam.api.json.DomainCompanyJSON;
import com.esferalia.aon.occam.api.json.DomainJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.DomainLinked;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "DomainServlet", urlPatterns = {"/ms/api/domain/*"})
public class DomainCompanyServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(DomainCompanyServlet.class.getName());
	
	public static final String DOMAINS = "/";
//	public static final String CUSTOMER_DOMAINS = "/:customer"; //buscar entre todos los schemas los que tengan ese aonCustomer
	public static final String DOMAIN_LINKED = "/link/";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		put(req, resp);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		delete(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(DOMAINS, DomainCompanyServlet::getDomains)
//				.addRoute(CUSTOMER_DOMAINS, DomainCompanyServlet::getCustomerDomains)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void put(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(DOMAINS, DomainCompanyServlet::updateCustomerDomains)
				.addRoute(DOMAIN_LINKED, DomainCompanyServlet::saveDomainLinked)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void delete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
//				.addRoute(CUSTOMER_DOMAINS, DomainCompanyServlet::deleteAction)
				.addRoute(DOMAIN_LINKED, DomainCompanyServlet::deleteDomainLinked)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONObject getAction(AonApiData api) {
		return new JSONObject();
	}
	
	private static JSONArray getDomains(AonApiData api) {
		JSONArray domains = new JSONArray();
		String customerDocument = api.getData().optString(IJsonNames.REGISTRY_DOCUMENT);
		int customerId = api.getData().optInt("customerId");
		if (AonStringUtils.isBlank(customerDocument)) {
			CONSOLE.getAllDomains().map(DomainCompanyJSON::toJSON).forEach(domains::put);
		} else {
			CONSOLE.getDomainsByDocument(customerDocument, customerId).map(DomainCompanyJSON::toJSON).forEach(domains::put);
		}
		return domains;
	}
	
	private static JSONArray getCustomerDomains(AonApiData api) {
		JSONArray domains = new JSONArray();
  		JSONObject vars = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);
  		Integer customerId = vars.getInt(IJsonNames.CUSTOMER);
  		CONSOLE.getCustomerDomains(customerId).map(DomainCompanyJSON::toJSON).forEach(domains::put);
		return domains;
	}
	
	private static JSONArray updateCustomerDomains(AonApiData api) {
		JSONArray domainsJson = api.getData().optJSONArray(IJsonNames.DOMAINS);
		Integer customer = api.getData().optInt(IJsonNames.CUSTOMER) > 0 ? api.getData().optInt(IJsonNames.CUSTOMER) : null;
		List<DomainCompany> domains = DomainCompanyJSON.fromJSON(domainsJson);
		List<Domain> updatedDomain = CONSOLE.updateDomainCustomerData(domains, customer);
		return DomainJSON.toJSON(updatedDomain);
	}
	
	private static JSONObject saveDomainLinked(AonApiData api) {
		Integer customer = api.getData().optInt(IJsonNames.CUSTOMER) > 0 ? api.getData().optInt(IJsonNames.CUSTOMER) : null;
		JSONObject domainJson = api.getData().optJSONObject(IJsonNames.DOMAIN);
		DomainCompany domainCompany = DomainCompanyJSON.fromJSON(domainJson);
		if (customer != null) {
			Domain domain = domainCompany.getDomain();
			String schema = domainCompany.getSchema();
			DomainLinked domainLinked = new DomainLinked()
					.setId(domain.getId())
					.setName(domain.getName())
					.setRegistry(customer)
					.setSchema(schema)
					.setType(domain.getDomainType().getName());
			CONSOLE.saveDomainLink(api.getDomain(), api.getUser(), domainLinked);
		}
		return new JSONObject();
	}
	
	private static JSONObject deleteDomainLinked(AonApiData api) {
		Domain apiDomain = api.getDomain();
		User apiUser = api.getUser();
		Integer customer = api.getData().optInt(IJsonNames.CUSTOMER) > 0 ? api.getData().optInt(IJsonNames.CUSTOMER) : null;
		JSONObject domainJson = api.getData().optJSONObject(IJsonNames.DOMAIN);
		DomainCompany domainCompany = DomainCompanyJSON.fromJSON(domainJson);
		if (customer != null) {
			Domain domain = domainCompany.getDomain();
			List<DomainLinked> domainLinkeds = AON.getDomainLinkedList(apiDomain.getName(), apiDomain.getId(), apiUser.getLogin(), customer);
			domainLinkeds
			.stream()
			.filter(dl -> AonStringUtils.equals(dl.getName(), domain.getName())
					&& AonNumberUtils.equals(dl.getId(), domain.getId()))
			.forEach(dl -> {
				CONSOLE.deleteDomainLink(apiDomain, apiUser, dl);				
			});
			
		}
		return new JSONObject();
	}
	
	private static JSONObject putAction(AonApiData api) {
		return new JSONObject();
	}
	
	private static JSONObject deleteAction(AonApiData api) {
		return new JSONObject();
	}
}
