package net.aonsolutions.aon.api.servlet;

import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
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
		LOGGER.info("AON API LOCATION SERVLET - POST METHOD");
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
				LOGGER.info("LOCATION SERVLET - LOCATION-LIST");
				obj = getLocationList(aonToken);
				break;
			case "save":
				LOGGER.info("LOCATION SERVLET - LOCATION-SAVE");
				obj = saveLocation(aonToken);
			break;
			case "delete":
				LOGGER.info("LOCATION SERVLET - LOCATION-DELETE");
				obj = deleteLocation(aonToken);
			break;
			case "notification-test":
				LOGGER.info("LOCATION SERVLET - NOTIFICATION TEST");
				obj = notificationTest();
			break;
			default:
				break;
		}
		return obj;
	}
	
	private Object getLocationList(AonToken aonToken) {
		JSONArray array = new JSONArray();
		AON_SOLUTIONS.getLocationStream(getDomain(), "", f -> f.getDomainProperty().eq(getDomain().getId()))
		.forEach(lc -> {
			array.put(lc.toJSON());
		});
		return array;
	}

	
	private JSONObject saveLocation(AonToken aonToken) {
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

	private JSONObject deleteLocation(AonToken aonToken) {
		Location location = new Location().setId(getData().optInt("id"));
		AON_SOLUTIONS.deleteLocation(getDomain(), "", location);
		return new JSONObject();
	}
	

	public JSONObject notificationTest() {
		String urlFB = "https://fcm.googleapis.com/fcm/send";
		String keyFB = "AAAAQ_8KqDo:APA91bFXY2DUz7Ie9TM1qK9hO8RJ_8um9uKkIvT87QcyPobWunCFOvJpP4k961zzfJdGW0sUFWQUGGUMwsa9AOGsLtT0jTI_5sHl95MIgbBQBPDf6vbuOEQU16LQh84lVm1Jh2kNMl3G";
		JSONObject responseJSON =  new JSONObject();
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		try {
			String tokenUserFCM = getParams().optString("tokenFCM");
		    HttpPost httpPost = new HttpPost(urlFB);
			httpPost.addHeader("Authorization", "key="+keyFB);
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.addHeader("Accept", "*/*");
			
		    JSONObject payload = new JSONObject();
		    JSONObject notification = new JSONObject();
		    notification.put("body", "BODYYYYYYYY");
		    notification.put("title", "TITULOOOO");
		    payload.put("to", tokenUserFCM);
		    payload.put("notification", notification);
		    JSONObject dataJSON = new JSONObject(); //DATA 
		    payload.put("data", dataJSON);
		    
			StringEntity params = new StringEntity(payload.toString());
		    httpPost.setEntity(params);
	
		    CloseableHttpResponse response = httpClient.execute(httpPost);

		    HttpEntity responseEntity = response.getEntity();
		    if(responseEntity!=null) {
		        String responseString = EntityUtils.toString(responseEntity);
		        responseJSON = new JSONObject(responseString);
		    }
		    httpClient.close();
		} catch (Exception e) {
			 e.printStackTrace();
		}
		return responseJSON;
	}
	
}
