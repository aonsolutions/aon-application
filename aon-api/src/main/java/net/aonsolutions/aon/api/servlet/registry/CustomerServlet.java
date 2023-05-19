package net.aonsolutions.aon.api.servlet.registry;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.CustomerJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.CustomerProperties;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

/**
 * @deprecated  Replaced by CustomersServlet
 */
@Deprecated(forRemoval = true )
@SuppressWarnings("serial")
@WebServlet(name = "AonApiCustomeServlet", urlPatterns = {"/ms/api/customer/*"})
public class CustomerServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(CustomerServlet.class.getName());
	
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
		LOGGER.info("[PUT] /ms/api/customer/* - AON API CUSTOMER SERVLET");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, saveCustomer(api));
				break;
			case "/billable":
			case "/billable/":
				response(req, resp, updateCustomerBillable(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
			
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, getCustomers(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private Object getCustomer(AonApiData api) {
		Customer customer = AON.getCustomer(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> customerFilter(api, f));
		JSONObject object = CustomerJSON.toJSON(customer);
		
		return RegistryServlet.getRegistryAdditionalInfo(object, api, api.getData(), customer.getId(), null);
	}
	
	private Object getCustomers(AonApiData api) {
		if(api.getData().opt(IJsonNames.ID) != null || api.getData().opt(IJsonNames.REGISTRY) != null 
				|| api.getData().opt(IJsonNames.DOCUMENT) != null)
			return getCustomer(api);
		
		Integer page = api.getData().opt(IJsonNames.PAGE) != null 
			? api.getData().optInt(IJsonNames.PAGE) : 1;
		Integer perPage = api.getData().opt(IJsonNames.PER_PAGE) != null
			? api.getData().optInt(IJsonNames.PER_PAGE) : 50;
		if (api.getData().opt("additional_info") != null) {			
			Stream<Customer> customers = AON.getCustomerStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
					f -> customerFilter(api, f), perPage * (page -1), perPage);
			
			return RegistryServlet.getRegistryAdditionalInfo(api, customers);
		}
		
		return CustomerJSON.toJSON(AON.getCustomerStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
			f -> customerFilter(api, f), perPage * (page -1), perPage));
	}
	
	private Filter customerFilter(AonApiData api, CustomerProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId()) ;
				//.and(f.getStatusProperty().eq(RegistryStatus.ACTIVE.value()));
		
		if (api.getData().opt(IJsonNames.LINKED) != null) {
			Integer[] linkedCustomerRegistries = AON.getRegistryAddInfoStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					p -> p.getAttributeProperty().like("AON_DOMAIN%").and(p.getRegistryProperty().isNotNull()))
			.filter(Objects::nonNull)
			.map(RegistryAddInfo::getRegistry)
			.distinct()
			.toArray(Integer[]::new);
			
			if (api.getData().optBoolean(IJsonNames.LINKED)) {
				filter = filter.and(f.getRegistryProperty().in(linkedCustomerRegistries));
			} else {				
				filter = filter.and(f.getRegistryProperty().notIn(linkedCustomerRegistries));
			}
		}
		if (api.getData().opt(IJsonNames.BILLABLE) != null) {
			Integer[] notBillables = AON.getRegistryAddInfoStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					p -> p.getAttributeProperty().eq("AON_BILLABLE").and(p.getRegistryProperty().isNotNull()))
					.filter(Objects::nonNull)
					.map(RegistryAddInfo::getRegistry)
					.distinct()
					.toArray(Integer[]::new);
			
			if (api.getData().optBoolean(IJsonNames.BILLABLE)) {
				filter = filter.and(f.getRegistryProperty().notIn(notBillables));
			} else {				
				filter = filter.and(f.getRegistryProperty().in(notBillables));
			}
		}
		
		if(api.getData().opt(IJsonNames.REGISTRY) != null) {
			filter = filter.and(f.getIdProperty().eq(JsonUtils.getInteger(api.getData(), IJsonNames.REGISTRY)));
		} else if(api.getData().opt(IJsonNames.ID) != null) {
			filter = filter.and(f.getIdProperty().eq(JsonUtils.getInteger(api.getData(), IJsonNames.ID)));
		}
		
		if(api.getData().opt(IJsonNames.DOCUMENT) != null) {
			filter = filter.and(f.getDocumentProperty().eq(JsonUtils.getString(api.getData(), IJsonNames.DOCUMENT)));
		}
		
		if(api.getData().opt(IJsonNames.VALUE) != null) {
			String value = api.getData().optString(IJsonNames.VALUE);
			Filter valueFilter = f.getNameProperty().like("%" + value + "%")
					.or(f.getDocumentProperty().like("%" + value + "%"))
					.or(f.getAliasProperty().like("%" + value + "%"));
			filter = filter.and(valueFilter);
		}
		
		if (api.getData().opt(IJsonNames.STATUS) != null) {
			RegistryStatus status = RegistryStatus.safeValueOf(api.getData().optString(IJsonNames.STATUS));
			filter = filter.and(f.getStatusProperty().eq(AonEnumUtils.getByte(status)));
		}
		
		return filter;
	}
	
	private JSONObject updateCustomerBillable(AonApiData api) {
		JSONObject data = api.getData();
		if (data.opt(IJsonNames.BILLABLE) != null && data.opt(IJsonNames.CUSTOMER) != null) {
			JSONObject customerJson = data.optJSONObject(IJsonNames.CUSTOMER);
			Customer customer = CustomerJSON.fromJSON(customerJson);
			Integer customerId = customer.getId();
			if (AonNumberUtils.zeroIfNull(customerId) > 0) {
				//Primero borrar el que exista
				AON.getRegistryAddInfoStream(
						api.getDomain().getName(),
						api.getDomain().getId(),
						api.getUser().getLogin(),
						f -> f.getAttributeProperty().eq("AON_BILLABLE").and(f.getRegistryProperty().eq(customerId))
				)
				.filter(Objects::nonNull)
				.forEach(rai -> {
					if (AonNumberUtils.zeroIfNull(rai.getId()) > 0) {
						AON.deleteRegistryAddInfo(api.getDomain(), api.getUser().getLogin(), rai.getId());
					}
				});
				
				if (!data.optBoolean(IJsonNames.BILLABLE)) {
					RegistryAddInfo addInfo = new RegistryAddInfo()
							.setRegistry(customerId)
							.setDomain(customer.getDomain().getId())
							.setAttribute("AON_BILLABLE")
							.setValue("NOT_BILLABLE")
							.setDate(new Date());
					AON.insertRegistryAddInfo(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), addInfo);
				}
			}
			
			api.getData().put("additional_info", IJsonNames.BILLABLE);
			
			return RegistryServlet.getRegistryAdditionalInfo(CustomerJSON.toJSON(customer), api, api.getData(), customerId, null);
		}
		return new JSONObject();
	}
	
	public static JSONObject saveCustomer(AonApiData api) {
		Customer customer = CustomerJSON.fromJSON(api.getData());
		customer = AON.saveCustomer(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), customer);
		RegistryServlet.saveRegistryAdditionalInfo(api, customer.getId(), customer.getDomain().getId());
		return CustomerJSON.toJSON(customer);
	}
	
}
