package net.aonsolutions.aon.api.servlet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.CreditorSupplierJSON;
import com.esferalia.aon.occam.api.json.CustomerJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.RegistryAddressJSON;
import com.esferalia.aon.occam.api.json.RegistryMediaJSON;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Order;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.Properties.RegistryProperties;
import com.esferalia.aon.occam.api.model.PropertyOrders.CustomerPropertyOrders;
import com.esferalia.aon.occam.api.model.PropertyOrders.SupplierCreditorPropertyOrders;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.registry.RegistryAdditionalInfo;

@SuppressWarnings("serial")
@WebServlet(name = "SupplierCreditorServlet", urlPatterns = {"/ms/api/suppliercreditor/*"})
public class SupplierCreditorServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(SupplierCreditorServlet.class.getName());
	
	public static final String LIST = "/";
	public static final String COUNT = "/count";
	public static final String CUSTOMER_LIST = "/customer/";
	public static final String CUSTOMER_COUNT = "/customer/count";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		post(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		post(req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		post(req, resp);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		post(req, resp);
	}
	
	private void post(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			Object object = new AonRouting(api)
				.addRoute(LIST, SupplierCreditorServlet::creditorSupplierList)
				.addRoute(COUNT, SupplierCreditorServlet::creditorSupplierCount)
				.addRoute(CUSTOMER_LIST, SupplierCreditorServlet::customerList)
				.addRoute(CUSTOMER_COUNT, SupplierCreditorServlet::customerCount)
				.apply();
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONArray customerList(AonApiData api) {
		Integer page = api.getData().opt(IJsonNames.PAGE) != null 
				? api.getData().optInt(IJsonNames.PAGE) : 1;
		Integer perPage = api.getData().opt(IJsonNames.PER_PAGE) != null
			? api.getData().optInt(IJsonNames.PER_PAGE) : 50;
		String globalFilter = api.getData().optString(IJsonNames.GLOBAL);
		JSONArray result = CustomerJSON.toJSON(AON.getCustomerList(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> genericFilter(api, api.getData(), f),
				page, perPage, globalFilter, o -> customerOrder(api, o)));
		if (api.getData().opt("additional_info") != null) {
			result = getAdditionalInfo(api, result);
	    }
		return result;
	}
	
	private static JSONObject customerCount(AonApiData api) {
		String globalFilter = api.getData().optString(IJsonNames.GLOBAL);
		long count = AON.getCustomerCount(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> genericFilter(api, api.getData(), f), globalFilter);
		return new JSONObject().put(IJsonNames.COUNT, count);
	}
	
	private static JSONArray creditorSupplierList(AonApiData api) {
		Integer page = api.getData().opt(IJsonNames.PAGE) != null 
				? api.getData().optInt(IJsonNames.PAGE) : 1;
		Integer perPage = api.getData().opt(IJsonNames.PER_PAGE) != null
			? api.getData().optInt(IJsonNames.PER_PAGE) : 50;
		String globalFilter = api.getData().optString(IJsonNames.GLOBAL);
		JSONArray result = CreditorSupplierJSON.toJSON(AON.getSupplierCreditorStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
			f -> genericFilter(api, api.getData(), f), f -> genericFilter(api, api.getData(), f), page, perPage, globalFilter, o -> supplierCreditorOrder(api, o)));
		if (api.getData().opt("additional_info") != null) {
			result = getAdditionalInfo(api, result);
	    }
		return result;
	}
	
	private static JSONObject creditorSupplierCount(AonApiData api) {
		String globalFilter = api.getData().optString(IJsonNames.GLOBAL);
		long count = AON.getSupplierCreditorCount(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
				f -> genericFilter(api, api.getData(), f), f -> genericFilter(api, api.getData(), f), globalFilter);
		return new JSONObject().put(IJsonNames.COUNT, count);
	}
	
	private static <T extends RegistryProperties> Filter genericFilter(AonApiData api, JSONObject json, T f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		String jsonValue = json.optString(IJsonNames.VALUE);
		String global = json.optString(IJsonNames.GLOBAL);
		jsonValue = global.isEmpty() ? jsonValue : global;
		if(!jsonValue.isEmpty()) {
			Filter valueFilter = f.getNameProperty().like("%" + jsonValue + "%")
					.or(f.getDocumentProperty().like("%" + jsonValue + "%"));
			if(!global.isEmpty())
				valueFilter = valueFilter.or(f.getNationalityProperty().like("%" + jsonValue + "%"));
			filter = filter.and(valueFilter);
		}
		return filter;
	}
	
	private static JSONArray getAdditionalInfo(AonApiData api, JSONArray result) {
		LinkedList<RegistryAdditionalInfo> rais = new LinkedList<>();			
        JSONArray additionalInfo = (JSONArray) api.getData().opt("additional_info");
        for (int i = 0; i < additionalInfo.length(); i++) {
            rais.add(RegistryAdditionalInfo.safeValueOf(additionalInfo.get(i).toString()));
        }
        for (int i = 0; i < result.length(); i++) {
            JSONObject obj = result.getJSONObject(i);
            int id = obj.getInt("id");
            List<RegistryAddress> registryAddresses = new ArrayList<>();
            List<RegistryMedia> registryMedia = new ArrayList<>();
            rais.stream().forEach(rai -> {
                if (RegistryAdditionalInfo.ADDRESSES.equals(rai)) {
                    RegistryAddressFilter filtro = fil -> fil.getRegistryProperty().eq(id);
                    Stream<RegistryAddress> addresses = AON.getStream(api.getDomain(), api.getUser(), filtro);
                    addresses.forEach(registryAddresses::add); 
                }
                if (RegistryAdditionalInfo.MEDIA.equals(rai)) {
					RegistryMediaFilter filter  = f -> f.getRegistryProperty().eq(id);
					Stream<RegistryMedia> media = AON.getStream(api.getDomain(), api.getUser(), filter);
					media.forEach(registryMedia::add);
				}
            });
            obj.put("registryAddresses", RegistryAddressJSON.toJSON(registryAddresses));
            obj.put("media", RegistryMediaJSON.toJSON(registryMedia));
        }
        return result;
	}
	
	private static Order customerOrder(AonApiData api, CustomerPropertyOrders o) {
		Order order = null, aux;
		String[] orderByArray = JsonUtils.optString(api.getData(), IJsonNames.ORDER_BY).split(";");
		String[] orderArray = JsonUtils.optString(api.getData(), IJsonNames.ORDER).split(";");
		if(orderByArray.length == orderArray.length) {
			for(int i = 0; i < orderByArray.length; i++) {
				if(orderByArray[i].equals(IJsonNames.NAME)) {
					aux = orderArray[i].equals("asc") ? o.getRegistryNamePropertyName().orderBy().ASC() : o.getRegistryNamePropertyName().orderBy().DESC();
					order = order == null ? aux : order.and(aux);
				} else if(orderByArray[i].equals(IJsonNames.DOCUMENT)) {
					aux = orderArray[i].equals("asc") ? o.getRegistryDocumentPropertyName().orderBy().ASC() : o.getRegistryDocumentPropertyName().orderBy().DESC();
					order = order == null ? aux : order.and(aux);
				} else if(orderByArray[i].equals(IJsonNames.NATIONALITY)) {
					aux = orderArray[i].equals("asc") ? o.getRegistryNationalityPropertyName().orderBy().ASC() : o.getRegistryNationalityPropertyName().orderBy().DESC();
					order = order == null ? aux : order.and(aux);
				}
			}
		}
		if(order == null) {
			order = o.getRegistryNamePropertyName().orderBy().ASC();
		}
		return order;
	}
	
	private static Order supplierCreditorOrder(AonApiData api, SupplierCreditorPropertyOrders o) {
		Order order = null, aux;
		String[] orderByArray = JsonUtils.optString(api.getData(), IJsonNames.ORDER_BY).split(";");
		String[] orderArray = JsonUtils.optString(api.getData(), IJsonNames.ORDER).split(";");
		if(orderByArray.length == orderArray.length) {
			for(int i = 0; i < orderByArray.length; i++) {
				if(orderByArray[i].equals(IJsonNames.NAME)) {
					aux = orderArray[i].equals("asc") ? o.getRegistryNamePropertyName().orderBy().ASC() : o.getRegistryNamePropertyName().orderBy().DESC();
					order = order == null ? aux : order.and(aux);
				} else if(orderByArray[i].equals(IJsonNames.DOCUMENT)) {
					aux = orderArray[i].equals("asc") ? o.getRegistryDocumentPropertyName().orderBy().ASC() : o.getRegistryDocumentPropertyName().orderBy().DESC();
					order = order == null ? aux : order.and(aux);
				} else if(orderByArray[i].equals(IJsonNames.NATIONALITY)) {
					aux = orderArray[i].equals("asc") ? o.getRegistryNationalityPropertyName().orderBy().ASC() : o.getRegistryNationalityPropertyName().orderBy().DESC();
					order = order == null ? aux : order.and(aux);
				}
			}
		}
		if(order == null) {
			order = o.getRegistryNamePropertyName().orderBy().ASC();
		}
		return order;
	}
}
