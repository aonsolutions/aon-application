package net.aonsolutions.aon.api.utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jooq.tools.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.json.AuthJSON;
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.TaskProperties;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.NotificationSource;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskAttach;
import com.esferalia.aon.occam.api.model.task.TaskSource;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.api.model.task.TaskWorkflowType;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.model.mail.TaskMail;
import net.aonsolutions.aon.api.model.mail.TaskMailTemplate;
import net.aonsolutions.aon.api.notification.NotificationRequest;
import net.aonsolutions.aon.api.servlet.task.AppParamsRequest;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

public class TaskUtils {

	private TaskUtils() {
	    throw new IllegalStateException("Utility class");
	}
	
	private static final String EMAIL_SUPPORT = "soporte@aonsolutions.es";
	private static final String URL_BASE = "https://aon.solutions";
	  
	public static Filter taskFilter(AonApiData api, TaskProperties f, Domain domain, Customer customer) {
		JSONObject params = api.getParams();
		String email  = params.optString(IJsonNames.EMAIL);
		String search = params.optString(IJsonNames.SEARCH);
		String status = params.optString(IJsonNames.STATUS);

		String workgroupStr = params.optString(IJsonNames.WORKGROUPS);
		
		Filter filter = f.getDomainProperty().eq(domain.getId());
		if("pending".equalsIgnoreCase(status) || ( status.isEmpty() && customer!=null && customer.getId()!=null) ) 
			filter = filter.and(f.getStatusProperty().eq(TaskStatus.IN_PROGRESS.value()).or(f.getStatusProperty().eq(TaskStatus.PENDING.value())));
		else if(!status.isEmpty())
			filter = filter.and(f.getStatusProperty().eq(TaskStatus.safeValueOf(status).value()));
	
		if(!search.isEmpty()) 
			filter = filter.and(getSearchFilter(f, search));
		
		if(customer!=null && customer.getId()!=null) { //CUSTOMER
			Integer workgroup = params.optInt(IJsonNames.WORKGROUP);
			filter = filter.and(f.getRegistryProperty().eq(customer.getId()));
			
			if(Boolean.FALSE.equals(api.getDur().isMessengerManager())) 
				filter = filter.and(f.getGtaskIdProperty().eq(email));
			
			if(!params.optString(IJsonNames.TASK_HOLDER).isEmpty()) //----------RECIBIDAS
				filter = filter.and(f.getSenderProperty().isNotNull());
			else if(!params.optString(IJsonNames.SENDER).isEmpty()) //----------ENVIADAS
				filter = filter.and(f.getSenderProperty().isNull());
			
			if(workgroup != null && workgroup !=0) 
				filter = filter.and(f.getWorkgroupProperty().eq(workgroup));

		} else {
			 if(!workgroupStr.isEmpty()) {
				 String[]  str = workgroupStr.split(",");
				 Integer[] arr = new Integer[str.length];
				 for(int i=0; i<str.length; i++) arr[i] = Integer.parseInt(str[i]);
				 filter = filter.and(taskNotCustomerFilter(params, f, domain, customer).or(f.getWorkgroupProperty().in(arr)));
			 } 
			 else filter = filter.and(taskNotCustomerFilter(params, f, domain, customer));
		}
	
		return filter;
	}	
	
	private static Filter taskNotCustomerFilter(JSONObject params, TaskProperties f, Domain domain, Customer customer) {
		Integer workgroup = params.optInt(IJsonNames.WORKGROUP);
		Integer taskHolder = params.optInt(IJsonNames.TASK_HOLDER);
		Integer sender = params.optInt(IJsonNames.SENDER);
		Integer registry = params.optInt(IJsonNames.REGISTRY);
		String source = params.optString(IJsonNames.SOURCE);
		String email = params.optString(IJsonNames.EMAIL);
		
		Filter filter = f.getDomainProperty().eq(domain.getId());
		
		if(taskHolder != null && taskHolder!=0) 
			filter = filter.and(f.getTaskHolderProperty().eq(taskHolder));

		if(sender != null && sender!=0) 
			filter = filter.and(f.getSenderProperty().eq(sender));

		if(!source.isEmpty()) 
			filter = filter.and(f.getSourceProperty().eq(TaskSource.safeValueOf(source).value()));
	
		if(registry != null && registry!=0) 
			filter = filter.and(f.getRegistryProperty().eq(registry));
		
		if(!email.isEmpty() && (!params.optString("cau").isEmpty() && params.optInt("cau")>0) ) 
			filter = filter.and(f.getGtaskIdProperty().eq(email));
		
		if(workgroup != null && workgroup !=0) 
			filter = filter.and(f.getWorkgroupProperty().eq(workgroup));
		else if(params.optBoolean(IJsonNames.WORKGROUP))  //TRUE = ALL
			filter = filter.and(f.getWorkgroupProperty().isNull());
	
		if(!params.optString("startDate").isEmpty()) {
			Date startDate = AonDateUtils.parse(params.optString("startDate"), "yyyy-MM-dd");
			filter = filter.and(f.getStartDateProperty().eq(AonDateUtils.toTimestamp(startDate)));
		}
		return filter;
	}
	
	public static Filter taskFilterStatusCount(TaskProperties f, AonApiData api, Domain domain, Customer customer) {
		JSONObject params = api.getParams();

		String source = params.optString(IJsonNames.SOURCE);
		Integer taskHolder = params.optInt(IJsonNames.TASK_HOLDER);
		String workgroupStr = params.optString(IJsonNames.WORKGROUPS);
		String email = params.optString(IJsonNames.EMAIL);

		Filter filter = f.getDomainProperty().eq(domain.getId());

		if(taskHolder != null && taskHolder!=0 && !workgroupStr.isEmpty()) {
			String[]  str = workgroupStr.split(",");
			Integer[] arr = new Integer[str.length];
			for(int i=0; i<str.length; i++) arr[i] = Integer.parseInt(str[i]);
			filter.and(f.getTaskHolderProperty().eq(taskHolder)).or(f.getSenderProperty().eq(taskHolder).or(f.getWorkgroupProperty().in(arr)));
		} else if(taskHolder != null && taskHolder!=0 && customer.getId()==null)
			filter = filter.and(f.getTaskHolderProperty().eq(taskHolder)).or(f.getSenderProperty().eq(taskHolder));

		if(!source.isEmpty())
			filter = filter.and(f.getSourceProperty().eq(TaskSource.safeValueOf(source).value()));

		if(customer.getId() != null)
			filter = filter.and(f.getRegistryProperty().eq(customer.getId()));

		if(!params.optString("cau").isEmpty() && params.optInt("cau")>0 && !email.isEmpty())
			filter = filter.and(f.getGtaskIdProperty().eq(email));

		return filter;
	}
	
	public static Filter taskFilterCount(AonApiData api, Domain domain,  TaskProperties f, Customer customer) {
		
		Integer taskHolder = api.getParams().optString(IJsonNames.TASK_HOLDER).isEmpty() ? 0 : JsonUtils.getInteger(api.getParams(), IJsonNames.TASK_HOLDER);

		Filter filter  = f.getDomainProperty().eq(domain.getId()).and(f.getStatusProperty().eq(TaskStatus.PENDING.value()));
		
		if(customer.getId()!=null) 
			filter = filter.and(f.getRegistryProperty().eq(customer.getId()));
		
		if( taskHolder==0 || (customer.getId()!=null && !api.getDur().isMessengerManager()) ) 
			filter = filter.and( f.getGtaskIdProperty().eq(api.getParams().optString(IJsonNames.EMAIL)) );
			
		return filter;
	}

	public static String getLogoCompany(String companyName) {
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
		} catch (Exception e) {}

		return logo;
	}
	
	public static Matcher regexFile(Task task, String dataId) {
		String description = task.getDescription();
	    String regex = "(\\<\\S[^<>]*?href=[\\\\]?\")(blob[^\"\\\\]*?)([\\\\]?\"[^<>]*?data-id=[\\\\]?\""+dataId+"[\\\\]?\"[^<>]*?\\>)";
	    Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	    Matcher matcher = pattern.matcher(description);
	    if(!matcher.find()) {
		    regex = "(\\<\\S[^<>]*?src=[\\\\]?\")(blob[^\"\\\\]*?)([\\\\]?\"[^<>]*?data-id=[\\\\]?\""+dataId+"[\\\\]?\"[^<>]*?\\>)";
		    pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		    matcher = pattern.matcher(description);
		    if(!matcher.find()) 
		    	return null;
	    }
	    return matcher;
	}
	
	public static void sendWorkflowCommunication(AonApiData api, TaskWorkflow workflow) {
		Thread newThread = new Thread(() -> {
			try {
				Domain domain = api.getDomain();
				Task task = AON_SOLUTIONS.getTask(api.getDomain(), api.getUser(), f-> f.getIdProperty().eq(workflow.getTask()));
				if(workflow.getType().getName().equalsIgnoreCase(TaskWorkflowType.CLOSE.getName())) {
					
//					sendEmail(api, task, workflow, false);
					
					if( Boolean.TRUE.equals(isNotification(api, task, AppParamsRequest.APP_REQUESTS_INT_CLOSED)) ) {
						String body = workflow.getComment()!=null &&  Boolean.FALSE.equals(workflow.getComment().isEmpty()) 
								? workflow.getComment() :"Solicitud Cerrada." ;
						sendNotificationThAndWg(api, task, workflow, body);
					}
					if(task.getGtaskId()!=null && task.getGtaskId().indexOf("@")>=0 && 
						workflow.getEmail()!=null && !workflow.getEmail().equals(task.getGtaskId())) {
						ApplicationParameter exists = AON.getApplicationParameter(domain.getName(), domain.getId(), api.getUser().getLogin(), AppParamsRequest.APP_REQUESTS_EMAIL_RATING.name());
						if(exists.getId()!=null && exists.getValue().equals("true")) {
							Auth auth = AON_SOLUTIONS.getAuth(task.getGtaskId());
							if(!auth.getEmail().isEmpty()) 
								sendEmail(api, task, workflow, true);
						}
					}
					
				} else if(
					  workflow.getType().getName().equalsIgnoreCase(TaskWorkflowType.COMMENT.getName()) &&
					   Boolean.TRUE.equals(isNotification(api, task, AppParamsRequest.APP_REQUESTS_INT_COMMENT))
				) {
					String body = "Han comentado la Solicitud";
					Auth auth = AON_SOLUTIONS.getAuth(workflow.getEmail());
					if(auth!=null && !auth.getName().isEmpty()) 
						body = "<b>"+auth.getName() +"</b> ha comentado: <br>" + workflow.getComment();

					sendNotificationThAndWg(api, task, workflow, body);
				} else if(
						workflow.getType().getName().equalsIgnoreCase(TaskWorkflowType.OPEN.getName()) &&
						Boolean.TRUE.equals(isNotification(api, task, AppParamsRequest.APP_REQUESTS_INT_OPENED))
				) {
					sendEmail(api, task, workflow, false);
					String body = "Solicitud Abierta";
					Auth auth = AON_SOLUTIONS.getAuth(workflow.getEmail());
					if(auth!=null && !auth.getName().isEmpty()) 
						body += " por <b>" +auth.getName()+"</b>.";
					
					sendNotificationThAndWg(api, task, workflow, body);
				} else if(
						workflow.getType().getName().equalsIgnoreCase(TaskWorkflowType.ASSIGN.getName()) &&
						Boolean.TRUE.equals(isNotification(api, task, AppParamsRequest.APP_REQUESTS_INT_ASSIGN))
				) {
					String body = "Solicitud Reasignada a <b>" + workflow.getComment()+ "</b>.";
					sendNotificationThAndWg(api, task, workflow, body);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		newThread.start();
	}

	public static void sendHistoricWorkflow(AonApiData api, Task task) {
		Thread newThread = new Thread(() -> {
			Company company = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(api.getDomain().getId()));
			if(company!=null) {
				AonToken aonToken = SECURITY.getAonToken(api.getToken());
				Auth auth = AON_SOLUTIONS.getAuth(aonToken.getSchemaFirstDomain(), 0, aonToken.getAuth());
				String logo = TaskUtils.getLogoCompany(company.getDomain().getName());
				
				String to = auth.getEmail();
	
				String subject = "SOLICITUD Nº "+ task.getNumber();
				String url = "https://aon.solutions";
				
				TaskMail tm = new TaskMail()
				.setNumber(task.getNumber().toString())
				.setDate(task.getStartDate())
				.setTitle(task.getTitle())
				.setWorkflows(task.getWorkflows())
				.setUrl(url)
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
	
	//SEND TASK HOLDER AND WORKGROUP
	private static void sendNotificationThAndWg(AonApiData api, Task task, TaskWorkflow workflow, String body) {
		LinkedList<Auth> auths = new LinkedList<>();
		Domain domain = api.getDomain();
		User user = AON_SOLUTIONS.getUser(domain, api.getToken());
		//SEND SENDER
		if(task.getSender()!=null && task.getSender().getUserId()!=null && Integer.compare(task.getSender().getUserId(), user.getId())!=0 ) {
			User usr = AON.getUser(domain, api.getUser().getLogin(), f -> f.getIdProperty().eq(task.getSender().getUserId()));
			if(usr!=null) auths.add(usr.getAuth());
			System.out.println("SENDER SEND ID:"+ task.getSender().getId());
		} else if(task.getGtaskId()!=null && !workflow.getType().getName().equals(TaskWorkflowType.ASSIGN.getName())) {
			Auth authSender = AON_SOLUTIONS.getAuth(task.getGtaskId());
			if(
					authSender!=null && authSender.getEmail()!=null && 
					!Arrays.equals(user.getAuth().getAuth(), authSender.getAuth())
			) {
				auths.add(authSender);
				System.out.println("SENDER SEND EMAIL:"+ authSender.getEmail());
			}
		}
		//SEND TASKHOLDER ASSIGNED
		if(task.getTaskHolder().getId()!=null && !task.getTaskHolder().getId().equals(workflow.getTaskHolder().getId()) ) {
			User usr = AON.getUser(domain, api.getUser().getLogin(), f -> f.getIdProperty().eq(task.getTaskHolder().getUserId()));
			if(usr!=null) auths.add(usr.getAuth());
			System.out.println("TASKHOLDER SEND ID:"+ task.getTaskHolder().getId());
		} // SEND WORKGROUP ASSIGNED
		else if(task.getWorkgroup()!=null && task.getWorkgroup().getId()!=null){ 
			AON.getTaskHolderWorkgroupStream(
					domain, api.getUser(), 
					f->f.getIdProperty().ne(workflow.getTaskHolder().getId())
					.and(f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId()))),
					task.getWorkgroup().getId()
			)
			.forEach(th ->{
				User usr = AON.getUser(domain, api.getUser().getLogin(), f -> f.getIdProperty().eq(th.getUserId()));
				if(usr!=null) auths.add(usr.getAuth());
			});
			System.out.println("WORKGROUP SEND ID:"+ task.getWorkgroup().getId());
		}
		
		if(!auths.isEmpty()) {
			String title = task.getTitle()+" "+getNumberStr(task.getNumber());
	    	NotificationRequest notification = new NotificationRequest();
	    	notification.setTitle(title);
	    	notification.setBody(body);
	    	notification.setUser(user);
	    	notification.setSender(user.getAuth().getAuth());
	    	notification.setDomain(domain);
	    	notification.setSource(NotificationSource.MESSENGER);
	    	notification.setSourceId(task.getId());
	    	notification.setAuths(auths);
	    	notification.send();
		}
	}
	
	private static void sendEmail(AonApiData api, Task task, TaskWorkflow workflow, Boolean showRating){
		try {
			Company company = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(api.getDomain().getId()));
			if(company!=null) {
				String logo = getLogoCompany(company.getDomain().getName());
				String companyName = company.getName();
				
				String email = workflow.getEmail();
				
				Auth auth = AuthJSON.fromJSON(api.getData().optJSONObject("auth"));
				
				String to = auth.getEmail()!=null ? auth.getEmail() : email;
				
				String taskHolderName = auth.getName();
				
				String title = task.getTitle()+" "+getNumberStr(task.getNumber());
				String description = getTaskDescription(task);
				
				String url = URL_BASE;

				TaskMail tm = new TaskMail()
				.setUrl(url)
				.setLogo(logo)
				.setNumber(getNumberStr(task.getNumber()))
				.setDate(task.getModificationDate())
				.setTitle(task.getTitle())
				.setType(workflow.getType())
				.setDescription(description)
				.setCompanyName(companyName)
				.setTaskHolderName(taskHolderName)
				.setShowRating(showRating)
				;
				
				String body = TaskMailTemplate.taskContent(tm);
				
				SESMessage msg = new SESMessage()
				.setAlias(company.getName())
				.setSubject(title)
				.setBody(body)
				.setCc(EMAIL_SUPPORT)
				.setTo(to);
				
			    SES.sendEmail(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void checkFiles(AonApiData api, Task task){
		JSONArray files = JsonUtils.getJSONArray(api.getData(), "files");
		Domain domain = api.getDomain();
		 for (int i = 0 ; i < files.length(); i++) {
			try {
			    JSONObject file = files.getJSONObject(i);
			    String     dataId =  file.optString(IJsonNames.ID);
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
	
	public static void setCauInfo(AonApiData api, Task task) {
		try {
			JSONObject params = api.getData();
			if(!params.optString("cau").isEmpty() && params.optInt("cau")>0) {
				task.setDomain(api.getDomain());
				JSONObject description = new JSONObject(task.getDescription());
				JSONObject cauInfo = description.optJSONObject("cauInfo");
				JSONObject company = cauInfo.optJSONObject(IJsonNames.COMPANY);
				JSONObject parent = cauInfo.optJSONObject(IJsonNames.PARENT);
				JSONObject auth = cauInfo.optJSONObject(IJsonNames.AUTH);
				String docParent  = parent!=null && !parent.optString(IJsonNames.DOCUMENT).isEmpty() ?  parent.optString(IJsonNames.DOCUMENT) : null;
				String docCustomer = company !=null && !company.optString(IJsonNames.DOCUMENT).isEmpty() ? company.optString(IJsonNames.DOCUMENT) : null;
	
				String doc = docParent!=null ? docParent : docCustomer;
				
				if(!auth.optString(IJsonNames.EMAIL).isEmpty()) 
					task.setGtaskId(auth.optString(IJsonNames.EMAIL));
				
				if(doc!=null) {
					Registry registry = AON.getRegistry(api.getDomain(),  api.getUser(), f->f.getDocumentProperty().eq(doc.trim()));
					if(registry!=null && registry.getId()!=null)
						task.setRegistry(registry);
				}
			}
		} catch (Exception e) {e.printStackTrace();}
	}
	
	private static Filter getSearchFilter(TaskProperties f, String search) {
		Filter filter = f.getDescriptionProperty().like("%" + search + "%")
				.or(f.getRegistryNameProperty().like("%" + search + "%"))
				.or(f.getCommentsProperty().like("%" + search + "%")) ;
		Integer numberSearch = 0;
		try { numberSearch = Integer.parseInt(search.replaceAll("[^\\d]", "")); } 
		catch(NumberFormatException e){}
		if(numberSearch!=0)
			filter = filter.or(f.getNumberProperty().like(numberSearch));

		return filter;
	}
	
	private static String getNumberStr(Integer number) {
		if(number==null) number = 0;
		return "#"+StringUtils.leftPad(number.toString(), 5, "0");
	}
	
	private static Boolean isNotification(AonApiData api, Task task, AppParamsRequest param) {
		Boolean isExternal = 
			(api.getDomain().getDomainType().equals(DomainType.OFFICE) && task.getRegistry()!=null && task.getRegistry().getId()!=null) || 
			(!task.getDomain().getId().equals(api.getDomain().getId()));

		Domain domain = api.getDomain();

		if(Boolean.TRUE.equals(isExternal)) {
			switch (param) {
				case APP_REQUESTS_INT_OPENED:
					param = AppParamsRequest.APP_REQUESTS_EXT_OPENED;
				break;
				case APP_REQUESTS_INT_CLOSED:
					param = AppParamsRequest.APP_REQUESTS_EXT_CLOSED;
				break;
				case APP_REQUESTS_INT_ASSIGN:
					param = AppParamsRequest.APP_REQUESTS_EXT_ASSIGN;
				break;
				case APP_REQUESTS_INT_COMMENT:
					param = AppParamsRequest.APP_REQUESTS_EXT_COMMENT;
				break;
				default:
					break;
			}
			domain = task.getDomain();
		}
		ApplicationParameter appParam = AON.getApplicationParameter(domain.getName(), domain.getId(), "", param.name());
		System.out.println("param "+param+" "+ appParam.getName()+" "+appParam.getValue());
		return appParam.getId() == null || (appParam.getId()!=null && appParam.getValue().equals("true"));
	}
	
	private static String getTaskDescription(Task task) {
		try {
			JSONObject obj = new JSONObject(task.getDescription());
			return obj.optString("observation");
		} catch (Exception e) {}
		return task.getDescription();
	}
}
