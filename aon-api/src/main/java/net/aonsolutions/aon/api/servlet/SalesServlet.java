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
import com.esferalia.aon.occam.api.INGENET;
import com.esferalia.aon.occam.api.Options;
import com.esferalia.aon.occam.api.SERFRUIT;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.SalesJSON;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.SalesProperties;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.type.SalesStatus;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;

@WebServlet(name = "AonApiSalesServlet", urlPatterns = {"/ms/api/sales/*"})
public class SalesServlet extends AonApiHttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER  = Logger.getLogger(SalesServlet.class.getName());
	
	public static final String SALES = "/";
	public static final String SALE = "/:id";
	
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
	
	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		delete(req, resp);
	}
	
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(SALES, SalesServlet::getSales)
//				.addRoute(SALE, SalesServlet::getSale)
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
				.addRoute(SALES, SalesServlet::saveSales)
				.addRoute(SALE, SalesServlet::saveSales)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void delete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(SALES, SalesServlet::deleteSales)
				.addRoute(SALE, SalesServlet::deleteSales)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONArray getSales(AonApiData api) {
		if(JsonUtils.getboolean(api.getData(), IJsonNames.SERFRUIT)) {
			return getSerfruitSales(api);
		} else if(JsonUtils.getboolean(api.getData(), IJsonNames.INGENET)) {
			return getIngenetSales(api);
		} else return getAonSales(api);
	}
	
	private static JSONArray getAonSales(AonApiData api) {
		Stream<Sales> stream = AON.getSalesStream(api.getDomain(), api.getUser(), 
			f -> salesFilter(api, f), salesOptions(api));
		return SalesJSON.toJSON(stream);
	}
	
	private static JSONArray getSerfruitSales(AonApiData api) {
		Stream<Sales> stream = SERFRUIT.getSalesStream(api.getDomain(), api.getUser(), f -> salesFilter(api, f), salesOptions(api));
		return SalesJSON.toJSON(stream);
	}	
	
	private static JSONArray getIngenetSales(AonApiData api) {
		Stream<Sales> stream = INGENET.getSalesStream(api.getDomain(), api.getUser(), f -> salesFilter(api, f), salesOptions(api));
		return SalesJSON.toJSON(stream);
	}
	
	private static JSONObject saveSales(AonApiData api) {
		Sales sales = SalesJSON.fromJSON(api.getData());
		sales = AON.saveSales(api.getDomain(), api.getUser(), sales);
		return SalesJSON.toJSON(sales);
	}
	
	private static JSONObject deleteSales(AonApiData api) {
		Integer id = JsonUtils.getInteger(api.getData(), IJsonNames.ID);
		AON.deleteSales(api.getDomain(), api.getUser(), id);
		return new JSONObject();
	}
	
	private static Filter salesFilter(AonApiData api, SalesProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		
		String series = JsonUtils.getString(api.getData(), IJsonNames.SERIES);
		if(!AonStringUtils.isBlank(series)) {
			filter = filter.and(f.getSeriesProperty().eq(series));
		}
		
		Integer number = JsonUtils.getInteger(api.getData(), IJsonNames.NUMBER);
		if(number != null) {
			filter = filter.and(f.getNumberProperty().eq(number));
		}
		
		SalesStatus status = SalesStatus.safeValueOf(JsonUtils.getString(api.getData(), IJsonNames.STATUS));
		if(status != null) {
			filter = filter.and(f.getStatusProperty().eq(status.value()));
		}
	 	
		Date from = JsonUtils.getDate(api.getData(), IJsonNames.FROM);
		if(from != null) {
			filter = filter.and(f.getIssueDateProperty().ge(AonDateUtils.toSql(from)));
		}
		
		Date to = JsonUtils.getDate(api.getData(), IJsonNames.TO);
		if(to != null) {
			filter = filter.and(f.getIssueDateProperty().le(AonDateUtils.toSql(to)));
		}
			
		return filter;
	}
	
	private static Options salesOptions(AonApiData api) {
		Options options = new Options();
		options.setPage(JsonUtils.getInteger(api.getData(), IJsonNames.PAGE));
		options.setPerPage(JsonUtils.getInteger(api.getData(), IJsonNames.PER_PAGE));
		options.setFull(JsonUtils.getboolean(api.getData(), IJsonNames.FULL));
		return options;
	}
}
