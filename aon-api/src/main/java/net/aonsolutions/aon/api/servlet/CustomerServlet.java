package net.aonsolutions.aon.api.servlet;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;

import net.aonsolutions.aon.api.json.AonRegistryJSON;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiCustomeServlet", urlPatterns = {"/ms/api/customer/*"})
public class CustomerServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(CustomerServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API CUSTOMER SERVLET - GET METHOD");
		try {
			super.doGet(req, resp);
		
			switch (getPath()) {
			case "/":
				response(req, resp, getCustomers());
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
		LOGGER.info("AON API CUSTOMER SERVLET - POST METHOD");
		try {
			super.doPost(req, resp);
			switch (getPath()) {
			case "/":
				response(req, resp, getResponseObject());
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
			
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONArray getCustomers() {
		JSONArray array = new JSONArray();
		AON.getCustomerStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(), 
			f -> f.getDomainProperty().eq(getDomain().getId())).forEach(c -> {
				array.put(AonRegistryJSON.toJSON(c));
			});
		return array;
	}
	
	private JSONObject getResponseObject() {
		return new JSONObject();
	}
}
