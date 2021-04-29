package net.aonsolutions.aon.api.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
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
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.excel.TimeControlExcel;
import solutions.aon.seg.social.toolkit.Toolkit;

@SuppressWarnings("serial")
@WebServlet(name = "AonTimeControlServlet", urlPatterns = {"/ms/api/timecontrol/*"})
public class TimeControlServlet extends AonApiHttpServlet{

	private static final Logger LOGGER  = Logger.getLogger(TimeControlServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API TIMECONTROL SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
			case "/":
				response(req, resp, getTimeControl(api));
				break;
			case "/list":
				response(req, resp, getTimeControlList(api));
				break;
			case "/list-holder":
				response(req, resp, getTaskHolderTimeControlStream(api));
				break;
			case "/list-holder-detail":
				response(req, resp, getTimeControlDetailStream(api));
				break;
			case "/excel":
				responseFile(req, resp, getTimeControlExcel(req, api), MimeType.MS_EXCEL);
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
		LOGGER.info("AON API TIMECONTROL SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req, resp);

			switch (api.getPath()) {
			case "/":
				if(AonStringUtils.isEmpty(api.getToken())) {
					save(api, api.getDomain(), api.getUser());
					response ( req, resp, getTimeControl(api.getDomain(), api.getUser()));
				} else {
					AonToken aonToken = SECURITY.getAonToken(api.getToken());
					save(api, aonToken);
					response ( req, resp, getTimeControl(api, aonToken, api.getData().optInt("task_holder")) );
				}
				break;
			case "/save":
				if(AonStringUtils.isEmpty(api.getToken())) {
					response ( req, resp, save(api, api.getDomain(), api.getUser()));
				} else {
					AonToken aonToken = SECURITY.getAonToken(api.getToken());
					response ( req, resp, save(api, aonToken) );
				}
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
		LOGGER.info("AON API TIME-CONTROL SERVLET - DELETE METHOD");
		try {
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
			case "/":
				response(req, resp, delete(api));
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private Object getTimeControl(AonApiData api) throws Exception {
		if(AonStringUtils.isEmpty(api.getToken())) {
			return getTimeControl(api.getDomain(), api.getUser());
		} else {
			AonToken aonToken = SECURITY.getAonToken(api.getToken());
			return getTimeControl(api, aonToken, api.getParams().optInt("task_holder"));
		}
	}

	private Object getTimeControl(Domain domain, User user) throws Exception {
		TaskHolder taskHolder = AON.getTaskHolder(domain.getName(), domain.getId(), user.getLogin(), f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getUserIdProperty().eq(user.getId())));
		return getTimeControl(taskHolder);
	}
	
	private Object getTimeControl(AonApiData api, AonToken aonToken, Integer taskHolderId) throws Exception{
		LinkedList<TaskHolder> taskHolders = AON_SOLUTIONS.getTaskHolders(aonToken);
		
		TaskHolder taskHolder = taskHolders.stream().filter(th -> th.getId().equals(taskHolderId)).findFirst()
				.orElse(taskHolders.size() > 0 ? taskHolders.getFirst(): new TaskHolder());
		
		if(taskHolder == null || taskHolder.getId() == null) {
			taskHolder = AON.getTaskHolder(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
				f.getDomainProperty().eq(api.getDomain().getId())
				.and(f.getUserIdProperty().eq(api.getUser().getId())));
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
	
	private Object getTimeControlList(AonApiData api) {
		Date startDate = AonDateUtils.getDateWithoutTime(new Date());
		Date endDate = AonDateUtils.addDays(startDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);

		if(!api.getParams().optString("startDate").isEmpty()) startDate = Toolkit.parseDate(api.getParams().optString("startDate"), "yyyy-MM-dd");
		if(!api.getParams().optString("endDate").isEmpty()) endDate = Toolkit.parseDate(api.getParams().optString("endDate"), "yyyy-MM-dd");

		JSONArray array = new JSONArray();
		AON_SOLUTIONS.getTimeControlStream(api.getDomain(), "", startDate, endDate)
		.forEach(tc -> {
			array.put(tc.toJSON());
		});
		
		return array;
	}
	
	private Object getTaskHolderTimeControlStream(AonApiData api) {		
		Date startDate = AonDateUtils.getDateWithoutTime(new Date());
		Date endDate = AonDateUtils.addDays(startDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);
		
		if(!api.getParams().optString("startDate").isEmpty()) startDate = Toolkit.parseDate(api.getParams().optString("startDate"), "yyyy-MM-dd");
		if(!api.getParams().optString("endDate").isEmpty()) endDate = Toolkit.parseDate(api.getParams().optString("endDate"), "yyyy-MM-dd");

		JSONArray array = new JSONArray();
		TimeControlGroup timeCG = TimeControlGroup.safeValueOf(api.getParams().optString("group"));
		AON_SOLUTIONS.getTaskHolderTimeControlStream(api.getDomain(), "", api.getParams().optInt("taskHolderId"), startDate, endDate, timeCG)
		.forEach(tc -> {
			array.put(tc.toJSON());
		});
		
		return array;
	}
	
	private Object getTimeControlDetailStream(AonApiData api) {		
		Date startDate = null;
		Date endDate = null;
		if(!api.getParams().optString("startDate").isEmpty()) startDate = Toolkit.parseDate(api.getParams().optString("startDate"), "yyyy-MM-dd");
		if(!api.getParams().optString("endDate").isEmpty()) endDate = Toolkit.parseDate(api.getParams().optString("endDate"), "yyyy-MM-dd");
		
		startDate = AonDateUtils.getDateWithoutTime(startDate);
		endDate = AonDateUtils.getDateWithoutTime(endDate);
		endDate = AonDateUtils.addDays(endDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);
		
		Timestamp startTimestamp = new Timestamp(startDate.getTime());
		Timestamp endTimestamp = new Timestamp(endDate.getTime());
		
		JSONArray array = new JSONArray();
		AON_SOLUTIONS.getTimeControlDetailStream(api.getDomain(), "", f -> 
		f.getDomainProperty().eq(api.getDomain().getId())
		.and(f.getDateProperty().ge(startTimestamp))
		.and(f.getDateProperty().le(endTimestamp))
		.and(f.getTaskHolderProperty().eq(api.getParams().optInt("taskHolderId"))))
		.forEach(tc -> {
			array.put(tc.toJSON());
		});
		
		return array;
	}
	
	
	private JSONObject delete(AonApiData api) {
		AON_SOLUTIONS.deleteTimeControlDetail(api.getDomain(), api.getUser().getLogin(), f ->
				f.getIdProperty().eq(api.getData().optInt("id")));
		return new JSONObject();
	}
	
	private JSONObject save(AonApiData api, Domain domain, User user) {
		TaskHolder taskHolder = api.getData().opt("task_holder") != null 
		    ? AON.getTaskHolder(domain.getName(), domain.getId(), user.getLogin(), f -> 
		    	f.getDomainProperty().eq(domain.getId()).and(f.getIdProperty().eq(api.getData().optInt("task_holder"))))
		    : AON.getTaskHolder(domain.getName(), domain.getId(), user.getLogin(), f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getUserIdProperty().eq(user.getId())));
		  return save(api, taskHolder);
	}
	
	private JSONObject save(AonApiData api, AonToken aonToken) {
		TaskHolder taskHolder = null;
		if(api.getData().opt("task_holder") != null && api.getDomain().getId() != 0) {
			taskHolder = AON.getTaskHolder(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f ->
				f.getIdProperty().eq(api.getData().optInt("task_holder")));
		} else if(api.getData().opt("task_holder") != null) {
			taskHolder = AON_SOLUTIONS.getTaskHolders(aonToken).stream().filter(th -> th.getId().equals(api.getData().optInt("task_holder"))).findFirst().orElse(null);
		} else {
			taskHolder = AON_SOLUTIONS.getTaskHolders(aonToken).stream().findFirst().orElse(null);
		}
		
		if(taskHolder != null ) {
			return save(api, taskHolder);
		}
		return new JSONObject();
	}
	
	private JSONObject save(AonApiData api, TaskHolder taskHolder) {
		Coordinates coordinates = new Coordinates(api.getData().optString("coordinates"));
		Date date = !api.getData().optString("date").isEmpty() ?  new Date(api.getData().optLong("date")) : new Date();
		Location lc =  !api.getData().optString("location").isEmpty() 
				? AON_SOLUTIONS.getLocation(taskHolder.getDomain(), "",  f -> f.getIdProperty().ge(api.getData().optInt("location")) )
				: AON_SOLUTIONS.getLocation(taskHolder.getDomain(), "",  coordinates);
				
		TimeControlDetail tcd = new TimeControlDetail()
				.setId(api.getData().opt("id") != null ? api.getData().optInt("id") : null)
				.setDomain(taskHolder.getDomain())
				.setTaskHolder(taskHolder)
				.setComments(api.getData().optString("comments"))
				.setCoordinates(coordinates)
				.setDate(date)
				.setLocation(lc)
				.setStatus(TimeControlStatus.safeValueOf(api.getData().optString("status")));
		
		return AON_SOLUTIONS.saveTimeControlDetail(tcd.getDomain(), "", tcd).toJSON();
	}
	
	private File getTimeControlExcel(HttpServletRequest req, AonApiData api) throws Exception {
		LOGGER.info("[GET] TIME-CONTROL SERVLET EXCEL");

		Domain domain = AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), "", f -> f.getNameProperty().eq(api.getDomain().getName()));
		Date startDate = null;
		Date endDate = null;
		
		if(!api.getParams().optString("startDate").isEmpty()) startDate = Toolkit.parseDate(api.getParams().optString("startDate"), "yyyy-MM-dd");
		if(!api.getParams().optString("endDate").isEmpty()) endDate = Toolkit.parseDate(api.getParams().optString("endDate"), "yyyy-MM-dd");
	
		File file = File.createTempFile("timecontrol", "");
		TimeControlExcel.excelTimeControl(domain, new FileOutputStream(file), startDate, endDate);
		return file;

	}
	
}
