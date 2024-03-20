package net.aonsolutions.aon.api.servlet.registry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.CreditorJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.RegistryAddressJSON;
import com.esferalia.aon.occam.api.json.RegistryMediaJSON;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.Properties.CreditorProperties;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiCreditorsServlet", urlPatterns = {"/ms/api/creditors/*"})
public class CreditorsServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(CreditorsServlet.class.getName());
	
	public static final String CREDITORS = "/";
	public static final String CREDITOR = "/:id";
	
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
				.addRoute(CREDITORS, CreditorsServlet::getCreditors)
				.addRoute(CREDITOR, CreditorsServlet::getCreditor)
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
				.addRoute(CREDITORS, CreditorsServlet::saveCreditor)
				.addRoute(CREDITOR, CreditorsServlet::saveCreditor)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	
	private static JSONObject getCreditor(AonApiData api) {
		Integer id = api.getData().opt(IJsonNames.REGISTRY) != null 
				? api.getData().optInt(IJsonNames.REGISTRY)
				: api.getData().optInt(IJsonNames.ID);
		Creditor creditor = AON.getCreditor(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(id)).get();
		JSONObject object = CreditorJSON.toJSON(creditor);
		
		return RegistryServlet.getRegistryAdditionalInfo(object, api, api.getData(), id, null);
	}
	
	private static JSONArray getCreditors(AonApiData api) {
		Integer page = api.getData().opt(IJsonNames.PAGE) != null 
			? api.getData().optInt(IJsonNames.PAGE) : 1;
		Integer perPage = api.getData().opt(IJsonNames.PER_PAGE) != null
			? api.getData().optInt(IJsonNames.PER_PAGE) : 50;
		JSONArray result = CreditorJSON.toJSON(AON.getCreditorStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
			f -> creditorFilter(api, api.getData(), f), perPage * (page -1), perPage));
		 if (api.getData().opt("additional_info") != null) {
		        LinkedList<RegistryAdditionalInfo> rais = new LinkedList<>();			
		        JSONArray additionalInfo = (JSONArray) api.getData().opt("additional_info");
		        for (int i = 0; i < additionalInfo.length(); i++) {
		            rais.add(RegistryAdditionalInfo.safeValueOf(additionalInfo.get(i).toString()));
		        }
		        for (int i = 0; i < result.length(); i++) {
		            JSONObject creditorObj = result.getJSONObject(i);
		            int id = creditorObj.getInt("id");
		            List<RegistryAddress> registryAddresses = new ArrayList<>();
		            List<RegistryMedia> registryMedia = new ArrayList<>();
		            rais.stream().forEach(rai -> {
		                if (RegistryAdditionalInfo.ADDRESSES.equals(rai)) {
		                    RegistryAddressFilter filtro = fil -> fil.getRegistryProperty().eq(id);
		                    Stream<RegistryAddress> addresses = AON.getStream(api.getDomain(), api.getUser(), filtro);
		                    addresses.forEach(registryAddresses::add); 
		                }
		                if (RegistryAdditionalInfo.MEDIA.equals(rai)) {
							RegistryMediaFilter filter  = f -> f.getRegistryProperty().eq(id);
							Stream<RegistryMedia> media = AON.getStream(api.getDomain(), api.getUser(), filter);
							media.forEach(registryMedia::add);
						}
		            });
		            creditorObj.put("registryAddresses", RegistryAddressJSON.toJSON(registryAddresses));
		            creditorObj.put("media", RegistryMediaJSON.toJSON(registryMedia));
		        }
		    }
		return result;
	}
	
	private static Filter creditorFilter(AonApiData api, JSONObject json, CreditorProperties f) {
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
