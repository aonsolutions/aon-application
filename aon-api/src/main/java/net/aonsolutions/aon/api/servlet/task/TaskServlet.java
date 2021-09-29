package net.aonsolutions.aon.api.servlet.task;

import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
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
import com.esferalia.aon.occam.api.json.AuthJSON;
import com.esferalia.aon.occam.api.json.CompanyJSON;
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.TagJSON;
import com.esferalia.aon.occam.api.json.TaskAttachJSON;
import com.esferalia.aon.occam.api.json.TaskJSON;
import com.esferalia.aon.occam.api.json.TaskWorkflowJSON;
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
import net.aonsolutions.aon.api.model.mail.TaskMail;
import net.aonsolutions.aon.api.model.mail.TaskMailTemplate;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

@SuppressWarnings("serial")
@WebServlet(name = "TaskServlet", urlPatterns = {"/ms/api/task/*"})
public class TaskServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(TaskServlet.class.getName());
	
//	private static final String SIG_SESSION_ID = "SIGd95770f269e711eb94390242ac130002";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON TASK SERVLET GET");
		try {		
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
				case "/":
					response(req, resp,  getTasks(api));
					break;
				case "/notice":
					response(req, resp, new JSONObject());
					break;
				case "/one":
					response(req, resp,  getTask(api));
					break;
				case "/workflow":
					response(req, resp,  getTaskWorkflow(api));
					break;
				case "/tags":
					response(req, resp,  getTaskTags(api));
					break;
				case "/attach":
					response(req, resp,  getTaskAttach(api));
					break;
				case "/count":
					response(req, resp,  getTaskCount(api));
					break;
				case "/status/count":
					response(req, resp,  getTaskStatusCount(api));
					break;
				case "/cau":
					response(req, resp,  getCauInfo(api));
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
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
				case "/":
					response(req, resp, saveTask(api));
				break;
				case "/attach":
					response(req, resp,  saveTaskAttach(api));
					break;
				case "/historic-send":
					response(req, resp,  taskHistoricSend(api));
					break;
				case "/workflow":
					response(req, resp,  saveTaskWorkflow(api));
					break;
				case "/tag":
					response(req, resp,  saveTaskTag(api));
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
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API TASK SERVLET - DELETE METHOD");
		try {
			AonApiData api = initialize(req, resp);
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
		Integer page = api.getParams().optInt(IJsonNames.PAGE);
		Integer perPage = api.getParams().optInt(IJsonNames.PER_PAGE);
		JSONArray jsonArr = TaskJSON.toJSON(AON_SOLUTIONS.getTaskStream(api.getDomain(), api.getUser(), f -> UtilsTask.taskFilter(api, f), page, perPage));
		getTasksOffice(api, jsonArr);
		return jsonArr;
	}

	private JSONObject getTask(AonApiData api) {
		Integer taskId = api.getParams().optInt(IJsonNames.ID);
		Task task = AON_SOLUTIONS.getTask(api.getDomain(), api.getUser(), f-> f.getIdProperty().eq(taskId) );
		if(task.getId()==null) throw new AonApiException(AonApiError.EMPTY_DATA.getMessage());
		return TaskJSON.toJSON(task);
	}
	
	private JSONObject saveTask(AonApiData api) {
		Task task = TaskJSON.fromJSON(api.getData());
		boolean edit = task.getId() != null;
		setCauData(api, task);
		if(edit) {
			UtilsTask.checkFiles(api, task);
		}
		task = AON_SOLUTIONS.saveTask(api.getDomain(), api.getUser(), task);
		
		if(!edit) {
			UtilsTask.checkFiles(api, task);
			task = AON_SOLUTIONS.saveTask(api.getDomain(), api.getUser(), task);
		}
		
		if(!task.getWorkflows().isEmpty()) {
			saveAllTaskWorkflow(api, task); //ADD WORKFLOW
		}
		return TaskJSON.toJSON(task);
	}
	
	private JSONArray getTaskWorkflow(AonApiData api) {
		Integer task = api.getParams().optInt(IJsonNames.TASK);
		Domain domain = new Domain().setId(api.getParams().optInt(IJsonNames.DOMAIN_ID)).setName(api.getParams().optString(IJsonNames.DOMAIN_NAME));
		return TaskWorkflowJSON.toJSON(
				AON_SOLUTIONS.getTaskWorkflowStream(domain, new User(), 
				f->f.getTaskProperty().eq(task)) 
		);
	}
	
	private JSONArray getTaskTags(AonApiData api) {
		String type = api.getParams().optString("type");
		Domain domain = api.getDomain();
		return TagJSON.toJSON( AON.getTagList(
						domain.getName(), 
						domain.getId(), api.getUser().getLogin(),
						f->f.getDomainProperty().eq(domain.getId())
						.and(f.getTypeProperty().eq(TagType.safeValueOf(type).value()))
					) 
				);
	}
	
	private void saveAllTaskWorkflow(AonApiData api, Task task) {
		task.getWorkflows().stream().forEach(workflow -> AON_SOLUTIONS.saveTaskWorkflow(api.getDomain(), api.getUser(), workflow.setTask(task.getId())));
	}

	private JSONObject saveTaskWorkflow(AonApiData api) {
		TaskWorkflow workflow = AON_SOLUTIONS.saveTaskWorkflow(api.getDomain(), api.getUser(), TaskWorkflowJSON.fromJSON(api.getData()));
		UtilsTask.sendWorkflowCommunication(api, workflow);
		return TaskWorkflowJSON.toJSON(workflow);
	}

	private JSONObject saveTaskTag(AonApiData api) {
		Domain domain = api.getDomain();
		Tag tag = TagJSON.fromJSON(api.getData());
		if(tag.getId()!=null) {
			AON.updateTag(domain.getName(), domain.getId(), api.getUser().getLogin(), tag); 
		} else {
			tag = AON.insertTag(domain.getName(), domain.getId(), api.getUser().getLogin(), tag);
		}
		return TagJSON.toJSON(tag);
	}
	
	private JSONArray getTaskAttach(AonApiData api) {
		Integer task = api.getParams().optInt(IJsonNames.TASK);
		return TaskAttachJSON.toJSON( AON_SOLUTIONS.getTaskAttachList(api.getDomain(), api.getUser(), f-> f.getTaskProperty().eq(task)));
	}
	
	private JSONObject saveTaskAttach(AonApiData api) {
		Domain domain = api.getDomain();
		Integer task = api.getData().optInt(IJsonNames.TASK);
		if(api.getData().opt(IJsonNames.FILE)!= null) { 
			
			JSONObject file  = api.getData().optJSONObject(IJsonNames.FILE);
			String base64 = file.optString("content");
			String contentType = file.optString("contentType");
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
		JSONObject json = new JSONObject();
		AON_SOLUTIONS.getTaskStatusCount(api.getDomain(), api.getUser(),  f -> UtilsTask.taskFilterCount(api, f))
		.forEach((k,v)->json.put(TaskStatus.safeValueOf(k).getName(), v));
		return json;
	}
	
	private JSONObject getTaskCount(AonApiData api) {
		JSONObject json = new JSONObject();

		Integer taskHolder = JsonUtils.getInteger(api.getParams(), IJsonNames.TASK_HOLDER);
		Optional<String> email = Optional.ofNullable(null);
	
		if(taskHolder==null) {
			taskHolder = 0;
			email = Optional.of(api.getParams().optString(IJsonNames.EMAIL));
		}
		
		HashMap<String, Integer> counts = AON_SOLUTIONS.getTaskCount(api.getDomain(), api.getUser(),  
				f -> f.getDomainProperty().eq(api.getDomain().getId())
				.and(f.getStatusProperty().eq(TaskStatus.PENDING.value())), 
				taskHolder,
				email
		);
		
		Integer countTH = 0;
		Integer countSE = 0;
		
		for(Entry<String, Integer> count: counts.entrySet()) {
			if(count.getKey().equalsIgnoreCase(IJsonNames.TASK_HOLDER))
				countTH += count.getValue();
			else if(count.getKey().equalsIgnoreCase(IJsonNames.SENDER))
				countSE += count.getValue();
		}
		
		json.put(IJsonNames.TASK_HOLDER, countTH);
		json.put(IJsonNames.SENDER, countSE);
		
		return json;
	}
	
	private JSONObject getCauInfo(AonApiData api) {
		JSONObject json = new JSONObject();
		if(api.getDomain().isChild()) {
			Company parentCompany = AON.getCompany(api.getDomain().getName(), api.getDomain().getParentId(), api.getUser().getLogin(), f -> 
					f.getDomainProperty().eq(api.getDomain().getParentId()));
			json.put("parent", CompanyJSON.toJSON(parentCompany));
		}
		Company company = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
			f.getDomainProperty().eq(api.getDomain().getId()));
		json.put("company", CompanyJSON.toJSON(company));
		

		AonToken aonToken = SECURITY.getAonToken(api.getToken());
		Auth auth = AON_SOLUTIONS.getAuth(aonToken.getSchemaFirstDomain(), 0, aonToken.getAuth());
		json.put("auth", AuthJSON.toJSON(auth));
		
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
			 ).sorted((t1, t2)-> t2.getId().compareTo(t1.getId())) .collect(Collectors.toCollection(LinkedList::new));
			task.setWorkflows(taskWorkflow);
			
			sendHistoricWorkflow(api, task);
			
			Integer[] ids = taskWorkflow.stream().map(TaskWorkflow::getId).toArray(Integer[]::new);
		
			AON_SOLUTIONS.updateTaskWorkflowBetween(api.getDomain(), api.getUser(), 
					 f->f.getIdProperty().in(ids)
			 );
			
			json = TaskWorkflowJSON.toJSON(taskWorkflow);
		 }

		return json;
	}
	
	private void setCauData(AonApiData api, Task task) {
		if(!api.getParams().optString("cau").isEmpty() && api.getParams().optInt("cau")>0) {
			try {
				JSONObject description = new JSONObject(task.getDescription());
				JSONObject cauData = description.optJSONObject("cauData");
				JSONObject company = cauData.optJSONObject(IJsonNames.COMPANY);
				JSONObject auth = cauData.optJSONObject(IJsonNames.AUTH);
				if(!auth.optString("email").isEmpty()) {
					task.setGtaskId(auth.optString(IJsonNames.EMAIL));
				}
				if(!company.optString(IJsonNames.DOCUMENT).isEmpty()) {
					Customer customer = AON.getCustomer(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
							f->f.getDocumentProperty().eq(company.optString(IJsonNames.DOCUMENT)));
					if(!customer.getDocument().isEmpty()){
						task.setRegistry(customer.get());
					}
				}
				
			} catch (Exception e) {e.printStackTrace();}
		}
	}

	private void sendHistoricWorkflow(AonApiData api, Task task) {
		Thread newThread = new Thread(() -> {
			AonToken aonToken = SECURITY.getAonToken(api.getToken());
			Auth auth = AON_SOLUTIONS.getAuth(aonToken.getSchemaFirstDomain(), 0, aonToken.getAuth());
			Company company = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(api.getDomain().getId()));
			if(company!=null) {
				String logo = UtilsTask.getLogoCompany(company.getDomain().getName());
				
				String to = auth.getEmail();
	
				String subject = "SOLICITUD Nº "+ task.getNumber();
				String url = "https://aon.solutions";
				
				TaskMail tm = new TaskMail()
				.setNumber(task.getNumber().toString())
				.setDate(task.getStartDate())
				.setUrl(url)
				.setTitle(task.getTitle())
				.setWorkflows(task.getWorkflows())
				.setLogo(logo);
				
				String body = TaskMailTemplate.taskWorkflowContent(tm);
				
				SESMessage msg = new SESMessage()
				.setAlias(company.getName())
				.setSubject(subject)
				.setBody(body)
				.setTo(to);
				
			    SES.sendEmail(msg);
			}
		});
		newThread.start();
	}
	
	private JSONArray getTasksOffice(AonApiData api, JSONArray arr) {
		String status = api.getParams().optString("status");
		if(status.isEmpty() || "pending".equalsIgnoreCase(status)) {
			Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
			AON.getDomainOfficeLinked(api.getDomain(), api.getUser().getLogin()).stream().forEach(domain -> {
				Customer customer = AON.getCustomer(domain.getName(), domain.getId(), "", f -> f.getDomainProperty().eq(domain.getId()).and(f.getDocumentProperty().eq(company.getDocument())));
				AON_SOLUTIONS.getTaskStream(domain, new User(), f -> UtilsTask.taskOfficeFilter(api, f, customer, domain))
				.forEach(t -> arr.put(TaskJSON.toJSON(t)));
			});
		}
		return arr;
	}
}
