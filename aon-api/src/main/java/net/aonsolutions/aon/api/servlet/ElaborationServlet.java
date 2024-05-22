package net.aonsolutions.aon.api.servlet;

import java.util.Date;
import java.util.logging.Logger;

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
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@WebServlet(name = "AonApiElaborationServlet", urlPatterns = {"/ms/api/elaboration/*"})
public class ElaborationServlet extends AonApiHttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER  = Logger.getLogger(ElaborationServlet.class.getName());
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp) {
		put(req, resp);
	}
	
	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		delete(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, getElaborations(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void put(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, saveElaboration(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void delete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, deleteElaboration(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONObject getElaboration(AonApiData api) {
		Elaboration elaboration = AON.getElaboration(api.getDomain(), api.getUser(),
				f -> elaborationFilter(api, f), new Options().setFull(true));
		return ElaborationJSON.toJSON(elaboration);
	}
	
	private Object getElaborations(AonApiData api) {
		if(api.getData().opt(IJsonNames.ID) != null) return getElaboration(api);
		return ElaborationJSON.toJSON(AON.getElaborationList(api.getDomain(), api.getUser().getLogin(), f -> elaborationFilter(api, f), elaborationOptions(api)));
	}
	
	private JSONObject saveElaboration(AonApiData api) {
		Elaboration elaboration = ElaborationJSON.fromJSON(api.getData());
		elaboration = AON.saveElaboration(api.getDomain(), api.getUser(), elaboration);
		return ElaborationJSON.toJSON(elaboration);
	}
	
	private JSONObject deleteElaboration(AonApiData api) {
		Integer id = JsonUtils.getInteger(api.getData(), IJsonNames.ID);
		AON.deleteElaboration(api.getDomain(), api.getUser(), id);
		return new JSONObject();
	}
	
	private Filter elaborationFilter(AonApiData api, ElaborationProperties f) {
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
			filter = filter.and(f.getDateProperty().le(AonDateUtils.toTimestamp(to)));
		}
		
		String value = JsonUtils.getString(api.getData(), IJsonNames.VALUE);
		if(AonStringUtils.isNotBlank(value)) {
			filter = filter.and(f.getDescriptionProperty().like("%" + value + "%"));
		}
		
		Integer warehouse = JsonUtils.getInteger(api.getData(), IJsonNames.WAREHOUSE);
		if(warehouse != null) {
			filter = filter.and(f.getWarehouseProperty().eq(warehouse));
		}
			
		return filter;
	}
	
	private Options elaborationOptions(AonApiData api) {
		Options options = new Options();
		options.setPage(JsonUtils.getInteger(api.getData(), IJsonNames.PAGE));
		options.setPerPage(JsonUtils.getInteger(api.getData(), IJsonNames.PER_PAGE));
		options.setFull(JsonUtils.getboolean(api.getData(), IJsonNames.FULL));
		return options;
	}
}
