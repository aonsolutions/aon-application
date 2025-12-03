package net.aonsolutions.aon.api.servlet;

import java.util.Optional;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.InventoryJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Properties.InventoryProperties;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.warehouse.Inventory;
import com.esferalia.aon.occam.api.model.warehouse.InventoryDetail;
import com.esferalia.aon.occam.api.model.warehouse.InventoryStatus;
import com.esferalia.aon.watson.server.AonDateUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;


@SuppressWarnings("serial")
@WebServlet(name = "AonApiInventoriesServlet", urlPatterns = { "/ms/api/inventories/*" })
public class InventoriesServlet extends AonApiHttpServlet {

	private static final Logger LOGGER = Logger.getLogger(InventoriesServlet.class.getName());

	public static final String INVENTORIES = "/";
	public static final String INVENTORY = "/:id";
	public static final String INVENTORY_DETAIL = "/:id/detail";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		put(req, resp);
	}

	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);

			Object object = new AonRouting(api).addRoute(INVENTORIES, InventoriesServlet::getInventories)
					.addRoute(INVENTORY, InventoriesServlet::getInventory)
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

			Object object = new AonRouting(api).addRoute(INVENTORIES, InventoriesServlet::saveInventory)
					.addRoute(INVENTORY, InventoriesServlet::saveInventory)
					.addRoute(INVENTORY_DETAIL, InventoriesServlet::saveInventoryDetail)
					.apply();

			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	private static JSONObject getInventory(AonApiData api) {
		JSONObject vars = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);
		Integer id = vars.getInt(IJsonNames.ID);
		Optional<JSONObject> json =  InventoryJSON.to(AON.getInventory(api.getOccam(), api.getDomain().getId(), id, new Options().setFull(true)));
		if(json.isEmpty()) throw new AonApiException("El Inventario no existe");
		return json.get();
	}

	private static JSONArray getInventories(AonApiData api) {
		Integer page = api.getData().opt(IJsonNames.PAGE) != null ? api.getData().optInt(IJsonNames.PAGE) : 1;
		Integer perPage = api.getData().opt(IJsonNames.PER_PAGE) != null ? api.getData().optInt(IJsonNames.PER_PAGE) : 30;
		return InventoryJSON.to(AON.getInventoryList(api.getOccam(), f -> inventoryFilter(api, f), new Options().setPage(page).setPerPage(perPage)));
	}

	private static Filter inventoryFilter(AonApiData api, InventoryProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		
		if(JsonUtils.has(api.getData(), IJsonNames.ID)) {
			filter = filter.and(f.getIdProperty().eq(JsonUtils.getInteger(api.getData(), IJsonNames.ID)));
		}
		
		if(JsonUtils.has(api.getData(), IJsonNames.WAREHOUSE)) {
			filter = filter.and(f.getWarehouseProperty().eq(JsonUtils.getInteger(api.getData(), IJsonNames.WAREHOUSE)));
		}
		
		if(JsonUtils.has(api.getData(), IJsonNames.VALUE)) {
			filter = filter.and(f.getDescriptionProperty().like("%" + JsonUtils.getString(api.getData(), IJsonNames.VALUE) + "%"));
		}
		
		if(JsonUtils.has(api.getData(), IJsonNames.DESCRIPTION)) {
			filter = filter.and(f.getDescriptionProperty().eq(JsonUtils.getString(api.getData(), IJsonNames.DESCRIPTION)));
		}
		
		if(JsonUtils.has(api.getData(), IJsonNames.STATUS)) {
			filter = filter.and(f.getStatusProperty().eq(InventoryStatus.safeValueOf(JsonUtils.getString(api.getData(), IJsonNames.STATUS)).value()));
		}		
		
		if(JsonUtils.has(api.getData(), IJsonNames.FROM)) {
			filter = filter.and(f.getInventoryDateProperty().ge(AonDateUtils.toSql(JsonUtils.getDate(api.getData(), IJsonNames.FROM))));
		}
		
		if(JsonUtils.has(api.getData(), IJsonNames.TO)) {
			filter = filter.and(f.getInventoryDateProperty().le(AonDateUtils.toSql(JsonUtils.getDate(api.getData(), IJsonNames.TO))));
		}

		return filter;
	}

	public static JSONObject saveInventory(AonApiData api) {
		// TODO 
		return new JSONObject();
	}
	
	public static JSONObject saveInventoryDetail(AonApiData api) {
		JSONObject vars = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);
		Integer inventoryId = vars.getInt(IJsonNames.ID);
		Integer itemPackage = JsonUtils.getInteger(api.getData(), IJsonNames.ITEM_PACKAGE);
	
		// TODO Pasarlo al DAO.
		Item pack = AON.getItem(api.getDomain(), api.getUser().getLogin(), f -> f.getIdProperty().eq(itemPackage), new Options().setFull(true));
		InventoryDetail packageInventoryDetail = new InventoryDetail()
				.setInventory(new Inventory().setId(inventoryId))
				.setDomain(api.getDomain().getId())
				.setCost(0.0)
				.setActualQuantity(1.0)
				.setRealQuantity(1.0)
				.setItem(new Item().setId(itemPackage));
		AON.saveInventoryDetail(api.getOccam(), packageInventoryDetail);
		
		JSONArray details = JsonUtils.getJSONArray(api.getData(), IJsonNames.DETAILS);
		for(Integer i = 0; i < details.length(); i++) {
			JSONObject detail = details.getJSONObject(i);
			Integer id = JsonUtils.getInteger(detail, IJsonNames.DETAIL);
			Integer itemId = JsonUtils.getInteger(detail, IJsonNames.ITEM);
			// Integer itemPackageId = JsonUtils.getInteger(detail, IJsonNames.ITEM_PACKAGE);
			double quantity = JsonUtils.getdouble(detail, IJsonNames.QUANTITY);
			
			InventoryDetail itemInventoryDetail = AON.getInventoryDetail(api.getOccam(), api.getDomain().getId(), id);
			itemInventoryDetail.setRealQuantity(itemInventoryDetail.getRealQuantity() + quantity);
			
			AON.saveInventoryDetail(api.getOccam(), itemInventoryDetail);
			pack.getItemComposition().stream().filter(it -> it.getComposition().getId().equals(itemId)).forEach(c -> {
				if(c.getQuantity() != quantity) {
					c.setQuantity(quantity);
					AON.saveItemComposition(api.getDomain(), api.getUser().getLogin(), c);
				}
			});
		}
		return new JSONObject();
	}
}
