package net.aonsolutions.aon.api.servlet.registry;
import java.util.LinkedList;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.RegistryJSON;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.RegistryProperties;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryType;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiRegistrySuggestionServlet", urlPatterns = {"/ms/api/suggestion/registry/*"})
public class RegistrySuggestionServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(RegistrySuggestionServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API REGISTRY SUGGESTION SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req);
		
			switch (api.getPath()) {
			case "/":
				response(req, resp, getRegistries(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}			
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	private JSONArray getRegistries(AonApiData api) {
		JSONArray array = new JSONArray();
		String types = api.getData().optString("types");
		String[] types2 = types.split(",");
		LinkedList<RegistryType> list = new LinkedList<>();
		if (types != null) {
			for (Integer i = 0; i < types2.length; i++) {
				RegistryType rt = RegistryType.safeValueOf(types2[i]);
				if(rt != null) list.add(rt);
			}
		}
		
		AON_SOLUTIONS.getSuggestionRegistries(api.getDomain(), api.getUser().getLogin(), list,
				f -> rfilter(api, f)).forEach(r -> array.put(registryToJSON(r, false)));
		if(array.length() <= 0) {
			AON_SOLUTIONS.getGlobalSuggestionRegistries(api.getDomain(), api.getUser().getLogin(), 
					f -> globalRfilter(api, f)).forEach(r -> array.put(registryToJSON(r, true)));
		}
 		return array;
	}
	
	private Filter rfilter(AonApiData api, RegistryProperties f) {
    	Filter filter =  f.getDomainProperty().eq(api.getDomain().getId());
 
    	String name = api.getData().optString("name");
		String document = api.getData().optString("document");
		
		if(!AonStringUtils.isBlank(document)) {
			filter = filter.and(f.getDocumentProperty().like("%" + document + "%"));
		} else if(!AonStringUtils.isBlank(name)) {
			filter = filter.and(f.getNameProperty().like("%" + name + "%"));
		}
		return filter;
	}
	
	private Filter globalRfilter(AonApiData api, RegistryProperties f) {
    	Filter filter =  f.getDomainProperty().eq(0);
 
    	String name = api.getData().optString("name");
		String document = api.getData().optString("document");
		
		if(!AonStringUtils.isBlank(document)) {
			filter = filter.and(f.getDocumentProperty().like("%" + document + "%"));
		} else if(!AonStringUtils.isBlank(name)) {
			filter = filter.and(f.getNameProperty().like("%" + name + "%"));
		}
		return filter;
	}
	
	
	private JSONObject registryToJSON(Registry reg, Boolean global) {
		return RegistryJSON.toJSON(reg).put("global", global);
	}
}
