package net.aonsolutions.aon.api.servlet;

import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.ItemProperties;
import com.esferalia.aon.occam.api.model.Properties.ProductProperties;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiProductServlet", urlPatterns = {"/ms/api/product/*"})
public class ProductServlet extends AonApiHttpServlet {

	private static final Logger LOGGER  = Logger.getLogger(ProductServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API PRODUCT SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req, resp);
		
			switch (api.getPath()) {
			case "/":
				response(req, resp, getProducts(api));
				break;
			case "/item":
				response(req, resp, getItems(api));
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}	
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API PRODUCT SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req, resp);
		
			switch (api.getPath()) {
			case "/":
//				response(req, resp, getResponseObject());
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}	
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API PRODUCT SERVLET - PUT METHOD");
		try {
			AonApiData api = initialize(req, resp);
		
			switch (api.getPath()) {
			case "/":
//				response(req, resp, getResponseObject());
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}	
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API PRODUCT SERVLET - DELETE METHOD");
		try {
			AonApiData api = initialize(req, resp);
		
			switch (api.getPath()) {
			case "/":
//				response(req, resp, getResponseObject());
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}	
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONArray getProducts(AonApiData api) {
		return AON_SOLUTIONS.getProducts(api.getDomain(), api.getUser(), f -> 
				productFilter(api, f));
	}
	
	private JSONArray getItems(AonApiData api) {
		return AON_SOLUTIONS.getItems(api.getDomain(), api.getUser(), f -> 
			itemFilter(api, f));
	}
	
	private Filter productFilter(AonApiData api, ProductProperties f) {
		Filter filter = null; // = f.getDomainProperty().eq(api.getDomain().getId());
		
		if(!AonStringUtils.isBlank(api.getParams().optString(IJsonNames.VALUE))) {
			String value = api.getParams().optString(IJsonNames.VALUE);
			Filter valueFilter = f.getCodeProperty().like("%" + value + "%")
					.or(f.getNameProperty().like("%" + value + "%"));
			filter = valueFilter;
		}
		
		return filter;
	}
	
	private Filter itemFilter(AonApiData api, ItemProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		
		if(!AonStringUtils.isBlank(api.getParams().optString(IJsonNames.VALUE))) {
			String value = api.getParams().optString(IJsonNames.VALUE);
			filter = filter.and(
					f.getDescriptionProperty().like("%" + value + "%")
					.or(f.getProductCodeProperty().like("%" + value + "%"))
					.or(f.getProductNameProperty().like("%" + value + "%"))
			);
		}
		
		return filter;
	}
}
