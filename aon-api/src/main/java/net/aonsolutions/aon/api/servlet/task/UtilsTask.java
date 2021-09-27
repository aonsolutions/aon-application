package net.aonsolutions.aon.api.servlet.task;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.TaskProperties;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.NotificationSource;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskAttach;
import com.esferalia.aon.occam.api.model.task.TaskSource;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.api.model.task.TaskWorkflowType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.model.mail.TaskMail;
import net.aonsolutions.aon.api.model.mail.TaskMailTemplate;
import net.aonsolutions.aon.api.notification.NotificationRequest;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

public class UtilsTask {

	  private UtilsTask() {
	    throw new IllegalStateException("Utility class");
	  }
	  
	public static Filter taskFilter(AonApiData api, TaskProperties f) {
		Integer workgroup = api.getParams().optInt(IJsonNames.WORKGROUP);
		Integer taskHolder = api.getParams().optInt(IJsonNames.TASK_HOLDER);
		Integer sender = api.getParams().optInt(IJsonNames.SENDER);
		Integer registry = api.getParams().optInt(IJsonNames.REGISTRY);
		String status = api.getParams().optString("status");
		String source = api.getParams().optString(IJsonNames.SOURCE);
		String search = api.getParams().optString("search");
		String email = api.getParams().optString(IJsonNames.EMAIL);
		String workgroupStr = api.getParams().optString(IJsonNames.WORKGROUPS);
		

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
		
		if(!email.isEmpty() && (!api.getParams().optString("cau").isEmpty() && api.getParams().optInt("cau")>0) ) {
			filter = filter.and(f.getGtaskIdProperty().eq(email));
		}
		
		if(!workgroupStr.isEmpty()) {
			String[]  str = workgroupStr.split(",");
			Integer[] arr = new Integer[str.length];
			for(int i=0; i<str.length; i++) arr[i] = Integer.parseInt(str[i]);
			filter = filter.and(f.getWorkgroupProperty().in(arr));
		}
		
		if(!api.getParams().optString("startDate").isEmpty()) {
			Date startDate = AonDateUtils.parse(api.getParams().optString("startDate"), "yyyy-MM-dd");
			filter = filter.and(f.getStartDateProperty().eq(AonDateUtils.toTimestamp(startDate)));
		}
		
		return filter;
	}
	  
	public static Filter taskFilterCount(AonApiData api, TaskProperties f) {
		String source = api.getParams().optString("source");
		Integer workgroup = api.getParams().optInt(IJsonNames.WORKGROUP);
		Integer taskHolder = api.getParams().optInt(IJsonNames.TASK_HOLDER);
		Integer sender = api.getParams().optInt(IJsonNames.SENDER);
		
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());

		if(workgroup != null && workgroup!= 0) 
			filter = filter.and(f.getWorkgroupProperty().eq(workgroup));

		if(taskHolder != null && taskHolder!=0) 
			filter = filter.and(f.getTaskHolderProperty().eq(taskHolder));

		if(sender != null && sender!=0) 
			filter = filter.and(f.getSenderProperty().eq(sender));

		if(!source.isEmpty()) 
			filter = filter.and(f.getSourceProperty().eq(TaskSource.safeValueOf(source).value()));
		
		if(!api.getParams().optString("cau").isEmpty() && api.getParams().optInt("cau")>0) {
			String email = api.getParams().optString(IJsonNames.EMAIL);
			if(!email.isEmpty())
				filter = filter.and(f.getGtaskIdProperty().eq(email));
		}
		
		return filter;
	}

	public static Filter taskOfficeFilter(AonApiData api, TaskProperties f, Customer customer, Domain domain) {
		String search = api.getParams().optString("search");

		Filter filter = f.getDomainProperty().eq(domain.getId()).and(f.getRegistryProperty().eq(customer.getId())).and(f.getStatusProperty().eq(TaskStatus.IN_PROGRESS.value()).or(f.getStatusProperty().eq(TaskStatus.PENDING.value())));
		
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
		
		return filter;
	}
	
	public static void sendNotificationComment(AonApiData api, Task task, TaskWorkflow workflow) {
		LinkedList<Auth> auths = new LinkedList<Auth>();
		Domain domain = api.getDomain();
		User user = AON_SOLUTIONS.getUser(domain, api.getToken());

		if(task.getTaskHolder().getId()!=null && !task.getTaskHolder().getId().equals(workflow.getTaskHolder().getId()) ) {
			User usr = AON.getUser(domain, api.getUser().getLogin(), f -> f.getIdProperty().eq(task.getTaskHolder().getUserId()));
			if(usr!=null) auths.add(usr.getAuth());
			System.out.println("COMMENT TASKHOLDER SEND "+ task.getTaskHolder().getId());
		} else if(task.getWorkgroup()!=null && task.getWorkgroup().getId()!=null){
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
			System.out.println("COMMENT WORKGROUP SEND "+ task.getWorkgroup().getId());
		}
		
		if(auths.size()>0) {
			String title = "SOLICITUD | AON SOLUTIONS";
	    	NotificationRequest notification = new NotificationRequest();
	    	notification.setTitle(title);
	    	notification.setBody("Han respondido en la solicitud Nº "+ task.getNumber());
	    	notification.setSender(user.getAuth().getAuth());
	    	notification.setDomain(domain);
	    	notification.setUser(api.getUser());
	    	notification.setSource(NotificationSource.MESSENGER);
	    	notification.setSourceId(task.getId());
	    	notification.setAuths(auths);
	    	notification.send();
		}
	}
	
	public static void sendNotification(AonApiData api, Task task, Auth auth){
		try {
			User myUser = AON_SOLUTIONS.getUser(api.getDomain(), api.getToken());
			String title = "SOLICITUD | AON SOLUTIONS";
			String body = "Solicitud Nº "+task.getNumber() + " Cerrada";
			LinkedList<Auth> auths = new LinkedList<>();
			auths.add(auth);

	    	NotificationRequest notification = new NotificationRequest();
	    	notification.setTitle(title);
	    	notification.setBody(body);
	    	notification.setSender(myUser.getAuth().getAuth());
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
	
	public static void sendEmail(AonApiData api, Task task, Auth auth){
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
				
				String body = TaskMailTemplate.taskContent(tm);
				
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
		    if(!matcher.find()) {
		    	return null;
		    }
	    }
	    return matcher;
	}
	
	public static void sendWorkflowCommunication(AonApiData api, TaskWorkflow workflow) {
		Thread newThread = new Thread(() -> {
			try {
				Task task = AON_SOLUTIONS.getTask(api.getDomain(), api.getUser(), f-> f.getIdProperty().eq(workflow.getTask()));
				if(workflow.getType().getName().equalsIgnoreCase(TaskWorkflowType.CLOSE.getName())) {
//					if(!task.getGtaskId().isEmpty() && task.getGtaskId().indexOf("@")>=0) {
//						Auth auth = AON_SOLUTIONS.getAuth(task.getGtaskId());
						AonToken aonToken = SECURITY.getAonToken(api.getToken());
						Auth auth = AON_SOLUTIONS.getAuth(aonToken.getSchemaFirstDomain(), 0, aonToken.getAuth());
						if(!auth.getEmail().isEmpty()) {
							UtilsTask.sendNotification(api, task, auth);
							UtilsTask.sendEmail(api, task, auth);
						}
//					}
				} else if(workflow.getType().getName().equalsIgnoreCase(TaskWorkflowType.COMMENT.getName())) {
					UtilsTask.sendNotificationComment(api, task, workflow);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		newThread.start();
	}
	
	public static void checkFiles(AonApiData api, Task task){
		JSONArray files = JsonUtils.getJSONArray(api.getData(), "files");
		Domain domain = api.getDomain();
		 for (int i = 0 ; i < files.length(); i++) {
			try {
			    JSONObject file = files.getJSONObject(i);
			    String     dataId =  file.optString("id");
			    Matcher    matcher = UtilsTask.regexFile(task, dataId);
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
}
