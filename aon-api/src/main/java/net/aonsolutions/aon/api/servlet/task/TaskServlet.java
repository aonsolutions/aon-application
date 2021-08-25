package net.aonsolutions.aon.api.servlet.task;

import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.json.AuthJSON;
import com.esferalia.aon.occam.api.json.CompanyJSON;
import com.esferalia.aon.occam.api.json.TaskAttachJSON;
import com.esferalia.aon.occam.api.json.TaskJSON;
import com.esferalia.aon.occam.api.json.TaskWorkflowJSON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.TaskProperties;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.NotificationSource;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskAttach;
import com.esferalia.aon.occam.api.model.task.TaskSource;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.api.model.task.TaskWorkflowType;
import com.esferalia.aon.occam.api.model.type.MimeType;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.notification.NotificationRequest;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

@SuppressWarnings("serial")
@WebServlet(name = "TaskServlet", urlPatterns = {"/ms/api/task/*"})
public class TaskServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(TaskServlet.class.getName());
	
	private static final String SIG_SESSION_ID = "SIGd95770f269e711eb94390242ac130002";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON TASK SERVLET GET");
		try {		
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
				case "/":
					response(req, resp,  getTasks(api));
					break;
				case "/one":
					response(req, resp,  getTask(api));
					break;
				case "/workflow":
					response(req, resp,  getTaskWorkflow(api));
					break;
				case "/attach":
					response(req, resp,  getTasksAttach(api));
					break;
				case "/count-status-task":
					response(req, resp,  getTaskStatusCount(api));
					break;
				case "/cau":
					response(req, resp,  getCauInfo(api));
					break;
				default:
					throw new Exception("La ruta introducida es incorrecta.");
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
				case "/workflow":
					response(req, resp,  saveTaskWorkflow(api));
					break;
				default:
					throw new Exception("La ruta introducida es incorrecta.");
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
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private Object getTasks(AonApiData api) {
		Integer page = api.getParams().optInt("page");
		Integer perPage = api.getParams().optInt("perPage");
		return TaskJSON.toJSON(
				AON_SOLUTIONS.getTaskStream(api.getDomain(), api.getUser(), f -> taskFilter(api, f), page, perPage));
	}
	
	private Filter taskFilter(AonApiData api, TaskProperties f) {
		Integer workgroup = api.getParams().optInt("workgroup");
		String status = api.getParams().optString("status");
		String source = api.getParams().optString("source");
		Integer taskHolder = api.getParams().optInt("task_holder");
		Integer sender = api.getParams().optInt("sender");
		String search = api.getParams().optString("search");

		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		
		if("pending".equalsIgnoreCase(status)) {
			filter = filter.and(f.getStatusProperty().eq(TaskStatus.IN_PROGRESS.value()).or(f.getStatusProperty().eq(TaskStatus.PENDING.value())));
		} else 
			filter = filter.and(f.getStatusProperty().eq(TaskStatus.safeValueOf(status).value()));
		
		if(workgroup != null && workgroup !=0) 
			filter = filter.and(f.getWorkgroupProperty().eq(workgroup));

		if(taskHolder != null && taskHolder!=0) 
			filter = filter.and(f.getTaskHolderProperty().eq(taskHolder));

		if(sender != null && sender!=0) 
			filter = filter.and(f.getSenderProperty().eq(sender));

		if(!source.isEmpty()) 
			filter = filter.and(f.getSourceProperty().eq(TaskSource.safeValueOf(source).value()));
		
		if(!search.isEmpty()) {
			filter = filter.and(f.getDescriptionProperty().like("%" + search + "%"));
			
//			String numberSearch = search.replaceAll("[^\\d]", "");
//			if(!numberSearch.isEmpty())
//				filter = filter.and(f.getNumberProperty().like("%" + numberSearch + "%"));
		}
		
		if(!api.getParams().optString("cau").isEmpty() && api.getParams().optInt("cau")>0) {
			String email = api.getParams().optString("email");
			if(!email.isEmpty())
				filter = filter.and(f.getGtaskIdProperty().eq(email));
		}
		
		return filter;
	}
	
	private Object getTask(AonApiData api) {
		Integer taskId = api.getParams().optInt("id");
		return TaskJSON.toJSON( AON_SOLUTIONS.getTask(api.getDomain(), api.getUser(), f-> f.getIdProperty().eq(taskId)));
	}
	
	private Object saveTask(AonApiData api) {
		Task task = TaskJSON.fromJSON(api.getData());
		setCauData(api, task);
		task = AON_SOLUTIONS.saveTask(api.getDomain(), api.getUser(), task);

		if(task.getWorkflows().size() > 0) {
			saveAllTaskWorkflow(api, task); //ADD WORKFLOW
		}
		return TaskJSON.toJSON(task);
	}
	
	private Object getTaskWorkflow(AonApiData api) {
		Integer taskId = api.getParams().optInt("taskId");
		return TaskWorkflowJSON.toJSON(
				AON_SOLUTIONS.getTaskWorkflowStream(api.getDomain(), api.getUser(), 
				f->f.getTaskProperty().eq(taskId)) 
		);
	}
	
	private void saveAllTaskWorkflow(AonApiData api, Task task) {
		task.getWorkflows().stream().forEach(workflow -> AON_SOLUTIONS.saveTaskWorkflow(api.getDomain(), api.getUser(), workflow.setTask(task.getId())));
	}
	
	private JSONObject saveTaskWorkflow(AonApiData api) {
		TaskWorkflow workflow = AON_SOLUTIONS.saveTaskWorkflow(api.getDomain(), api.getUser(), TaskWorkflowJSON.fromJSON(api.getData()));
//		sendWorkflowCommunication(api, workflow);
		return TaskWorkflowJSON.toJSON(workflow);
	}
	
	private Object getTasksAttach(AonApiData api) {
		Integer taskId = api.getParams().optInt("taskId");
		return TaskAttachJSON.toJSON( AON_SOLUTIONS.getTaskAttachList(api.getDomain(), api.getUser(), f-> f.getTaskProperty().eq(taskId)));
	}
	
	private JSONObject saveTaskAttach(AonApiData api) {
		Domain domain = api.getDomain();
		Integer taskId = api.getData().optInt("taskId");
		if(api.getData().opt("file")!= null) { 
			
			JSONObject file  = api.getData().optJSONObject("file");
			String base64 = file.optString("content");
			String contentType = file.optString("contentType");
			byte[] fileData = Base64.getDecoder().decode(base64);
			TaskAttach taskAttach = new TaskAttach()
			.setDomain(domain.getId())
			.setTask(taskId)
			.setData(fileData)
			.setMimetype(MimeType.get(contentType));
			
			return TaskAttachJSON.toJSON(AON_SOLUTIONS.saveTaskAttach(domain, api.getUser(), taskAttach));
		}
		return new JSONObject();
	}
	
	private JSONObject getTaskStatusCount(AonApiData api) {
		JSONObject json = new JSONObject();
		HashMap<Byte, Integer> map = AON_SOLUTIONS.getTaskStatusCount(api.getDomain(), api.getUser(),  f -> taskFilterCount(api, f));
		map.forEach((k,v)->json.put(TaskStatus.safeValueOf(k).getName(), v));
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
	
	private Filter taskFilterCount(AonApiData api, TaskProperties f) {
		String source = api.getParams().optString("source");
		Integer workgroup = api.getParams().optInt("workgroup");
		Integer taskHolder = api.getParams().optInt("task_holder");
		Integer sender = api.getParams().optInt("sender");

		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());

		if(workgroup != null && workgroup!= 0) 
			filter = filter.and(f.getWorkgroupProperty().eq(workgroup));

		if(taskHolder != null && taskHolder!=0) 
			filter = filter.and(f.getTaskHolderProperty().eq(taskHolder));

		if(sender != null && sender!=0) 
			filter = filter.and(f.getSenderProperty().eq(sender));

		if(!source.isEmpty()) 
			filter = filter.and(f.getSourceProperty().eq(TaskSource.safeValueOf(source).value()));
		
		return filter;
	}
	
	private JSONObject deleteTask(AonApiData api) {
		Integer taskId = api.getData().optInt("taskId");
		AON_SOLUTIONS.deleteTask(api.getDomain(), api.getUser(), taskId);
		return new JSONObject();
	}
	
	private void sendWorkflowCommunication(AonApiData api, TaskWorkflow workflow) {
		Thread newThread = new Thread(() -> {
			try {
//				if(workflow.getType().getName().equalsIgnoreCase(TaskWorkflowType.CLOSE.getName())) {
			    Task task = AON_SOLUTIONS.getTask(api.getDomain(), api.getUser(), f-> f.getIdProperty().eq(workflow.getTask()));
				if(!task.getGtaskId().isEmpty() && task.getGtaskId().indexOf("@")>=0) {
					Auth auth = AON_SOLUTIONS.getAuth(task.getGtaskId());
					if(!auth.getEmail().isEmpty()) {
						sendNotification(api, task, workflow, auth);
					}
				}
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		newThread.start();
	}
	
	private void sendNotification(AonApiData api, Task task, TaskWorkflow workflow, Auth auth){
		User user = AON_SOLUTIONS.getUser(api.getDomain(), api.getToken());
		String title = "SOLICITUD | AON SOLUTIONS";
		String body = "Solicitud Nº "+task.getNumber() + " " + workflow.getType();
		LinkedList<Auth> auths = new LinkedList<Auth>();
		auths.add(auth);

    	NotificationRequest notification = new NotificationRequest();
    	notification.setTitle(title);
    	notification.setBody(body);
    	notification.setSender(user.getAuth());
    	notification.setDomain(api.getDomain());
    	notification.setUser(api.getUser());
    	notification.setSource(NotificationSource.MESSENGER);
    	notification.setSourceId(task.getId());
    	notification.setAuths( auths );
    	notification.send();
	}
	
	
	private void setCauData(AonApiData api, Task task) {
		if(!api.getParams().optString("cau").isEmpty() && api.getParams().optInt("cau")>0) {
			try {
				JSONObject description = new JSONObject(task.getDescription());
				JSONObject cauData = description.optJSONObject("cauData");
				JSONObject company = cauData.optJSONObject("company");
				JSONObject auth = cauData.optJSONObject("auth");
				if(!auth.optString("email").isEmpty()) {
					task.setGtaskId(auth.optString("email"));
				}
				if(!company.optString("document").isEmpty()) {
					Customer customer = AON.getCustomer(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
							f->f.getDocumentProperty().eq(company.optString("document")));
					if(!customer.getDocument().isEmpty()){
						task.setRegistry(customer.get());
					}
				}
				
			} catch (Exception e) {e.printStackTrace();}
		}
	}
}
