package net.aonsolutions.aon.api.servlet.task;

import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.json.AppParamJSON;
import com.esferalia.aon.occam.api.json.AuthJSON;
import com.esferalia.aon.occam.api.json.CompanyJSON;
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.TagJSON;
import com.esferalia.aon.occam.api.json.TaskAttachJSON;
import com.esferalia.aon.occam.api.json.TaskJSON;
import com.esferalia.aon.occam.api.json.TaskWorkflowJSON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskAttach;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.api.model.task.TaskWorkflowType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.TagType;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.utils.TaskUtils;

@SuppressWarnings("serial")
@WebServlet(name = "TaskServlet", urlPatterns = {"/ms/api/task/*"})
public class TaskServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(TaskServlet.class.getName());
	private static final String LINES = "-------------";	
//	private static final String SIG_SESSION_ID = "SIGd95770f269e711eb94390242ac130002";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON TASK SERVLET GET");
		try {		
			AonApiData api = initialize(req);
			switch (api.getPath()) {
				case "/":
					response(req, resp, getTasks(api));
					break;
				case "/notice":
					response(req, resp, new JSONObject());
					break;
				case "/one":
					response(req, resp, getTask(api));
					break;
				case "/workflow":
					response(req, resp, getWorkflows(api));
					break;
				case "/tags":
					response(req, resp, getTaskTags(api));
					break;
				case "/attach":
					response(req, resp, getTaskAttach(api));
					break;
				case "/count":
					response(req, resp, getTaskCount(api));
					break;
				case "/status/count":
					response(req, resp, getTaskStatusCount(api));
					break;
				case "/cau":
					response(req, resp, getCauInfo(api));
					break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)  {
		LOGGER.info("AON TASK SERVLET POST");
		try {		
			AonApiData api = initialize(req);
			switch (api.getPath()) {
				case "/":
					response(req, resp, saveTask(api));
				break;
				case "/attach":
					response(req, resp, saveTaskAttach(api));
					break;
				case "/historic-send":
					response(req, resp, taskHistoricSend(api));
					break;
				case "/workflow":
					response(req, resp, saveWorkflow(api, Optional.empty()));
					break;
				case "/tag":
					response(req, resp, saveTaskTag(api));
					break;
				case "/app-params":
					response(req, resp, saveAppParams(api));
					break;
				case "/get-app-params":
					response(req, resp, getAppParams(api));
					break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API TASK SERVLET - DELETE METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, deleteTask(api));
				break;
			case "/tag":
				response(req, resp, deleteTaskTag(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONArray getTasks(AonApiData api) {
		Integer page = api.getData().optInt(IJsonNames.PAGE);
		Integer perPage = api.getData().optInt(IJsonNames.PER_PAGE);
		JSONArray jsonArr = TaskJSON.toJSON(AON_SOLUTIONS.getTaskStream(api.getDomain(), api.getUser(),
				f -> TaskUtils.taskFilter(api, f, api.getDomain(), new Customer()), page, perPage));
		if(page==1)
			getTasksOffice(api, jsonArr);
		return jsonArr;
	}

	private JSONObject getTask(AonApiData api) {
		Integer taskId = api.getData().optInt(IJsonNames.ID);
		Task task = AON_SOLUTIONS.getTask(api.getDomain(), api.getUser(), f-> f.getIdProperty().eq(taskId) );
		if(task.getId()==null) throw new AonApiException(AonApiError.EMPTY_DATA.getMessage());
		return TaskJSON.toJSON(task);
	}
	
	private JSONArray getWorkflows(AonApiData api) {
		Integer task = api.getData().optInt(IJsonNames.TASK);
		Domain domain = new Domain().setId(api.getData().optInt(IJsonNames.DOMAIN_ID)).setName(api.getData().optString(IJsonNames.DOMAIN_NAME));
		return TaskWorkflowJSON.toJSON(
				AON_SOLUTIONS.getTaskWorkflowStream(domain, new User(), 
				f->f.getTaskProperty().eq(task)) 
		);
	}
	
	private JSONArray getTaskTags(AonApiData api) {
		String type = api.getData().optString(IJsonNames.TYPE);
		Domain domain = api.getDomain();
		return TagJSON.toJSON( 
			AON.getTagList(
				domain.getName(), 
				domain.getId(), api.getUser().getLogin(),
				f->f.getDomainProperty().eq(domain.getId())
				.and(f.getTypeProperty().eq(TagType.safeValueOf(type).value()))
			) 
		);
	}

	private JSONArray getTaskAttach(AonApiData api) {
		return TaskAttachJSON.toJSON( AON_SOLUTIONS.getTaskAttachList(api.getDomain(), api.getUser(), 
				f -> f.getTaskProperty().eq(api.getData().optInt(IJsonNames.TASK))));
	}
	
	private JSONObject saveTask(AonApiData api) {
		Task task = TaskJSON.fromJSON(api.getData());
		boolean edit = task.getId() != null;
		TaskUtils.setCauInfo(api, task);
		if(edit) {
			TaskUtils.checkFiles(api, task);
		}
		task = AON_SOLUTIONS.saveTask(api.getDomain(), api.getUser(), task);
		
		if(!edit) {
			TaskUtils.checkFiles(api, task);
			task = AON_SOLUTIONS.saveTask(api.getDomain(), api.getUser(), task);
		}
		
		if(!task.getWorkflows().isEmpty()) {
			Integer taskId = task.getId();
			task.getWorkflows().stream().forEach(w-> saveWorkflow( api, Optional.of(w.setTask(taskId)) )); // SAVE WORKFLOW ALL
		}
		return TaskJSON.toJSON(task);
	}
	
	private JSONObject saveWorkflow(AonApiData api, Optional<TaskWorkflow> workflowOpt) {
		TaskWorkflow workflowTmp = workflowOpt.isPresent() ? workflowOpt.get() : TaskWorkflowJSON.fromJSON(api.getData());
		TaskWorkflow workflow = AON_SOLUTIONS.saveTaskWorkflow(api.getDomain(), api.getUser(), workflowTmp);
		TaskUtils.sendWorkflowCommunication(api, workflow);
		return TaskWorkflowJSON.toJSON(workflow);
	}

	private JSONObject saveTaskTag(AonApiData api) {
		Domain domain = api.getDomain();
		Tag tag = TagJSON.fromJSON(api.getData());
		if(tag.getId()!=null) 
			AON.updateTag(domain.getName(), domain.getId(), api.getUser().getLogin(), tag); 
		else 
			tag = AON.insertTag(domain.getName(), domain.getId(), api.getUser().getLogin(), tag);
		return TagJSON.toJSON(tag);
	}

	private JSONObject saveTaskAttach(AonApiData api) {
		Domain domain = api.getDomain();
		JSONObject params = api.getData();
		Integer task = api.getData().optInt(IJsonNames.TASK);
		if(params.opt(IJsonNames.FILE)!= null) { 
			JSONObject file  = params.optJSONObject(IJsonNames.FILE);
			String base64 = file.optString(IJsonNames.CONTENT);
			String contentType = file.optString(IJsonNames.CONTENT_TYPE);
			byte[] fileData = Base64.getDecoder().decode(base64);
			TaskAttach taskAttach = new TaskAttach()
			.setDomain(domain.getId())
			.setTask(task)
			.setData(fileData)
			.setMimetype(MimeType.get(contentType));
			
			return TaskAttachJSON.toJSON(AON_SOLUTIONS.saveTaskAttach(domain, api.getUser(), taskAttach));
		}
		return new JSONObject();
	}
	
	private JSONObject getTaskStatusCount(AonApiData api) {

		HashMap<Byte, Integer> counts = AON_SOLUTIONS.getTaskStatusCount(api.getDomain(), api.getUser(),  f -> TaskUtils.taskFilterStatusCount(f, api, api.getDomain(), new Customer()));
		
		Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		
		AON.getDomainOfficeLinked(api.getDomain(), api.getUser().getLogin()).stream().forEach(domain -> {
			Customer customer = AON.getCustomer(domain.getName(), domain.getId(), "", f -> f.getDomainProperty().eq(domain.getId()).and(f.getDocumentProperty().eq(company.getDocument())));
			HashMap<Byte, Integer> aux = AON_SOLUTIONS.getTaskStatusCount(domain, new User(),  f -> TaskUtils.taskFilterStatusCount(f, api, domain, customer));
			aux.keySet().stream().forEach(key -> {
				if(counts.containsKey(key)) 
					counts.put(key, counts.get(key) + aux.get(key));
			    else 
			    	counts.put(key, aux.get(key));
			});
		});
		
		JSONObject json = new JSONObject();
		counts.keySet().stream().forEach(k-> json.put(TaskStatus.safeValueOf(k).getName(),counts.get(k)) );
		
		return json;
	}
	
	private JSONObject getTaskCount(AonApiData api) {
		Integer taskHolder = api.getData().optString(IJsonNames.TASK_HOLDER).isEmpty() 
				? 0 : JsonUtils.getInteger(api.getData(), IJsonNames.TASK_HOLDER);
		
		HashMap<String, Integer> counts = AON_SOLUTIONS.getTaskCount(api.getDomain(), api.getUser(), 
				f-> TaskUtils.taskFilterCount(api, api.getDomain(), f, new Customer()), 
				taskHolder
		);
		
		Integer taskHolder1  = Boolean.TRUE.equals(api.getDur().isMessengerManager()) ? 0 : taskHolder;
		Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		AON.getDomainOfficeLinked(api.getDomain(), api.getUser().getLogin()).stream().forEach(domain -> {
			Customer customer = AON.getCustomer(domain.getName(), domain.getId(), "", f -> f.getDomainProperty().eq(domain.getId()).and(f.getDocumentProperty().eq(company.getDocument())));
			HashMap<String, Integer> aux = AON_SOLUTIONS.getTaskCount(domain, new User(),
			f -> TaskUtils.taskFilterCount(api, domain, f, customer), taskHolder1);
			aux.keySet().stream().forEach(key -> {
				if(counts.containsKey(key)) 
					counts.put(key, counts.get(key) + aux.get(key));
				else 
					counts.put(key, aux.get(key));
			});
		});
		
		JSONObject json = new JSONObject();
		counts.keySet().stream().forEach(k-> json.put(k, counts.get(k)) );
		
		return json;
	}
	
	private JSONObject getCauInfo(AonApiData api) {
		JSONObject json = new JSONObject();
		if(api.getDomain().isChild()) {
			Company parentCompany = AON.getCompany(api.getDomain().getName(), api.getDomain().getParentId(), api.getUser().getLogin(), f -> 
					f.getDomainProperty().eq(api.getDomain().getParentId()));
			json.put(IJsonNames.PARENT, CompanyJSON.toJSON(parentCompany));
		}
		Company company = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
			f.getDomainProperty().eq(api.getDomain().getId()));
		json.put(IJsonNames.COMPANY, CompanyJSON.toJSON(company));
		
		AonToken aonToken = SECURITY.getAonToken(api.getToken());
		Auth auth = AON_SOLUTIONS.getAuth(aonToken.getSchemaFirstDomain(), 0, aonToken.getAuth());
		json.put(IJsonNames.AUTH, AuthJSON.toJSON(auth));
		
		return json;
	}
	
	private JSONObject deleteTask(AonApiData api) {
		Integer task = api.getData().optInt(IJsonNames.TASK);
		AON_SOLUTIONS.deleteTask(api.getDomain(), api.getUser(), task);
		return new JSONObject();
	}
	
	private JSONObject deleteTaskTag(AonApiData api) {
		AON.deleteTag(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),TagJSON.fromJSON(api.getData()));
		return new JSONObject();
	}
	
	private JSONArray taskHistoricSend(AonApiData api) {
		 Integer workflowId = api.getData().optInt("workflowId");
		 JSONArray json = new JSONArray();
		 if(workflowId > 0) {
			
			Task task = TaskJSON.fromJSON(api.getData());
			LinkedList<TaskWorkflow> taskWorkflow = AON_SOLUTIONS.getTaskWorkflowStream(api.getDomain(), api.getUser(), 
					 f->f.getTaskProperty().eq(task.getId())
					 .and(f.getTypeProperty().eq(TaskWorkflowType.COMMENT.value()))
					 .and(f.getNotificationUserProperty().isNotNull())
					 .or(f.getIdProperty().eq(workflowId))
			 ).sorted((t1, t2)-> t2.getId().compareTo(t1.getId())).collect(Collectors.toCollection(LinkedList::new));
			task.setWorkflows(taskWorkflow);
			
			TaskUtils.sendHistoricWorkflow(api, task);
			
			Integer[] ids = taskWorkflow.stream().map(TaskWorkflow::getId).toArray(Integer[]::new);
		
			AON_SOLUTIONS.updateTaskWorkflowBetween(api.getDomain(), api.getUser(), 
					 f->f.getIdProperty().in(ids)
			 );
			
			json = TaskWorkflowJSON.toJSON(taskWorkflow);
		 }

		return json;
	}

	private JSONArray getTasksOffice(AonApiData api, JSONArray arr) {
//		String status = api.getParams().optString("status");
//		if(status.isEmpty() || "pending".equalsIgnoreCase(status)) {
			Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
			AON.getDomainOfficeLinked(api.getDomain(), api.getUser().getLogin()).stream().forEach(domain -> {
				Customer customer = AON.getCustomer(domain.getName(), domain.getId(), "", f -> f.getDomainProperty().eq(domain.getId()).and(f.getDocumentProperty().eq(company.getDocument())));
				AON_SOLUTIONS.getTaskStream(domain, new User(), f -> TaskUtils.taskFilter(api, f, domain, customer))
				.forEach(t -> arr.put(TaskJSON.toJSON(t)));
			});
//		}
		return arr;
	}
	
	private JSONArray saveAppParams(AonApiData api) {
		JSONArray arr = new JSONArray();
		Domain domain = api.getDomain();
		LinkedList<ApplicationParameter> appParams = AppParamJSON.fromJSON(api.getData().optJSONArray("appParams"));
		for (ApplicationParameter param : appParams) {

			 ApplicationParameter exists = AON.getApplicationParameter(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), param.getName());
			 if(exists.getId()!=null) {
				 if(param.getValue()!=null) {
					 exists.setValue(param.getValue()); 
					 LOGGER.info("--------UPDATE APP PARAMS "+ param.getName() + LINES);
					 AON.updateApplicationParameter(domain.getName(), domain.getId(), api.getUser().getLogin(), exists, 
								f->f.getDomainProperty().eq(exists.getDomain()).and(f.getNameProperty().eq(exists.getName()))
					);
				 } else {
					 LOGGER.info("--------DELETE APP PARAMS "+ param.getName() + LINES);
					 AON.deleteApplicationParameter(domain.getName(), domain.getId(), api.getUser().getLogin(),
							 f-> f.getDomainProperty().eq(domain.getId()).and(f.getNameProperty().eq(param.getName()))
					);
				 }
			 } else if(param.getValue()!=null) {
				 LOGGER.info("--------SAVE APP PARAMS "+ param.getName() + LINES);
				 AON.insertApplicationParameter(domain.getName(), domain.getId(), api.getUser().getLogin(), param);
			 }
		}
		return arr;
	}
	
	private JSONArray getAppParams(AonApiData api) {
		JSONArray params = api.getData().optJSONArray("params");
		if(params!=null) {
		    String[] names = new String[params.length()];
		    
			for(int i=0; i<params.length(); i++) 
				names[i]=params.optString(i);
			
			return AppParamJSON.toJSON(
				AON.getApplicationParameterStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					f-> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getNameProperty().in(names))
				)
			);
		}
		return new JSONArray();
	}
}
