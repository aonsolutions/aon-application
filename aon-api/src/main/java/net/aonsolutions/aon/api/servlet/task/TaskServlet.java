package net.aonsolutions.aon.api.servlet.task;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskAttach;
import com.esferalia.aon.occam.api.model.task.TaskCounts;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.api.model.task.TaskWorkflowType;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.MimeType;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
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
				case "/general/count":
					response(req, resp, getTaskGeneralCount(api));
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
				case "/historic":
					response(req, resp, sendTaskHistoric(api));
					break;
				case "/historic-email":
					response(req, resp, sendTaskHistoricEmail(api));
					break;
				case "/workflow":
					response(req, resp, saveWorkflow(api, Optional.empty()));
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
		JSONObject params = api.getData();
		Integer page = params.optInt(IJsonNames.PAGE);
		Integer perPage = params.optInt(IJsonNames.PER_PAGE);
		
		List<Task> list = new ArrayList<>(); 
		
		if(TaskUtils.isCau(params) && params.optString(IJsonNames.EMAIL).isEmpty()) {
			throw new AonApiException("Auth inexistente");
		}
		
		list.addAll(
			AON_SOLUTIONS.getTaskStream(api.getDomain(), api.getUser(), 
				f -> TaskFilter.task(api, f, api.getDomain(), new Customer()), page, perPage
			)
			.collect(Collectors.toList())
		);
		
		if(page==1) {
			List<Task> listOffice = getTasksOffice(api);
			list.addAll(listOffice);
		}
		
		list = list.stream().filter(TaskUtils.distinctByKey(Task::getId)).collect(Collectors.toList());
		
		return TaskJSON.toJSON(list);
		
//		JSONArray arr = new JSONArray();
//		
//		List<Task> childs = list.stream().filter(t -> t.getParent()!=null).collect(Collectors.toList());
//		
//		list.stream().filter(f-> f.getParent()==null).forEach(task->{
//			JSONObject json = TaskJSON.toJSON(task);
//			json.put("childs", TaskJSON.toJSON( childs.stream().filter(t -> t.getParent().equals(task.getId())) ));
//			arr.put(json);
//		});

//		return arr;
	}

	private JSONObject getTask(AonApiData api) {
		Domain domain = api.getDomain();
		User user = api.getUser();
		JSONObject params = api.getData();
		Integer taskId = params.optInt(IJsonNames.ID);
		Integer number = params.optInt(IJsonNames.NUMBER);

		Task task = AON_SOLUTIONS.getTask(domain, user, f-> 
			number > 0 
			? f.getNumberProperty().eq(number) 
			: f.getIdProperty().eq(taskId) 
		);
		
		if(task.getId()==null) {
			throw new AonApiException(AonApiError.EMPTY_DATA.getMessage());
		}
		
		JSONObject json = TaskJSON.toJSON(task);
		
		if(task.getParent()!=null) {
			json.put("parentObj", TaskJSON.toJSON( AON_SOLUTIONS.getTask(domain, user, f-> f.getIdProperty().eq(task.getParent()) ) ));
		} else {
			json.put("childs", TaskJSON.toJSON( AON_SOLUTIONS.getTaskStream(domain, user, f-> f.getParentProperty().eq(task.getId()) ) ));
		}
		
		return json;
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
		if(TaskUtils.isCau(api.getData())) 
			TaskUtils.setCauInfo(api, task);
		
		if(edit) 
			TaskUtils.checkFilesAndSave(api, task);
		else 
		 setWgAndThDefault(api, task);

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
				
				saveWorkflow( api, Optional.of(w.setTask(taskId)) );
			}); // SAVE WORKFLOW ALL
		}
		return TaskJSON.toJSON(task);
	}
	
	private JSONObject saveWorkflow(AonApiData api, Optional<TaskWorkflow> workflowOpt) {
		TaskWorkflow workflowTmp = workflowOpt.isPresent() ? workflowOpt.get() : TaskWorkflowJSON.fromJSON(api.getData());
		
		if(TaskUtils.isCau(api.getData())) 
			TaskUtils.setCauWorkflow(api, workflowTmp);
		
		if(workflowTmp.getDomain()==null) 
			workflowTmp.setDomain(api.getDomain().getId());	
	
		TaskWorkflow workflow = AON_SOLUTIONS.saveTaskWorkflow(api.getDomain(), api.getUser(), workflowTmp);
		TaskUtils.onSaveWorkflow(api, workflow);
		return TaskWorkflowJSON.toJSON(workflow);
	}
	
	
	private JSONObject updateWorkflow(AonApiData api) {
		JSONObject params = api.getData();
		Integer taskId = params.optInt(IJsonNames.TASK);
		Integer workflow = params.getInt(IJsonNames.WORKFLOW);
		String comment = params.optString(IJsonNames.COMMENT);
		
		if(!comment.isEmpty()) {
			TaskWorkflow data = AON_SOLUTIONS.getTaskWorkflow(
				api.getDomain(), new User(), 
				f-> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getTaskProperty().eq(taskId)).and(f.getIdProperty().eq(workflow))
			);
			data.setComment(comment);
			
			return TaskWorkflowJSON.toJSON(AON_SOLUTIONS.saveTaskWorkflow(api.getDomain(), api.getUser(), data));
		}
		
		return new JSONObject();

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
		JSONObject json = new JSONObject();
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
			
			json = TaskAttachJSON.toJSON(AON_SOLUTIONS.saveTaskAttach(domain, api.getUser(), taskAttach));
			json.put("domain_name", domain.getName());
			json.put("attach_type", "task");
		}
		return json;
	}
	
	private JSONObject getTaskCount(AonApiData api) {
	
		Map<String, Integer> counts = AON_SOLUTIONS.getTaskCount(api.getDomain(), api.getUser(), 
				f -> TaskFilter.taskSenderCount(api, api.getDomain(), f, new Customer()), 
				f -> TaskFilter.taskReceiverCount(api, api.getDomain(), f, new Customer())
		);
		
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
		
		JSONObject json = new JSONObject();
		counts.keySet().stream().forEach(k-> json.put(k, counts.get(k)) );
		
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
			Optional.of( f-> TaskFilter.taskStatusCount(f, api, api.getDomain(), new Customer()) ),
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
		Auth auth = AON_SOLUTIONS.getAuth(aonToken.getSchemaFirstDomain(), 0, aonToken.getAuth());
		json.put(IJsonNames.AUTH, AuthJSON.toJSON(auth));
		
		return json;
	}
	
	private JSONObject deleteTask(AonApiData api) {
		Integer task = api.getData().optInt(IJsonNames.TASK);
		AON_SOLUTIONS.deleteTask(api.getDomain(), api.getUser(), task);
		return new JSONObject();
	}
	
	private JSONObject deleteTaskWorkflow(AonApiData api) {
		Integer id = api.getData().optInt(IJsonNames.ID);
		AON_SOLUTIONS.deleteTaskWorkflow(api.getDomain(), api.getUser(), id);
		return new JSONObject();
	}
	
	private JSONObject deleteTaskTag(AonApiData api) {
		AON.deleteTag(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),TagJSON.fromJSON(api.getData()));
		return new JSONObject();
	}
	
	private JSONArray sendTaskHistoric(AonApiData api) {
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

	private JSONObject sendTaskHistoricEmail(AonApiData api) {
		
		JSONObject params = api.getData();
		
		Domain domain = api.getDomain();
		
		String login = api.getUser().getLogin();
		
		Integer taskId = params.optInt(IJsonNames.TASK);
		
		TaskWorkflow workflow = TaskWorkflowJSON.fromJSON(params);
		
		String comment = workflow.getComment();
		
		TaskHolder taskHolderReceiver = TaskHolderJSON.fromJSON(params.optJSONObject("task_holder_receiver"));
		
		Workgroup workgroup = WorkgroupJSON.fromJSON(params.optJSONObject(IJsonNames.WORKGROUP));

		User userReceiver = AON.getUser(domain, login, f->f.getIdProperty().eq(taskHolderReceiver.getUserId()));
		
		Auth auth = AON_SOLUTIONS.getAuth(userReceiver.getAuth().getAuth());
		
	    String email = auth.getEmail();
		
		Task taskParent = AON_SOLUTIONS.getTask(domain, userReceiver, f-> f.getIdProperty().eq(taskId) );
		   
		AON_SOLUTIONS.saveTask(api.getDomain(), api.getUser(), taskParent.setStatus(TaskStatus.IN_PROGRESS));
		
		List<Byte> types = new ArrayList<>(Arrays.asList(TaskWorkflowType.OPEN.value()));
		
	    LinkedList<TaskWorkflow> taskWorkflow = AON_SOLUTIONS.getTaskWorkflowStream(domain, new User(), 
				f->f.getTaskProperty().eq(taskId)
				.and(f.getTypeProperty().in(types.toArray(Byte[]::new)))
		).sorted((t1, t2)-> t2.getId().compareTo(t1.getId())).collect(Collectors.toCollection(LinkedList::new));
	    
	   
	   taskParent.setTaskHolder(taskHolderReceiver);
	   taskParent.setWorkgroup(workgroup);
	   taskParent.setStatus(TaskStatus.PENDING);
	   
	   taskParent.setId(null);
	   
	   taskParent.setGtaskId(null);
	   
	   taskParent.setParent(taskId);
	   
	   taskParent.setSender(workflow.getTaskHolder());

	   Task newTask = AON_SOLUTIONS.saveTask(api.getDomain(), api.getUser(), taskParent);
	   
	   saveWorkflow( api, Optional.of(workflow.setTask(taskId).setComment(newTask.getNumber().toString())) ); // SAVE CONNECTED TASK PARENT

	   taskWorkflow.forEach(wf->{
		    wf.setId(null);
		   	wf.setTask(newTask.getId());
		   	
		    if(wf.getType().equals(TaskWorkflowType.OPEN)) {
		    	wf.setComment(comment);
			    wf.setEmail(workflow.getEmail());
			    wf.setTaskHolder(workflow.getTaskHolder());
		    }
	
			saveWorkflow( api, Optional.of(wf) );
	   });
  
	   newTask.setGtaskId(email);
	   
	   if(comment!=null && !comment.isEmpty())
		   newTask.setGtasklistId(comment);
	   
	   newTask.setWorkflows(newTask.getWorkflows().stream().filter(w-> w.getType().equals(TaskWorkflowType.CLOSE)).collect(Collectors.toList()));
	   
	   TaskNotification.sendHistoricWorkflow(api, newTask, false);
	

	   return new JSONObject();
   }

	private List<Task> getTasksOffice(AonApiData api) {
		List<Task> list = new ArrayList<>();
		if(!DomainType.CONSULTANCY.equals(api.getDomain().getDomainType())) {
//			String status = api.getParams().optString("status");
//			if(status.isEmpty() || "pending".equalsIgnoreCase(status)) {
				Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
				
				AON.getDomainOfficeLinked(api.getDomain(), api.getUser().getLogin()).stream().forEach(domain -> {
				
					Customer customer = AON.getCustomer(domain.getName(), domain.getId(), "", f -> f.getDomainProperty().eq(domain.getId()).and(f.getDocumentProperty().eq(company.getDocument())));
				
					AON_SOLUTIONS.getTaskStream(domain, new User(), f -> TaskFilter.task(api, f, domain, customer))
					.forEach(list::add);
				});
//			}
		}
		return list;
	}
	
	private List<ApplicationParameter> getAppParamsList(AonApiData api, List<String> listNames) {
		if(!listNames.isEmpty()) {
		    String[] names = listNames.toArray(String[]::new);
			
			return AON.getApplicationParameterStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					f-> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getNameProperty().in(names))).collect(Collectors.toList());
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
						).findFirst().ifPresent(d->{
							TaskHolder th = new TaskHolder();
							th.setId(Integer.parseInt(d.getValue()));
							task.setTaskHolder(th);
						});
					}
				}
			}
		} catch (Exception e) {}
	}
	
}
