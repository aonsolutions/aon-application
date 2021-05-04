package net.aonsolutions.aon.api.servlet;

import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;

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
		String value = api.getParams().optString("value");
		return AON_SOLUTIONS.getProducts(api.getDomain(), api.getUser(), f -> 
				f.getDomainProperty().eq(api.getDomain().getId()));
	}
	
	private JSONArray getItems(AonApiData api) {
		String value = api.getParams().optString("value");
		return AON_SOLUTIONS.getItems(api.getDomain(), api.getUser(), f -> 
			f.getDomainProperty().eq(api.getDomain().getId())
			.and(
				f.getDescriptionProperty().like("%" + value + "%")
				.or(f.getProductCodeProperty().like("%" + value + "%"))
				.or(f.getProductNameProperty().like("%" + value + "%"))
			));
	}
	
}
