package net.aonsolutions.aon.api.servlet;

import java.util.LinkedList;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.PayMethodJSON;
import com.esferalia.aon.occam.api.model.finance.PayMethod;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonPaymethodServlet", urlPatterns = {"/ms/api/paymethod/*"})
public class PaymethodServlet extends AonApiHttpServlet{
	
	private static final Logger LOGGER  = Logger.getLogger(InvoiceServlet.class.getName());

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API INVOICE SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
			case "/":
				response(req, resp, getPaymethods(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API INVOICE SERVLET - POST METHOD");
	}
	
	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API INVOICE SERVLET - PUT METHOD");
	}
	
	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API INVOICE SERVLET - DELETE METHOD");
	}
		
	private JSONArray getPaymethods(AonApiData api) {
		LinkedList<PayMethod> list = AON.getPayMethods(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		return PayMethodJSON.toJSON(list);
	}

}
