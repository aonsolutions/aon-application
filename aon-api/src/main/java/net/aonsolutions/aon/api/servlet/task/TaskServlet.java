package net.aonsolutions.aon.api.servlet.task;

import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
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
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.TaskProperties;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.NotificationSource;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskAttach;
import com.esferalia.aon.occam.api.model.task.TaskSource;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.api.model.task.TaskWorkflowType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.model.mail.TaskMail;
import net.aonsolutions.aon.api.notification.NotificationRequest;
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
					response(req, resp,  getTasksAttach(api));
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
	
	private Object getTasks(AonApiData api) {
		Integer page = api.getParams().optInt(IJsonNames.PAGE);
		Integer perPage = api.getParams().optInt(IJsonNames.PER_PAGE);
		return TaskJSON.toJSON(
				AON_SOLUTIONS.getTaskStream(api.getDomain(), api.getUser(), f -> taskFilter(api, f), page, perPage));
	}
	
	private Filter taskFilter(AonApiData api, TaskProperties f) {
		Integer workgroup = api.getParams().optInt("workgroup");
		Integer taskHolder = api.getParams().optInt("task_holder");
		Integer sender = api.getParams().optInt("sender");
		Integer registry = api.getParams().optInt("registry");
		String status = api.getParams().optString("status");
		String source = api.getParams().optString("source");
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
		
		if(registry != null && registry!=0) 
			filter = filter.and(f.getRegistryProperty().eq(registry));
		
		if(!search.isEmpty()) {
			Filter filter1 = filter.and(f.getDescriptionProperty().like("%" + search + "%"));
			Integer numberSearch = 0;
			try { 
				numberSearch = Integer.parseInt(search.replaceAll("[^\\d]", ""));} 
			catch(NumberFormatException e){
				e.printStackTrace();
			}
			if(numberSearch!=0)
				filter1 = filter1.or(f.getNumberProperty().like(numberSearch));
			
			filter = filter.and(filter1);
		}
		
		if(!api.getParams().optString("cau").isEmpty() && api.getParams().optInt("cau")>0) {
			String email = api.getParams().optString(IJsonNames.EMAIL);
			if(!email.isEmpty())
				filter = filter.and(f.getGtaskIdProperty().eq(email));
		}
		
		if(!api.getParams().optString("startDate").isEmpty()) {
			Date startDate = AonDateUtils.parse(api.getParams().optString("startDate"), "yyyy-MM-dd");
			filter = filter.and(f.getStartDateProperty().eq(AonDateUtils.toTimestamp(startDate)));
		}
			
		
		return filter;
	}
	
	private Object getTask(AonApiData api) {
		Integer taskId = api.getParams().optInt("id");
		return TaskJSON.toJSON( AON_SOLUTIONS.getTask(api.getDomain(), api.getUser(), f-> f.getIdProperty().eq(taskId) )   );
	}
	
	private Object saveTask(AonApiData api) {
		Task task = TaskJSON.fromJSON(api.getData());
		boolean edit = task.getId() != null;
		setCauData(api, task);
		if(edit) {
			checkFiles(api, task);
		}
		task = AON_SOLUTIONS.saveTask(api.getDomain(), api.getUser(), task);
		
		if(!edit) {
			checkFiles(api, task);
			task = AON_SOLUTIONS.saveTask(api.getDomain(), api.getUser(), task);
		}
		
		if(!task.getWorkflows().isEmpty()) {
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
	
	private Object getTaskTags(AonApiData api) {
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
		sendWorkflowCommunication(api, workflow);
		return TaskWorkflowJSON.toJSON(workflow);
	}

	private JSONObject saveTaskTag(AonApiData api) {
		Domain domain = api.getDomain();
		Tag tag =  TagJSON.fromJSON(api.getData());
		if(tag.getId()!=null) {
			AON.updateTag(domain.getName(), domain.getId(), api.getUser().getLogin(), tag); 
		} else {
			tag = AON.insertTag(domain.getName(), domain.getId(), api.getUser().getLogin(), tag);
		}
		return TagJSON.toJSON(tag);
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
		AON_SOLUTIONS.getTaskStatusCount(api.getDomain(), api.getUser(),  f -> taskFilterCount(api, f))
		.forEach((k,v)->json.put(TaskStatus.safeValueOf(k).getName(), v));
		return json;
	}
	
	private JSONObject getTaskCount(AonApiData api) {
		JSONObject json = new JSONObject();
		Domain domain = api.getDomain();
		Integer taskHolder = api.getParams().getInt("task_holder");
		AON_SOLUTIONS.getTaskCount(api.getDomain(), api.getUser(),  
				f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getStatusProperty().eq(TaskStatus.PENDING.value())), 
				taskHolder
		)
		.forEach((k,v) -> json.put(k, v));
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
	
	private JSONObject deleteTaskTag(AonApiData api) {
		AON.deleteTag(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),TagJSON.fromJSON(api.getData()));
		return new JSONObject();
	}
	
	private void sendWorkflowCommunication(AonApiData api, TaskWorkflow workflow) {
		Thread newThread = new Thread(() -> {
			try {
				if(workflow.getType().getName().equalsIgnoreCase(TaskWorkflowType.CLOSE.getName())) {
				    Task task = AON_SOLUTIONS.getTask(api.getDomain(), api.getUser(), f-> f.getIdProperty().eq(workflow.getTask()));
//					if(!task.getGtaskId().isEmpty() && task.getGtaskId().indexOf("@")>=0) {
//						Auth auth = AON_SOLUTIONS.getAuth(task.getGtaskId());
						AonToken aonToken = SECURITY.getAonToken(api.getToken());
						Auth auth = AON_SOLUTIONS.getAuth(aonToken.getSchemaFirstDomain(), 0, aonToken.getAuth());
						if(!auth.getEmail().isEmpty()) {
							sendNotification(api, task, workflow, auth);
							sendEmail(api, task, workflow, auth);
						}
//					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		newThread.start();
	}
	
	private JSONObject taskHistoricSend(AonApiData api) {
		 Integer workflowId = api.getData().optInt("workflowId");
		 if(workflowId > 0) {
			AonToken aonToken = SECURITY.getAonToken(api.getToken());
			Auth auth = AON_SOLUTIONS.getAuth(aonToken.getSchemaFirstDomain(), 0, aonToken.getAuth());
			Task task = TaskJSON.fromJSON(api.getData());
			LinkedList<TaskWorkflow> taskWorkflow = AON_SOLUTIONS.getTaskWorkflowStream(api.getDomain(), api.getUser(), 
					 f->f.getTaskProperty().eq(task.getId())
					 .and(f.getTypeProperty().eq(TaskWorkflowType.COMMENT.value()))
					 .and(f.getIdProperty().le(workflowId))
			 ).sorted((t1, t2)-> t2.getId().compareTo(t1.getId())) .collect(Collectors.toCollection(LinkedList::new));
			
			task.setWorkflows(taskWorkflow);
			sendHistoricWorkflow(api, task, auth);
		 }

		return new JSONObject();
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
	
	private void sendNotification(AonApiData api, Task task, TaskWorkflow workflow, Auth auth){
		try {
			User myUser = AON_SOLUTIONS.getUser(api.getDomain(), api.getToken());
			String title = "SOLICITUD | AON SOLUTIONS";
			String body = "Solicitud Nº "+task.getNumber() + " Cerrada";
			LinkedList<Auth> auths = new LinkedList<>();
			auths.add(auth);

	    	NotificationRequest notification = new NotificationRequest();
	    	notification.setTitle(title);
	    	notification.setBody(body);
	    	notification.setSender(myUser.getAuth());
	    	notification.setDomain(api.getDomain());
	    	notification.setUser(api.getUser());
	    	notification.setSource(NotificationSource.MESSENGER);
	    	notification.setSourceId(task.getId());
	    	notification.setAuths( auths );
	    	notification.send();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendEmail(AonApiData api, Task task, TaskWorkflow workflow, Auth auth){
		try {
			Company company = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(api.getDomain().getId()));
			if(company!=null) {
				String logo = getLogoCompany(company.getDomain().getName());
				
				String to = auth.getEmail();
				
				String subject = "SOLICITUD Nº "+ task.getNumber();
				
				String url = "https://aon.solutions";
				
				TaskMail tm = new TaskMail()
				.setNumber(task.getNumber().toString())
				.setDate(task.getModificationDate())
				.setUrl(url)
				.setTitle(task.getTitle())
				.setLogo(logo);
				
				String body = taskContentEmail(tm);
				
				SESMessage msg = new SESMessage()
				.setAlias(company.getName())
				.setSubject(subject)
				.setBody(body)
				.setTo(to);
				
			    SES.sendEmail(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void checkFiles(AonApiData api, Task task){
		JSONArray files = JsonUtils.getJSONArray(api.getData(), "files");
		Domain domain = api.getDomain();
		 for (int i = 0 ; i < files.length(); i++) {
			try {
			    JSONObject file = files.getJSONObject(i);
			    String     dataId =  file.optString("id");
			    Matcher    matcher = regexFile(task, dataId);
			    if(matcher!=null) {
					String base64 = file.optString("content");
					String contentType = file.optString("contentType");
					byte[] fileData = Base64.getDecoder().decode(base64);
					TaskAttach taskAttach = new TaskAttach()
					.setDomain(api.getDomain().getId())
					.setTask(task.getId())
					.setData(fileData)
					.setMimetype(MimeType.get(contentType));
										
					taskAttach = AON_SOLUTIONS.saveTaskAttach(domain, api.getUser(), taskAttach);
					
					JSONObject jsonFile = new JSONObject(); 
					jsonFile.put("domain_name", domain.getName());
					jsonFile.put("domain_id", domain.getId());
					jsonFile.put("attach_type", "task");
					jsonFile.put("id", taskAttach.getId());

					String base64FileStr = new String(Base64.getEncoder().encode(jsonFile.toString().getBytes()));

	                String link = "/ms/api/file/"+base64FileStr;
	                task.setDescription(matcher.replaceAll("$1" + link + "$3"));
			    }

			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	}
	
	private Matcher regexFile(Task task, String dataId) {
		String description = task.getDescription();
	    String regex = "(\\<\\S[^<>]*?href=[\\\\]?\")(blob[^\"\\\\]*?)([\\\\]?\"[^<>]*?data-id=[\\\\]?\""+dataId+"[\\\\]?\"[^<>]*?\\>)";
	    Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	    Matcher matcher = pattern.matcher(description);
	    if(!matcher.find()) {
		    regex = "(\\<\\S[^<>]*?src=[\\\\]?\")(blob[^\"\\\\]*?)([\\\\]?\"[^<>]*?data-id=[\\\\]?\""+dataId+"[\\\\]?\"[^<>]*?\\>)";
		    pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		    matcher = pattern.matcher(description);
		    if(!matcher.find()) {
		    	return null;
		    }
	    }
	    return matcher;
	}
	
	private String getLogoCompany(String companyName) {
		String logo = "https://aon.solutions/assets/aon-logo.png";
		try {
			String urlLogo = "https://" + companyName + "/aonDocuments/company.logo";
		    final URL url = new URL(urlLogo);
	        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
	        int statusCode = connection.getResponseCode();
	        if(200 == statusCode) {
	        	logo = urlLogo;
	        }
            connection.disconnect();
		}catch (Exception e) {}

		return logo;
	}
	
	private String taskContentEmail(TaskMail taskMail) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();	
		
		VelocityContext context = new VelocityContext();
		context.put("task", taskMail);
		
		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/task.vm");
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
	
	private void sendHistoricWorkflow(AonApiData api, Task task, Auth auth) {
		Thread newThread = new Thread(() -> {
			Company company = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(api.getDomain().getId()));
			if(company!=null) {
				String logo = getLogoCompany(company.getDomain().getName());
				
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
				
				String body = emailTaskWorkflowContent(tm);
				
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
	
	private String emailTaskWorkflowContent(TaskMail taskMail) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();	
		
		VelocityContext context = new VelocityContext();
		context.put("task", taskMail);
		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/task-historic.vm");
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		return writer.toString();
	}
}
