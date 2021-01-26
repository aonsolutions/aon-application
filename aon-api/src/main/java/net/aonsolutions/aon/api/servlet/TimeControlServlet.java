package net.aonsolutions.aon.api.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.Coordinates;
import com.esferalia.aon.occam.api.model.aonsolutions.Location;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControl;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlDetail;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlGroup;
import com.esferalia.aon.occam.api.model.aonsolutions.TimeControlStatus;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.toolkit.Toolkit;

@SuppressWarnings("serial")
@WebServlet(name = "AonTimeControlServlet", urlPatterns = {"/ms/api/timecontrol/*"})
public class TimeControlServlet extends HttpServlet{

	
	private static final Logger LOGGER  = Logger.getLogger(TimeControlServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API TIMECONTROL SERVLET - GET METHOD");
		String token = req.getHeader("session_id");
		
		Object responseObject = new JSONObject();
		JSONObject json = Utils.getParamsJSON(req);
		String domainName = req.getHeader("domain_name");
		
		Integer domainId = !"null".equalsIgnoreCase(req.getHeader("domain_id")) && AonNumberUtils.toInteger(req.getHeader("domain_id")) != null 
				? AonNumberUtils.toInteger(req.getHeader("domain_id")) : 0;
		Domain domain = AON.getDomain(domainName, domainId, "", f -> f.getNameProperty().eq(domainName));
		
		String[] pathInfo = req.getPathInfo()!= null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo().split("/") : null;
		try {
			AonToken aonToken = SECURITY.getAonToken(token);
			responseObject = routerGet(pathInfo, aonToken, json, domain); 
		}catch (Exception e) {
			e.printStackTrace();
		}
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, responseObject, new JSONObject());	
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("AON API TIMECONTROL SERVLET - POST METHOD");
		String token = req.getHeader("session_id");

		JSONObject json = Utils.getRequestJSON(req);

		AonToken aonToken = SECURITY.getAonToken(token);
		
		save(aonToken, json);
		Object responseObject = getTimeControl(aonToken); 
		
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, responseObject, new JSONObject());	
	}

	//router
	private Object routerGet(String[] pathInfo, AonToken aonToken, JSONObject json, Domain domain) throws SegSocialException, Exception {
		Object obj = new Object();
		String route = "default";
		if(pathInfo!=null) {
			route = pathInfo[1];
		}
		switch (route) {
			case "list":
				LOGGER.info("TIMECONTROL SERVLET - GET TIME-CONTROL-LIST");
				obj = getTimeControlList(aonToken, json);
				break;
			case "list-holder":
				LOGGER.info("TIMECONTROL SERVLET - GET-TASK-HOLDER-TIME-CONTROL");
				obj = getTaskHolderTimeControlStream(aonToken, json, domain);
			break;
			case "default":
				LOGGER.info("TIMECONTROL SERVLET - GET TIME-CONTROL");
				obj = getTimeControl(aonToken);
			break;
			default:
				break;
		}
		return obj;
	}
	private Object getTimeControl(AonToken aonToken) {
		TaskHolder taskHolder = AON_SOLUTIONS.getTaskHolder(aonToken);
		Date startDate = AonDateUtils.getDateWithoutTime(new Date());
		Date endDate = AonDateUtils.addDays(startDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);
		
		TimeControl tc = new TimeControl();
		if(taskHolder != null && taskHolder.getId() != null) {
			tc = AON_SOLUTIONS.getTaskHolderTimeControl(taskHolder.getDomain(), "", taskHolder.getId(), startDate, endDate);
		}
		return tc.toJSON();
	}
	
	private Object getTimeControlList(AonToken aonToken, JSONObject json) {
		TaskHolder taskHolder = AON_SOLUTIONS.getTaskHolder(aonToken);
		Date startDate = AonDateUtils.getDateWithoutTime(new Date());
		Date endDate = AonDateUtils.addDays(startDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);

		if(!json.optString("startDate").isEmpty()) startDate = Toolkit.parseDate(json.optString("startDate"), "yyyy-MM-dd");
		if(!json.optString("endDate").isEmpty()) endDate = Toolkit.parseDate(json.optString("endDate"), "yyyy-MM-dd");

		JSONArray array = new JSONArray();
		if(taskHolder != null && taskHolder.getId() != null) {
			AON_SOLUTIONS.getTimeControlStream(taskHolder.getDomain(), "", startDate, endDate)
			.forEach(tc -> {
				array.put(tc.toJSON());
			});
		}
		return array;
	}
	
	private Object getTaskHolderTimeControlStream(AonToken aonToken, JSONObject json, Domain domain) {
		TaskHolder taskHolder = AON_SOLUTIONS.getTaskHolder(aonToken);
		
		Date startDate = AonDateUtils.getDateWithoutTime(new Date());
		Date endDate = AonDateUtils.addDays(startDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);
		
		if(!json.optString("startDate").isEmpty()) startDate = Toolkit.parseDate(json.optString("startDate"), "yyyy-MM-dd");
		if(!json.optString("endDate").isEmpty()) endDate = Toolkit.parseDate(json.optString("endDate"), "yyyy-MM-dd");


		String group = json.optString("group");
		JSONArray array = new JSONArray();
		TimeControlGroup timeCG;

		switch (group) {
			case "WEEK":
				timeCG = TimeControlGroup.WEEK;
				break;
			case "MONTH":
				timeCG = TimeControlGroup.MONTH;
				break;
			case "YEAR":
				timeCG = TimeControlGroup.YEAR;
				break;
			default:
				timeCG = TimeControlGroup.DAY;
				break;
		}
		
		if(taskHolder != null && taskHolder.getId() != null) {
			AON_SOLUTIONS.getTaskHolderTimeControlStream(domain, "", Integer.parseInt(json.optString("taskHolderId")), startDate, endDate, timeCG)
			.forEach(tc -> {
				array.put(tc.toJSON());
			});
		}
		return array;
	}
	
	private void save(AonToken aonToken, JSONObject json) {
		TaskHolder taskHolder = AON_SOLUTIONS.getTaskHolder(aonToken);
		Coordinates coordinates = new Coordinates(json.optString("coordinates"));
		TimeControlDetail tcd = new TimeControlDetail()
				.setDomain(taskHolder.getDomain())
				.setTaskHolder(taskHolder)
				.setComments(json.optString("comments"))
				.setCoordinates(coordinates)
				.setDate(new Date())
				.setLocation(new Location())
				.setStatus(TimeControlStatus.safeValueOf(json.optString("status")));
		
		AON_SOLUTIONS.saveTimeControlDetail(tcd.getDomain(), "", tcd);
	}
	
}
