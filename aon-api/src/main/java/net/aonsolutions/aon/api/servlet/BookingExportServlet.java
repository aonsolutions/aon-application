package net.aonsolutions.aon.api.servlet;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.BookingJSON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "BookingExportServlet", urlPatterns = {"/ms/api/booking_export/*"})
public class BookingExportServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(BookingExportServlet.class.getName());
	
	public static final String BOOKING= "/";
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			JSONObject bookingJson = getBooking(api);
			responseFile(resp, "booking", bookingJson.toString().getBytes(), MimeType.TXT);
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
}
