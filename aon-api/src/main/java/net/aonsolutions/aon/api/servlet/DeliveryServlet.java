package net.aonsolutions.aon.api.servlet;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.DeliveryJSON;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.DeliveryProperties;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiDeliveryServlet", urlPatterns = {"/ms/api/delivery/*"})
public class DeliveryServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(DeliveryServlet.class.getName());
	
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
				response(req, resp, getDeliveries(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONArray getDeliveries(AonApiData api) {
		return DeliveryJSON.toJSON(
			AON.getDeliveryStream(api.getDomain(), api.getUser(), f -> deliveryFilter(api, f)));
	}
	
	private Filter deliveryFilter(AonApiData api, DeliveryProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		// TODO
		return filter;
	}
}
