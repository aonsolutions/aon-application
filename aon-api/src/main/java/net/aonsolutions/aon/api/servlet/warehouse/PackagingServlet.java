package net.aonsolutions.aon.api.servlet.warehouse;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.ElaborationJSON;
import com.esferalia.aon.occam.api.json.ItemJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.watson.server.AonDateUtils;

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
				response(req, resp, getPackagingInfo(api));
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
				response(req, resp, saveElaboration(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONObject getPackagingInfo(AonApiData api) {
		String barcode = JsonUtils.getString(api.getData(), IJsonNames.BARCODE);
		String serialNumber = barcode.contains("(") ? "" : Integer.toString(AonDateUtils.getDayOfYear(new Date()));
		Date serialDate = new Date();

		Item item = AON.getItem(api.getDomain(), api.getUser().getLogin(), f ->
				f.getDomainProperty().eq(api.getDomain().getId())
				.and(f.getBarcodeProperty().eq(barcode))
				.and(f.getSerialNumberProperty().isNull()));
		if(item.isEmpty()) throw new AonApiException("El producto no existe.");
		Item item2 = AON.getItem(api.getDomain(), api.getUser().getLogin(), f -> 
			f.getDomainProperty().eq(api.getDomain().getId())
			.and(f.getProductProperty().eq(item.getProduct().getId()))
			.and(f.getSerialNumberProperty().eq(serialNumber)));
		if(item2.isEmpty()) {
			item2 = item;
			item2.setId(null).setBarcode(null).setSerialNumber(serialNumber).setSerialDate(serialDate);
			item2 = AON.saveItem(api.getDomain(), api.getUser().getLogin(), item2);
		}
		Integer[] items = AON.getItemCompositionStream(api.getDomain(), api.getUser().getLogin(), f -> f.getCompositionItemProperty().eq(item.getId()))
				.map(ItemComposition::getItemId).toArray(Integer[]::new);
		
		List<Item> container = AON.getItemList(api.getDomain(),  api.getUser().getLogin(), f -> f.getIdProperty().in(items));
		for (int i = 0; i < container.size(); i++) {
			Integer id = container.get(i).getId();
			List<ItemComposition> icList = AON.getItemCompositionList(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), id);
			if(icList.size() == 1) container.get(i).setItemComposition(icList);
			else container.remove(i);
		}
		JSONObject json = new JSONObject();
		json.put(IJsonNames.CONTAINER, ItemJSON.toJSON(container));
		json.put(IJsonNames.ITEM, ItemJSON.toJSON(item2));
		return json;
	}
	
	private JSONObject saveElaboration(AonApiData api) {
		Elaboration elaboration = ElaborationJSON.fromJSON(api.getData());
		elaboration = AON.saveElaboration(api.getDomain(), api.getUser(), elaboration);
		return ElaborationJSON.toJSON(elaboration);
	}
}
