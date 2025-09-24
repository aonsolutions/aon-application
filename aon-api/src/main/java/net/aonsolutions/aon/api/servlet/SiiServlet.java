package net.aonsolutions.aon.api.servlet;

import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.invoice.InvoiceCommunicationConfigurationJSON;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonSiiServlet", urlPatterns = {"/ms/api/sii/*"})
public class SiiServlet extends AonApiHttpServlet{

	
	private static final Logger LOGGER  = Logger.getLogger(SiiServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API SII SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
				case "/configuration":
					response(req, resp, getConfiguration(api));
					break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	private JSONObject getConfiguration(AonApiData api) {
		return InvoiceCommunicationConfigurationJSON.toJSON(
			AON.getInvoiceCommunicationConfiguration(api.getOccam())).orElse(new JSONObject());
	}
}
