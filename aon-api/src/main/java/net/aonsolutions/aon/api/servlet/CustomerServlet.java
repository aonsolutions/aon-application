package net.aonsolutions.aon.api.servlet;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.RegistryJSON;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiCustomeServlet", urlPatterns = {"/ms/api/customer/*"})
public class CustomerServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(CustomerServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API CUSTOMER SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req, resp);
		
			switch (api.getPath()) {
			case "/":
				response(req, resp, getCustomers(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}			
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API CUSTOMER SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
			case "/":
				response(req, resp, getResponseObject());
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
			
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONArray getCustomers(AonApiData api) {
		JSONArray array = new JSONArray();
		AON.getCustomerStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
			f -> f.getDomainProperty().eq(api.getDomain().getId()))
		.forEach(c -> array.put(RegistryJSON.toJSON(c)));
		return array;
	}
	
	private JSONObject getResponseObject() {
		return new JSONObject();
	}
}
