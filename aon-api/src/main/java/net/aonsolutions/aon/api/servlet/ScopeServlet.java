package net.aonsolutions.aon.api.servlet;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.security.Scope;

import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiScopeServlet", urlPatterns = {"/ms/api/scope/*"})
public class ScopeServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(ScopeServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API SCOPE SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req, resp);
		
			switch (api.getPath()) {
			case "/":
				response(req, resp, getScopes(api));
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
		
			
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API SCOPE SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
			case "/":
				//response(req, resp, getResponseObject());
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
			
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API SCOPE SERVLET - DELETE METHOD");
		try {
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
			case "/":
				//response(req, resp, getResponseObject());
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
			
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONArray getScopes(AonApiData api) {
		JSONArray array = new JSONArray();
		if(!api.getDomain().isParent() && api.getDomain().isEnableHeredity()) {
			if(api.getUser().getDomain().equals(api.getDomain().getId())) {
				AON.getUserScopeStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), api.getUser().getId(), 
						f -> f.getDomainProperty().eq(api.getDomain().getId()).or(f.getDomainProperty().eq(api.getDomain().getParentId())))
					.forEach(s -> array.put(scopeToJSON(s)));
			} else {
				AON.getScopeStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(api.getDomain().getId()))
					.forEach(s -> array.put(scopeToJSON(s)));
				AON.getUserScopeStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), api.getUser().getId(), f -> f.getDomainProperty().eq(api.getDomain().getParentId()))
					.forEach(s -> array.put(scopeToJSON(s)));
			}
		} else {
			if(api.getUser().getDomain().equals(api.getDomain().getId())) {
				AON.getUserScopeStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), api.getUser().getId(), f -> f.getDomainProperty().eq(api.getDomain().getId()))
				.forEach(s -> array.put(scopeToJSON(s)));
			} else {
				AON.getScopeStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(api.getDomain().getId()))
					.forEach(s -> array.put(scopeToJSON(s)));
			}
		}
		return array;	
	}
	
	public static JSONObject scopeToJSON(Scope scope){	
		return new JSONObject()
			.put("id",scope.getId())
			.put("domain", scope.getDomain())
			.put("name", scope.getDescription());
	}

}
