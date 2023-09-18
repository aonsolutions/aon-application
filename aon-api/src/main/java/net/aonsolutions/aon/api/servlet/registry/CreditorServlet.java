package net.aonsolutions.aon.api.servlet.registry;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.CreditorJSON;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.CreditorProperties;
import com.esferalia.aon.occam.api.model.registry.Creditor;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

/**
 * @deprecated  Replaced by CreditorsServlet
 */
@Deprecated(forRemoval = true )
@SuppressWarnings("serial")
@WebServlet(name = "AonApiCreditorServlet", urlPatterns = {"/ms/api/creditor/*"})
public class CreditorServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(CreditorServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[GET] /ms/api/creditor/* - AON API CREDITOR SERVLET");
		try {
			AonApiData api = initialize(req);
		
			switch (api.getPath()) {
			case "/":
				response(req, resp, getCreditors(api, api.getData()));
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
		LOGGER.info("[POST] /ms/api/creditor/* - AON API CREDITOR SERVLET");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, getCreditors(api, api.getData()));
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
		LOGGER.info("[PUT] /ms/api/creditor/* - AON API CREDITOR SERVLET");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, saveCreditor(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
			
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private Object getCreditor(AonApiData api, JSONObject json) {
		Integer id = api.getData().opt(IJsonNames.REGISTRY) != null 
				? api.getData().optInt(IJsonNames.REGISTRY)
				: api.getData().optInt(IJsonNames.ID);
		Creditor creditor = AON.getCreditor(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(id)).get();
		JSONObject object = CreditorJSON.toJSON(creditor);
		
		return RegistryServlet.getRegistryAdditionalInfo(object, api, json, id, null);
	}
	
	private Object getCreditors(AonApiData api, JSONObject json) {
		if(json.opt(IJsonNames.ID) != null || json.opt(IJsonNames.REGISTRY) != null)
			return getCreditor(api, json);
		
		Integer page = json.opt(IJsonNames.PAGE) != null 
			? json.optInt(IJsonNames.PAGE) : 1;
		Integer perPage = json.opt(IJsonNames.PER_PAGE) != null
			? json.optInt(IJsonNames.PER_PAGE) : 50;

		return CreditorJSON.toJSON(AON.getCreditorStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
			f -> creditorFilter(api, json, f), perPage * (page -1), perPage));
	}
	
	private Filter creditorFilter(AonApiData api, JSONObject json, CreditorProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		
		if(json.opt(IJsonNames.VALUE) != null) {
			String value = json.optString(IJsonNames.VALUE);
			Filter valueFilter = f.getNameProperty().like("%" + value + "%")
					.or(f.getDocumentProperty().like("%" + value + "%"));
			filter = filter.and(valueFilter);
		}
		return filter;
	}
	
	public static JSONObject saveCreditor(AonApiData api) {
		Creditor creditor = CreditorJSON.fromJSON(api.getData());
		creditor = AON.saveCreditor(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), creditor);
		RegistryServlet.saveRegistryAdditionalInfo(api, creditor.getId(), creditor.getDomain().getId());
		return  CreditorJSON.toJSON(creditor);
	}
	
}
