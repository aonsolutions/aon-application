package net.aonsolutions.aon.api.servlet;
import java.util.Date;
import java.util.logging.Logger;
import java.util.stream.Stream;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonInvoiceClosingServlet", urlPatterns = {"/ms/api/invoiceClosing/*"})
public class InvoiceClosingServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(InvoiceClosingServlet.class.getName());
	
	public static final String CLOSING = "/";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		put(req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		put(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(CLOSING, InvoiceClosingServlet::getAction)
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
				.addRoute(CLOSING, InvoiceClosingServlet::putAction)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONArray getAction(AonApiData api) {
		JSONArray array = new JSONArray();
		AON.getInvoiceClosing(api.getDomain(), api.getUser()).forEach(r -> {
			array.put(new JSONObject()
					.put(IJsonNames.ID, r.getId())
					.put(IJsonNames.DESCRIPTION, r.getDescription())
					.put(IJsonNames.START_DATE, AonDateUtils.format(r.getDate(), "dd/MM/yyyy"))
					.put(IJsonNames.END_DATE, AonDateUtils.format(r.getEndDate(), "dd/MM/yyyy"))
					.put("hash", "-")
			);
		});
		return array;
	}
	
	private static JSONObject putAction(AonApiData api) {
		String periodStr = JsonUtils.getString(api.getData(), IJsonNames.PERIOD);
		Integer year = JsonUtils.getInteger(api.getData(), IJsonNames.YEAR);
		Period period = Period.safeValueOf(periodStr);
		Date startDate = getStartDate(period, year);
		Date endDate = getEndDate(period, year);
		InvoiceBatch invoiceBatch = new InvoiceBatch()
				.setDomain(api.getDomain().getId())
				.setDescription(period.getDescription() + " " + year)
				.setDate(startDate)
				.setEndDate(endDate)
				.setType(InvoiceCommunicationType.CLOSING)
				.setOperation(InvoiceCommunicationOperation.REGISTER);
		
		AON.saveInvoiceClosing(api.getDomain(), api.getUser(), invoiceBatch);
		
		return new JSONObject();
	}
	
	private static Date getStartDate(Period period, Integer year) {
		return AonDateUtils.getDate(year, period.getStartMonth(), 1);		
	}
	
	private static Date getEndDate(Period period, Integer year) {
		Date date = AonDateUtils.getDate(year, period.getDueMonth(), 1);		
		return AonDateUtils.getMonthLastDay(date);
	}
}
