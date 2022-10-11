package net.aonsolutions.aon.api.servlet.registry;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.SupplierJSON;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.SupplierProperties;
import com.esferalia.aon.occam.api.model.registry.Supplier;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

/**
 * @deprecated  Replaced by SuppliersServlet
 */
@Deprecated(forRemoval = true )
@SuppressWarnings("serial")
@WebServlet(name = "AonApiSupplierServlet", urlPatterns = {"/ms/api/supplier/*"})
public class SupplierServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(SupplierServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[GET] /ms/api/supplier/* - AON API SUPPLIER SERVLET");
		try {
			AonApiData api = initialize(req);
		
			switch (api.getPath()) {
			case "/":
				response(req, resp, getSuppliers(api, api.getData()));
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
		LOGGER.info("[POST] /ms/api/supplier/* - AON API SUPPLIER SERVLET");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, getSuppliers(api, api.getData()));
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
		LOGGER.info("[PUT] /ms/api/supplier/* - AON API SUPPLIER SERVLET");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, saveSupplier(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
			
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private Object getSupplier(AonApiData api, JSONObject json) {
		Integer id = api.getData().opt(IJsonNames.REGISTRY) != null 
				? api.getData().optInt(IJsonNames.REGISTRY)
				: api.getData().optInt(IJsonNames.ID);
		Supplier supplier = AON.getSupplier(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(id)).get();
		JSONObject object = SupplierJSON.toJSON(supplier);
		
		return RegistryServlet.getRegistryAdditionalInfo(object, api, json, id, null);
	}
	
	private Object getSuppliers(AonApiData api, JSONObject json) {
		if(json.opt(IJsonNames.ID) != null || json.opt(IJsonNames.REGISTRY) != null)
			return getSupplier(api, json);
		
		Integer page = json.opt(IJsonNames.PAGE) != null 
			? json.optInt(IJsonNames.PAGE) : 1;
		Integer perPage = json.opt(IJsonNames.PER_PAGE) != null
			? json.optInt(IJsonNames.PER_PAGE) : 50;

		return SupplierJSON.toJSON(AON.getSupplierStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
			f -> supplierFilter(api, json, f), perPage * (page -1), perPage));
	}
	
	private Filter supplierFilter(AonApiData api, JSONObject json, SupplierProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		
		if(json.opt(IJsonNames.VALUE) != null) {
			String value = json.optString(IJsonNames.VALUE);
			Filter valueFilter = f.getNameProperty().like("%" + value + "%")
					.or(f.getDocumentProperty().like("%" + value + "%"));
			filter = filter.and(valueFilter);
		}
		return filter;
	}
	
	public static JSONObject saveSupplier(AonApiData api) {
		Supplier supplier = SupplierJSON.fromJSON(api.getData());
		supplier = AON.saveSupplier(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), supplier);
		RegistryServlet.saveRegistryAdditionalInfo(api, supplier.getId(), supplier.getDomain().getId());
		return SupplierJSON.toJSON(supplier);
	}
	
}
