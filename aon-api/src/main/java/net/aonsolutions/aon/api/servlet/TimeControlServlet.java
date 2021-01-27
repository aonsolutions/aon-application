package net.aonsolutions.aon.api.servlet;

import java.util.Date;
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
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControl;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlDetail;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlGroup;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlStatus;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.watson.server.AonDateUtils;

import solutions.aon.seg.social.toolkit.Toolkit;

@SuppressWarnings("serial")
@WebServlet(name = "AonTimeControlServlet", urlPatterns = {"/ms/api/timecontrol/*"})
public class TimeControlServlet extends AonApiHttpServlet{

	
	private static final Logger LOGGER  = Logger.getLogger(TimeControlServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API TIMECONTROL SERVLET - GET METHOD");
		
		try {
			super.doGet(req, resp);
		
			Object responseObject = new JSONObject();		
			String[] pathInfo = req.getPathInfo()!= null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo().split("/") : null;
			try {
				AonToken aonToken = SECURITY.getAonToken(getToken());
				responseObject = routerGet(pathInfo, aonToken); 
			}catch (Exception e) {
				e.printStackTrace();
			}
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
		
			AonToken aonToken = SECURITY.getAonToken(getToken());
			save(aonToken);
			Object responseObject = getTimeControl(aonToken); 
		
			response(req, resp, responseObject);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	//router
	private Object routerGet(String[] pathInfo, AonToken aonToken) throws Exception {
		Object obj = new Object();
		String route = "default";
		if(pathInfo!=null) {
			route = pathInfo[1];
		}
		switch (route) {
			case "list":
				LOGGER.info("TIMECONTROL SERVLET - GET TIME-CONTROL-LIST");
				obj = getTimeControlList(aonToken, getParams());
				break;
			case "list-holder":
				LOGGER.info("TIMECONTROL SERVLET - GET-TASK-HOLDER-TIME-CONTROL");
				obj = getTaskHolderTimeControlStream(aonToken, getParams());
				break;
			default:
				LOGGER.info("TIMECONTROL SERVLET - GET TIME-CONTROL");
				obj = getTimeControl(aonToken);
				break;
		}
		return obj;
	}
	
	private Object getTimeControl(AonToken aonToken) throws Exception{
		TaskHolder taskHolder = AON_SOLUTIONS.getTaskHolder(aonToken);
		Date startDate = AonDateUtils.getDateWithoutTime(new Date());
		Date endDate = AonDateUtils.addDays(startDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);
		
		TimeControl tc = new TimeControl();
		if(taskHolder != null && taskHolder.getId() != null) {
			tc = AON_SOLUTIONS.getTaskHolderTimeControl(taskHolder.getDomain(), "", taskHolder.getId(), startDate, endDate);
		} else {
			throw new Exception("No existe Task Holder asociado al usuario.");
		}
		return tc.toJSON();
	}
	
	private Object getTimeControlList(AonToken aonToken, JSONObject json) {
		Date startDate = AonDateUtils.getDateWithoutTime(new Date());
		Date endDate = AonDateUtils.addDays(startDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);

		if(!json.optString("startDate").isEmpty()) startDate = Toolkit.parseDate(json.optString("startDate"), "yyyy-MM-dd");
		if(!json.optString("endDate").isEmpty()) endDate = Toolkit.parseDate(json.optString("endDate"), "yyyy-MM-dd");

		JSONArray array = new JSONArray();
		AON_SOLUTIONS.getTimeControlStream(getDomain(), "", startDate, endDate)
		.forEach(tc -> {
			array.put(tc.toJSON());
		});
		
		return array;
	}
	
	private Object getTaskHolderTimeControlStream(AonToken aonToken, JSONObject json) {		
		Date startDate = AonDateUtils.getDateWithoutTime(new Date());
		Date endDate = AonDateUtils.addDays(startDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);
		
		if(!json.optString("startDate").isEmpty()) startDate = Toolkit.parseDate(json.optString("startDate"), "yyyy-MM-dd");
		if(!json.optString("endDate").isEmpty()) endDate = Toolkit.parseDate(json.optString("endDate"), "yyyy-MM-dd");

		JSONArray array = new JSONArray();
		TimeControlGroup timeCG = TimeControlGroup.safeValueOf(json.optString("group"));
		AON_SOLUTIONS.getTaskHolderTimeControlStream(getDomain(), "", Integer.parseInt(json.optString("taskHolderId")), startDate, endDate, timeCG)
		.forEach(tc -> {
			array.put(tc.toJSON());
		});
		
		return array;
	}
	
	private void save(AonToken aonToken) {
		TaskHolder taskHolder = AON_SOLUTIONS.getTaskHolder(aonToken);
		Coordinates coordinates = new Coordinates(getData().optString("coordinates"));
		TimeControlDetail tcd = new TimeControlDetail()
				.setDomain(taskHolder.getDomain())
				.setTaskHolder(taskHolder)
				.setComments(getData().optString("comments"))
				.setCoordinates(coordinates)
				.setDate(new Date())
				.setLocation(new Location())
				.setStatus(TimeControlStatus.safeValueOf(getData().optString("status")));
		
		AON_SOLUTIONS.saveTimeControlDetail(tcd.getDomain(), "", tcd);
	}
	
}
