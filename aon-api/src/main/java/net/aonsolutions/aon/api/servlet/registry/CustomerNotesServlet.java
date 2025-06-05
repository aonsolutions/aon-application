package net.aonsolutions.aon.api.servlet.registry;
import java.util.logging.Logger;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.RegistryNoteJSON;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiCustomerNotessServlet", urlPatterns = {"/ms/api/customersNotes/*"})
public class CustomerNotesServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(CustomerNotesServlet.class.getName());
	
	public static final String CUSTOMER_NOTES = "/";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(CUSTOMER_NOTES, CustomerNotesServlet::getCustomerNotes)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONArray getCustomerNotes(AonApiData api) {
		Integer customerId = api.getData().opt("customer") != null ? api.getData().optInt("customer") : null;
		
		return RegistryNoteJSON.toJSON(
				AON.getRegistryNoteList(
						api.getDomain().getName(), 
						api.getDomain().getId(), 
						api.getUser().getLogin(), 
						f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getRegistryProperty().eq(customerId))
				)
		);
	}
	
}
