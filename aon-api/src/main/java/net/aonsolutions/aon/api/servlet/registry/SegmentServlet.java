package net.aonsolutions.aon.api.servlet.registry;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.SegmentJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.Segment;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiSegmentServlet", urlPatterns = {"/ms/api/segments/*"})
public class SegmentServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(SegmentServlet.class.getName());
	
	public static final String SEGMENTS = "/";
	public static final String SEGMENT = "/:id";
	
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
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		delete(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(SEGMENTS, SegmentServlet::getSegments)
				.addRoute(SEGMENT, SegmentServlet::getSegment)
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
				.addRoute(SEGMENTS, SegmentServlet::saveSegment)
				.addRoute(SEGMENT, SegmentServlet::saveSegment)
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
				.addRoute(SEGMENT, SegmentServlet::deleteSegment)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONArray getSegments(AonApiData api) {
		return SegmentJSON.toJSON( 
			AON.getSegmentStream(api.getDomain(), api.getUser(), f-> f.getDomainProperty().eq(api.getDomain().getId())) 
		);
	}
	
	private static JSONObject getSegment(AonApiData api) {
		JSONObject json = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);	
		Integer id = JsonUtils.getInteger(json, IJsonNames.ID);
		return SegmentJSON.toJSON(
			AON.getSegmentStream(api.getDomain(), api.getUser(), f -> f.getIdProperty().eq(id))
			.findFirst().orElse(new Segment())
		);
	}
	
	public static JSONObject saveSegment(AonApiData api) {
		// TODO 
		// return SegmentJSON.toJSON(AON.saveSegment(api.getDomain(), api.getUser(),
		//		SegmentJSON.fromJSON(api.getData())));
		return new JSONObject();
	}

	private static JSONObject deleteSegment(AonApiData api) {
//		JSONObject json = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);	
//		Integer id = JsonUtils.getInteger(json, IJsonNames.ID);
		// TODO AON.deleteSegment(api.getDomain(), api.getUser(), id);
		return new JSONObject();
	}
}
