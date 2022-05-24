package net.aonsolutions.aon.api.servlet.task;

import static net.aonsolutions.aon.api.servlet.task.AppParamsRequest.APP_REQUESTS_EXT_ASSIGN;
import static net.aonsolutions.aon.api.servlet.task.AppParamsRequest.APP_REQUESTS_EXT_CLOSED;
import static net.aonsolutions.aon.api.servlet.task.AppParamsRequest.APP_REQUESTS_EXT_COMMENT;
import static net.aonsolutions.aon.api.servlet.task.AppParamsRequest.APP_REQUESTS_EXT_EMAIL_OPENED;
import static net.aonsolutions.aon.api.servlet.task.AppParamsRequest.APP_REQUESTS_EXT_OPENED;
import static net.aonsolutions.aon.api.servlet.task.AppParamsRequest.APP_REQUESTS_INT_ASSIGN;
import static net.aonsolutions.aon.api.servlet.task.AppParamsRequest.APP_REQUESTS_INT_CLOSED;
import static net.aonsolutions.aon.api.servlet.task.AppParamsRequest.APP_REQUESTS_INT_COMMENT;
import static net.aonsolutions.aon.api.servlet.task.AppParamsRequest.APP_REQUESTS_INT_EMAIL_OPENED;
import static net.aonsolutions.aon.api.servlet.task.AppParamsRequest.APP_REQUESTS_INT_OPENED;

import java.util.LinkedList;
import java.util.Optional;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.AuthJSON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.NotificationSource;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.model.mail.TaskMail;
import net.aonsolutions.aon.api.model.mail.TaskMailTemplate;
import net.aonsolutions.aon.api.notification.NotificationRequest;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

public class TaskNotification {
	
	private static final String EMAIL_SUPPORT = "soporte@aonsolutions.es";
	private static final String URL_BASE = "https://aon.solutions";

	private TaskNotification() {
	    throw new IllegalStateException("Utility class");
	}
	
	/**
	 * SEND NOTIFICATION ON OPEN
	 * @param api
	 * @param task
	 * @param workflow
	 */
	public static void onOpenNotification(AonApiData api, Task task, TaskWorkflow workflow){
		if(
			isAllowed(
				api, 
				task, 
				TaskUtils.isExternal(task, api.getDomain()) ? APP_REQUESTS_EXT_OPENED: APP_REQUESTS_INT_OPENED
			)
		) {
			
			String body = "Solicitud Abierta";
			Auth auth = AON_SOLUTIONS.getAuth(workflow.getEmail());
			if(auth!=null && !auth.getName().isEmpty()) 
				body += " por <b>" +auth.getName()+"</b>.";
			
			sendNotificationWorkflow(api, task, workflow, body);
		}
	}
		
	/**
	 * SEND EMAIL ON OPEN
	 * @param api
	 * @param task
	 * @param workflow
	 */
	public static void onOpenEmail(AonApiData api, Task task, TaskWorkflow workflow){
		if(TaskUtils.isCau(api.getData()) && TaskUtils.isExternal(task, api.getDomain()) && isAllowed(api, task, APP_REQUESTS_EXT_EMAIL_OPENED) ) {
			sendEmailWorkflow(api, task, workflow, Optional.empty(), false); // false
		} else if(
			isAllowed(
				api, 
				task, 
				APP_REQUESTS_INT_EMAIL_OPENED
			)
		) {
			
//			LinkedList<Auth> auths = TaskUtils.getAuthsTask(api, task, workflow, api.getUser());
			//SEND TASKHOLDER ASSIGNED
			if(task.getTaskHolder().getId()!=null && !task.getTaskHolder().getId().equals(workflow.getTaskHolder().getId()) ) {
				TaskUtils.getAuthForTaskHolder(api, task.getTaskHolder())
				.ifPresent(a->{
					task.setGtaskId(a.getEmail());
				   	sendHistoricWorkflow(api, task, false);
				});
			} 
		
		}
	}
		
	
	/**
	 * NOTIFICATION ON COMMENT
	 * @param api
	 * @param task
	 * @param workflow
	 */
	public static void onCommentNotification(AonApiData api, Task task, TaskWorkflow workflow){
		if(
			isAllowed(
				api, 
				task, 
				TaskUtils.isExternal(task, api.getDomain()) ? APP_REQUESTS_EXT_COMMENT: APP_REQUESTS_INT_COMMENT
			)
		) {
			String body = "Han comentado la Solicitud";
			Auth auth = AON_SOLUTIONS.getAuth(workflow.getEmail());
			if(auth!=null && !auth.getName().isEmpty()) 
				body = "<b>"+auth.getName() +"</b> ha comentado: <br>" + workflow.getComment();

			sendNotificationWorkflow(api, task, workflow, body);
		} 
	}
	
	/**
	 * SEND NOTIFICATION ON ASSIGN
	 * @param api
	 * @param task
	 * @param workflow
	 */
	public static void onAssignNotification(AonApiData api, Task task, TaskWorkflow workflow){
		if(
			isAllowed(
				api, 
				task, 
				TaskUtils.isExternal(task, api.getDomain()) ? APP_REQUESTS_EXT_ASSIGN: APP_REQUESTS_INT_ASSIGN
			)
		) {
			String body = "Solicitud Reasignada a <b>" + workflow.getComment()+ "</b>.";
			sendNotificationWorkflow(api, task, workflow, body);
		}
	}
	
	/**
	 * SEND NOTIFICATION ON CLOSE
	 * @param api
	 * @param task
	 * @param workflow
	 */
	public static void onCloseNotification(AonApiData api, Task task, TaskWorkflow workflow){
		if( 
			isAllowed(
				api, 
				task, 
				TaskUtils.isExternal(task, api.getDomain()) ? APP_REQUESTS_EXT_CLOSED: APP_REQUESTS_INT_CLOSED
			)
		) {
			
			String body = workflow.getComment()!=null &&  Boolean.FALSE.equals(workflow.getComment().isEmpty()) 
					? workflow.getComment() :"Solicitud Cerrada." ;
					
				sendNotificationWorkflow(api, task, workflow, body);
		}
	}
	
	/**
	 * SEND EMAIL
	 * @param api
	 * @param task
	 * @param workflow
	 */
	public static void onCloseEmail(AonApiData api, Task task, TaskWorkflow workflow){		
		
		if(	TaskUtils.isExternal(task, api.getDomain()) ) {
			if(
				isAllowed(api, task, AppParamsRequest.APP_REQUESTS_EXT_EMAIL_CLOSED ) &&
				task.getGtaskId()!=null && task.getGtaskId().indexOf("@")>=0 && workflow.getEmail()!=null && !workflow.getEmail().equals(task.getGtaskId())
			) {
				ApplicationParameter exists = AON.getApplicationParameter(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), AppParamsRequest.APP_REQUESTS_EMAIL_RATING.name());
				
				if(exists.getId()!=null && exists.getValue().equals("true")) {
					Auth auth = AON_SOLUTIONS.getAuth(task.getGtaskId());
					
					if(!auth.getEmail().isEmpty()) 
						sendEmailWorkflow(api, task, workflow, Optional.of(auth), true); // true
				}
			}
		} else {
			if(
				isAllowed(api, task, AppParamsRequest.APP_REQUESTS_INT_EMAIL_CLOSED ) &&
				task.getParent()!=null && task.getParent()>0 && task.getSender()!=null && task.getSender().getUserId()!=null
			) {
				TaskUtils.getAuthForTaskHolder(api, task.getSender())
				.ifPresent(a->
					sendEmailWorkflow(api, task, workflow, Optional.of(a), false)
				);
			}
		}	
	}
	
	
	private static void sendEmailWorkflow(AonApiData api, Task task, TaskWorkflow workflow, Optional<Auth> authOpt, boolean showRating){
		try {
			Domain domain = api.getDomain();
			JSONObject params = api.getData();
			Company company = AON.getCompany(domain.getName(),domain.getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(domain.getId()));
			if(company!=null) {
				String logo = TaskUtils.getLogoCompany(company.getDomain().getName());
				String companyName = company.getName();
				
				String to = workflow.getEmail();
				String taskHolderName = to;	
				Auth auth = new Auth();

				if(authOpt.isPresent()) {
					auth = authOpt.get();
				} else if(!params.isNull(IJsonNames.AUTH)) {
					auth = AuthJSON.fromJSON(params.optJSONObject(IJsonNames.AUTH));
				}
				
				if(auth.getEmail()!=null && auth.getName()!=null) {
					to = auth.getEmail();
					taskHolderName = auth.getName();
				}

				String title = task.getTitle()+" "+TaskUtils.parseNumber(task.getNumber());
		
				String url = URL_BASE;
				
				String description = TaskUtils.parseDescription(task).replaceAll("\\<img[^\\>]*\\>|\\&amp;|\n|<br[^\\>]*\\>", " ");

				TaskMail tm = new TaskMail()
				.setNumber(TaskUtils.parseNumber(task.getNumber()))
				.setDate(task.getModificationDate())
				.setTitle(task.getTitle())
				.setType(workflow.getType())
				.setShowRating(showRating)
				.setUrl(url)
				.setLogo(logo)
				.setDescription(description)
				.setCompanyName(companyName)
				.setTaskHolderName(taskHolderName)
				;
				
				String body = TaskMailTemplate.taskContent(tm);
				
				SESMessage msg = new SESMessage()
				.setAlias(company.getName())
				.setSubject(title)
				.setBody(body)
				.setTo(to);
				
				if(TaskUtils.isCau(params))
					msg.setBcc(EMAIL_SUPPORT);

			    SES.sendEmail(msg);
			    System.out.println("SEND EMAIL TO: "+ to);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/*
	 * Send Notification TASK_HOLDER, WORKGROUP
	 */
	private static void sendNotificationWorkflow(AonApiData api, Task task, TaskWorkflow workflow, String body) {
		Domain domain = api.getDomain();
		User user = api.getUser(); // AON_SOLUTIONS.getUser(domain, api.getToken());
		
		LinkedList<Auth> auths = TaskUtils.getAuthsTask(api, task, workflow, user);
		
		if(!auths.isEmpty()) {
			String title = task.getTitle()+" "+TaskUtils.parseNumber(task.getNumber());
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
	
	public static void sendHistoricWorkflow(AonApiData api, Task task, boolean sendSupport) {
		Thread newThread = new Thread(() -> {
			Company company = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(api.getDomain().getId()));
			if(company!=null) {
				String logo = TaskUtils.getLogoCompany(company.getDomain().getName());
				String to = null;
				String bcc = null;
				if( TaskUtils.isCau(api.getData()) ) {
					to = EMAIL_SUPPORT;
				} else if( task.getGtaskId()!=null && !task.getGtaskId().isEmpty() ) {
					to = task.getGtaskId();
					bcc = EMAIL_SUPPORT;
				}
				
				if(to!=null) {
					String subject = "SOLICITUD Nº "+ task.getNumber();
					String url = URL_BASE;
					
					String description = TaskUtils.parseDescription(task).replaceAll("\\<img[^\\>]*\\>|\\&amp;|\n|<br[^\\>]*\\>", " ");
					
					TaskMail tm = new TaskMail()
					.setNumber(TaskUtils.parseNumber(task.getNumber()))
					.setDate(task.getStartDate())
					.setTitle(task.getTitle())
					.setWorkflows(task.getWorkflows())
					.setDomainName(getTaskDomainName(task))
					.setDescription(description)
					.setUrl(url)
					.setLogo(logo);
					
					task.getTmp().ifPresent(tm::setNote);
				
					String body = TaskMailTemplate.taskWorkflowContent(tm);
					
					SESMessage msg = new SESMessage()
					.setAlias(company.getName())
					.setSubject(subject)
					.setBody(body)
					.setTo(to)
//					.setReplyTo(to)
					;
					
					if(bcc!=null && sendSupport) 
						msg.setBcc(bcc);

				    SES.sendEmail(msg);
					System.out.println("SEND EMAIL HISTORIC: "+ to);
				}
			}
		});
		newThread.start();
	}

	private static boolean isAllowed(AonApiData api, Task task, AppParamsRequest param) {

		boolean isExternal = TaskUtils.isExternal(task, api.getDomain());
		
		Domain domain = isExternal ? task.getDomain() : api.getDomain();

		ApplicationParameter appParam = AON.getApplicationParameter(domain.getName(), domain.getId(), "", param.name());
		System.out.println("param "+param+" "+ appParam.getName()+" "+appParam.getValue());
		return appParam.getId() == null || (appParam.getId()!=null && appParam.getValue().equals("true"));
	}
	
	private static String getTaskDomainName(Task task) {
		try {
			JSONObject obj = new JSONObject(task.getDescription());
			JSONObject cauInfo = obj.optJSONObject("cauInfo");
			JSONObject company = cauInfo.optJSONObject("company");
			JSONObject domain = company.optJSONObject("domain");
		
			return domain.optString(IJsonNames.NAME);
		} catch (Exception e) {}
		return task.getDomain().getName();
	}
}
