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
import com.esferalia.aon.in.payroll.pdf.jooq.JooqTimeControlTemplate;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.Company;
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
import com.esferalia.aon.occam.api.model.task.TaskHolderType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.excel.TimeControlExcel;

@SuppressWarnings("serial")
@WebServlet(name = "AonTimeControlServlet", urlPatterns = {"/ms/api/timecontrol/*"})
public class TimeControlServlet extends AonApiHttpServlet{

	private static final Logger LOGGER  = Logger.getLogger(TimeControlServlet.class.getName());
	private static final String FORMAT_DATE = "yyyy-MM-dd"; 
	
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
			case "/historic":
				response(req, resp, getTimeControlHistoric(api));
				break;
			case "/list-holder":
				response(req, resp, getTaskHolderTimeControlStream(api));
				break;
			case "/list-holder-detail":
				response(req, resp, getTimeControlDetailStream(api));
				break;
			case "/pdf":
				responseFile(req, resp, getTimeControlPdfManual(api), MimeType.PDF);
				break;
			case "/excel":
				responseFile(req, resp, getTimeControlExcel(req, api), MimeType.MS_EXCEL);
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
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
				boolean parent = api.getData().optBoolean("parent");
				if(parent && !AonStringUtils.isEmpty(api.getToken())) {
					AonToken aonToken = SECURITY.getAonToken(api.getToken());
					save(api, aonToken);
					response ( req, resp, getTimeControl(api, aonToken, api.getData().optInt("task_holder")) );
				} else {
					save(api, api.getDomain(), api.getUser());
					response ( req, resp, getTimeControl(api.getDomain(), api.getUser()));	
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
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
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
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private Object getTimeControl(AonApiData api) throws Exception {
		boolean parent = api.getParams().optBoolean("parent");
		if(parent && !AonStringUtils.isEmpty(api.getToken())) {
			AonToken aonToken = SECURITY.getAonToken(api.getToken());
			return getTimeControl(api, aonToken, api.getParams().optInt("task_holder"));
		} else {
			return getTimeControl(api.getDomain(), api.getUser());
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
		
		TaskHolder taskHolder = taskHolders.stream().filter(th -> th.getId().equals(taskHolderId) 
				&& th.isActive() && TaskHolderType.INTERNAL.equals(th.getType())).findFirst()
				.orElse(taskHolders.size() > 0 ? taskHolders.getFirst(): new TaskHolder());
		
		if(taskHolder == null || taskHolder.getId() == null) {
			taskHolder = AON.getTaskHolder(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
				f.getDomainProperty().eq(api.getDomain().getId())
				.and(f.getUserIdProperty().eq(api.getUser().getId()))
				.and(f.getActiveProperty().eq((byte) 1)
				.and(f.getTypeProperty().eq(TaskHolderType.INTERNAL.value()))));
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
		Boolean active = !api.getParams().optString("active").isEmpty() ?  api.getParams().getBoolean("active") : true;
		
		if(!api.getParams().optString("startDate").isEmpty()) 
			startDate =  AonDateUtils.parse(api.getParams().optString("startDate"), FORMAT_DATE);
		
		if(!api.getParams().optString("endDate").isEmpty()) 
			endDate = AonDateUtils.parse(api.getParams().optString("endDate"), FORMAT_DATE);
		
		JSONArray array = new JSONArray();
		AON_SOLUTIONS.getTimeControlStream(api.getDomain(), "", startDate, endDate)
		.filter(f-> f.getTaskHolder()!=null && f.getTaskHolder().isActive().equals(active))
		.forEach(tc -> {
			array.put(tc.toJSON());
		});
		
		return array;
	}
	
	private Object getTimeControlHistoric(AonApiData api) {
		Integer id = api.getParams().optInt("id");
		JSONArray array = new JSONArray();
		AON_SOLUTIONS.getTimeControlHistoric(api.getDomain(), api.getUser().getLogin(), 
				f->f.getDomainProperty().eq(api.getDomain().getId()).and(f.getModificatedTimeControlProperty().eq(id))
		)
		.forEach(tc -> 
			array.put(tc.toJSON())
		);
		return array;
	}
	
	private Object getTaskHolderTimeControlStream(AonApiData api) {		
		Date startDate = AonDateUtils.getDateWithoutTime(new Date());
		Date endDate = AonDateUtils.addDays(startDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);
		
		if(!api.getParams().optString("startDate").isEmpty()) startDate = AonDateUtils.parse(api.getParams().optString("startDate"), FORMAT_DATE);
		if(!api.getParams().optString("endDate").isEmpty()) endDate = AonDateUtils.parse(api.getParams().optString("endDate"), FORMAT_DATE);

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
		if(!api.getParams().optString("startDate").isEmpty()) startDate = AonDateUtils.parse(api.getParams().optString("startDate"), FORMAT_DATE);
		if(!api.getParams().optString("endDate").isEmpty()) endDate = AonDateUtils.parse(api.getParams().optString("endDate"), FORMAT_DATE);
		
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
		Integer tmId = api.getData().optInt("id");
		AON_SOLUTIONS.deleteTimeControlDetail(api.getDomain(), api.getUser().getLogin(), tmId);
		return new JSONObject();
	}
	
	private JSONObject save(AonApiData api, Domain domain, User user) {
		TaskHolder taskHolder = api.getData().opt("task_holder") != null 
		    ? AON.getTaskHolder(domain.getName(), domain.getId(), user.getLogin(), f -> 
		    	f.getDomainProperty().eq(domain.getId())
		    	.and(f.getIdProperty().eq(api.getData().optInt("task_holder")))
		    	.and(f.getActiveProperty().eq( (byte) 1))
		    	.and(f.getTypeProperty().eq(TaskHolderType.INTERNAL.value())))
		    : AON.getTaskHolder(domain.getName(), domain.getId(), user.getLogin(), f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getUserIdProperty().eq(user.getId()))
				.and(f.getActiveProperty().eq( (byte) 1))
				.and(f.getTypeProperty().eq(TaskHolderType.INTERNAL.value())));
		  return save(api, taskHolder);
	}
	
	private JSONObject save(AonApiData api, AonToken aonToken) {
		TaskHolder taskHolder = null;
		if(api.getData().opt("task_holder") != null && api.getDomain().getId() != 0) {
			taskHolder = AON.getTaskHolder(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f ->
				f.getIdProperty().eq(api.getData().optInt("task_holder"))
				.and(f.getActiveProperty().eq( (byte) 1))
				.and(f.getTypeProperty().eq(TaskHolderType.INTERNAL.value())));
		} else if(api.getData().opt("task_holder") != null) {
			taskHolder = AON_SOLUTIONS.getTaskHolders(aonToken).stream()
				.filter(th -> th.getId().equals(api.getData().optInt("task_holder"))
					&& th.isActive() && TaskHolderType.INTERNAL.equals(th.getType()))
				.findFirst().orElse(null);
		} else {
			taskHolder = AON_SOLUTIONS.getTaskHolders(aonToken).stream()
				.filter(th -> th.isActive() && TaskHolderType.INTERNAL.equals(th.getType()))
				.findFirst().orElse(null);
		}
		
		if(taskHolder != null ) {
			return save(api, taskHolder);
		}
		return new JSONObject();
	}
	
	private JSONObject save(AonApiData api, TaskHolder taskHolder) {
		Coordinates coordinates = new Coordinates(api.getData().optString("coordinates"));
		Date date = !api.getData().optString("date").isEmpty() ?  new Date(api.getData().optLong("date")) : new Date();
		Location lc = new Location();
		if(!coordinates.isEmpty()) {
			  lc = !api.getData().optString("location").isEmpty() 
				? AON_SOLUTIONS.getLocation(taskHolder.getDomain(), "",  f -> f.getIdProperty().ge(api.getData().optInt("location")) )
				: AON_SOLUTIONS.getLocationByCoordinates(taskHolder.getDomain(), "",  coordinates);
		}
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
		
		if(!api.getParams().optString("startDate").isEmpty()) startDate = AonDateUtils.parse(api.getParams().optString("startDate"), "yyyy-MM-dd");
		if(!api.getParams().optString("endDate").isEmpty()) endDate = AonDateUtils.parse(api.getParams().optString("endDate"), "yyyy-MM-dd");
	
		Boolean active = !api.getParams().optString("active").isEmpty() ?  api.getParams().getBoolean("active") : true;
		
		File file = File.createTempFile("timecontrol", "");
		TimeControlExcel.excelTimeControl(domain, new FileOutputStream(file), startDate, endDate, active);
		return file;
	}
	
	private File getTimeControlPdfManual(AonApiData api) throws Exception {
		Domain domain = AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getNameProperty().eq(api.getDomain().getName()));
		Company company = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f->f.getDomainProperty().eq(api.getDomain().getId()));
		
		Date startDate = new Date();

		if(!api.getParams().optString("startDate").isEmpty()) startDate = AonDateUtils.parse(api.getParams().optString("startDate"), "yyyy-MM-dd");

		File file = File.createTempFile("timecontrol-pdf", "");
		JooqTimeControlTemplate.generateTimeControlTemplate(
				new FileOutputStream(file), 
				domain.getName(), 
				api.getUser().getLogin(), 
				company.getId(), 
				startDate
		);
		return file;
	}
	
}
