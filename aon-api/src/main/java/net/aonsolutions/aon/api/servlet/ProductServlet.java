package net.aonsolutions.aon.api.servlet;

import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.ItemJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.ProductCategoryJSON;
import com.esferalia.aon.occam.api.json.ProductJSON;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.ItemProperties;
import com.esferalia.aon.occam.api.model.Properties.ProductProperties;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiProductServlet", urlPatterns = {"/ms/api/product/*"})
public class ProductServlet extends AonApiHttpServlet {

	private static final Logger LOGGER  = Logger.getLogger(ProductServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API PRODUCT SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req);
		
			switch (api.getPath()) {
			case "/":
				response(req, resp, getProducts(api));
				break;
			case "/item":
				response(req, resp, getItem(api));
				break;
			case "/items":
				response(req, resp, getItems(api));
				break;
			case "/category":
				response(req, resp, getProductCategories(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}	
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API PRODUCT SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req);
		
			switch (api.getPath()) {
			case "/":
				response(req, resp, saveProduct(api));
				break;
			case "/item":
				response(req, resp, saveItem(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}	
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API PRODUCT SERVLET - PUT METHOD");
		try {
			AonApiData api = initialize(req);
		
			switch (api.getPath()) {
			case "/":
//				response(req, resp, getResponseObject());
				break;
			case "/item":
				response(req, resp, saveItem(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}	
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API PRODUCT SERVLET - DELETE METHOD");
		try {
			AonApiData api = initialize(req);
		
			switch (api.getPath()) {
			case "/":
//				response(req, resp, getResponseObject());
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}	
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONArray getProducts(AonApiData api) {
		if(api.getData().opt(IJsonNames.PAGE) !=null) {
			int page = api.getData().optInt(IJsonNames.PAGE);
			int perPage = api.getData().optInt(IJsonNames.PER_PAGE);
			return ProductJSON.toJSON(
				AON_SOLUTIONS.getProducts(api.getDomain(), api.getUser(), f -> 
				productFilter(api, f), page, perPage)
			);
		} 
		
		return AON_SOLUTIONS.getProducts(api.getDomain(), api.getUser(), f -> 
				productFilter(api, f));
	}
	
	private JSONObject getItem(AonApiData api) {
		Item item = AON.getItem(api.getDomain(), api.getUser().getLogin(), f -> 
			itemFilter(api, f));
		return ItemJSON.toJSON(item);
	}
	
	private JSONArray getItems(AonApiData api) {
		return AON_SOLUTIONS.getItems(api.getDomain(), api.getUser(), f -> 
			itemFilter(api, f));
	}
	
	private JSONArray getProductCategories(AonApiData api) {
		return ProductCategoryJSON.toJSON(AON.getProductCategoryStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
				f -> f.getDomainProperty().eq(api.getDomain().getId())));
	}

	private JSONObject saveProduct(AonApiData api) {
		Product product = ProductJSON.fromJSON(api.getData());
		product = AON.saveProduct(api.getDomain(), api.getUser().getLogin(), product);
		return ProductJSON.toJSON(product);
	}
	
	private JSONObject saveItem(AonApiData api) {
		Item item = ItemJSON.fromJSON(api.getData());
		item = AON.saveItem(api.getDomain(), api.getUser().getLogin(), item);
		return ItemJSON.toJSON(item);
	}
	
	private Filter productFilter(AonApiData api, ProductProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		
		if(api.getData().opt("expense") !=null) {
			if(JsonUtils.getboolean(api.getData(), "expense")) {
				filter = filter.and(f.getTypeProperty().eq(ProductType.EXPENSE.value()));
			} else {
				filter = filter.and(f.getTypeProperty().ne(ProductType.EXPENSE.value()));
			}
		}
		
		if(!AonStringUtils.isBlank(api.getData().optString(IJsonNames.VALUE))) {
			String value = api.getData().optString(IJsonNames.VALUE);
			Filter valueFilter = f.getCodeProperty().like("%" + value + "%")
					.or(f.getNameProperty().like("%" + value + "%"));
			filter = filter.and(valueFilter);
		}
		
		if(api.getData().opt("type") !=null) {
			ProductType type = ProductType.safeValueOf(JsonUtils.getString(api.getData(), IJsonNames.TYPE));
			filter = filter.and(f.getTypeProperty().eq(type.value()));
		}
		
		return filter;
	}
	
	private Filter itemFilter(AonApiData api, ItemProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		
		if(api.getData().opt(IJsonNames.PRODUCT) != null){
			Integer product = api.getData().optInt(IJsonNames.PRODUCT);
			filter = filter.and(f.getProductProperty().eq(product));
		}
		
		if(!AonStringUtils.isBlank(api.getData().optString(IJsonNames.VALUE))) {
			String value = api.getData().optString(IJsonNames.VALUE);
			filter = filter.and(
					f.getDescriptionProperty().like("%" + value + "%")
					.or(f.getProductCodeProperty().like("%" + value + "%"))
					.or(f.getProductNameProperty().like("%" + value + "%"))
			);
		}
		
		if(api.getData().opt(IJsonNames.SERIAL_NUMBER) != null) {
			filter = filter.and(f.getSerialNumberProperty().eq(JsonUtils.getString(api.getData(), IJsonNames.SERIAL_NUMBER)));
		}
		
		return filter;
	}
}
