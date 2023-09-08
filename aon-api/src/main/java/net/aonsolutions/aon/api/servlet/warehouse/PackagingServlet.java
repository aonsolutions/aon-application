package net.aonsolutions.aon.api.servlet.warehouse;

import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.DeliveryPackagingJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.PackagingJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryPackaging;
import com.esferalia.aon.occam.api.model.warehouse.Packaging;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

@WebServlet(name = "AonApiPackagingServlet", urlPatterns = {"/ms/api/packaging/*"})
public class PackagingServlet extends AonApiHttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER  = Logger.getLogger(PackagingServlet.class.getName());
	
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
			switch (api.getPath()) {
			case "/":
				response(req, resp, getPackaging(api));
				break;
			case "/deliveryPackaging":
				response(req, resp, getDeliveryPackaging(api));
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
				response(req, resp, savePackaging(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONObject getPackaging(AonApiData api) {
		String barcode = JsonUtils.getString(api.getData(), IJsonNames.BARCODE);
		Packaging packaging = AON.getPackaging(api.getDomain(), api.getUser(), barcode);
		return PackagingJSON.toJSON(packaging);
	}
	
	private JSONObject getDeliveryPackaging(AonApiData api) {
		String sscc = JsonUtils.getString(api.getData(), IJsonNames.SSCC);
		Integer delivery = JsonUtils.getInteger(api.getData(), IJsonNames.DELIVERY);
		DeliveryPackaging deliveryPackaging = AON.getDeliveryPackaging(api.getDomain(), api.getUser(), sscc, delivery);
		return DeliveryPackagingJSON.toJSON(deliveryPackaging);
	}
	
	private JSONObject savePackaging(AonApiData api) {
		Packaging packaging = PackagingJSON.fromJSON(api.getData());
		packaging = AON.savePackaging(api.getDomain(), api.getUser(), packaging);
		return PackagingJSON.toJSON(packaging);
	}
}
