package net.aonsolutions.aon.api.servlet;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.json.DomainUserRolesJSON;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;

import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "DomainUserRolesServlet", urlPatterns = {"/ms/api/domainUserRoles/*"})
public class DomainUserRolesServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(DomainUserRolesServlet.class.getName());
	
	public static final String ROOT = "/";
		
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(ROOT, DomainUserRolesServlet::getAction)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONObject getAction(AonApiData api) {
		DomainUserRoles dur = SECURITY.getDomainUserRoles(api.getDomain(), api.getUser().getLogin(), api.getUser().getId());
		return DomainUserRolesJSON.toJSON(dur);
	}
}
