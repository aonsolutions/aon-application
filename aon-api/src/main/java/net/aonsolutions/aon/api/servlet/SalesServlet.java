package net.aonsolutions.aon.api.servlet;

import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.SalesJSON;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.SalesProperties;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@WebServlet(name = "AonApiSalesServlet", urlPatterns = {"/ms/api/sales/*"})
public class SalesServlet extends AonApiHttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER  = Logger.getLogger(SalesServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] /ms/api/delivery");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, getSales(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONArray getSales(AonApiData api) {
		return SalesJSON.toJSON(
			AON.getSalesStream(api.getDomain(), api.getUser(), f -> salesFilter(api, f)));
	}
	
	private Filter salesFilter(AonApiData api, SalesProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		return filter;
	}
}
