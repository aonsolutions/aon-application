package net.aonsolutions.aon.api.servlet;

import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.aonsolutions.Coordinates;
import com.esferalia.aon.occam.api.model.aonsolutions.Location;

import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonLocationServlet", urlPatterns = {"/ms/api/location/*"})
public class LocationServlet extends AonApiHttpServlet{

	
	private static final Logger LOGGER  = Logger.getLogger(LocationServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API LOCATION SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
				case "/":
					response(req, resp, getLocationList(api));
					break;
				default:
					throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		LOGGER.info("AON API LOCATION SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
				case "/":
					response(req, resp, saveLocation(api));
					break;
				default:
					throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp){
		LOGGER.info("AON API LOCATION SERVLET - DELETE METHOD");
		try {
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
				case "/":
					response(req, resp, deleteLocation(api));
					break;
				default:
					throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	private Object getLocationList(AonApiData api) {
		JSONArray array = new JSONArray();
		AON_SOLUTIONS.getLocationStream(api.getDomain(), "", f -> f.getDomainProperty().eq(api.getDomain().getId()))
		.forEach(lc -> {
			array.put(lc.toJSON());
		});
		return array;
	}

	
	private JSONObject saveLocation(AonApiData api) {
		Coordinates coordinates = new Coordinates(api.getData().optString("coordinates"));
		Location location = new Location()
				.setDomain(api.getDomain())
				.setDescription(api.getData().optString("description"))
				.setCoordinates(coordinates)
				.setRadio(api.getData().optInt("radio"))
				.setId(api.getData().optInt("id"));

		location = AON_SOLUTIONS.saveLocation(api.getDomain(), "", location);
		JSONObject respObject = location.toJSON();
		return respObject;
	}

	private JSONObject deleteLocation(AonApiData api) {
		Location location = new Location().setId(api.getData().optInt("id"));
		AON_SOLUTIONS.deleteLocation(api.getDomain(), "", location);
		return new JSONObject();
	}
	
}
