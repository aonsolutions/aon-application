package net.aonsolutions.aon.api.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.in.payroll.pdf.jooq.JooqTimeControlTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.Coordinates;
import com.esferalia.aon.occam.api.model.aonsolutions.Location;
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
	private static final String START_DATE = "startDate"; 
	private static final String END_DATE = "endDate"; 
	private static final String TASK_HOLDER = "task_holder";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API TIMECONTROL SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req);
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
				responseFile(resp, getTimeControlPdfManual(api), MimeType.PDF);
				break;
			case "/excel":
				responseFile(resp, getTimeControlExcel(api), MimeType.MS_EXCEL);
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
			AonApiData api = initialize(req);

			switch (api.getPath()) {
			case "/":
				response ( req, resp, saveTimeControl(api));	
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
			AonApiData api = initialize(req);
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
	
	private JSONObject getTimeControl(AonApiData api) {
		boolean parent = api.getData().optBoolean(IJsonNames.PARENT);
		if(parent && !AonStringUtils.isEmpty(api.getToken())) {
			AonToken aonToken = SECURITY.getAonToken(api.getToken());
			return getTimeControl(api, aonToken, api.getData().optInt(TASK_HOLDER));
		} else {
			return getTimeControl(api.getDomain(), api.getUser());
		}
	}

	private JSONObject getTimeControl(Domain domain, User user) {
		TaskHolder taskHolder = AON.getTaskHolder(domain.getName(), domain.getId(), user.getLogin(), f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getUserIdProperty().eq(user.getId())));
		return getTimeControl(domain, user, taskHolder);
	}
	
	private JSONObject getTimeControl(AonApiData api, AonToken aonToken, Integer taskHolderId) {
		LinkedList<TaskHolder> taskHolders = AON_SOLUTIONS.getTaskHolders(aonToken);
		
		TaskHolder taskHolder = taskHolders.stream().filter(th -> th.getId().equals(taskHolderId) 
				&& th.isActive() && TaskHolderType.INTERNAL.equals(th.getType())).findFirst()
				.orElse(taskHolders.isEmpty() ? new TaskHolder() : taskHolders.getFirst());
		
		if(taskHolder == null || taskHolder.getId() == null) {
			taskHolder = AON.getTaskHolder(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
				f.getDomainProperty().eq(api.getDomain().getId())
				.and(f.getUserIdProperty().eq(api.getUser().getId()))
				.and(f.getActiveProperty().eq((byte) 1)
				.and(f.getTypeProperty().eq(TaskHolderType.INTERNAL.value()))));
		}
		return getTimeControl(api.getDomain(), api.getUser(), taskHolder);
	}
	
	private JSONObject saveTimeControl(AonApiData api) {
		JSONObject json = new JSONObject();
		JSONObject jsonSave = new JSONObject();
		
		boolean parent = api.getData().optBoolean(IJsonNames.PARENT);
		
		if(parent && !AonStringUtils.isEmpty(api.getToken())) {
			AonToken aonToken = SECURITY.getAonToken(api.getToken());
			jsonSave = save(api, aonToken);
			json = getTimeControl(api, aonToken, api.getData().optInt(TASK_HOLDER));
		} else {
			jsonSave = save(api, api.getDomain(), api.getUser());
			json = getTimeControl(api.getDomain(), api.getUser());	
		}
		
		if(jsonSave.optInt(IJsonNames.ID)!=0) {
			json.put(IJsonNames.ID, jsonSave.optInt(IJsonNames.ID));
		}
		
		return json;
	}
	
	private JSONObject getTimeControl(Domain domain , User user, TaskHolder taskHolder) throws AonApiException {
		Date startDate = AonDateUtils.getDateWithoutTime(new Date());
		Date endDate = AonDateUtils.addDays(startDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);

		if(taskHolder != null && taskHolder.getId() != null) {
			return AON_SOLUTIONS.getTaskHolderTimeControl(domain, user.getLogin(), taskHolder.getId(), startDate, endDate)
					.toJSON();
		} else {
			throw new AonApiException("No existe Task Holder asociado al usuario.");
		}
	}
	
	private JSONArray getTimeControlList(AonApiData api) {
		Date startDate = AonDateUtils.getDateWithoutTime(new Date());
		Date endDate = AonDateUtils.addDays(startDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);
		Boolean active = api.getData().optString(IJsonNames.ACTIVE).isEmpty() || api.getData().getBoolean(IJsonNames.ACTIVE);
		
		if(!api.getData().optString(START_DATE).isEmpty()) 
			startDate =  AonDateUtils.parse(api.getData().optString(START_DATE), FORMAT_DATE);
		
		if(!api.getData().optString(END_DATE).isEmpty()) 
			endDate = AonDateUtils.parse(api.getData().optString(END_DATE), FORMAT_DATE);
		
		JSONArray array = new JSONArray();
		AON_SOLUTIONS.getTimeControlStream(api.getDomain(), "", startDate, endDate)
		.filter(f-> f.getTaskHolder()!=null && f.getTaskHolder().isActive().equals(active))
		.forEach(tc -> 	array.put(tc.toJSON()));
		
		return array;
	}
	
	private JSONArray getTimeControlHistoric(AonApiData api) {
		Integer id = api.getData().optInt(IJsonNames.ID);
		JSONArray array = new JSONArray();
		
		AON_SOLUTIONS.getTimeControlHistoric(api.getDomain(), api.getUser().getLogin(), 
				f->f.getDomainProperty().eq(api.getDomain().getId())
				.and( f.getIdProperty().eq(id).or( f.getModificatedTimeControlProperty().eq(id)) )
		)
		.forEach(tc -> 
			array.put(tc.toJSON())
		);
		return array;
	}
	
	private JSONArray getTaskHolderTimeControlStream(AonApiData api) {		
		Date startDate = AonDateUtils.getDateWithoutTime(new Date());
		Date endDate = AonDateUtils.addDays(startDate, 1);
		endDate = AonDateUtils.addSeconds(endDate, -1);
		
		if(!api.getData().optString(START_DATE).isEmpty()) 
			startDate = AonDateUtils.parse(api.getData().optString(START_DATE), FORMAT_DATE);
		
		if(!api.getData().optString(END_DATE).isEmpty()) 
			endDate = AonDateUtils.parse(api.getData().optString(END_DATE), FORMAT_DATE);

		JSONArray array = new JSONArray();
		TimeControlGroup timeCG = TimeControlGroup.safeValueOf(api.getData().optString("group"));
		AON_SOLUTIONS.getTaskHolderTimeControlStream(api.getDomain(), "", api.getData().optInt("taskHolderId"), startDate, endDate, timeCG)
		.forEach(tc -> array.put(tc.toJSON()));
		
		return array;
	}
	
	private JSONArray getTimeControlDetailStream(AonApiData api) {		
		Date startDate = null;
		Date endDate = null;
		if(!api.getData().optString(START_DATE).isEmpty()) startDate = AonDateUtils.parse(api.getData().optString(START_DATE), FORMAT_DATE);
		if(!api.getData().optString(END_DATE).isEmpty()) endDate = AonDateUtils.parse(api.getData().optString(END_DATE), FORMAT_DATE);
		
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
		.and(f.getTaskHolderProperty().eq(api.getData().optInt("taskHolderId"))))
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
		TaskHolder taskHolder = api.getData().opt(TASK_HOLDER) != null 
		    ? AON.getTaskHolder(domain.getName(), domain.getId(), user.getLogin(), f -> 
		    	f.getDomainProperty().eq(domain.getId())
		    	.and(f.getIdProperty().eq(api.getData().optInt(TASK_HOLDER)))
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
		if(api.getData().opt(TASK_HOLDER) != null && api.getDomain().getId() != 0) {
			taskHolder = AON.getTaskHolder(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f ->
				f.getIdProperty().eq(api.getData().optInt(TASK_HOLDER))
				.and(f.getActiveProperty().eq( (byte) 1))
				.and(f.getTypeProperty().eq(TaskHolderType.INTERNAL.value())));
		} else if(api.getData().opt(TASK_HOLDER) != null) {
			taskHolder = AON_SOLUTIONS.getTaskHolders(aonToken).stream()
				.filter(th -> th.getId().equals(api.getData().optInt(TASK_HOLDER))
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
		JSONObject params = api.getData();
		validateSave(params, taskHolder);
		
		Coordinates coordinates = new Coordinates(params.optString("coordinates"));
	
		Date date = !params.optString(IJsonNames.DATE).isEmpty() ?  new Date(params.optLong(IJsonNames.DATE)) : new Date();

		Location lc = new Location();
		
		if(!coordinates.isEmpty()) {
			Integer locationId = params.optInt("location");
			  lc = locationId!=0
				? AON_SOLUTIONS.getLocation(taskHolder.getDomain(), "",  f -> f.getIdProperty().eq(locationId) )
				: AON_SOLUTIONS.getLocationByCoordinates(taskHolder.getDomain(), "",  coordinates);
		}
		
		TimeControlDetail tcd = new TimeControlDetail()
				.setId(params.opt(IJsonNames.ID) != null ? params.optInt(IJsonNames.ID) : null)
				.setDomain(taskHolder.getDomain())
				.setTaskHolder(taskHolder)
				.setCoordinates(coordinates)
				.setDate(date)
				.setLocation(lc)
				.setComments(params.optString(IJsonNames.COMMENTS))
				.setStatus(TimeControlStatus.safeValueOf(params.optString(IJsonNames.STATUS)));
	
		return AON_SOLUTIONS.saveTimeControlDetail(tcd.getDomain(), api.getUser().getLogin(), tcd).toJSON();
	}
	
	private File getTimeControlExcel(AonApiData api) throws Exception {
		LOGGER.info("[GET] TIME-CONTROL SERVLET EXCEL");

		Domain domain = AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), "", f -> f.getNameProperty().eq(api.getDomain().getName()));
		Date startDate = null;
		Date endDate = null;
		
		if(!api.getData().optString(START_DATE).isEmpty()) 
			startDate = AonDateUtils.parse(api.getData().optString(START_DATE), FORMAT_DATE);
		
		if(!api.getData().optString(END_DATE).isEmpty()) 
			endDate = AonDateUtils.parse(api.getData().optString(END_DATE), FORMAT_DATE);
	
		Boolean active = api.getData().optString(IJsonNames.ACTIVE).isEmpty() || api.getData().getBoolean(IJsonNames.ACTIVE);
		
		File file = File.createTempFile("timecontrol", "");
		TimeControlExcel.excelTimeControl(domain, new FileOutputStream(file), startDate, endDate, active);
		return file;
	}
	
	private File getTimeControlPdfManual(AonApiData api) throws IOException, CanNotCreatePdfException {
		Domain domain = AON.getDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getNameProperty().eq(api.getDomain().getName()));
		Company company = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f->f.getDomainProperty().eq(api.getDomain().getId()));
		
		Date startDate = new Date();

		if(!api.getData().optString(START_DATE).isEmpty()) 
			startDate = AonDateUtils.parse(api.getData().optString(START_DATE), FORMAT_DATE);

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
	
	private void validateSave(JSONObject params, TaskHolder taskHolder) {
		if(params.isNull(IJsonNames.STATUS)) {
			throw new AonApiException("Estado requerido");
		} else if( !(taskHolder!=null && taskHolder.getId()!=null) ){
			throw new AonApiException("Empleado requerido");
		}
	}
}
