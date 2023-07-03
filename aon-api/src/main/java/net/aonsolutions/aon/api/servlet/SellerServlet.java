package net.aonsolutions.aon.api.servlet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.CustomerJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.RegistrySellerJSON;
import com.esferalia.aon.occam.api.json.SellerJSON;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.RegistrySellerProperties;
import com.esferalia.aon.occam.api.model.Properties.SellerProperties;
import com.esferalia.aon.occam.api.model.registry.RegistrySeller;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.RegistrySellerStatus;
import com.esferalia.aon.occam.api.model.type.RegistrySellerType;
import com.esferalia.aon.watson.util.AonNumberUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiSellerServlet", urlPatterns = {"/ms/api/seller/*"})
public class SellerServlet extends AonApiHttpServlet {

	private static final Logger LOGGER  = Logger.getLogger(SellerServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API SELLER SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req);
		
			switch (api.getPath()) {
			case "/":
				response(req, resp, getSellers(api));
				break;
			case "/rseller":
			case "/rseller/":
				response(req, resp, getRSeller(api));
				break;
			case "/rsellers":
			case "/rsellers/":
				response(req, resp, getRSellers(api));
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
		LOGGER.info("AON API SELLER SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req);
		
			switch (api.getPath()) {
			case "/":
//				response(req, resp, getProducts(api));
				break;
			case "/rseller":
			case "/rseller/":
				response(req, resp, saveRegistrySeller(api));
			case "/rseller/update":
			case "/rseller/update/":
				response(req, resp, updateRegistrySeller(api));
				break;
			case "/rsellers":
			case "/rsellers/":
				response(req, resp, saveRegistrySeller(api));
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
		LOGGER.info("AON API SELLER SERVLET - PUT METHOD");
		try {
			AonApiData api = initialize(req);
		
			switch (api.getPath()) {
			case "/":
//				response(req, resp, getResponseObject());
				break;
			case "/rseller":
			case "/rseller/":
				response(req, resp, saveRSeller(api));
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
		LOGGER.info("AON API SELLER SERVLET - DELETE METHOD");
		try {
			AonApiData api = initialize(req);
		
			switch (api.getPath()) {
			case "/":
//				response(req, resp, getResponseObject());
				break;
			case "/rseller":
			case "/rseller/":
				response(req, resp, deleteRSeller(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}	
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	
	private JSONObject getRSeller(AonApiData api) {
		RegistrySeller rseller = AON.getRegistrySeller(api.getDomain(), api.getUser().getLogin(),
				f -> rsellerFilter(api, f));
		return RegistrySellerJSON.toJSON(rseller);
	}
	
	private JSONArray getRSellers(AonApiData api) {
		
		Integer page = api.getData().opt(IJsonNames.PAGE) != null 
				? api.getData().optInt(IJsonNames.PAGE) : 1;
		Integer perPage = api.getData().opt(IJsonNames.PER_PAGE) != null
				? api.getData().optInt(IJsonNames.PER_PAGE) : 50;
		
		Stream<RegistrySeller> rsellers = AON.getRegistrySellerStream(api.getDomain(), api.getUser().getLogin(),
				f -> rsellerFilter(api, f), perPage * (page -1), perPage);
		List<RegistrySeller> sl = rsellers.toList();
		return RegistrySellerJSON.toJSON(sl);
	}
	
	private JSONArray getSellers(AonApiData api) {
		Integer page = api.getData().opt(IJsonNames.PAGE) != null 
				? api.getData().optInt(IJsonNames.PAGE) : 1;
		Integer perPage = api.getData().opt(IJsonNames.PER_PAGE) != null
				? api.getData().optInt(IJsonNames.PER_PAGE) : 50;
		
		Stream<Seller> sellers = AON.getSellerStream(api.getDomain().getName(),
				api.getDomain().getId(),
				api.getUser().getLogin(),
				f -> sellerFilter(api, f),
				perPage, perPage * (page -1));
		
		return SellerJSON.toJSON(sellers);
	}
	
	private JSONObject saveRSeller(AonApiData api) {
		if (api.getData().opt(IJsonNames.RSELLER) != null) {
			RegistrySeller rseller = RegistrySellerJSON.fromJSON(api.getData().optJSONObject(IJsonNames.RSELLER));
			rseller = AON.saveRegistrySeller(api.getDomain(), api.getUser().getLogin(), rseller);
			return RegistrySellerJSON.toJSON(rseller);
		}
		return new JSONObject();
	}
	
	private static JSONArray saveRegistrySeller(AonApiData api) {
		JSONArray sellersJson = api.getData().optJSONArray(IJsonNames.SELLERS);
		 List<Seller> sellers = SellerJSON.fromJSON(sellersJson);
		 List<Customer> customers  = CustomerJSON.fromJSON(api.getData().optJSONArray(IJsonNames.CUSTOMERS));
		 RegistrySellerType type = RegistrySellerType.safeValueOf(api.getData().optString(IJsonNames.TYPE));
		 RegistrySellerStatus status = RegistrySellerStatus.safeValueOf(api.getData().optString(IJsonNames.STATUS));
		 Date startDate = JsonUtils.getDate(api.getData(), IJsonNames.START_DATE);
		 Date endDate = JsonUtils.getDate(api.getData(), IJsonNames.END_DATE);
		 
		 
		 List<RegistrySeller> registrySellers = new ArrayList<>();
		 
		 customers.forEach(customer->{
			 sellers.forEach(seller-> {
				 RegistrySeller rseller = new RegistrySeller()
						 .setDomain(seller.getDomain())
						 .setRegistry(customer.getId())
						 .setSeller(seller)
						 .setStartDate(startDate)
						 .setEndDate(endDate)
						 .setType(type)
						 .setStatus(status);
				 registrySellers.add(rseller);
			 });
			 
		 });
		
		 if(!registrySellers.isEmpty()) {
			 //SAVE RITEM
			 RegistrySeller[] add = registrySellers.stream()
			 .filter(r-> !r.isRemoved()).toArray(RegistrySeller[]::new);
			 
			 if(add.length>0) {
				 for(int i=0;i<add.length; i++) {
					 AON.saveRegistrySeller(api.getDomain(), api.getUser().getLogin(), add[i]); 
				 }
			 }
			 
			 //DELETE RITEM
			 registrySellers.stream()
			 .filter(RegistrySeller::isRemoved)
			 .forEach(r->{
				 AON.deleteRegistrySeller(api.getDomain(), api.getUser().getLogin(),
						 f-> f.getDomainProperty().eq(r.getDomain().getId())
						 .and(f.getSellerProperty().eq(r.getSeller() != null ? r.getSeller().getId() : null))
						 .and(f.getRegistryProperty().eq(r.getRegistry()))
						 );
			 });
		 }
		 
		 return SellerJSON.toJSON(sellers);
	}
	
	private static JSONObject updateRegistrySeller(AonApiData api) {
		Domain domain = api.getDomain();
		User user = api.getUser();
		JSONObject rsellerJson = api.getData().optJSONObject("rseller");		//NOT NULL
		Integer sellerId = api.getData().optInt(IJsonNames.SELLER);				//NOT NULL
		RegistrySellerType type = RegistrySellerType.safeValueOf(api.getData().optString(IJsonNames.TYPE));
		RegistrySellerStatus status = RegistrySellerStatus.safeValueOf(api.getData().optString(IJsonNames.STATUS));
		
		Date startDate = JsonUtils.getDate(api.getData(), IJsonNames.START_DATE);	//NULLABLE
		Date endDate = JsonUtils.getDate(api.getData(), IJsonNames.END_DATE);		//NULLABLE
		if (rsellerJson != null && sellerId > 0 && status != null) {
			RegistrySeller rseller = RegistrySellerJSON.fromJSON(rsellerJson);
			Seller seller = new Seller().setId(sellerId);
			if (AonNumberUtils.zeroIfNull(rseller.getId()) > 0) {
				AON.deleteRItem(domain, user, f -> f.getIdProperty().eq(rseller.getId()));
				
				rseller
				.setSeller(seller)
				.setStartDate(startDate)
				.setEndDate(endDate)
				.setStatus(status)
				.setType(type);
				
				RegistrySeller updatedRSellers = AON.saveRegistrySeller(domain, user.getLogin(), rseller);
				if (updatedRSellers != null) {
					return RegistrySellerJSON.toJSON(updatedRSellers);
				}
			}
			
		}
		
		return new JSONObject();
		
	}
	
	private JSONObject deleteRSeller(AonApiData api) {
		int deleted = AON.deleteRegistrySeller(api.getDomain(), api.getUser().getLogin(),
				f -> rsellerFilter(api, f));
		JSONObject json = new JSONObject();
		json.append(IJsonNames.DELETED, deleted);
		return json;
	}
	
	private Filter rsellerFilter(AonApiData api, RegistrySellerProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		
		if(api.getData().opt(IJsonNames.ID) != null) {
			int id = api.getData().optInt(IJsonNames.ID);
			filter = filter.and(f.getIdProperty().eq(id));
		}
		
		if (api.getData().opt(IJsonNames.REGISTRY) != null) {
			int registry = api.getData().optInt(IJsonNames.REGISTRY);
			filter = filter.and(f.getRegistryProperty().eq(registry));
		}
		
		if (api.getData().opt(IJsonNames.SELLER) != null) {
			int seller = api.getData().optInt(IJsonNames.SELLER);
			filter = filter.and(f.getSellerProperty().eq(seller));
		}
		
		if (api.getData().opt(IJsonNames.STATUS) != null) {
			RegistrySellerStatus status = api.getData().optEnum(RegistrySellerStatus.class, IJsonNames.STATUS);
			filter = filter.and(f.getStatusProperty().eq(status != null ? status.value() : null));
		}
		
		if (api.getData().opt(IJsonNames.TYPE) != null) {
			RegistrySellerType type = api.getData().optEnum(RegistrySellerType.class, IJsonNames.TYPE);
			filter = filter.and(f.getTypeProperty().eq(type != null ? type.value() : null));
		}
		
		return filter;
	}
	
	private Filter sellerFilter(AonApiData api, SellerProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		
		if(api.getData().opt(IJsonNames.ID) != null) {
			int id = api.getData().optInt(IJsonNames.ID);
			filter = filter.and(f.getIdProperty().eq(id));
		}
		
		if (api.getData().opt(IJsonNames.REGISTRY) != null) {
			int registry = api.getData().optInt(IJsonNames.REGISTRY);
			filter = filter.and(f.getRegistryProperty().eq(registry));
		}
		
		if (api.getData().opt(IJsonNames.STATUS) != null) {
			RegistrySellerStatus status = api.getData().optEnum(RegistrySellerStatus.class, IJsonNames.STATUS);
			filter = filter.and(f.getStatusProperty().eq(status != null ? status.value() : null));
		}
		
		if (api.getData().opt(IJsonNames.TYPE) != null) {
			RegistrySellerType type = api.getData().optEnum(RegistrySellerType.class, IJsonNames.TYPE);
			filter = filter.and(f.getTypeProperty().eq(type != null ? type.value() : null));
		}
		
		return filter;
	}
}
