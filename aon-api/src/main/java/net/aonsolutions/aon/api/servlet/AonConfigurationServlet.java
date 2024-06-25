package net.aonsolutions.aon.api.servlet;

import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.AonConfiguration;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonConfigurationServlet", urlPatterns = {"/ms/api/aon-configuration/*"})
public class AonConfigurationServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(AonConfigurationServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON APLICATION-PARAMETER SERVLET GET");
		try {		
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/menu-options":
				response(req, resp, getMenuOptions(api));
			break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)  {
		LOGGER.info("AON APLICATION-PARAMETER - POST");
		try {		
			AonApiData api = initialize(req);
			switch (api.getPath()) {
				case "/menu-options":
					response(req, resp, getMenuOptions(api));
				break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
	}
	
	private JSONObject getMenuOptions(AonApiData api) {
		JSONObject params = api.getData();
		AonConfiguration config = new AonConfiguration();
		config.getCompany().getId();
		return params;
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON APLICATION-PARAMETER - DELETE METHOD");
		throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
	}
	
	
	
}
