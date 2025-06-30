package net.aonsolutions.aon.api.servlet.registry;
import java.util.logging.Logger;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.RegistryNoteJSON;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiRegistryNotesServlet", urlPatterns = {"/ms/api/registryNotes/*"})
public class RegistryNotesServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(RegistryNotesServlet.class.getName());
	
	public static final String REGISTRY_NOTES = "/";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(REGISTRY_NOTES, RegistryNotesServlet::getRegistryNotes)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONArray getRegistryNotes(AonApiData api) {
		Integer registryId = api.getData().opt("registry") != null ? api.getData().optInt("registry") : null;
		String source = api.getData().opt("source") != null ? api.getData().optString("source") : null;
		
		if(AonStringUtils.isBlank(source))
			return RegistryNoteJSON.toJSON(
					AON.getRegistryNoteList(
							api.getDomain().getName(), 
							api.getDomain().getId(), 
							api.getUser().getLogin(), 
							f -> f.getRegistryProperty().eq(registryId)
					)
			);
		else
			return RegistryNoteJSON.toJSON(
					AON.getRegistryNoteList(
							api.getDomain().getName(), 
							api.getDomain().getId(), 
							api.getUser().getLogin(), 
							f -> f.getRegistryProperty().eq(registryId).and(f.getNoteTypeProperty().eq(NoteType.valueOf(source).value()))
					)
			);
	}
	
}
