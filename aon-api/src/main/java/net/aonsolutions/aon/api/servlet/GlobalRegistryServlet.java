package net.aonsolutions.aon.api.servlet;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.RegistryJSON;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.RegistryProperties;

@SuppressWarnings("serial")
@WebServlet(name = "GlobalRegistryServlet", urlPatterns = {"/ms/api/global/registry/*"})
public class GlobalRegistryServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(GlobalRegistryServlet.class.getName());
	
	private static final String DOMAIN_NAME = "global.aonsolutions.net";
	private static final Integer DOMAIN_ID = 0;
	private static final String USER = "";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API GLOBAL REGISTRY SERVLET - GET METHOD");
		Object object = new JSONObject();
		object = getRegistries(req);
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, object, new JSONObject());
	}
	
	public JSONArray getRegistries(HttpServletRequest req) {
		JSONArray array = new JSONArray();
		AON.getAonRegistryStream(DOMAIN_NAME, DOMAIN_ID, USER, f -> registryFilter(f, req))
		.forEach(r -> {
			array.put(RegistryJSON.toJSON(r));
		});
		return array;
	}
	
	public static Filter registryFilter(RegistryProperties f, HttpServletRequest req) {
    	Filter filter =  f.getDomainProperty().eq(DOMAIN_ID);
    	
    	String document = req.getParameter("document");
    	if(document != null) {
    		filter = filter.and(f.getDocumentProperty().like(document + "%"));
    	} 
    	
    	String name = req.getParameter("name");
    	if(name != null) {
    		filter = filter.and(f.getNameProperty().like("%" + name + "%"));    		
    	} 
    	
    	String value = req.getParameter("value");
    	if(value != null) {
    		filter = filter.and(
    				f.getDocumentProperty().like("%" + value + "%")
    				.or(f.getNameProperty().like("%" + value + "%")));
    	}
    
    	filter.page(1);
    	filter.perPage(20);
    	
		return filter;
    }

	
}
