package net.aonsolutions.aon.api.servlet;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.BookingJSON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "BookingServlet", urlPatterns = {"/ms/api/booking/*"})
public class BookingServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(BookingServlet.class.getName());
	
	public static final String BOOKING= "/";
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp) {
		put(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(BOOKING, BookingServlet::getBooking)
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
				.addRoute(BOOKING, BookingServlet::putBooking)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONObject getBooking(AonApiData api) {
		String domainName = api.getData().optString(IJsonNames.DOMAIN_NAME);
		Integer domainId = api.getData().optInt(IJsonNames.DOMAIN_ID);
		if (AonStringUtils.isNotBlank(domainName) && AonNumberUtils.zeroIfNull(domainId) > 0) {
			Domain domain = AON.getDomain(domainName, domainId, api.getUser().getLogin());
			return BookingJSON.toJSON(AON.getBooking(domain, api.getUser()));			
		}
		return BookingJSON.toJSON(AON.getBooking(api.getDomain(), api.getUser()));
	}
	
	private static JSONObject putBooking(AonApiData api) {
		return BookingJSON.toJSON(AON.saveBooking(api.getDomain(), api.getUser(),
				BookingJSON.fromJSON(api.getData())));
	}
}
