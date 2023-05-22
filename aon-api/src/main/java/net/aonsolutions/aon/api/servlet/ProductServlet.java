package net.aonsolutions.aon.api.servlet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.CustomerJSON;
import com.esferalia.aon.occam.api.json.ItemJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.ProductCategoryJSON;
import com.esferalia.aon.occam.api.json.ProductJSON;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.ItemProperties;
import com.esferalia.aon.occam.api.model.Properties.ProductProperties;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.api.model.registry.RegistryItemStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryMode;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.watson.server.AonDateUtils;
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
//			case "/ritem":
//				response(req, resp, getRItems(api));
//				break;
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
		 List<Item> items          = ItemJSON.fromJSON(api.getData().optJSONArray(IJsonNames.ITEMS));
		 List<Customer> customers  = CustomerJSON.fromJSON(api.getData().optJSONArray(IJsonNames.CUSTOMERS));
		 RegistryItemStatus status = RegistryItemStatus.INTERESTED; // RegistryItemStatus.safeValueOf(api.getData().optString(IJsonNames.STATUS));
		 
		 List<RegistryItem>registryItems = new ArrayList<>();
		 
		 customers.forEach(customer->{
			 items.forEach(item-> 			 	registryItems.add( 
		 			new RegistryItem()
					 .setDomain(item.getDomain().getId())
					 .setRegistry(customer.getId())
					 .setItem(item.getId())
					 .setPrice(item.getPrice())
					 .setStatus(status)
					 .setType(RegistryMode.TARGET)
					 .setPriority(Priority.NONE)
					 .setRemoved(item.isRemoved())
				 )
			 );
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
					.and(f.getItemProperty().eq(r.getItem()))
					.and(f.getRegistryProperty().eq(r.getRegistry()))
				);
			 });
		 }
		 
		 return ItemJSON.toJSON(items);
	}
	
	private static JSONArray updateRegistryItem(AonApiData api) {
		
		Date startDate = AonDateUtils.parse(api.getData().optString(IJsonNames.START_DATE), AonDateUtils.SIMPLE_DATE_FORMAT4);
		boolean question = api.getData().optBoolean("question");
		Domain domain = api.getDomain();
		User user = api.getUser();
		
//		AON_SOLUTIONS.getRItemStream(domain, user, null);
//		List<Customer> customers = AON.getCustomerStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
//				f -> f.getDomainProperty().eq(api.getDomain().getId())).collect(Collectors.toList());
//		
//		Integer[] customersIds = customers.stream().map(Customer::getId).toArray(Integer[]::new);
//		
//		List<Invoice> invoices = AON_SOLUTIONS.getInvoices(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDomainProperty().eq(domain.getId()).and(f.getRegistryProperty().in(customersIds))).collect(Collectors.toList());
//		
//		for (Invoice invoice : invoices) {
//			invoice.getDetails();
//		}
		
		return new JSONArray();
		
//		List<Item> items          = ItemJSON.fromJSON(api.getData().optJSONArray(IJsonNames.ITEMS));
//		List<Customer> customers  = CustomerJSON.fromJSON(api.getData().optJSONArray(IJsonNames.CUSTOMERS));
//		RegistryItemStatus status = RegistryItemStatus.INTERESTED; // RegistryItemStatus.safeValueOf(api.getData().optString(IJsonNames.STATUS));
//		
//		List<RegistryItem>registryItems = new ArrayList<>();
//		
//		customers.forEach(customer->{
//			items.forEach(item-> 			 	registryItems.add( 
//					new RegistryItem()
//					.setDomain(item.getDomain().getId())
//					.setRegistry(customer.getId())
//					.setItem(item.getId())
//					.setPrice(item.getPrice())
//					.setStatus(status)
//					.setType(RegistryMode.TARGET)
//					.setPriority(Priority.NONE)
//					.setRemoved(item.isRemoved())
//					)
//					);
//		});
//		
//		
//		if(!registryItems.isEmpty()) {
//			//SAVE RITEM
//			RegistryItem[] add = registryItems.stream()
//					.filter(r-> !r.isRemoved()).toArray(RegistryItem[]::new);
//			
//			if(add.length>0) {
//				AON.saveRItem(api.getDomain(), api.getUser(), add);
//			}
//			
//			//DELETE RITEM
//			registryItems.stream()
//			.filter(RegistryItem::isRemoved)
//			.forEach(r->{
//				AON.deleteRItem(api.getDomain(), api.getUser(), 
//						f-> f.getDomainProperty().eq(r.getDomain())
//						.and(f.getItemProperty().eq(r.getItem()))
//						.and(f.getRegistryProperty().eq(r.getRegistry()))
//						);
//			});
//		}
//		
//		return ItemJSON.toJSON(items);
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
		
		return filter;
	}
}
