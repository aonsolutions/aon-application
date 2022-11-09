package net.aonsolutions.aon.api.servlet.registry;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.TargetJSON;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.TargetProperties;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.type.TargetStatus;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiTargetServlet", urlPatterns = {"/ms/api/target/*"})
public class TargetServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(TargetServlet.class.getName());
	
	public static final String TARGETS = "/";
	public static final String TARGET = "/:id";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(TARGETS, TargetServlet::saveTarget)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
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
				.addRoute(TARGETS, TargetServlet::getTargets)
				.addRoute(TARGET, TargetServlet::getTarget)
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
				.addRoute(TARGET, TargetServlet::saveTarget)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONObject getTarget(AonApiData api) {
		Optional<Target> opt = AON.getTarget(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> filter(api, f));
		if(opt.isPresent()) {
			Target target = opt.get();
			JSONObject object = TargetJSON.toJSON(target);
			
			return RegistryServlet.getRegistryAdditionalInfo(object, api, api.getData(), target.getId(), null);
		}
		
		throw new AonApiException(AonApiError.EMPTY_DATA.getMessage());
	}

	private static JSONArray getTargets(AonApiData api) {
//		Integer page = api.getData().opt(IJsonNames.PAGE) != null 
//			? api.getData().optInt(IJsonNames.PAGE) : 1;
//		Integer perPage = api.getData().opt(IJsonNames.PER_PAGE) != null
//			? api.getData().optInt(IJsonNames.PER_PAGE) : 50;

		return TargetJSON.toJSON(AON.getTargetStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
			f -> filter(api, f)));
	}
	
	private static Filter filter(AonApiData api, TargetProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId()) ;
		
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
		
		
		if(api.getData().opt(IJsonNames.STATUS) != null) {
			ArrayList<String> list = new ArrayList<>();
		
			api.getData().optJSONArray(IJsonNames.STATUS)
			.forEach(str -> list.add(str.toString()));
				
			 Byte[] status = TargetStatus.safeValueOf(list)
			.stream()
			.map(TargetStatus::value)
			.toArray(Byte[]::new);
			
			filter = filter.and(f.getStatusProperty().in(status));
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
	
	public static JSONObject saveTarget(AonApiData api) {
		Target target = TargetJSON.fromJSON(api.getData());
		target = AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), target);
		RegistryServlet.saveRegistryAdditionalInfo(api, target.getId(), target.getDomain().getId());
		return TargetJSON.toJSON(target);
	}
}
