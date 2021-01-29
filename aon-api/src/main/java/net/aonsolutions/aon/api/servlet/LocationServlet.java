package net.aonsolutions.aon.api.servlet;

import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.Coordinates;
import com.esferalia.aon.occam.api.model.aonsolutions.Location;
import com.esferalia.aon.occam.api.model.task.TaskHolder;

@SuppressWarnings("serial")
@WebServlet(name = "AonLocationServlet", urlPatterns = {"/ms/api/location/*"})
public class LocationServlet extends AonApiHttpServlet{

	
	private static final Logger LOGGER  = Logger.getLogger(LocationServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API TIMECONTROL SERVLET - GET METHOD");
		try {
			super.doGet(req, resp);
		
			Object responseObject = new JSONObject();		
			String[] pathInfo = req.getPathInfo()!= null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo().split("/") : null;
			
			AonToken aonToken = SECURITY.getAonToken(getToken());
			responseObject = router(pathInfo, aonToken); 
		
			response(req, resp, responseObject);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		LOGGER.info("AON API TIMECONTROL SERVLET - POST METHOD");
		try {
			super.doPost(req, resp);
			String[] pathInfo = req.getPathInfo()!= null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo().split("/") : null;
			AonToken aonToken = SECURITY.getAonToken(getToken());
		
			Object responseObject = router(pathInfo, aonToken); 
			
			response(req, resp, responseObject);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	//router
	private Object router(String[] pathInfo, AonToken aonToken) throws Exception {
		Object obj = new Object();
		String route = null;
		if(pathInfo!=null) {
			route = pathInfo[1];
		}
		switch (route) {
			case "list":
				LOGGER.info("TIMECONTROL SERVLET - LOCATION-LIST");
				obj = getLocationList(aonToken, getParams());
				break;
			case "save":
				LOGGER.info("TIMECONTROL SERVLET - LOCATION-SAVE");
				obj = saveLocation(aonToken, getParams());
			break;
			case "delete":
				LOGGER.info("TIMECONTROL SERVLET - LOCATION-DELETE");
				obj = deleteLocation(aonToken);
			break;
			default:
				break;
		}
		return obj;
	}
	
	private Object getLocationList(AonToken aonToken, JSONObject json) {
		JSONArray array = new JSONArray();
		AON_SOLUTIONS.getLocationStream(getDomain(), "", f -> f.getDomainProperty().eq(getDomain().getId()))
		.forEach(lc -> {
			array.put(lc.toJSON());
		});
		return array;
	}

	
	private Object saveLocation(AonToken aonToken, JSONObject json) {
		TaskHolder taskHolder = AON_SOLUTIONS.getTaskHolder(aonToken);
		Coordinates coordinates = new Coordinates(getData().optString("coordinates"));
		Location location = new Location()
				.setDomain(taskHolder.getDomain())
				.setDescription(getData().optString("description"))
				.setCoordinates(coordinates)
				.setRadio(getData().optInt("radio"))
				.setId(getData().optInt("id"))
				;

		location = AON_SOLUTIONS.saveLocation(getDomain(), "", location);
		JSONObject respObject = location.toJSON();
		return respObject;
	}

	private Object deleteLocation(AonToken aonToken) {
		JSONArray respObject = new JSONArray();
		System.out.println("deleteLocation");
		return respObject;
	}
	
}
