package net.aonsolutions.aon.api.servlet.task;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jooq.tools.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskAttach;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.api.model.task.TaskWorkflowType;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.MimeType;

import net.aonsolutions.aon.api.ewok.AonApiData;

public class TaskUtils {
	
	private TaskUtils() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static void onNotification(AonApiData api, TaskWorkflow workflow) {
		Thread newThread = new Thread(() -> {
			try {
				Task task = AON_SOLUTIONS.getTask(api.getDomain(), api.getUser(), f-> f.getIdProperty().eq(workflow.getTask()));
				switch (workflow.getType()) {
					case OPEN:
						TaskNotification.onOpenNotification(api, task, workflow);
						TaskNotification.onOpenEmail(api, task, workflow);
						break;
					case COMMENT:
						TaskNotification.onCommentNotification(api, task, workflow);
						break;
					case ASSIGN:
						TaskNotification.onAssignNotification(api, task, workflow);
						TaskNotification.onAssignEmail(api, task, workflow);
						break;
					case CLOSE:
						TaskNotification.onCloseNotification(api, task, workflow);
						TaskNotification.onCloseEmail(api, task, workflow);
						changeStatusTask(api, task, workflow);
						break;
					case EVALUATION:
						TaskNotification.onEvaluationCloseEmail(api, task, workflow);
						//TODO
						break;
					case REOPEN:
						//TODO
						break;
					case CONNECTED:
						//TODO
						break;
					default:
						break;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		newThread.start();
	}
	
	public static boolean isCau(JSONObject params) {
		return params.optInt("cau") != 0;
	}
	
	public static String parseDescription(Task task) {
		try {
			JSONObject obj = new JSONObject(task.getDescription());
			return obj.optString("observation");
		} catch (Exception e) {}
		return task.getDescription();
	}
	
	public static JSONObject parseCauInfo(Task task) {
		try {
			return new JSONObject(task.getDescription()).optJSONObject("cauInfo");
		} catch (Exception e) {}
		return new JSONObject();
	}
	
	public static JSONObject parseAuth(Task task) {
		try {
			return parseCauInfo(task).optJSONObject(IJsonNames.AUTH);
		} catch (Exception e) {}
		return new JSONObject();
	}
	
	public static String parseNumber(Integer number) {
		if(number==null) number = 0;
		return "\u0023"+StringUtils.leftPad(number.toString(), 5, "0");
	}
	
	public static void setCauInfo(AonApiData api, Task task) {
		try {
			boolean edit = task.getId() != null;
			task.setDomain(api.getDomain());
			JSONObject cauInfo = parseCauInfo(task);
			JSONObject auth    = parseAuth(task);
			JSONObject company = cauInfo.optJSONObject(IJsonNames.COMPANY);
			JSONObject parent = cauInfo.optJSONObject(IJsonNames.PARENT);
			
			String docParent   = parent!=null  && !parent.optString(IJsonNames.DOCUMENT).isEmpty() ?  parent.optString(IJsonNames.DOCUMENT) : null;
			String docCustomer = company!=null && !company.optString(IJsonNames.DOCUMENT).isEmpty() ? company.optString(IJsonNames.DOCUMENT) : null;

			String doc = docParent!=null ? docParent : docCustomer;
			
			if(!auth.optString(IJsonNames.EMAIL).isEmpty()) {
				task.setGtaskId(auth.optString(IJsonNames.EMAIL));
			}
	
			if(doc!=null && !(task.getRegistry()!=null && task.getRegistry().getId()!=null)) {
				Registry registry = AON.getRegistry(api.getDomain(), api.getUser(), f->f.getDocumentProperty().eq(doc.trim()));
				if(registry!=null && registry.getId()!=null) {					
					task.setRegistry(registry);
				}
			}
			
			task.setSender(new TaskHolder());
			if(!edit) {
				task.setTaskHolder(new TaskHolder());
				task.setWorkgroup(new Workgroup());
			}

		} catch (Exception e) {e.printStackTrace();}
	}
	
	public static void setCauWorkflow(AonApiData api, TaskWorkflow workflow) {
		workflow.setDomain(api.getDomain().getId());
		workflow.setTaskHolder(new TaskHolder());
	}
	
	public static void checkFilesAndSave(AonApiData api, Task task){
		JSONArray files = JsonUtils.getJSONArray(api.getData(), "files");
		Domain domain = api.getDomain();
		 for (int i = 0 ; i < files.length(); i++) {
			try {
			    JSONObject file    = files.getJSONObject(i);
			    String     dataId  = file.optString(IJsonNames.ID);
			    Matcher    matcher = regexFile(task.getDescription(), dataId);
				String     base64  = null;
				String     contentType = null;
				
			    if(matcher!=null) {
					base64 = file.optString(IJsonNames.CONTENT);
					contentType = file.optString(IJsonNames.CONTENT_TYPE);
			    }
			    
			    if(base64!=null && contentType!=null) {
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
					jsonFile.put("attach_type", AttachType.TASK.getName()); // "task"
			
					jsonFile.put(IJsonNames.ID, taskAttach.getId());

					String base64FileStr = new String(Base64.getEncoder().encode(jsonFile.toString().getBytes()));

	                String link = "/ms/api/file/"+base64FileStr;
	                task.setDescription(matcher.replaceAll("$1" + link + "$3"));
			    }


			} catch (Exception e) {
				e.printStackTrace();
			}
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
	
	public static LinkedList<Auth> getAuthsTask(AonApiData api, Task task, TaskWorkflow workflow, User user) {
		LinkedList<Auth> list = new LinkedList<>();
		Domain domain = api.getDomain();
		Optional<String> gtaskId = task.getGtaskId();
		//SEND SENDER
		if(task.getSender()!=null && task.getSender().getUserId()!=null && Integer.compare(task.getSender().getUserId(), user.getId())!=0 ) {
			
			getAuthForTaskHolder(api, task.getSender()).ifPresent(list::add);
			System.out.println("SENDER ID:"+ task.getSender().getId());
			
		} else if(gtaskId.isPresent() && !workflow.getType().getName().equals(TaskWorkflowType.ASSIGN.getName())) {
			
			Auth authSender = AON_SOLUTIONS.getAuth(gtaskId.get());
			
			if( authSender!=null && authSender.getEmail()!=null &&  !Arrays.equals(user.getAuth().getAuth(), authSender.getAuth())) {
				list.add(authSender);
				System.out.println("SENDER EMAIL:"+ authSender.getEmail());
			}
		}
		
		//SEND TASKHOLDER ASSIGNED
		if(task.getTaskHolder().getId()!=null) {
			if(!task.getTaskHolder().getId().equals(workflow.getTaskHolder().getId())) {
				User usr = AON.getUser(domain, api.getUser().getLogin(), f -> f.getIdProperty().eq(task.getTaskHolder().getUserId()));
				
				if(usr!=null) list.add(usr.getAuth());
				
				System.out.println("TASKHOLDER ID:"+ task.getTaskHolder().getId());
			}		
		} else if(task.getWorkgroup()!=null && task.getWorkgroup().getId()!=null){ // SEND WORKGROUP ASSIGNED
			AON.getTaskHolderWorkgroupStream(
					domain, api.getUser(), 
					f-> f.getIdProperty().isNotNull().and(
							workflow.getTaskHolder().getId()!=null ?
							f.getIdProperty().ne(workflow.getTaskHolder().getId()) :
							f.getIdProperty().isNotNull()
					)
					.and(f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId()))),
					task.getWorkgroup().getId()
			)
			.forEach(th ->{
				User usr = AON.getUser(domain, api.getUser().getLogin(), f -> f.getIdProperty().eq(th.getUserId()));
				if(usr!=null) list.add(usr.getAuth());
			});
			System.out.println("WORKGROUP ID:"+ task.getWorkgroup().getId());
		}
		
		return list;
	}
	
	public static Optional<Auth> getAuthForTaskHolder(AonApiData api, TaskHolder th) {
		User user = AON.getUser(api.getDomain(), api.getUser().getLogin(), f -> f.getIdProperty().eq(th.getUserId()));
		Auth auth = AON_SOLUTIONS.getAuth(user.getAuth().getAuth());
		if(auth.getEmail()!=null && !auth.getEmail().isEmpty()) {
			return Optional.of(auth);
		}
		return Optional.empty();
	}
	
	private static Matcher regexFile(String str, String dataId) {
	    String regex = "(\\<\\S[^<>]*?href=[\\\\]?\")(blob[^\"\\\\]*?)([\\\\]?\"[^<>]*?data-id=[\\\\]?\""+dataId+"[\\\\]?\"[^<>]*?\\>)";
	    Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	    Matcher matcher = pattern.matcher(str);
	    if(!matcher.find()) {
		    regex = "(\\<\\S[^<>]*?src=[\\\\]?\")(blob[^\"\\\\]*?)([\\\\]?\"[^<>]*?data-id=[\\\\]?\""+dataId+"[\\\\]?\"[^<>]*?\\>)";
		    pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		    matcher = pattern.matcher(str);
		    if(!matcher.find()) 
		    	return null;
	    }
	    return matcher;
	}
	
	private static void changeStatusTask(AonApiData api, Task task, TaskWorkflow workflow) {
		Domain domain = task.getDomain();
		List<Byte> types = Arrays.asList(TaskStatus.PENDING.value(), TaskStatus.IN_PROGRESS.value());
		if(task.isChild()) {
			
			Task parent = AON_SOLUTIONS.getTask(domain, api.getUser(), f-> f.getIdProperty().eq(task.getParent()));
			
			if(parent!=null && parent.getId()!=null) {
				
				List<Task> childs = AON_SOLUTIONS.getTaskStream(domain, api.getUser(), 
					f-> f.getParentProperty().eq(task.getParent())
					.and(f.getStatusProperty().in(types.toArray(Byte[]::new)))
				).collect(Collectors.toList());

				parent.setStatus(childs.isEmpty() ? TaskStatus.PENDING : TaskStatus.IN_PROGRESS);
				AON_SOLUTIONS.saveTask(domain, api.getUser(), parent);
			}
		} else { // IS PARENT
			
//			workflow.setId(null).setTask(task.getId()).setType(TaskWorkflowType.CLOSE);
			
//			AON_SOLUTIONS.saveTaskWorkflow(domain, new User(), workflow); /// SAVE WORKFLOW
			
			AON_SOLUTIONS.getTaskStream(domain, api.getUser(), 
				f-> f.getParentProperty().eq(task.getId())
				.and(f.getStatusProperty().in(types.toArray(Byte[]::new)))
			)
			.forEach(t->{
				AON_SOLUTIONS.saveTask(t.getDomain(), new User(), t.setStatus(TaskStatus.FINISHED));
			});
		}
	}
	
	/**
	 * DISTINCT STREAM
	 * @param <T>
	 * @param keyExtractor
	 * @return 
	 */
	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> uniqueMap = new ConcurrentHashMap<>();
		return t -> uniqueMap.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	public static boolean isExternal(Task task, Domain domain) {
		return !task.getDomain().getId().equals(domain.getId()) ||
		(
			domain.getDomainType().equals(DomainType.OFFICE) && 
			task.getRegistry()!=null && task.getRegistry().getId()!=null &&
			!(task.getSender()!=null && task.getSender().getId()!=null)
		);
	}
	
//	private static Matcher regexFileBase64(String str) {
//	    String regex = "\\<img.*?src=[\\\"\\']data\\:(?<datatype>[^\\;]*)\\;base64,(?<base64>[^\\\"]*)[\\\"\\'].*\\>";
//	    
//	    Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
//	    Matcher matcher = pattern.matcher(str);
//	    while (matcher.find()) {
//	    	String contentType = matcher.group("datatype");
//	    	String base64 = matcher.group("base64");
//	    	System.out.println(contentType);
//	    	System.out.println(base64);
//		}
//	    return matcher;
//	}
}
