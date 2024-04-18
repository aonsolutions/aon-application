package net.aonsolutions.aon.api.servlet.registry;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.CarrierJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Properties.CarrierProperties;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.type.MediaType;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiCarriersServlet", urlPatterns = {"/ms/api/carriers/*"})
public class CarriersServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(CarriersServlet.class.getName());
	
	public static final String CARRIERS = "/";
	public static final String CARRIER = "/:id";
	public static final String CARRIER_EMAILS = "/:id/emails";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		put(req, resp);
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
				.addRoute(CARRIERS, CarriersServlet::getCarriers)
				.addRoute(CARRIER, CarriersServlet::getCarrier)
				.addRoute(CARRIER_EMAILS, CarriersServlet::getCarrierEmails)
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
				.addRoute(CARRIERS, CarriersServlet::saveCarrier)
				.addRoute(CARRIER, CarriersServlet::saveCarrier)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONObject getCarrier(AonApiData api) {
		Carrier carrier = AON.getCarrier(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> carrierFilter(api, f));
		JSONObject object = CarrierJSON.toJSON(carrier);
		
		return RegistryServlet.getRegistryAdditionalInfo(object, api, api.getData(), carrier.getId(), null);
	}
	
	private static JSONArray getCarrierEmails(AonApiData api) {
		JSONObject vars = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);
		Integer carrierId = JsonUtils.getInteger(vars, IJsonNames.ID);
		ArrayList<String> list =AON.getRegistryMediaStream(api.getDomain(), api.getUser(), f -> 
			f.getDomainProperty().eq(api.getDomain().getId())
			.and(f.getRegistryProperty().eq(carrierId))
			.and(f.getMediaProperty().eq(MediaType.EMAIL.value())))
		.map(r -> r.getValue()).collect(Collectors.toCollection(ArrayList::new));
		return new JSONArray(list);
	}
	
	private static JSONArray getCarriers(AonApiData api) {
		Integer page = api.getData().opt(IJsonNames.PAGE) != null 
			? api.getData().optInt(IJsonNames.PAGE) : 1;
		Integer perPage = api.getData().opt(IJsonNames.PER_PAGE) != null
			? api.getData().optInt(IJsonNames.PER_PAGE) : 50;
		Options options = new Options().setPage(page).setPerPage(perPage);
		return CarrierJSON.toJSON(AON.getCarrierStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
			f -> carrierFilter(api, f), options));
	}

	private static Filter carrierFilter(AonApiData api, CarrierProperties f) {
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
		
		
		if(api.getData().opt(IJsonNames.VALUE) != null) {
			String value = api.getData().optString(IJsonNames.VALUE);
			Filter valueFilter = f.getNameProperty().like("%" + value + "%")
					.or(f.getDocumentProperty().like("%" + value + "%"))
					.or(f.getAliasProperty().like("%" + value + "%"));
			filter = filter.and(valueFilter);
		}
		return filter;
	}
	
	public static JSONObject saveCarrier(AonApiData api) {
		Carrier carrier = CarrierJSON.fromJSON(api.getData());
		carrier = AON.saveCarrier(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), carrier);
		RegistryServlet.saveRegistryAdditionalInfo(api, carrier.getId(), carrier.getDomain().getId());
		return CarrierJSON.toJSON(carrier);
	}
	
}
