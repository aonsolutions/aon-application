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
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.TaskProperties;
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
	  
	public static Filter taskFilter(AonApiData api, TaskProperties f, Domain domain, Customer customer) {
		JSONObject json = api.getParams();
		String email = json.optString(IJsonNames.EMAIL);
		String status = json.optString("status");
		String search = json.optString("search");
		String workgroupStr = json.optString(IJsonNames.WORKGROUPS);
		
		Filter filter = f.getDomainProperty().eq(domain.getId());
		if("pending".equalsIgnoreCase(status) || ( status.isEmpty() && customer!=null && customer.getId()!=null) ) 
			filter = filter.and(f.getStatusProperty().eq(TaskStatus.IN_PROGRESS.value()).or(f.getStatusProperty().eq(TaskStatus.PENDING.value())));
		else if(!status.isEmpty())
			filter = filter.and(f.getStatusProperty().eq(TaskStatus.safeValueOf(status).value()));
	
		if(Boolean.FALSE.equals(api.getDur().isMessengerManager())) 
			filter = filter.and(f.getGtaskIdProperty().eq(email));
		
		if(!search.isEmpty()) 
			filter = filter.and(getSearchFilter(f, search));
		
		if(customer!=null && customer.getId()!=null) { //CUSTOMER
			filter = filter.and(f.getRegistryProperty().eq(customer.getId()));
			
			if(!json.optString(IJsonNames.TASK_HOLDER).isEmpty()) //----------RECIBIDAS
				filter = filter.and(f.getSenderProperty().isNotNull());
			else if(!json.optString(IJsonNames.SENDER).isEmpty()) //----------ENVIADAS
				filter = filter.and(f.getSenderProperty().isNull());
			
		} else 
			filter = filter.and(taskNotCustomerFilter(json, f, domain, customer));
		
		if(!workgroupStr.isEmpty()) {
			String[]  str = workgroupStr.split(",");
			Integer[] arr = new Integer[str.length];
			for(int i=0; i<str.length; i++) arr[i] = Integer.parseInt(str[i]);
			filter = filter.or(f.getWorkgroupProperty().in(arr));
		}
	
		return filter;
	}	
	
	private static Filter taskNotCustomerFilter(JSONObject json, TaskProperties f, Domain domain, Customer customer) {
		Integer workgroup = json.optInt(IJsonNames.WORKGROUP);
		Integer taskHolder = json.optInt(IJsonNames.TASK_HOLDER);
		Integer sender = json.optInt(IJsonNames.SENDER);
		String source = json.optString(IJsonNames.SOURCE);
		Integer registry = json.optInt(IJsonNames.REGISTRY);
		String email = json.optString(IJsonNames.EMAIL);
		
		Filter filter = f.getDomainProperty().eq(domain.getId());
		
		if(taskHolder != null && taskHolder!=0) 
			filter = filter.and(f.getTaskHolderProperty().eq(taskHolder));

		if(sender != null && sender!=0) 
			filter = filter.and(f.getSenderProperty().eq(sender));

		if(!source.isEmpty()) 
			filter = filter.and(f.getSourceProperty().eq(TaskSource.safeValueOf(source).value()));
	
		if(registry != null && registry!=0) 
			filter = filter.and(f.getRegistryProperty().eq(registry));
		
		if(!email.isEmpty() && (!json.optString("cau").isEmpty() && json.optInt("cau")>0) ) {
			filter = filter.and(f.getGtaskIdProperty().eq(email));
		}
		
		if(workgroup != null && workgroup !=0) 
			filter = filter.and(f.getWorkgroupProperty().eq(workgroup));
		else if(json.optBoolean(IJsonNames.WORKGROUP))  //TRUE = ALL
			filter = filter.and(f.getWorkgroupProperty().isNull());
	
		if(!json.optString("startDate").isEmpty()) {
			Date startDate = AonDateUtils.parse(json.optString("startDate"), "yyyy-MM-dd");
			filter = filter.and(f.getStartDateProperty().eq(AonDateUtils.toTimestamp(startDate)));
		}
		return filter;
	}
	
	public static Filter taskFilterStatusCount(TaskProperties f, AonApiData api, Domain domain, Customer customer) {
		String source = api.getParams().optString("source");
		Integer taskHolder = api.getParams().optInt(IJsonNames.TASK_HOLDER);
		String workgroupStr = api.getParams().optString(IJsonNames.WORKGROUPS);
		String email = api.getParams().optString(IJsonNames.EMAIL);
		
		Filter filter = f.getDomainProperty().eq(domain.getId());

		if(taskHolder != null && taskHolder!=0) {
			filter = filter.and(f.getTaskHolderProperty().eq(taskHolder)).or(f.getSenderProperty().eq(taskHolder));
		}
		
		if(!source.isEmpty()) 
			filter = filter.and(f.getSourceProperty().eq(TaskSource.safeValueOf(source).value()));
		
		if(customer.getId()!=null) 
			filter = filter.and(f.getRegistryProperty().eq(customer.getId()));
		
		if(!api.getParams().optString("cau").isEmpty() && api.getParams().optInt("cau")>0 && !email.isEmpty()) {
			filter = filter.and(f.getGtaskIdProperty().eq(email));
		}
		
		if(!workgroupStr.isEmpty()) {
			String[]  str = workgroupStr.split(",");
			Integer[] arr = new Integer[str.length];
			for(int i=0; i<str.length; i++) arr[i] = Integer.parseInt(str[i]);
			filter = filter.or(f.getWorkgroupProperty().in(arr));
		}
		
		return filter;
	}
	
	public static Filter taskFilterCount(AonApiData api, Domain domain,  TaskProperties f, Customer customer) {
		
		Integer taskHolder = api.getParams().optString(IJsonNames.TASK_HOLDER).isEmpty() ? 0 : JsonUtils.getInteger(api.getParams(), IJsonNames.TASK_HOLDER);

		Filter filter  = f.getDomainProperty().eq(domain.getId()).and(f.getStatusProperty().eq(TaskStatus.PENDING.value()));
		
		if(customer.getId()!=null) 
			filter = filter.and(f.getRegistryProperty().eq(customer.getId()));
		
		if( taskHolder==0 || (customer.getId()!=null && !api.getDur().isMessengerManager()) ) {
			filter = filter.and( f.getGtaskIdProperty().eq(api.getParams().optString(IJsonNames.EMAIL)) );
		}
			
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
				Task task = AON_SOLUTIONS.getTask(api.getDomain(), api.getUser(), f-> f.getIdProperty().eq(workflow.getTask()));
				if(workflow.getType().getName().equalsIgnoreCase(TaskWorkflowType.CLOSE.getName())) {
					if(!task.getGtaskId().isEmpty() && task.getGtaskId().indexOf("@")>=0 && 
						workflow.getEmail()!=null && !workflow.getEmail().equals(task.getGtaskId())) {
						Auth auth = AON_SOLUTIONS.getAuth(task.getGtaskId());
//						AonToken aonToken = SECURITY.getAonToken(api.getToken());
//						Auth auth = AON_SOLUTIONS.getAuth(aonToken.getSchemaFirstDomain(), 0, aonToken.getAuth());
						if(!auth.getEmail().isEmpty()) 
							sendEmail(api, task, auth);
					}
					sentNotificationThAndWg(api, task, workflow, workflow.getComment().isEmpty() ? "Solicitud cerrada" : workflow.getComment());
				} else if(workflow.getType().getName().equalsIgnoreCase(TaskWorkflowType.COMMENT.getName())) {
					String body = "Han comentado la solicitud";
					Auth auth = AON_SOLUTIONS.getAuth(workflow.getEmail());
					if(auth!=null && !auth.getName().isEmpty()) 
						body = auth.getName() +" ha comentado la solicitud";
					sentNotificationThAndWg(api, task, workflow, body);
				} else if(workflow.getType().getName().equalsIgnoreCase(TaskWorkflowType.OPEN.getName())) {
					sentNotificationThAndWg(api, task, workflow, "Solicitud abierta");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		newThread.start();
	}

	//SEND TASK HOLDER AND WORKGROUP
	private static void sentNotificationThAndWg(AonApiData api, Task task, TaskWorkflow workflow, String body) {
		LinkedList<Auth> auths = new LinkedList<>();
		Domain domain = api.getDomain();
		User user = AON_SOLUTIONS.getUser(domain, api.getToken());

		if(task.getSender().getId()!=null && !task.getSender().getId().equals(workflow.getTaskHolder().getId()) ) {
			User usr = AON.getUser(domain, api.getUser().getLogin(), f -> f.getIdProperty().eq(task.getSender().getUserId()));
			if(usr!=null) auths.add(usr.getAuth());
			System.out.println("COMMENT SENDER SEND ID"+ task.getSender().getId());
		}
		
		if(task.getTaskHolder().getId()!=null && !task.getTaskHolder().getId().equals(workflow.getTaskHolder().getId()) ) {
			User usr = AON.getUser(domain, api.getUser().getLogin(), f -> f.getIdProperty().eq(task.getTaskHolder().getUserId()));
			if(usr!=null) auths.add(usr.getAuth());
			System.out.println("COMMENT TASKHOLDER SEND ID"+ task.getTaskHolder().getId());
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
			System.out.println("COMMENT WORKGROUP SEND ID"+ task.getWorkgroup().getId());
		}
		
		if(auths.size()>0) {
			String title = "SOLICITUD "+task.getNumber()+" | "+ task.getTitle();
	    	NotificationRequest notification = new NotificationRequest();
	    	notification.setTitle(title);
	    	notification.setBody(body);
	    	notification.setSender(user.getAuth().getAuth());
	    	notification.setDomain(domain);
	    	notification.setUser(api.getUser());
	    	notification.setSource(NotificationSource.MESSENGER);
	    	notification.setSourceId(task.getId());
	    	notification.setAuths(auths);
	    	notification.send();
		}
	}
	
	private static void sendEmail(AonApiData api, Task task, Auth auth){
		try {
			Company company = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(api.getDomain().getId()));
			if(company!=null) {
				String logo = getLogoCompany(company.getDomain().getName());
				
				String to = auth.getEmail();
				
				String subject = "SOLICITUD "+task.getNumber()+" | "+ task.getTitle();
				
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
	
	public static void checkFiles(AonApiData api, Task task){
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
}
