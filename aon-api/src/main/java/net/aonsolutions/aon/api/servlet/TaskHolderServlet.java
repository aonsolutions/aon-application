package net.aonsolutions.aon.api.servlet;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.TaskHolderJSON;
import com.esferalia.aon.occam.api.json.TaskHolderWorkgroupJSON;
import com.esferalia.aon.occam.api.json.WorkgroupJSON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Filter.TaskHolderWorkgroupFilter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Properties.TaskHolderProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskHolderWorkgroupProperties;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.TaskHolderWorkgroup;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.registry.RegistryServlet;

@SuppressWarnings("serial")
@WebServlet(name = "AonTaskHolderServlet", urlPatterns = {"/ms/api/taskholder/*"})
public class TaskHolderServlet extends AonApiHttpServlet{

	
	private static final Logger LOGGER  = Logger.getLogger(TaskHolderServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API TASKHOLDER SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, getTaskHolder(api));
				break;
			case "/list":
				response(req, resp, getTaskHolders(api));
				break;
			case "/employee/count":
				response(req, resp, getTaskHoldersCount(api));
				break;
			case "/employee":
				response(req, resp, getTaskHolderEmployee(api));
				break;
			case "/fullList":
				response(req, resp, getTaskHoldersFull(api));
				break;
			case "/enterprise":
				response(req, resp, getTaskHoldersEnterprise(api));
				break;
			case "/workgroup":
				response(req, resp, getTaskHoldersWorkGroup(api));
				break;
			case "/taskHolderWorkgroup":
				response(req, resp, getTaskHolderWorkGroups(api));
				break;
			case "/user":
				response(req, resp, getTaskHoldersUser(api));
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
		LOGGER.info("AON API TASKHOLDER SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, setTaskHolder(api));
				break;
			case "/nocache":
				response(req, resp, getTaskHolder(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("EXAMPLE SERVLET - PUT METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/workgroup":
				response(req, resp, insertTaskHolderWorkgroup(api));
				break;
			case "/taskHolderWorkgroup":
				response(req, resp, saveTaskHolderWorkGroups(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("EXAMPLE SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/workgroup":
				response(req, resp, removeTaskHolderWorkgroup(api));
				break;
			case "/taskHolderWorkgroup":
				response(req, resp, deleteTaskHolderWorkGroup(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONObject insertTaskHolderWorkgroup(AonApiData api) {
		Integer wId = api.getData().getInt(IJsonNames.WORKGROUP);
		Integer uId = api.getData().getInt(IJsonNames.TASK_HOLDER);
		TaskHolder taskHolder = AON.getTaskHolder(api.getDomain(), api.getUser(), f-> f.getIdProperty().eq(uId));
		taskHolder.addWorkgroup(new Workgroup().setId(wId).setDomain(api.getDomain().getId()));
		AON.saveTaskHolderWorkgroups(api.getDomain(), api.getUser(), taskHolder);
		
		return new JSONObject();
	}
	
	private JSONObject removeTaskHolderWorkgroup(AonApiData api) {
		Integer workgroupId = api.getData().getInt(IJsonNames.WORKGROUP);
		Integer taskHolderId = api.getData().getInt(IJsonNames.TASK_HOLDER);
		
		AON.deleteTaskHolderWorkgroup(api.getDomain(), api.getUser(), f -> 
			f.getTaskHolderProperty().eq(taskHolderId)
			.and(f.getWorkgroupProperty().eq(workgroupId)));
		return new JSONObject();
	}
	

	private Object getTaskHoldersUser(AonApiData api) throws Exception {
		AonToken aonToken = SECURITY.getAonToken(api.getToken());
		LinkedList<TaskHolder> taskHolders = AON_SOLUTIONS.getTaskHolders(aonToken);
		JSONArray array = new JSONArray();
		taskHolders.stream()
		.forEach(th -> {
			JSONObject json = new JSONObject();
			json.put("id", th.getId());
			json.put("name", th.getName());
			json.put("company", th.getDomain().getDescription());
			json.put("domain_id", th.getDomain().getId());
			json.put("domain_name", th.getDomain().getName());
			array.put(json);
		});
		return array;
	}	

	private JSONArray getTaskHoldersEnterprise(AonApiData api) {
		Domain domain = api.getDomain();
		JSONArray array = new JSONArray();
		AON.getTaskHolderStream(domain.getName(), domain.getId(), api.getUser().getLogin(), 
				f -> f.getDomainProperty().eq(domain.getId()).and(f.getActiveProperty().eq((byte)1)))
		.forEach(th->{
			JSONObject json = new JSONObject();
			json.put("id", th.getId());
			json.put("name", th.getName());
			json.put("alias", th.getAlias());
			json.put("document", th.getDocument());
			json.put("company", th.getDomain().getDescription());
			json.put("domain_id", th.getDomain().getId());
			json.put("domain_name", th.getDomain().getName());
			array.put(json);
		});
		return array;
	}
	
	private JSONArray getTaskHolderEmployee(AonApiData api) {
		Domain domain = api.getDomain();
		JSONArray array = new JSONArray();
		Integer page = api.getData().has(IJsonNames.PAGE) ? api.getData().optInt(IJsonNames.PAGE) : null;
		Integer perPage = api.getData().has(IJsonNames.PER_PAGE) ? api.getData().optInt(IJsonNames.PER_PAGE) : null;
		AON.getTaskHolderEmployee(domain.getName(), domain.getId(), api.getUser().getLogin(), 
				f-> filter(api, f), page, perPage)
			.forEach(th->{
				JSONObject json = new JSONObject();
				json.put("id", th.getId());
				json.put("name", th.getName());
				json.put("alias", th.getAlias());
				json.put("document", th.getDocument());
				json.put("company", th.getDomain().getDescription());
				json.put("domain_id", th.getDomain().getId());
				json.put("domain_name", th.getDomain().getName());
				array.put(json);
		});
		return array;
	}
	
	private JSONArray getTaskHolders(AonApiData api) {
		Domain domain = api.getDomain();
		
		Integer workgroup = JsonUtils.getInteger(api.getData(), IJsonNames.WORKGROUP);
		Integer page = JsonUtils.getInteger(api.getData(), IJsonNames.PAGE);
		Integer perPage = JsonUtils.getInteger(api.getData(), IJsonNames.PER_PAGE);
		
		if(workgroup != null) {
			return TaskHolderJSON.toJSON(AON.getTaskHolderWorkgroupStream(domain, api.getUser(), 
					f-> filter(api, f), workgroup, perPage * (page -1), perPage));
		} else {
			return TaskHolderJSON.toJSON(AON.getTaskHolderStream(domain.getName(), domain.getId(), api.getUser().getLogin(), 
					f-> filter(api, f), new Options().setPage(page).setPerPage(perPage)));
		}
	}
	
	private JSONArray getTaskHoldersFull(AonApiData api) {
		return TaskHolderJSON.toJSON(AON.getTaskHolderStream(api.getDomain(), api.getUser(), 
				f-> filter(api, f),
				new Options().setFull(true)));
	}
	
	private long getTaskHoldersCount(AonApiData api) {
		return AON.getTaskHolderCount(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f-> filter(api, f));
	}
	
	private JSONObject getTaskHolder(AonApiData api) {
		Domain domain = api.getDomain();
		
		TaskHolder taskholder = AON.getTaskHolder(domain.getName(), domain.getId(), api.getUser().getLogin(), 
				f->	api.getData().opt(IJsonNames.ID)!=null ? 
				    filter(api, f) :
				    f.getDomainProperty().eq(domain.getId()).and(f.getUserIdProperty().eq(api.getUser().getId()))
		);
		
		
		JSONObject object = TaskHolderJSON.toJSON(taskholder);
	
		if(api.getData().optBoolean(IJsonNames.WORKGROUPS)) {
			TaskHolderWorkgroupFilter filter  = f -> f.getTaskHolderProperty().eq(taskholder.getId());
			object.put(IJsonNames.WORKGROUPS,
				WorkgroupJSON.toJSON(
					AON.getTaskHolderWorkgroupStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), filter)
					.collect(Collectors.toList())
				)
			);
		}
	
		return RegistryServlet.getRegistryAdditionalInfo(object, api, api.getData(), taskholder.getId(), null);
		
	}
	
	private JSONArray getTaskHoldersWorkGroup(AonApiData api) {
		Domain domain     = api.getDomain();
		Integer workgroup = api.getData().optInt(IJsonNames.WORKGROUP);
		Integer active    = api.getData().optInt(IJsonNames.ACTIVE);
		return TaskHolderJSON.toJSON(AON.getTaskHolderWorkgroupStream(domain, api.getUser(), 
				f->f.getUserIdProperty().isNotNull()
				.and(f.getActiveProperty().eq(active.byteValue()))
				.and(f.getDomainProperty().eq(api.getDomain().getId())), 
				workgroup, 0, Integer.MAX_VALUE));
	}
	
	private JSONArray getTaskHolderWorkGroups(AonApiData api) {
		List<TaskHolderWorkgroup> taskHolderWorkgroups = AON.getTaskHolderWorkgroupsList(api.getDomain(), api.getUser(), f-> filterTaskHolderWorkGroup(api, f));
		return TaskHolderWorkgroupJSON.toJSON(taskHolderWorkgroups);
	}
	
	public static Filter filterTaskHolderWorkGroup(AonApiData api, TaskHolderWorkgroupProperties f) {
		Integer taskHolder = api.getData().optInt(IJsonNames.TASK_HOLDER);
		
		Filter filter  = f.getDomainProperty().eq(api.getDomain().getId());
		
		if(taskHolder!=0) filter = filter.and(f.getTaskHolderProperty().eq(taskHolder));
		
		return filter;
	}
	
	private JSONObject saveTaskHolderWorkGroups(AonApiData api) {
		TaskHolderWorkgroup taskHolderWorkgroup = TaskHolderWorkgroupJSON.fromJSON(api.getData());
		taskHolderWorkgroup.setTaskHolder(taskHolderWorkgroup.getTaskHolderObj().getId());
		taskHolderWorkgroup.setDomain(api.getDomain().getId());
		
		AON.saveTaskHolderWorkgroup(api.getDomain(), api.getUser(), taskHolderWorkgroup);
		
		return new JSONObject();
	}
	
	private JSONObject deleteTaskHolderWorkGroup(AonApiData api) {
		Integer workgroup = api.getData().optInt(IJsonNames.WORKGROUP);
		Integer taskHolder = api.getData().optInt(IJsonNames.TASK_HOLDER);
		
		AON.deleteTaskHolderWorkgroup(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getTaskHolderProperty().eq(taskHolder).and(f.getWorkgroupProperty().eq(workgroup)).and(f.getDomainProperty().eq(api.getDomain().getId())));
		
		return new JSONObject();
	}
	
	private JSONObject setTaskHolder(AonApiData api) {
		Domain domain = api.getDomain();
		TaskHolder th = TaskHolderJSON.fromJSON(api.getData());

		Optional<TaskHolder> opt = Optional.empty();
		if(th.getUserId()!=null && th.getId()==null) {
			opt = AON.getTaskHolderStream(domain.getName(), domain.getId(), api.getUser().getLogin(), 
					f-> f.getDomainProperty().eq(domain.getId())
					.and(f.getUserIdProperty().eq(th.getUserId())))
			.findFirst();	
		}
		
		TaskHolder taskHolder = AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),  
				opt.isPresent() ? opt.get().setActive(th.isActive()) : th
		);
		
		RegistryServlet.saveRegistryAdditionalInfo(api, taskHolder.getId(), taskHolder.getDomain().getId());
		
//		saveTaskHolderWorkgroup(api, taskHolder);
		
		return TaskHolderJSON.toJSON(taskHolder);
	}

	private static void saveTaskHolderWorkgroup(AonApiData api, TaskHolder taskHolder) {
		JSONArray workgroups = api.getData().optJSONArray(IJsonNames.WORKGROUPS);
		Integer userId = taskHolder.getUserId();
		User user = AON.getUser(api.getDomain(), api.getUser().getLogin(), f-> f.getIdProperty().eq(userId));

		if(!workgroups.isEmpty()) {
			List<Workgroup> list = WorkgroupJSON.fromJSON(workgroups)
			    .stream()
				.filter(w->w.getId()!=null)
				.collect(Collectors.toList());
			
			if(!list.isEmpty()) {
				user.setWorkgroups(list);
				AON.saveUserWorkgroups(api.getDomain(), api.getUser().getLogin(), user);	
			}
		}
	}
	
	public static Filter filter(AonApiData api, TaskHolderProperties f) {
		JSONObject params  = api.getData();
		Integer id = params.optInt(IJsonNames.ID);
		String search = params.optString(IJsonNames.SEARCH);
		Integer user = params.optInt(IJsonNames.USER);
		Filter filter  = f.getDomainProperty().eq(api.getDomain().getId());
		if(id != 0) {
			filter = filter.and(f.getIdProperty().eq(id));
		}
		if(user != 0) {
			filter = filter.and(f.getUserIdProperty().eq(user));
		}
		if(params.opt(IJsonNames.ACTIVE)!=null && !AonStringUtils.equalsIgnoreCase(params.optString(IJsonNames.ACTIVE), "undefined")) {
			filter = filter.and(f.getActiveProperty().eq( (byte)(params.optBoolean(IJsonNames.ACTIVE) ? 1 : 0)) );
		}
		if(!search.isEmpty()) {
			Filter searchFilter = f.getNameProperty().like("%" + search + "%")
					.or(f.getDocumentProperty().like("%" + search + "%"))
					.or(f.getAliasProperty().like("%" + search + "%"));
			filter = filter.and(searchFilter);
		}
		return filter;
	}	
}
