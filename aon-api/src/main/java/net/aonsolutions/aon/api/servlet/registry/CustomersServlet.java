package net.aonsolutions.aon.api.servlet.registry;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.CustomerJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.CustomerProperties;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiCustomersServlet", urlPatterns = {"/ms/api/customers/*"})
public class CustomersServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(CustomersServlet.class.getName());
	
	public static final String CUSTOMERS = "/";
	public static final String CUSTOMER = "/:id";
	public static final String CUSTOMER_EMAILS = "/:id/emails";
	public static final String CUSTOMER_PHONES = "/:id/phones";
	
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
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(CUSTOMERS, CustomersServlet::getCustomers)
				.addRoute(CUSTOMER, CustomersServlet::getCustomer)
				.addRoute(CUSTOMER_EMAILS, RegistriesServlet::getRegistryEmails)
                .addRoute(CUSTOMER_PHONES, RegistriesServlet::getRegistryPhones)
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
				.addRoute(CUSTOMERS, CustomersServlet::saveCustomer)
				.addRoute(CUSTOMER, CustomersServlet::saveCustomer)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONObject getCustomer(AonApiData api) {
		Customer customer = AON.getCustomer(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> customerFilter(api, f));
		JSONObject object = CustomerJSON.toJSON(customer);
		
		return RegistryServlet.getRegistryAdditionalInfo(object, api, api.getData(), customer.getId(), null);
	}
	
	private static JSONArray getCustomers(AonApiData api) {
		Integer page = api.getData().opt(IJsonNames.PAGE) != null 
			? api.getData().optInt(IJsonNames.PAGE) : 1;
		Integer perPage = api.getData().opt(IJsonNames.PER_PAGE) != null
			? api.getData().optInt(IJsonNames.PER_PAGE) : 50;

		return CustomerJSON.toJSON(AON.getCustomerStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
			f -> customerFilter(api, f), perPage * (page -1), perPage));
	}
	
	private static Filter customerFilter(AonApiData api, CustomerProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId()) ;
				//.and(f.getStatusProperty().eq(RegistryStatus.ACTIVE.value()));
		
		if(api.getData().opt(IJsonNames.REGISTRY) != null) {
			filter = filter.and(f.getIdProperty().eq(JsonUtils.getInteger(api.getData(), IJsonNames.REGISTRY)));
		} else if(api.getData().opt(IJsonNames.ID) != null) {
			filter = filter.and(f.getIdProperty().eq(JsonUtils.getInteger(api.getData(), IJsonNames.ID)));
		}
		
		if(api.getData().opt(IJsonNames.DOCUMENT) != null) {
			filter = filter.and(f.getDocumentProperty().eq(JsonUtils.getString(api.getData(), IJsonNames.DOCUMENT)));
		}
		
		if(api.getData().opt(IJsonNames.SCOPE) != null) {
			filter = filter.and(f.getScopeProperty().eq(JsonUtils.getInt(api.getData(), IJsonNames.SCOPE)));
		}
		
		int projectType = api.getData().optInt("projectType");
		if(projectType!=0) {
			filter = filter.and(f.getProjectTypeProperty().eq(projectType));
		}
		
		if(api.getData().opt(IJsonNames.STATUS) != null) {
			ArrayList<String> list = new ArrayList<>();
			
			api.getData().optJSONArray(IJsonNames.STATUS)
			.forEach(str -> list.add(str.toString()));
				
			 Byte[] status = RegistryStatus.safeValueOf(list)
				.stream()
				.map(RegistryStatus::value)
				.toArray(Byte[]::new)
			;
			
			filter = filter.and(f.getStatusProperty().in(status));
		}
		
		if(api.getData().opt("rrelationship") != null) {
			boolean rrelationship = api.getData().optBoolean("rrelationship");
			filter = filter.and(
				rrelationship ? 
				f.getRegistryRelationProperty().isNotNull() : 
				f.getRegistryRelationProperty().isNull()
			);
		}
		
		if(api.getData().opt(IJsonNames.VALUE) != null) {
			String value = api.getData().optString(IJsonNames.VALUE);
			Filter valueFilter = f.getNameProperty().like("%" + value + "%")
					.or(f.getDocumentProperty().like("%" + value + "%"))
					.or(f.getAliasProperty().like("%" + value + "%"));
			filter = filter.and(valueFilter);
		}
		return filter;
	}
	
	public static JSONObject saveCustomer(AonApiData api) {
		Customer customer = CustomerJSON.fromJSON(api.getData());
		customer = AON.saveCustomer(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), customer);
		RegistryServlet.saveRegistryAdditionalInfo(api, customer.getId(), customer.getDomain().getId());
		return CustomerJSON.toJSON(customer);
	}
	
}
