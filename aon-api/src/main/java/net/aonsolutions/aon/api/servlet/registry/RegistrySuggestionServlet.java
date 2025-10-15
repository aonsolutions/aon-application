package net.aonsolutions.aon.api.servlet.registry;
import java.util.LinkedList;
import java.util.Locale;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.RegistryJSON;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.RegistryProperties;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryType;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
		String[] types2 = types
				.replace("[", "")
				.replace("]", "")
				.replace("\"", "")
				.split(",");
		LinkedList<RegistryType> list = new LinkedList<>();
		if(AonStringUtils.isNotBlank(types)) {
			for (Integer i = 0; i < types2.length; i++) {
				RegistryType rt = RegistryType.safeValueOf(types2[i]);
				if(rt != null) list.add(rt);
			}
		}
		
		AON_SOLUTIONS.getSuggestionRegistries(api.getDomain(), api.getUser().getLogin(), list,
				f -> rfilter(api, f, false)).forEach(r -> array.put(registryToJSON(r, false)));
		if(array.length() <= 0) {
			AON_SOLUTIONS.getGlobalSuggestionRegistries(api.getDomain(), api.getUser().getLogin(), 
					f -> rfilter(api, f, true)).forEach(r -> array.put(registryToJSON(r, true)));
		}
 		return array;
	}
	
	private Filter rfilter(AonApiData api, RegistryProperties f, boolean global) {
    	Filter filter =  f.getDomainProperty().eq(global ? 0 : api.getDomain().getId());
 
    	String name = JsonUtils.getString(api.getData(), IJsonNames.NAME);
		String document = JsonUtils.getString(api.getData(), IJsonNames.DOCUMENT);
		
		if(!AonStringUtils.isBlank(document)) {
			filter = filter.and(f.getDocumentProperty().match(toBooleanMode(document)));
		} else if(!AonStringUtils.isBlank(name)) {
			filter = filter.and(f.getNameProperty().match(toBooleanMode(name)));
		}
		return filter;
	}
	
	private JSONObject registryToJSON(Registry reg, boolean global) {
		JSONObject registryJSON = RegistryJSON.toJSON(reg).put(IJsonNames.GLOBAL, global);
		if(global) registryJSON.remove(IJsonNames.ID);
		return registryJSON;
	}

	public static String toBooleanMode(String str) {
        StringBuilder buffer = new StringBuilder();
        // Separa en lo que no sea letra o número
        for (String word : str.split("[^\\p{L}\\p{N}]+")) {
            if (!word.isEmpty()) {
                // Normalizar solo a minúsculas, sin quitar acentos ni ñ
                String clean = word.toLowerCase(Locale.ROOT);
                buffer.append("+").append(clean).append("* ");
            }
        }
        return buffer.toString().trim();
    }	
}
