package net.aonsolutions.aon.api.servlet;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.ScopeJSON;

import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiScopeServlet", urlPatterns = {"/ms/api/scopes/*"})
public class ScopeServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(ScopeServlet.class.getName());
	
	public static final String SCOPES = "/";
	public static final String SCOPE = "/:id";
	
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
				.addRoute(SCOPES, ScopeServlet::getScopes)
//				.addRoute(SCOPE, ScopeServlet::getScope)
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
//				.addRoute(SCOPES, ScopeServlet::saveScope)
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
//				.addRoute(SCOPE, ScopeServlet::deleteSupplier)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONArray getScopes(AonApiData api) {
		if(!api.getDomain().isParent() && api.getDomain().isEnableHeredity()) {
			if(api.getUser().getDomain().equals(api.getDomain().getId())) {
				return ScopeJSON.toJSON(AON.getUserScopeStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), api.getUser().getId(), 
						f -> f.getDomainProperty().eq(api.getDomain().getId()).or(f.getDomainProperty().eq(api.getDomain().getParentId()))));
			} else {
				JSONArray array = new JSONArray();
				AON.getScopeStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(api.getDomain().getId()))
					.forEach(s -> array.put(ScopeJSON.toJSON(s)));
				AON.getUserScopeStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), api.getUser().getId(), f -> f.getDomainProperty().eq(api.getDomain().getParentId()))
					.forEach(s -> array.put(ScopeJSON.toJSON(s)));
				return array;
			}
		} else {
			if(api.getUser().getDomain().equals(api.getDomain().getId())) {
				return ScopeJSON.toJSON(AON.getUserScopeStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), api.getUser().getId(), f -> f.getDomainProperty().eq(api.getDomain().getId())));
			} else {
				return ScopeJSON.toJSON(AON.getScopeStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(api.getDomain().getId())));
			}
		}
	}
}
