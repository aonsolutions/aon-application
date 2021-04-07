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

@SuppressWarnings("serial")
@WebServlet(name = "AonLocationServlet", urlPatterns = {"/ms/api/location/*"})
public class LocationServlet extends AonApiHttpServlet{

	
	private static final Logger LOGGER  = Logger.getLogger(LocationServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API LOCATION SERVLET - GET METHOD");
		try {
			super.doGet(req, resp);
			switch (getPath()) {
				case "/":
					response(req, resp, getLocationList());
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
			super.doPost(req, resp);
			switch (getPath()) {
				case "/":
					response(req, resp, saveLocation());
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
			super.doDelete(req, resp);
			switch (getPath()) {
				case "/":
					response(req, resp, deleteLocation());
					break;
				default:
					throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	private Object getLocationList() {
		JSONArray array = new JSONArray();
		AON_SOLUTIONS.getLocationStream(getDomain(), "", f -> f.getDomainProperty().eq(getDomain().getId()))
		.forEach(lc -> {
			array.put(lc.toJSON());
		});
		return array;
	}

	
	private JSONObject saveLocation() {
		Coordinates coordinates = new Coordinates(getData().optString("coordinates"));
		Location location = new Location()
				.setDomain(getDomain())
				.setDescription(getData().optString("description"))
				.setCoordinates(coordinates)
				.setRadio(getData().optInt("radio"))
				.setId(getData().optInt("id"));

		location = AON_SOLUTIONS.saveLocation(getDomain(), "", location);
		JSONObject respObject = location.toJSON();
		return respObject;
	}

	private JSONObject deleteLocation() {
		Location location = new Location().setId(getData().optInt("id"));
		AON_SOLUTIONS.deleteLocation(getDomain(), "", location);
		return new JSONObject();
	}
	
}
