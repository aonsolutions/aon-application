package net.aonsolutions.aon.api.servlet;

import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
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
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

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

			responseObject = routerGet(pathInfo); 
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
			Object responseObject = null;
			if(AonStringUtils.isEmpty(getToken())) {
				save(getDomain(), getUser());
				responseObject = getTimeControl(getDomain(), getUser());
			} else {
				AonToken aonToken = SECURITY.getAonToken(getToken());
				save(aonToken);
				responseObject = getTimeControl(aonToken);
			}
			
			response(req, resp, responseObject);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	//router
	private Object routerGet(String[] pathInfo) throws Exception {
		Object obj = new Object();
		String route = "default" ;
		if(pathInfo!=null) {
			route = pathInfo[1];
		}
		switch (route) {
			case "list":
				LOGGER.info("TIMECONTROL SERVLET - GET TIME-CONTROL-LIST");
				obj = getTimeControlList();
			break;
			case "list-holder":
				LOGGER.info("TIMECONTROL SERVLET - GET-TASK-HOLDER-TIME-CONTROL");
				obj = getTaskHolderTimeControlStream();
			break;
			default:
				LOGGER.info("TIMECONTROL SERVLET - GET TIME-CONTROL");
				if(AonStringUtils.isEmpty(getToken())) {
					obj = getTimeControl(getDomain(), getUser());
				} else {
					AonToken aonToken = SECURITY.getAonToken(getToken());
					obj = getTimeControl(aonToken);
				}
			break;
		}
		return obj;
	}
	
	private Object getTimeControl(Domain domain, User user) throws Exception {
		TaskHolder taskHolder = AON.getTaskHolder(domain.getName(), domain.getId(), user.getLogin(), f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getUserIdProperty().eq(user.getId())));
		return getTimeControl(taskHolder);
	}
	
	private Object getTimeControl(AonToken aonToken) throws Exception{
		TaskHolder taskHolder = AON_SOLUTIONS.getTaskHolder(aonToken);
		if(taskHolder == null || taskHolder.getId() == null) {
			taskHolder = AON.getTaskHolder(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> 
				f.getDomainProperty().eq(getDomain().getId())
				.and(f.getUserIdProperty().eq(getUser().getId())));
		}
		return getTimeControl(taskHolder);
	}
	
	private Object getTimeControl(TaskHolder taskHolder) throws Exception{
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
	
	private Object getTimeControlList() {
		Date startDate = AonDateUtils.getDateWithoutTime(new Date());
		Date endDate = AonDateUtils.addDays(startDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);

		if(!getParams().optString("startDate").isEmpty()) startDate = Toolkit.parseDate(getParams().optString("startDate"), "yyyy-MM-dd");
		if(!getParams().optString("endDate").isEmpty()) endDate = Toolkit.parseDate(getParams().optString("endDate"), "yyyy-MM-dd");

		JSONArray array = new JSONArray();
		AON_SOLUTIONS.getTimeControlStream(getDomain(), "", startDate, endDate)
		.forEach(tc -> {
			array.put(tc.toJSON());
		});
		
		return array;
	}
	
	private Object getTaskHolderTimeControlStream() {		
		Date startDate = AonDateUtils.getDateWithoutTime(new Date());
		Date endDate = AonDateUtils.addDays(startDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);
		
		if(!getParams().optString("startDate").isEmpty()) startDate = Toolkit.parseDate(getParams().optString("startDate"), "yyyy-MM-dd");
		if(!getParams().optString("endDate").isEmpty()) endDate = Toolkit.parseDate(getParams().optString("endDate"), "yyyy-MM-dd");

		JSONArray array = new JSONArray();
		TimeControlGroup timeCG = TimeControlGroup.safeValueOf(getParams().optString("group"));
		AON_SOLUTIONS.getTaskHolderTimeControlStream(getDomain(), "", getParams().optInt("taskHolderId"), startDate, endDate, timeCG)
		.forEach(tc -> {
			array.put(tc.toJSON());
		});
		
		return array;
	}
	
	private void save(Domain domain, User user) {
		TaskHolder taskHolder = AON.getTaskHolder(domain.getName(), domain.getId(), user.getLogin(), f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getUserIdProperty().eq(user.getId())));
		save(taskHolder);
	}
	
	private void save(AonToken aonToken) {
	
	    TaskHolder taskHolder = getData().opt("taskHolderId")!=null 
	    		? AON.getTaskHolder(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f-> f.getIdProperty().eq(getData().optInt("taskHolderId"))) 
	    		: AON_SOLUTIONS.getTaskHolder(aonToken);
	
		save(taskHolder);
	}
	
	private void save(TaskHolder taskHolder) {
		Coordinates coordinates = new Coordinates(getData().optString("coordinates"));
		Date date = !getData().optString("date").isEmpty() ?  new Date(getData().optLong("date")) : new Date();
		Location lc =  !getData().optString("location").isEmpty() 
				? AON_SOLUTIONS.getLocation(taskHolder.getDomain(), "",  f -> f.getIdProperty().ge(getData().optInt("location")) )
				: AON_SOLUTIONS.getLocation(taskHolder.getDomain(), "",  coordinates);
		TimeControlDetail tcd = new TimeControlDetail()
				.setDomain(taskHolder.getDomain())
				.setTaskHolder(taskHolder)
				.setComments(getData().optString("comments"))
				.setCoordinates(coordinates)
				.setDate(date)
				.setLocation(lc)
				.setStatus(TimeControlStatus.safeValueOf(getData().optString("status")));
		
		AON_SOLUTIONS.saveTimeControlDetail(tcd.getDomain(), "", tcd);
	}
	
}
