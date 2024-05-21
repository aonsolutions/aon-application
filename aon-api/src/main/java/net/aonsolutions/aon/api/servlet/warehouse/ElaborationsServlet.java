package net.aonsolutions.aon.api.servlet.warehouse;
import java.util.Date;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.ElaborationJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationProperties;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.type.SalesStatus;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;

@SuppressWarnings("serial")
@WebServlet(name = "ElaborationsServlet", urlPatterns = {"/ms/api/elaborations/*"})
public class ElaborationsServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(ElaborationsServlet.class.getName());
	
	public static final String ELABORATIONS = "/";
	public static final String ELABORATION = "/:id";
	public static final String DETAILS = "/:id/details";
	public static final String DETAIL = "/:id/details/:detail";
	public static final String PACKAGES = "/:id/packages";
	public static final String PACKAGE = "/packages/:package";
	
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

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		delete(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(ELABORATIONS, ElaborationsServlet::getElaborations)
				.addRoute(ELABORATION, ElaborationsServlet::getElaboration)
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
				.addRoute(ELABORATIONS, ElaborationsServlet::saveElaboration)
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
				.addRoute(ELABORATIONS, ElaborationsServlet::deleteElaboration)
				.addRoute(PACKAGE, ElaborationsServlet::deleteElaborationPackage)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONObject getElaboration(AonApiData api) {
		Elaboration elaboration = AON.getElaboration(api.getDomain(), api.getUser(),
				f -> elaborationFilter(api, f), new Options().setFull(true));
		return ElaborationJSON.toJSON(elaboration);
	}
	
	private static JSONArray getElaborations(AonApiData api) {
		return ElaborationJSON.toJSON(AON.getElaborationList(api.getDomain(), api.getUser().getLogin(), f -> elaborationFilter(api, f), elaborationOptions(api)));
	}
	private static JSONObject saveElaboration(AonApiData api) {
		Elaboration elaboration = ElaborationJSON.fromJSON(api.getData());
		elaboration = AON.saveElaboration(api.getDomain(), api.getUser(), elaboration);
		return ElaborationJSON.toJSON(elaboration);
	}
	
	private static JSONObject deleteElaboration(AonApiData api) {
		Integer id = JsonUtils.getInteger(api.getData(), IJsonNames.ID);
		AON.deleteElaboration(api.getDomain(), api.getUser(), id);
		return new JSONObject();
	}
	
	private static JSONObject deleteElaborationPackage(AonApiData api) {
		Integer id = JsonUtils.getInteger(api.getData(), IJsonNames.ID);
		AON.deleteElaborationPackage(api.getDomain(), api.getUser(), id);
		return new JSONObject();
	}
	
	private static Filter elaborationFilter(AonApiData api, ElaborationProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		
		Integer id = JsonUtils.getInteger(api.getData(), IJsonNames.ID);
		if(id != null) {
			filter = filter.and(f.getIdProperty().eq(id));
		}
		
		String series = JsonUtils.getString(api.getData(), IJsonNames.SERIES);
		if(!AonStringUtils.isBlank(series)) {
			filter = filter.and(f.getSeriesProperty().eq(series));
		}
		
		Integer number = JsonUtils.getInteger(api.getData(), IJsonNames.NUMBER);
		if(number != null) {
			filter = filter.and(f.getNumberProperty().eq(number));
		}
		
		SalesStatus status = SalesStatus.safeValueOf(JsonUtils.getString(api.getData(), IJsonNames.STATUS));
		if(status != null) {
			filter = filter.and(f.getStatusProperty().eq(status.value()));
		}
	 	
		Date from = JsonUtils.getDate(api.getData(), IJsonNames.FROM);
		if(from != null) {
			filter = filter.and(f.getDateProperty().ge(AonDateUtils.toTimestamp(from)));
		}
		
		Date to = JsonUtils.getDate(api.getData(), IJsonNames.TO);
		if(to != null) {
			filter = filter.and(f.getDateProperty().ge(AonDateUtils.toTimestamp(to)));
		}
			
		return filter;
	}
	
	private static Options elaborationOptions(AonApiData api) {
		Options options = new Options();
		options.setPage(JsonUtils.getInteger(api.getData(), IJsonNames.PAGE));
		options.setPerPage(JsonUtils.getInteger(api.getData(), IJsonNames.PER_PAGE));
		options.setFull(JsonUtils.getboolean(api.getData(), IJsonNames.FULL));
		return options;
	}
}
