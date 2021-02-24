package net.aonsolutions.aon.api.servlet;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.security.Scope;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiScopeServlet", urlPatterns = {"/ms/api/scope/*"})
public class ScopeServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(ScopeServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API SCOPE SERVLET - GET METHOD");
		try {
			super.doGet(req, resp);
		
			switch (getPath()) {
			case "/":
				response(req, resp, getScopes());
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
			super.doPost(req, resp);
			switch (getPath()) {
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
			super.doDelete(req, resp);
			switch (getPath()) {
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
	
	private JSONArray getScopes() {
		JSONArray array = new JSONArray();
		if(!getDomain().isParent() && getDomain().isEnableHeredity()) {
			if(getUser().getDomain().equals(getDomain().getId())) {
				AON.getUserScopeStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(), getUser().getId(), 
						f -> f.getDomainProperty().eq(getDomain().getId()).or(f.getDomainProperty().eq(getDomain().getParentId())))
					.forEach(s -> array.put(scopeToJSON(s)));
			} else {
				AON.getScopeStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> f.getDomainProperty().eq(getDomain().getId()))
					.forEach(s -> array.put(scopeToJSON(s)));
				AON.getUserScopeStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(), getUser().getId(), f -> f.getDomainProperty().eq(getDomain().getParentId()))
					.forEach(s -> array.put(scopeToJSON(s)));
			}
		} else {
			if(getUser().getDomain().equals(getDomain().getId())) {
				AON.getUserScopeStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(), getUser().getId(), f -> f.getDomainProperty().eq(getDomain().getId()))
				.forEach(s -> array.put(scopeToJSON(s)));
			} else {
				AON.getScopeStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> f.getDomainProperty().eq(getDomain().getId()))
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
