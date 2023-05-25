package net.aonsolutions.aon.api.servlet;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.invoice.TbaiConfigurationJSON;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonTbaiServlet", urlPatterns = {"/ms/api/tbai/*"})
public class TbaiServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(TbaiServlet.class.getName());
		
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API SII SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
				case "/configuration":
					response(req, resp, getTbaiConfiguration(api));
					break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	private JSONObject getTbaiConfiguration(AonApiData api) {
		return TbaiConfigurationJSON.toJSON(AON.getTbaiConfiguration(api.getDomain(), api.getUser()));
	}
}
