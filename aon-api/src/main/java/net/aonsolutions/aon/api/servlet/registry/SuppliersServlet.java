package net.aonsolutions.aon.api.servlet.registry;
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
import com.esferalia.aon.occam.api.json.RegistryAddressJSON;
import com.esferalia.aon.occam.api.json.RegistryMediaJSON;
import com.esferalia.aon.occam.api.json.SupplierJSON;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.Properties.SupplierProperties;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Supplier;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiSuppliersServlet", urlPatterns = {"/ms/api/suppliers/*"})
public class SuppliersServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(SuppliersServlet.class.getName());
	
	public static final String SUPPLIERS = "/";
	public static final String SUPPLIER = "/:id";
	public static final String SUPPLIER_TRANSACTION = "/:id/transaction";
	public static final String SUPPLIERS_COUNT = "/count";
	
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
			
			Object object = new AonRouting(api)
				.addRoute(SUPPLIERS, SuppliersServlet::getSuppliers)
				.addRoute(SUPPLIERS_COUNT, SuppliersServlet::getSuppliersCount)
				.addRoute(SUPPLIER, SuppliersServlet::getSupplier)
				.addRoute(SUPPLIER_TRANSACTION, SuppliersServlet::getSupplierTransaction)
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
			
			Object object = new AonRouting(api)
				.addRoute(SUPPLIERS, SuppliersServlet::saveSupplier)
				.addRoute(SUPPLIER, SuppliersServlet::saveSupplier)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	
	private static JSONObject getSupplier(AonApiData api) {
		Integer id = api.getData().opt(IJsonNames.REGISTRY) != null 
				? api.getData().optInt(IJsonNames.REGISTRY)
				: api.getData().optInt(IJsonNames.ID);
		Supplier supplier = AON.getSupplier(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(id))
				.orElse(new Supplier());
		JSONObject object = SupplierJSON.toJSON(supplier);
		
		return RegistryServlet.getRegistryAdditionalInfo(object, api, api.getData(), id, null);
	}
	
	private static JSONObject getSupplierTransaction(AonApiData api) {
		Integer id = api.getData().opt(IJsonNames.REGISTRY) != null 
				? api.getData().optInt(IJsonNames.REGISTRY)
				: api.getData().optInt(IJsonNames.ID);
		
		Supplier supplier = AON.getSupplier(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(id))
				.orElse(new Supplier());

		JSONObject object = new JSONObject();
		if(supplier != null && !supplier.isEmpty() && supplier.getTransaction() != null) {
			object.put(IJsonNames.TRANSACTION, supplier.getTransaction().getTediName());
		}
		return object;
	}
	
	private static JSONArray getSuppliers(AonApiData api) {
		Integer page = api.getData().opt(IJsonNames.PAGE) != null 
			? api.getData().optInt(IJsonNames.PAGE) : 1;
		Integer perPage = api.getData().opt(IJsonNames.PER_PAGE) != null
			? api.getData().optInt(IJsonNames.PER_PAGE) : 50;
		
		JSONArray result = SupplierJSON.toJSON(AON.getSupplierStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
			f -> supplierFilter(api, api.getData(), f), perPage * (page -1), perPage));
		 if (api.getData().opt("additional_info") != null) {
		        LinkedList<RegistryAdditionalInfo> rais = new LinkedList<>();			
		        JSONArray additionalInfo = (JSONArray) api.getData().opt("additional_info");
		        for (int i = 0; i < additionalInfo.length(); i++) {
		            rais.add(RegistryAdditionalInfo.safeValueOf(additionalInfo.get(i).toString()));
		        }
		        for (int i = 0; i < result.length(); i++) {
		            JSONObject supplierObj = result.getJSONObject(i);
		            int id = supplierObj.getInt("id");
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
		            supplierObj.put("registryAddresses", RegistryAddressJSON.toJSON(registryAddresses));
		            supplierObj.put("media", RegistryMediaJSON.toJSON(registryMedia));
		        }
		    }
		return result;
	}
	
	private static long getSuppliersCount(AonApiData api) {
		return AON.getSuppliersCount(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
				 f -> supplierFilter(api,api.getData(), f));
	}
	
	private static Filter supplierFilter(AonApiData api, JSONObject json, SupplierProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		
		if(json.opt(IJsonNames.VALUE) != null) {
			String value = json.optString(IJsonNames.VALUE);
			Filter valueFilter = f.getNameProperty().like("%" + value + "%")
					.or(f.getDocumentProperty().like("%" + value + "%"));
			filter = filter.and(valueFilter);
		}
		return filter;
	}
	
	public static JSONObject saveSupplier(AonApiData api) {
		Supplier supplier = SupplierJSON.fromJSON(api.getData());
		supplier = AON.saveSupplier(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), supplier);
		RegistryServlet.saveRegistryAdditionalInfo(api, supplier.getId(), supplier.getDomain().getId());
		return SupplierJSON.toJSON(supplier);
	}
	
}
