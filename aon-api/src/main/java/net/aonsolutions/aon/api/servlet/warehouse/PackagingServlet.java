package net.aonsolutions.aon.api.servlet.warehouse;

import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.DeliveryPackagingJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.PackagingDeliveryJSON;
import com.esferalia.aon.occam.api.json.PackagingJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryPackaging;
import com.esferalia.aon.occam.api.model.warehouse.Packaging;
import com.esferalia.aon.occam.api.model.warehouse.PackagingDelivery;

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
			case "/deliveryPackaging":
				response(req, resp, saveDeliveryPackaging(api));
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
		if(sscc.length() > 18 && sscc.substring(0, 2).equals("00")) sscc = sscc.substring(2);  
		else if(sscc.length() != 18) throw new AonApiException("El SSCC introducido no es correcto.");

		Integer delivery = JsonUtils.getInteger(api.getData(), IJsonNames.DELIVERY);
		Integer product = JsonUtils.getInteger(api.getData(), IJsonNames.PRODUCT);
		DeliveryPackaging deliveryPackaging = AON.getDeliveryPackaging(api.getDomain(), api.getUser(), sscc, delivery, product);
		return DeliveryPackagingJSON.toJSON(deliveryPackaging);
	}
	
	private JSONArray savePackaging(AonApiData api) {
		Packaging packaging = PackagingJSON.fromJSON(api.getData());
		return PackagingJSON.toJSON(AON.savePackaging(api.getDomain(), api.getUser(), packaging));
	}
	
	private JSONObject saveDeliveryPackaging(AonApiData api) {
		PackagingDelivery packaging = PackagingDeliveryJSON.fromJSON(api.getData());
		packaging = AON.saveDeliveryPackaging(api.getDomain(), api.getUser(), packaging);
		return PackagingDeliveryJSON.toJSON(packaging);
	}
}
