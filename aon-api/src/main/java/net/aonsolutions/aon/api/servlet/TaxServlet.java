package net.aonsolutions.aon.api.servlet;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.TaxJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiTaxServlet", urlPatterns = {"/ms/api/taxes/*"})
public class TaxServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(TaxServlet.class.getName());
	
	public static final String TAXES = "/";
	public static final String TAX = "/:id";
	public static final String VATS = "/vats";
	public static final String RETENTIONS = "/withholdings";
	
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
				.addRoute(TAXES, TaxServlet::getTaxes)
				.addRoute(VATS, TaxServlet::getVats)
				.addRoute(RETENTIONS, TaxServlet::getWithholdings)
				.addRoute(TAX, TaxServlet::getTax)

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
				.addRoute(TAXES, TaxServlet::saveTax)
				.addRoute(TAX, TaxServlet::saveTax)
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
				.addRoute(TAX, TaxServlet::deleteTax)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONArray getTaxes(AonApiData api) {
		return TaxJSON.toJSON(AON.getTaxStream(api.getOccam(), api.getDomain().getId()));
	}
	
	private static JSONArray getVats(AonApiData api) {
		return TaxJSON.toJSON(AON.getVatStream(api.getOccam(), api.getDomain().getId()));
	}
	
	private static JSONArray getWithholdings(AonApiData api) {
		return TaxJSON.toJSON(AON.getWithholdingStream(api.getOccam(), api.getDomain().getId()));
	}
	
	private static JSONObject getTax(AonApiData api) {
		Integer taxId = JsonUtils.getInteger(api.getData(), IJsonNames.ID);
		return TaxJSON.toJSON(AON.getTax(api.getOccam(), api.getDomain().getId(), taxId).orElse(null));
	}
	
	private static JSONObject saveTax(AonApiData api) {
		// TODO: Implement saveTax
		return new JSONObject();
	}
	
	private static JSONObject deleteTax(AonApiData api) {
		// TODO: Implement deleteTax
		return new JSONObject();
	}
}
