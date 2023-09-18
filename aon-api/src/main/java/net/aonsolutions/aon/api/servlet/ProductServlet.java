package net.aonsolutions.aon.api.servlet;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.CustomerJSON;
import com.esferalia.aon.occam.api.json.ItemJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.ProductCategoryJSON;
import com.esferalia.aon.occam.api.json.ProductJSON;
import com.esferalia.aon.occam.api.json.RegistryItemJSON;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.ItemProperties;
import com.esferalia.aon.occam.api.model.Properties.ProductProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryItemProperties;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.BookingStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.api.model.registry.RegistryItemStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryMode;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
			case "/ritem":
				response(req, resp, getRItems(api));
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
			case "/ritem":
				response(req, resp, saveRegistryItem(api));
				break;
			case "/ritem/update":
				response(req, resp, updateRegistryItem(api));
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
			case "/update-all-target-item":
				response(req, resp, updateAllTargetItem(api));
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
		if(api.getData().opt(IJsonNames.REGISTRY) !=null) {
			return ItemJSON.toJSON(
				AON_SOLUTIONS.getRItemStream(
					api.getDomain(), 
					api.getUser(), 
					f -> itemFilter(api, f)
				)
			);
		} else if(api.getData().opt(IJsonNames.PAGE) !=null) {
			int page = api.getData().optInt(IJsonNames.PAGE);
			int perPage = api.getData().optInt(IJsonNames.PER_PAGE);
			return ItemJSON.toJSON(
					AON_SOLUTIONS.getItems(
						api.getDomain(), 
						api.getUser(), 
						f -> itemFilter(api, f), 
						page, perPage
					)
			);
		} 
		
		return AON_SOLUTIONS.getItems(api.getDomain(), api.getUser(), f -> 
			itemFilter(api, f));
	}
	
	
	private JSONArray getRItems(AonApiData api) {
		
		Integer page = api.getData().opt(IJsonNames.PAGE) != null 
				? api.getData().optInt(IJsonNames.PAGE) : 1;
		Integer perPage = api.getData().opt(IJsonNames.PER_PAGE) != null
				? api.getData().optInt(IJsonNames.PER_PAGE) : 50;
		
		LinkedList<RegistryItem> ritems = AON.getRItemList(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
				f -> ritemFilter(api, f), perPage, perPage * (page -1));
		return RegistryItemJSON.toJSON(ritems);
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
	
	private JSONObject updateAllTargetItem(AonApiData api) {
		JSONObject params = api.getData();
		Date startDate = AonDateUtils.parse(params.optString(IJsonNames.START_DATE), AonDateUtils.SIMPLE_DATE_FORMAT4);
		
		if (startDate == null) {
			throw new AonApiException(AonApiError.EMPTY_DATA.getMessage());
		}
		
		boolean disable = params.optBoolean("question");
		
		AON.updateAllTargetItem(api.getDomain(), api.getUser(), f ->
		f.getDomainProperty().eq(api.getDomain().getId()).and(f.getStartIssueDateProperty().ge(startDate))
		, disable);
		return new JSONObject();
	}
	
	private static JSONArray saveRegistryItem(AonApiData api) {
		JSONArray itemsJson = api.getData().optJSONArray(IJsonNames.ITEMS);
		 List<Item> items          = ItemJSON.fromJSON(itemsJson);
		 List<Customer> customers  = CustomerJSON.fromJSON(api.getData().optJSONArray(IJsonNames.CUSTOMERS));
		 String typeStr = api.getData().optString(IJsonNames.TYPE);
		 RegistryMode type = AonStringUtils.isNotBlank(typeStr) ? RegistryMode.valueOf(typeStr) : RegistryMode.TARGET;
		 Date startDate = JsonUtils.getDate(api.getData(), IJsonNames.START_DATE);
		 Date endDate = JsonUtils.getDate(api.getData(), IJsonNames.END_DATE);
		 
		 
		 List<RegistryItem>registryItems = new ArrayList<>();
		 
		 customers.forEach(customer->{
			 items.forEach(item-> {
				 
				 RegistryItem ritem = new RegistryItem()
						 .setDomain(item.getDomain().getId())
						 .setRegistry(customer.getId())
						 .setItem(item)
						 .setPrice(item.getPrice())
						 .setType(type)
						 .setPriority(Priority.NONE)
						 .setRemoved(item.isRemoved())
						 .setStartDate(startDate)
						 .setEndDate(endDate);
				 if (api.getData().opt("bookingStatus") != null && AonStringUtils.isNotBlank(api.getData().optString("bookingStatus"))) {
					 BookingStatus bookingStatus = BookingStatus.valueOf(api.getData().optString("bookingStatus"));
					 ritem.setBookingStatus(bookingStatus);
				 } else if (api.getData().opt("status") != null && AonStringUtils.isNotBlank(api.getData().optString("status"))) {
					 RegistryItemStatus status = RegistryItemStatus.valueOf(api.getData().optString("status"));
					 ritem.setStatus(status);
				 } else {
					 ritem.setStatus(RegistryItemStatus.INTERESTED);
				 }
				 registryItems.add(ritem);
			 });
			 
		 });
		
		 
		 if(!registryItems.isEmpty()) {
			 //SAVE RITEM
			 RegistryItem[] add = registryItems.stream()
			 .filter(r-> !r.isRemoved()).toArray(RegistryItem[]::new);
			 
			 if(add.length>0) {
				 AON.saveRItem(api.getDomain(), api.getUser(), add);
			 }
			 
			 //DELETE RITEM
			 registryItems.stream()
			 .filter(RegistryItem::isRemoved)
			 .forEach(r->{
				 AON.deleteRItem(api.getDomain(), api.getUser(), 
					f-> f.getDomainProperty().eq(r.getDomain())
					.and(f.getItemProperty().eq(r.getItem() != null ? r.getItem().getId() : null))
					.and(f.getRegistryProperty().eq(r.getRegistry()))
				);
			 });
		 }
		 
		 return ItemJSON.toJSON(items);
	}
	
	private static JSONObject updateRegistryItem(AonApiData api) {
		Domain domain = api.getDomain();
		User user = api.getUser();
		JSONObject ritemJson = api.getData().optJSONObject("ritem");		//NOT NULL
		Integer itemId = api.getData().optInt(IJsonNames.ITEM);				//NOT NULL
		RegistryItemStatus status = null;									//NOT NULL
		if (api.getData().opt("bookingStatus") != null) {
			BookingStatus bookingStatus = api.getData().getEnum(BookingStatus.class, "bookingStatus");
			status = RegistryItemStatus.safeValueOf(bookingStatus.value());
		} else if (api.getData().opt(IJsonNames.STATUS) != null) {
			status = api.getData().getEnum(RegistryItemStatus.class, IJsonNames.STATUS);
		}
		
		Date startDate = JsonUtils.getDate(api.getData(), IJsonNames.START_DATE);	//NULLABLE
		Date endDate = JsonUtils.getDate(api.getData(), IJsonNames.END_DATE);		//NULLABLE
		if (ritemJson != null && itemId > 0 && status != null) {
			RegistryItem ritem = RegistryItemJSON.fromJSON(ritemJson);
			Item item = new Item().setId(itemId);
			if (AonNumberUtils.zeroIfNull(ritem.getId()) > 0) {
				AON.deleteRItem(domain, user, f -> f.getIdProperty().eq(ritem.getId()));
				
				ritem
				.setItem(item)
				.setStartDate(startDate)
				.setEndDate(endDate)
				.setStatus(status);
				
				RegistryItem[] updatedRitems = AON.saveRItem(domain, user, ritem);
				if (updatedRitems != null && updatedRitems.length > 0) {
					return RegistryItemJSON.toJSON(updatedRitems[0]);
				}
			}
			
		}
		
		return new JSONObject();
		
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
	
		if(api.getData().opt(IJsonNames.REGISTRY) != null){
			Integer registry = api.getData().optInt(IJsonNames.REGISTRY);
			filter = filter.and(f.getRegistryProperty().eq(registry));
		}
		
		if(api.getData().opt(IJsonNames.SERIAL_NUMBER) != null) {
			filter = filter.and(f.getSerialNumberProperty().eq(JsonUtils.getString(api.getData(), IJsonNames.SERIAL_NUMBER)));
		}
		
		if(api.getData().opt(IJsonNames.TYPE) != null) {
			RegistryMode type = api.getData().getEnum(RegistryMode.class, IJsonNames.TYPE);
			if (type != null) {				
				filter = filter.and(f.getTypeProperty().eq(type.value()));
			}
		}
		
		return filter;
	}
	
	private Filter ritemFilter(AonApiData api, RegistryItemProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		
		if(api.getData().opt(IJsonNames.TYPE) != null) {
			RegistryMode rm = api.getData().getEnum(RegistryMode.class, IJsonNames.TYPE);
			if (rm != null) {
				filter = filter.and(f.getTypeProperty().eq(rm.value()));
			}
		}
		
		if(api.getData().opt(IJsonNames.REGISTRY) != null) {
			Integer registry = AonNumberUtils.zeroIfNull(api.getData().optInt(IJsonNames.REGISTRY));
			if (registry > 0) {
				filter = filter.and(f.getRegistryProperty().eq(registry));
			}
		}
		
		return filter;
	}
}
