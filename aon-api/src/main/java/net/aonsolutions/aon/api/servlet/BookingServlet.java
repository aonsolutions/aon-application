package net.aonsolutions.aon.api.servlet;
import java.util.logging.Logger;
import java.util.stream.Stream;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.CONSOLE;
import com.esferalia.aon.occam.api.json.BookingJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "BookingServlet", urlPatterns = {"/ms/api/booking/*"})
public class BookingServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(BookingServlet.class.getName());
	
	public static final String BOOKING= "/";
	public static final String BOOKING_CUSTOMER = "/customer";
	
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
				.addRoute(BOOKING_CUSTOMER, BookingServlet::getBookingCustomerArr)
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
			return BookingJSON.toJSON(AON.getBooking(domain, api.getUser()), true);			
		}
		return BookingJSON.toJSON(AON.getBooking(api.getDomain(), api.getUser()), true);
	}
	
	private static JSONObject getBookingCustomer(AonApiData api) {
		Integer customer = JsonUtils.getInteger(api.getData(), IJsonNames.CUSTOMER);
		DomainCompany dc = CONSOLE.getDomains(f -> f.getAonCustomerProperty().eq(customer))
				.findFirst().orElse(null);
		if(dc == null) {
			throw new AonApiException("No existe ningún dominio asociado al cliente " + customer);
		}
		return BookingJSON.toJSON(AON.getBooking(dc.getDomain(), api.getUser()));
	}
	
	private static JSONArray getBookingCustomerArr(AonApiData api) {
		JSONArray bookingArr = new JSONArray();
		
		Integer customer = JsonUtils.getInteger(api.getData(), IJsonNames.CUSTOMER);
		
		Stream<DomainCompany> domainCompanies = CONSOLE.getDomains(f -> f.getAonCustomerProperty().eq(customer));
		
		if(domainCompanies == null) throw new AonApiException("No existe ningún dominio asociado al cliente " + customer);
		
		domainCompanies.forEach(domainCompany -> bookingArr.put(BookingJSON.toJSON(AON.getBooking(domainCompany.getDomain(), api.getUser()), true)));
		
		return bookingArr;
	}
	
	
	private static JSONObject putBooking(AonApiData api) {
		return BookingJSON.toJSON(AON.saveBooking(api.getDomain(), api.getUser(),
				BookingJSON.fromJSON(api.getData())));
	}
}
