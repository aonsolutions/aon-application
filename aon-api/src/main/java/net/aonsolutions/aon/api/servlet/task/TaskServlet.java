package net.aonsolutions.aon.api.servlet.task;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import com.esferalia.aon.occam.api.json.DailyTrackingJSON;
import com.esferalia.aon.occam.api.json.DomainJSON;
import com.esferalia.aon.occam.api.json.JobTypeJSON;
import com.esferalia.aon.occam.api.json.TagJSON;
import com.esferalia.aon.occam.api.json.TaskAttachJSON;
import com.esferalia.aon.occam.api.json.TaskHolderJSON;
import com.esferalia.aon.occam.api.json.TaskJSON;
import com.esferalia.aon.occam.api.json.TaskWorkflowJSON;
import com.esferalia.aon.occam.api.json.WorkgroupJSON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.DailyTracking;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskAttach;
import com.esferalia.aon.occam.api.model.task.TaskCounts;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskSource;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.api.model.task.TaskWorkflowType;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.MimeType;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.excel.TaskExcel;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

@SuppressWarnings("serial")
@WebServlet(name = "TaskServlet", urlPatterns = {"/ms/api/task/*"})
public class TaskServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(TaskServlet.class.getName());
//	private static final String SIG_SESSION_ID = "SIGd95770f269e711eb94390242ac130002";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON TASK SERVLET GET");
		try {		
			AonApiData api = initialize(req);
			setDomain(api);
			switch (api.getPath()) {
				case "/":
					response(req, resp, getTasks(api));
					break;
				case "/one":
					response(req, resp, getTask(api));
					break;
				case "/notice":
					response(req, resp, new JSONObject());
					break;
				case "/job-type":
					response(req, resp, getJobType(api));
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
				case "/general/count":
					response(req, resp, getTaskGeneralCount(api));
					break;
				case "/cau":
					response(req, resp, getCauInfo(api));
					break;
				case "/daily-tracking-by-task":
					response(req, resp, getDailyTrackingByTask(api));
					break;
				case "/excel":
					responseFile(resp, getTaskExcel(api), MimeType.MS_EXCEL);
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
			setDomain(api);
			switch (api.getPath()) {
				case "/":
					response(req, resp, saveTask(api));
				break;
				case "/attach":
					response(req, resp, saveTaskAttach(api));
					break;
				case "/historic":
					response(req, resp, sendTaskHistoric(api));
					break;
				case "/branch":
					response(req, resp, saveBranch(api));
					break;
				case "/workflow":
					response(req, resp, saveWorkflow(api, Optional.empty(), true));
					break;
				case "/workflow/update":
					response(req, resp, updateWorkflow(api));
					break;
				case "/tag":
					response(req, resp, saveTaskTag(api));
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
			setDomain(api);
			switch (api.getPath()) {
			case "/":
				response(req, resp, deleteTask(api));
				break;
			case "/workflow":
				response(req, resp, deleteTaskWorkflow(api));
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
		JSONObject params  = api.getData();
		Integer page       = params.optInt(IJsonNames.PAGE);
		Integer perPage    = params.optInt(IJsonNames.PER_PAGE);
		boolean isReceived = params.isNull(IJsonNames.SENDER) && !params.isNull(IJsonNames.TASK_HOLDER);
		boolean isSent     = !params.isNull(IJsonNames.SENDER) && params.isNull(IJsonNames.TASK_HOLDER);
		boolean all        = !isReceived && !isSent;
		boolean isCau      = TaskUtils.isCau(params);

		List<Task> tasks = new ArrayList<>(); 
		
		if(isCau && params.optString(IJsonNames.EMAIL).isEmpty()) {
			throw new AonApiException("Auth inexistente");
		}
		
		if(all && !isCau) {
			tasks.addAll(
				AON_SOLUTIONS.getTaskParentOrChildStream(api.getDomain(), api.getUser(), 
					f -> TaskFilter.task(api, f, api.getDomain(), new Customer()), page, perPage
				)
				.collect(Collectors.toList())
			);
		} else {
			tasks.addAll(TaskUtils.getTasksNotAll(api));
		} 
		
		
		setTasksOffice(api, tasks); // set tasks office domains

		JSONArray array = new JSONArray();
		
		tasks.forEach(t ->{
			JSONObject json = TaskJSON.toJSON(t);
			if(t.getParentObj()!=null) {
				json.put("parentObj", TaskJSON.toJSON(t.getParentObj()));
			}
			array.put(json);
		});
		
		return array;
	}

	private JSONObject getTask(AonApiData api) {
		JSONObject params = api.getData();
		Domain domain     = api.getDomain();
		User user         = api.getUser();
		Integer taskId    = params.optInt(IJsonNames.ID);
		Integer number    = params.optInt(IJsonNames.NUMBER);

		Task task = AON_SOLUTIONS.getTaskAndChilds(domain, user, f-> 
			f.getDomainProperty().eq(domain.getId())
			.and(number != 0 ? f.getNumberProperty().eq(number)  : f.getIdProperty().eq(taskId) )
		);
		
		if(task.getId()==null) {
			throw new AonApiException(AonApiError.EMPTY_DATA.getMessage());
		}
		
		JSONObject json = TaskJSON.toJSON(task);
		
		if(TaskUtils.isCau(params)) {
			json.put("childs", new JSONArray());
		} else {
			if(task.isChild()) {
				Task parent = AON_SOLUTIONS.getTask(domain, user, f-> f.getDomainProperty().eq(domain.getId()).and(f.getIdProperty().eq(task.getParent())) );
				if(parent.getId()!=null) {
					json.put("parentObj", TaskJSON.toJSON(parent));
				}
			} 
		}

		return json;
	}
	
	private JSONArray getJobType(AonApiData api) {
		Domain domain     = api.getDomain();
		User user         = api.getUser();

		return JobTypeJSON.toJSON(
				AON_SOLUTIONS.getJobTypeStream(domain, user, f->f.getDomainProperty().eq(domain.getId()))
		);
	}
	
	private JSONArray getDailyTrackingByTask(AonApiData api) {
		Domain  domain = api.getDomain();
		User    user   = api.getUser();
		Integer task   = api.getData().optInt(IJsonNames.TASK);

		return DailyTrackingJSON.toJSON(
				AON_SOLUTIONS.getDailyTrackingStream(domain, user, f->f.getDomainProperty().eq(domain.getId()).and(f.getTaskProperty().eq(task)))
		);
	}
	
	private JSONArray getWorkflows(AonApiData api) {
		Domain domain = new Domain().setId(api.getData().optInt(IJsonNames.DOMAIN_ID)).setName(api.getData().optString(IJsonNames.DOMAIN_NAME));
		
		return TaskWorkflowJSON.toJSON(
			AON_SOLUTIONS.getTaskWorkflowStream(domain, new User(), 
				f-> TaskFilter.workflow(api, f)
			)
		);
	}
	
	private JSONArray getTaskTags(AonApiData api) {
		Domain domain = api.getDomain();
		
		return TagJSON.toJSON( 
			AON.getTagList(
				domain.getName(), 
				domain.getId(), 
				api.getUser().getLogin(),
				f-> TaskFilter.tags(f, api, domain)
			) 
		);
	}

	private JSONArray getTaskAttach(AonApiData api) {
		return TaskAttachJSON.toJSON( AON_SOLUTIONS.getTaskAttachList(api.getDomain(), api.getUser(), f-> f.getTaskProperty().eq(api.getData().optInt(IJsonNames.TASK))));
	}
	
	private JSONObject saveTask(AonApiData api) {
		Task task = TaskJSON.fromJSON(api.getData());
		boolean edit = task.getId() != null;
		if(TaskUtils.isCau(api.getData())) {
			TaskUtils.setCauInfo(api, task);
		}
	
		if(edit) {
			TaskUtils.checkFilesAndSave(api, task);
		} else {
			setWgAndThDefault(api, task);
		}

		task = AON_SOLUTIONS.saveTask(api.getDomain(), api.getUser(), task);
		
		if(!edit) { // SAVE CREATE
			TaskUtils.checkFilesAndSave(api, task);
			task = AON_SOLUTIONS.saveTask(api.getDomain(), api.getUser(), task);
		}
		
		if(!task.getWorkflows().isEmpty()) {
			Integer taskId = task.getId();
		
			String description = TaskUtils.parseDescription(task);
			
			task.getWorkflows().stream()
			.forEach(w-> {
				
				if(w.getType().equals(TaskWorkflowType.OPEN)) {
					w.setComment(description);
				}
				
				saveWorkflow( api, Optional.of(w.setTask(taskId)), true);
			}); // SAVE WORKFLOW ALL
		}
		return TaskJSON.toJSON(task);
	}
	
	private JSONObject saveWorkflow(AonApiData api, Optional<TaskWorkflow> workflowOpt, boolean notification) {
		TaskWorkflow workflowTmp = workflowOpt.isPresent() ? workflowOpt.get() : TaskWorkflowJSON.fromJSON(api.getData());
		
		if(TaskUtils.isCau(api.getData())) {
			TaskUtils.setCauWorkflow(api, workflowTmp);
		}
	
		if(workflowTmp.getDomain()==null) {
			workflowTmp.setDomain(api.getDomain().getId());		
		}
	
		TaskWorkflow workflow = AON_SOLUTIONS.saveTaskWorkflow(api.getDomain(), api.getUser(), workflowTmp);
		
		if(notification) {
			TaskUtils.onNotification(api, workflow);
		}
		
		 // save DAILY_TRACKING
		if(workflowTmp.getType().equals(TaskWorkflowType.CLOSE)){
			saveDailyTracking(api);
		}

		return TaskWorkflowJSON.toJSON(workflow);
	}
	
	
	private JSONObject updateWorkflow(AonApiData api) {
		JSONObject params = api.getData();
		
		Integer taskId   = params.optInt(IJsonNames.TASK);
		Integer workflow = params.optInt(IJsonNames.WORKFLOW);
		String comment   = params.optString(IJsonNames.COMMENT);
		
		if(workflow!=0 && !comment.isEmpty()) {
			TaskWorkflow data = AON_SOLUTIONS.getTaskWorkflow(
				api.getDomain(), new User(), 
				f-> f.getDomainProperty().eq(api.getDomain().getId())
				.and(f.getTaskProperty().eq(taskId))
				.and(f.getIdProperty().eq(workflow))
			);
			data.setComment(comment);
			
			return TaskWorkflowJSON.toJSON(AON_SOLUTIONS.saveTaskWorkflow(api.getDomain(), api.getUser(), data));
		}
		
		return new JSONObject();

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

	private JSONObject saveTaskAttach(AonApiData api) {
		JSONObject params = api.getData();
		JSONObject json   = new JSONObject();
		Task task         = TaskJSON.fromJSON(params.optJSONObject(IJsonNames.TASK));
		Domain domain     = task.getDomain();

		if(params.opt(IJsonNames.FILE)!= null) { 
			JSONObject file    = params.optJSONObject(IJsonNames.FILE);
			String base64      = file.optString(IJsonNames.CONTENT);
			String contentType = file.optString(IJsonNames.CONTENT_TYPE);
			byte[] fileData    = Base64.getDecoder().decode(base64);
			
			TaskAttach taskAttach = new TaskAttach()
			.setDomain(domain.getId())
			.setTask(task.getId())
			.setData(fileData)
			.setMimetype(MimeType.get(contentType));
			
			json = TaskAttachJSON.toJSON(TaskUtils.saveTaskAttach(domain, api.getUser(), taskAttach));
			json.put("domain_name", domain.getName());
			json.put("attach_type", "task");
		}
		return json;
	}
	
	private JSONObject getTaskCount(AonApiData api) {
		
		JSONObject json = new JSONObject();
		
		Map<String, Integer> counts = AON_SOLUTIONS.getTaskCount(api.getDomain(), api.getUser(), 
				f -> TaskFilter.taskSenderCount(api, api.getDomain(), f, new Customer()), 
				f -> TaskFilter.taskReceiverCount(api, api.getDomain(), f, new Customer())
		);
		
		counts.keySet().stream().forEach(k-> json.put(k, counts.get(k)) );
		
		
//		Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
//		AON.getDomainOfficeLinked(api.getDomain(), api.getUser().getLogin()).stream().forEach(domain -> {
//			Customer customer = AON.getCustomer(domain.getName(), domain.getId(), "", f -> f.getDomainProperty().eq(domain.getId()).and(f.getDocumentProperty().eq(company.getDocument())));
//			HashMap<String, Integer> aux = AON_SOLUTIONS.getTaskCount(
//					domain, new User(),
//					f -> TaskFilter.taskSenderCount(api, domain, f, customer),
//					f -> TaskFilter.taskReceiverCount(api, domain, f, customer)
//			);
//			
//			aux.keySet().stream().forEach(key -> {
//				if(counts.containsKey(key)) 
//					counts.put(key, counts.get(key) + aux.get(key));
//				else 
//					counts.put(key, aux.get(key));
//			});
//		});

		
		return json;
	}
	
	private JSONObject getTaskGeneralCount(AonApiData api) {
		TaskCounts taskCounts = AON_SOLUTIONS.getTaskGeneralCount(api.getDomain(), api.getUser(), 
			Optional.empty(),
			Optional.of( f-> TaskFilter.taskWorkgroupCount(f, api, api.getDomain()) ),
			Optional.of( f-> TaskFilter.taskTagCount(f, api, api.getDomain()) )
		);
		
		return taskCounts.toJSON();
	}
	
	private JSONObject getTaskStatusCount(AonApiData api) {
		TaskCounts taskCounts = AON_SOLUTIONS.getTaskGeneralCount(api.getDomain(), api.getUser(), 
			Optional.of( f-> TaskFilter.taskStatusCount(f, api, api.getDomain()) ),
			Optional.empty(),
			Optional.empty()
		);
		
		return taskCounts.toJSON();
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

		Auth auth = aonToken.getAuth() != null
			? AON_SOLUTIONS.getAuth(aonToken.getAuth())
			: AON_SOLUTIONS.getAuth(api.getUser().getAuth().getAuth());
		json.put(IJsonNames.AUTH, AuthJSON.toJSON(auth));
		
		return json;
	}
	
	private JSONObject deleteTask(AonApiData api) {
		Task task     = TaskJSON.fromJSON(api.getData());
		Domain domain = task.getDomain();
		AON_SOLUTIONS.deleteTask(domain, api.getUser(), task.getId());
		return new JSONObject();
	}
	
	private JSONObject deleteTaskWorkflow(AonApiData api) {
		JSONObject params = api.getData();
		Domain domain     = DomainJSON.fromJSON(params.optJSONObject(IJsonNames.DOMAIN));
		Integer workflow  = params.optInt(IJsonNames.WORKFLOW);
		
		AON_SOLUTIONS.deleteTaskWorkflow(domain, api.getUser(), workflow);
		return new JSONObject();
	}
	
	private JSONObject deleteTaskTag(AonApiData api) {
		AON.deleteTag(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), TagJSON.fromJSON(api.getData()));
		return new JSONObject();
	}
	
	private JSONArray sendTaskHistoric(AonApiData api) {
		 JSONObject params = api.getData();
		 
		 Integer taskId = params.optInt(IJsonNames.TASK);
		 Integer workflowId = params.optInt("workflowId");
		 
		 JSONArray json = new JSONArray();
		 
		 if(taskId!=0 && workflowId !=0) {
			
			Task task = AON_SOLUTIONS.getTask(api.getDomain(), api.getUser(), f-> f.getIdProperty().eq(taskId));
			
			LinkedList<TaskWorkflow> taskWorkflow = AON_SOLUTIONS.getTaskWorkflowStream(api.getDomain(), api.getUser(), 
					 f->f.getTaskProperty().eq(task.getId())
					 .and(f.getTypeProperty().eq(TaskWorkflowType.COMMENT.value()))
					 .and(f.getNotificationUserProperty().isNotNull())
					 .or(f.getIdProperty().eq(workflowId))
			 ).sorted((t1, t2)-> t2.getId().compareTo(t1.getId())).collect(Collectors.toCollection(LinkedList::new));
			
			task.setWorkflows(taskWorkflow);
			
			TaskNotification.sendHistoricWorkflow(api, task, true);
			
			Integer[] ids = taskWorkflow.stream().map(workflow->{
				workflow.setNotificationDate(new Date());
				return workflow.getId();
			}).toArray(Integer[]::new);
		
			AON_SOLUTIONS.updateTaskWorkflowBetween(api.getDomain(), api.getUser(),  f->f.getIdProperty().in(ids) );
			
			json = TaskWorkflowJSON.toJSON(taskWorkflow);
		 }

		return json;
	}

	private JSONObject saveBranch(AonApiData api) {
		JSONObject params = api.getData();
		Domain domain = api.getDomain();
		User user     = api.getUser();
		String login  = user.getLogin();
		Integer id    = params.optInt(IJsonNames.TASK);
		
		TaskWorkflow workflow = TaskWorkflowJSON.fromJSON(params);
		
		String comment = workflow.getComment();
		
		Workgroup workgroup = WorkgroupJSON.fromJSON(params.optJSONObject(IJsonNames.WORKGROUP));
		
		TaskHolder sender   = workflow.getTaskHolder();
		
		TaskHolder receiver = TaskHolderJSON.fromJSON(params.optJSONObject("task_holder_receiver"));
		
		User userReceiver = AON.getUser(domain, login, f->f.getIdProperty().eq(receiver.getUserId()));
	
		Task task = AON_SOLUTIONS.getTaskAndChilds(domain, userReceiver, f-> f.getIdProperty().eq(id) );
		
	    Integer taskId = task.getId();
	    
		if(task.getSource().equals(TaskSource.TASK) && task.isChild()) {
			taskId = task.getParent();
		} else {
			 task.setStatus(TaskStatus.IN_PROGRESS);
		}

	    AON_SOLUTIONS.saveTask(api.getDomain(), user, task);
		   
	    task.setId(null) 
	    .setGtaskId(null)
	    .setParent(taskId)
	    .setSender(sender)
	    .setTaskHolder(receiver)
	    .setWorkgroup(workgroup)
	    .setStatus(TaskStatus.PENDING)
	    .setSource(TaskSource.TASK);

	    Task newTask = AON_SOLUTIONS.saveTask(api.getDomain(), user, task);
		   
	    // SAVE CONNECTED TASK PARENT
	    saveWorkflow( api, Optional.of(workflow.setTask(taskId).setComment(newTask.getNumber().toString())), true); 
		   
	    //  ASIGNED NEW TASK
	   TaskWorkflow assign = workflow.clone()
       .setTask(newTask.getId())
	   .setComment(receiver.getName())
	   .setTaskHolder(sender)
	   .setType(TaskWorkflowType.ASSIGN)
	   .setDomain(newTask.getDomain().getId())
	   ;

	   // SAVE ASSIGN
	   saveWorkflow( api, Optional.of(assign), false);

	   // SAVE COMMENT
	   TaskWorkflow commentNew = assign.clone()
	   .setComment(comment!=null && !comment.isEmpty() ? comment : "")
	   .setType(TaskWorkflowType.COMMENT);
	   
	   saveWorkflow( api, Optional.of(commentNew), false);
	   
	   TaskUtils.onNotification(api, commentNew.setType(TaskWorkflowType.ASSIGN));

	   return new JSONObject();
    }
	
	//TODO 
	private void setTasksOffice(AonApiData api, List<Task> tasks) {
		List<String> errors = new ArrayList<>();
		if(!DomainType.CONSULTANCY.equals(api.getDomain().getDomainType())) {
			
			JSONObject params = api.getData();
			
			Integer page       = params.optInt(IJsonNames.PAGE);
			Integer perPage    = params.optInt(IJsonNames.PER_PAGE);
			
			boolean isCau     = TaskUtils.isCau(params);

			Company company   = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		
			String companyDoc = company.getDocument();
			
			AON.getDomainOfficeLinked(company)
			.forEach(domain -> {
				try {
					
					Customer customer = AON.getCustomer(domain.getName(), domain.getId(), "", 
						f -> f.getDomainProperty().eq(domain.getId()).and(f.getDocumentProperty().eq(companyDoc))
					);
					
					if(customer.getId()!=null) {
						if(isCau) {
							AON_SOLUTIONS.getTaskAndChildsStream(domain, new User(), f -> TaskFilter.task(api, f, domain, customer), page, perPage)
							.forEach(tasks::add);
						} else {
							AON_SOLUTIONS.getTaskParentOrChildStream(domain, new User(), f -> TaskFilter.task(api, f, domain, customer), page, perPage)
							.forEach(tasks::add);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					errors.add(e.getMessage());
				}
			});
		}
		
	}
	
	private List<ApplicationParameter> getAppParamsList(AonApiData api, List<String> listNames) {
		if(!listNames.isEmpty()) {
			return AON.getApplicationParameterStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					f-> f.getDomainProperty().eq(api.getDomain().getId())
					.and(f.getNameProperty().in(listNames.toArray(String[]::new)))
			).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
	
	private void setWgAndThDefault(AonApiData api, Task task) {
		try {
			boolean isCau = TaskUtils.isCau(api.getData());
			boolean workgroupExist = task.getWorkgroup().getId()!=null;
			boolean taskHolderExist = task.getTaskHolder().getId()!=null;
			if(!workgroupExist || !taskHolderExist) {
				List<String> params = new ArrayList<>();
				if(isCau) {
					params.add(AppParamsRequest.APP_REQUESTS_EXT_WORKGROUP.name());
					params.add(AppParamsRequest.APP_REQUESTS_EXT_TASK_HOLDER.name());
				} else {
					params.add(AppParamsRequest.APP_REQUESTS_INT_WORKGROUP.name());
					params.add(AppParamsRequest.APP_REQUESTS_INT_TASK_HOLDER.name());
				}

				List<ApplicationParameter> appParams = getAppParamsList(api, params).stream().filter(p-> p.getName()!=null && p.getValue()!=null).collect(Collectors.toList());
				if(!appParams.isEmpty()) {
					
					if(!workgroupExist) {
						appParams.stream().filter(p-> 
							p.getName().contentEquals(isCau ? AppParamsRequest.APP_REQUESTS_EXT_WORKGROUP.name() : AppParamsRequest.APP_REQUESTS_INT_WORKGROUP.name())
						).findFirst().ifPresent(d->
							task.setWorkgroup(new Workgroup().setId(Integer.parseInt(d.getValue())))
						);
					}

					if(!taskHolderExist) { 
						appParams.stream().filter(p-> 
							p.getName().contentEquals(isCau ? AppParamsRequest.APP_REQUESTS_EXT_TASK_HOLDER.name() : AppParamsRequest.APP_REQUESTS_INT_TASK_HOLDER.name())
						)
						.findFirst()
						.ifPresent(d->{
							TaskHolder th = new TaskHolder();
							th.setId(Integer.parseInt(d.getValue()));
							task.setTaskHolder(th);
						});
					}
				}
			}
		} catch (Exception e) {}
	}
	
	private File getTaskExcel(AonApiData api) throws Exception {
		LOGGER.info("[GET] TASK SERVLET EXCEL");


		File file = File.createTempFile("task", "");
		TaskExcel.buildExcel(new FileOutputStream(file), api);
		return file;
	}
	
	//---------DAILY_TRACKING----------
	private void saveDailyTracking(AonApiData api) {
		JSONObject json = api.getData().optJSONObject("dailyTracking");
		if(json!=null) {
			LOGGER.info("---- SAVE DAILY_TRACKING------");
			DailyTracking dailyTracking = DailyTrackingJSON.fromJSON(json);
			AON_SOLUTIONS.saveDailyTracking(dailyTracking.getDomain(), api.getUser(), dailyTracking);
		}
	}
	
	private void setDomain(AonApiData api) {
		JSONObject params = api.getData();
		
		int domainId = params.optInt(IJsonNames.DOMAIN_ID);
		String domainName = params.optString(IJsonNames.DOMAIN_NAME);
		String domainType = params.optString(IJsonNames.DOMAIN_TYPE);
		
		if(domainId!=0 && !domainName.isEmpty()) {
			Domain domain = new Domain()
			.setId(domainId)
			.setName(domainName);
			
			if(!domainType.isEmpty()) {
				domain.setDomainType(DomainType.safeValueOf(domainType));
			}
	
			api.setDomain(domain);
		}
	}
}
