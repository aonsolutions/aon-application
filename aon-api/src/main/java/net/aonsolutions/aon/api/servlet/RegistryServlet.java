package net.aonsolutions.aon.api.servlet;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.RegistryAddressJSON;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;

import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiRegistryServlet", urlPatterns = {"/ms/api/registry/*"})
public class RegistryServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(RegistryServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API REGISTRY SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
			case "/":
				response(req, resp, getResponseObject());
				break;
			case "/address":
				response(req, resp, getRegistryAddress(api));
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
		LOGGER.info("EXAMPLE SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
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
	
	private JSONObject getResponseObject() {
		return new JSONObject();
	}
	
	private JSONObject getRegistryAddress(AonApiData api) {
		RegistryAddress address = AON.getMain(api.getDomain(), api.getUser(), api.getParams().optInt(IJsonNames.REGISTRY));
		return RegistryAddressJSON.toJSON(address);
	}
}
