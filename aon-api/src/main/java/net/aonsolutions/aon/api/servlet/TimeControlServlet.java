package net.aonsolutions.aon.api.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.Base64;
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
			super.doGet(req, resp);

			switch (getPath()) {
			case "/":
				response(req, resp, getTimeControl());
				break;
			case "/list":
				response(req, resp, getTimeControlList());
				break;
			case "/list-holder":
				response(req, resp, getTaskHolderTimeControlStream());
				break;
			case "/list-holder-detail":
				response(req, resp, getTimeControlDetailStream());
				break;
			case "/taskholder":
				response(req, resp, getTaskHolders());
				break;
			case "/excel":
				responseFile(req, resp, getTimeControlExcel(req), MimeType.MS_EXCEL);
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
			super.doPost(req, resp);

			switch (getPath()) {
			case "/":
				if(AonStringUtils.isEmpty(getToken())) {
					save(getDomain(), getUser());
					response ( req, resp, getTimeControl(getDomain(), getUser()));
				} else {
					AonToken aonToken = SECURITY.getAonToken(getToken());
					save(aonToken);
					response ( req, resp, getTimeControl(aonToken, getData().optInt("task_holder")) );
				}
				break;
			case "/save":
				if(AonStringUtils.isEmpty(getToken())) {
					response ( req, resp, save(getDomain(), getUser()));
				} else {
					AonToken aonToken = SECURITY.getAonToken(getToken());
					response ( req, resp, save(aonToken) );
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
			super.doDelete(req, resp);
			switch (getPath()) {
			case "/":
				response(req, resp, delete());
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	private Object getTaskHolders() throws Exception {
		AonToken aonToken = SECURITY.getAonToken(getToken());
		LinkedList<TaskHolder> taskHolders = AON_SOLUTIONS.getTaskHolders(aonToken);
		JSONArray array = new JSONArray();
		taskHolders.stream().forEach(th -> {
			JSONObject json = new JSONObject();
			json.put("id", th.getId());
			json.put("name", th.getName());
			json.put("company", th.getDomain().getDescription());
			json.put("domain_id", th.getDomain().getId());
			array.put(json);
		});
		return array;
	}
	
	private Object getTimeControl() throws Exception {
		if(AonStringUtils.isEmpty(getToken())) {
			return getTimeControl(getDomain(), getUser());
		} else {
			AonToken aonToken = SECURITY.getAonToken(getToken());
			return getTimeControl(aonToken, getParams().optInt("task_holder"));
		}
	}

	private Object getTimeControl(Domain domain, User user) throws Exception {
		TaskHolder taskHolder = AON.getTaskHolder(domain.getName(), domain.getId(), user.getLogin(), f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getUserIdProperty().eq(user.getId())));
		return getTimeControl(taskHolder);
	}
	
	private Object getTimeControl(AonToken aonToken, Integer taskHolderId) throws Exception{
		LinkedList<TaskHolder> taskHolders = AON_SOLUTIONS.getTaskHolders(aonToken);
		
		TaskHolder taskHolder = taskHolders.stream().filter(th -> th.getId().equals(taskHolderId)).findFirst()
				.orElse(taskHolders.size() > 0 ? taskHolders.getFirst(): new TaskHolder());
		
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
	
	private Object getTimeControlDetailStream() {		
		Date startDate = null;
		Date endDate = null;
		if(!getParams().optString("startDate").isEmpty()) startDate = Toolkit.parseDate(getParams().optString("startDate"), "yyyy-MM-dd");
		if(!getParams().optString("endDate").isEmpty()) endDate = Toolkit.parseDate(getParams().optString("endDate"), "yyyy-MM-dd");
		
		startDate = AonDateUtils.getDateWithoutTime(startDate);
		endDate = AonDateUtils.getDateWithoutTime(endDate);
		endDate = AonDateUtils.addDays(endDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);
		
		Timestamp startTimestamp = new Timestamp(startDate.getTime());
		Timestamp endTimestamp = new Timestamp(endDate.getTime());
		
		JSONArray array = new JSONArray();
		AON_SOLUTIONS.getTimeControlDetailStream(getDomain(), "", f -> 
		f.getDomainProperty().eq(getDomain().getId())
		.and(f.getDateProperty().ge(startTimestamp))
		.and(f.getDateProperty().le(endTimestamp))
		.and(f.getTaskHolderProperty().eq(getParams().optInt("taskHolderId"))))
		.forEach(tc -> {
			array.put(tc.toJSON());
		});
		
		return array;
	}
	
	
	private JSONObject delete() {
		AON_SOLUTIONS.deleteTimeControlDetail(getDomain(), getUser().getLogin(), f ->
				f.getIdProperty().eq(getData().optInt("id")));
		return new JSONObject();
	}
	
	private JSONObject save(Domain domain, User user) {
		TaskHolder taskHolder = getData().opt("task_holder") != null 
		    ? AON.getTaskHolder(domain.getName(), domain.getId(), user.getLogin(), f -> 
		    	f.getDomainProperty().eq(domain.getId()).and(f.getIdProperty().eq(getData().optInt("task_holder"))))
		    : AON.getTaskHolder(domain.getName(), domain.getId(), user.getLogin(), f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getUserIdProperty().eq(user.getId())));
		  return save(taskHolder);
	}
	
	private JSONObject save(AonToken aonToken) {
		TaskHolder taskHolder = null;
		if(getData().opt("task_holder") != null) {
			taskHolder = AON.getTaskHolder(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f ->
				f.getIdProperty().eq(getData().optInt("task_holder")));
		} else {
			taskHolder = AON_SOLUTIONS.getTaskHolders(aonToken).stream().findFirst().orElse(null);
		}
		
		if(taskHolder != null ) {
			return save(taskHolder);
		}
		return new JSONObject();
	}
	
	private JSONObject save(TaskHolder taskHolder) {
		Coordinates coordinates = new Coordinates(getData().optString("coordinates"));
		Date date = !getData().optString("date").isEmpty() ?  new Date(getData().optLong("date")) : new Date();
		Location lc =  !getData().optString("location").isEmpty() 
				? AON_SOLUTIONS.getLocation(taskHolder.getDomain(), "",  f -> f.getIdProperty().ge(getData().optInt("location")) )
				: AON_SOLUTIONS.getLocation(taskHolder.getDomain(), "",  coordinates);
				
		TimeControlDetail tcd = new TimeControlDetail()
				.setId(getData().opt("id") != null ? getData().optInt("id") : null)
				.setDomain(taskHolder.getDomain())
				.setTaskHolder(taskHolder)
				.setComments(getData().optString("comments"))
				.setCoordinates(coordinates)
				.setDate(date)
				.setLocation(lc)
				.setStatus(TimeControlStatus.safeValueOf(getData().optString("status")));
		
		return AON_SOLUTIONS.saveTimeControlDetail(tcd.getDomain(), "", tcd).toJSON();
	}
	
	private File getTimeControlExcel(HttpServletRequest req) throws Exception {
		LOGGER.info("[GET] TIME-CONTROL SERVLET EXCEL");

		Domain domain = AON.getDomain(getDomain().getName(), getDomain().getId(), "", f -> f.getNameProperty().eq(getDomain().getName()));
		Date startDate = null;
		Date endDate = null;
		
		if(!getParams().optString("startDate").isEmpty()) startDate = Toolkit.parseDate(getParams().optString("startDate"), "yyyy-MM-dd");
		if(!getParams().optString("endDate").isEmpty()) endDate = Toolkit.parseDate(getParams().optString("endDate"), "yyyy-MM-dd");
	
		File file = File.createTempFile("timecontrol", "");
		TimeControlExcel.excelTimeControl(domain, new FileOutputStream(file), startDate, endDate);
		return file;

	}
	
}
