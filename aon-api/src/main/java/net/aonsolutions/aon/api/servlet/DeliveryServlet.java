package net.aonsolutions.aon.api.servlet;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.SERFRUIT;
import com.esferalia.aon.occam.api.json.CarrierPackingJSON;
import com.esferalia.aon.occam.api.json.DeliveryJSON;
import com.esferalia.aon.occam.api.json.DeliveryPackagingJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.DeliveryProperties;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryPackaging;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiDeliveryServlet", urlPatterns = {"/ms/api/delivery/*"})
public class DeliveryServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(DeliveryServlet.class.getName());
	
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
			switch (api.getPath()) {
			case "/":
				response(req, resp, getDeliveries(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void put(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, saveDelivery(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void delete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, deleteDelivery(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONArray getDeliveries(AonApiData api) {
		return DeliveryJSON.toJSON(
			AON.getDeliveryStream(api.getDomain(), api.getUser(), f -> deliveryFilter(api, f)));
	}
	
	private JSONObject saveDelivery(AonApiData api) {
		Delivery delivery = DeliveryJSON.fromJSON(api.getData());
		delivery = AON.saveDelivery(api.getDomain(), api.getUser(), delivery);
			
		if(JsonUtils.has(api.getData(), IJsonNames.PACKAGING)) {
			List<DeliveryPackaging> list =  DeliveryPackagingJSON.fromJSON(JsonUtils.getJSONArray(api.getData(), IJsonNames.PACKAGING));
			SERFRUIT.saveDeliveryPackaging(api.getDomain(), api.getUser()
					, delivery, list);
		}
		
		if(JsonUtils.has(api.getData(), IJsonNames.CARRIER_PACKING)) {
			CarrierPacking carrierPacking = CarrierPackingJSON.fromJSON(JsonUtils.getJSONObject(api.getData(), IJsonNames.CARRIER_PACKING));
			SERFRUIT.saveCarrierPacking(api.getDomain(), api.getUser(), delivery, carrierPacking);
		}

		return DeliveryJSON.toJSON(delivery);
	}
	
	private JSONObject deleteDelivery(AonApiData api) {
		Integer id = JsonUtils.getInteger(api.getData(), IJsonNames.ID);
		AON.deleteSales(api.getDomain(), api.getUser(), id);
		return new JSONObject();
	}
	
	private Filter deliveryFilter(AonApiData api, DeliveryProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		
		String series = JsonUtils.getString(api.getData(), IJsonNames.SERIES);
		if(!AonStringUtils.isBlank(series)) {
			filter = filter.and(f.getSeriesProperty().eq(series));
		}
		
		Integer number = JsonUtils.getInteger(api.getData(), IJsonNames.NUMBER);
		if(number != null) {
			filter = filter.and(f.getNumberProperty().eq(number));
		}
		
		DeliveryStatus status = DeliveryStatus.safeValueOf(JsonUtils.getString(api.getData(), IJsonNames.STATUS));
		if(status != null) {
			filter = filter.and(f.getStatusProperty().eq(status.value()));
		}
	 	
		Date from = JsonUtils.getDate(api.getData(), IJsonNames.FROM);
		if(from != null) {
			filter = filter.and(f.getIssueTimeProperty().ge(AonDateUtils.toTimestamp(from)));
		}
		
		Date to = JsonUtils.getDate(api.getData(), IJsonNames.TO);
		if(to != null) {
			filter = filter.and(f.getIssueTimeProperty().ge(AonDateUtils.toTimestamp(to)));
		}		
		return filter;
	}
}
