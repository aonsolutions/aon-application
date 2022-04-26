package net.aonsolutions.aon.api.servlet.registry;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.CustomerJSON;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.CustomerProperties;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiCustomeServlet", urlPatterns = {"/ms/api/customer/*"})
public class CustomerServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(CustomerServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[GET] /ms/api/customer/* - AON API CUSTOMER SERVLET");
		try {
			AonApiData api = initialize(req);
		
			switch (api.getPath()) {
			case "/":
				response(req, resp, getCustomers(api, api.getData()));
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
		LOGGER.info("[POST] /ms/api/customer/* - AON API CUSTOMER SERVLET");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, getCustomers(api, api.getData()));
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
		LOGGER.info("[PUT] /ms/api/customer/* - AON API CUSTOMER SERVLET");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, saveCustomer(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
			
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private Object getCustomer(AonApiData api, JSONObject json) {
		Integer id = api.getData().opt(IJsonNames.REGISTRY) != null 
				? api.getData().optInt(IJsonNames.REGISTRY)
				: api.getData().optInt(IJsonNames.ID);
		Customer customer = AON.getCustomer(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(id));
		JSONObject object = CustomerJSON.toJSON(customer);
		
		return RegistryServlet.getRegistryAdditionalInfo(object, api, json, id, null);
	}
	
	private Object getCustomers(AonApiData api, JSONObject json) {
		if(json.opt(IJsonNames.ID) != null || json.opt(IJsonNames.REGISTRY) != null)
			return getCustomer(api, json);
		
		Integer page = json.opt(IJsonNames.PAGE) != null 
			? json.optInt(IJsonNames.PAGE) : 1;
		Integer perPage = json.opt(IJsonNames.PER_PAGE) != null
			? json.optInt(IJsonNames.PER_PAGE) : 50;

		return CustomerJSON.toJSON(AON.getCustomerStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
			f -> customerFilter(api, json, f), perPage * (page -1), perPage));
	}
	
	private Filter customerFilter(AonApiData api, JSONObject json, CustomerProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		
		if(json.opt(IJsonNames.VALUE) != null) {
			String value = json.optString(IJsonNames.VALUE);
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
