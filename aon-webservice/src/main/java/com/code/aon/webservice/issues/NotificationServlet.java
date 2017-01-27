package com.code.aon.webservice.issues;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.code.aon.webservice.common.Utils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.occam.api.model.Task;
import com.esferalia.aon.occam.api.model.office.NotificationInfo;
import com.esferalia.aon.occam.api.model.office.NotificationType;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.task.NotificationMode;
import com.esferalia.aon.occam.api.model.task.TaskComment;
import com.esferalia.aon.occam.api.model.task.TaskEvent;
import com.esferalia.aon.occam.api.model.type.AppParam;

@WebServlet(name = "NotificationGwtServlet2", urlPatterns = { "/notification/*",
															  "/aon_gwt_aio/notification/*"})
public class NotificationServlet extends HttpServlet{
	
	private static final DBConsults DB = DBConsults.getInstance();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)  {
		System.out.println("GET METHOD");
		String accessToken = req.getParameter("access_token");
		String[] pathInfo = req.getPathInfo().split("/");
		String userName = pathInfo[1];
		String domainName = pathInfo[2]; 
			
		String md5 = Utils.getMd5(userName+domainName);
		if(accessToken.equals(md5)){
			Domain domain = DB.getDomain(domainName, userName);			
			Object object = new Object();
			NotificationInfo notificationInfo = DB.getNotificationInfo(domain, userName);
			LinkedList<MailAccount> mailAccountList = DB.getMailAccountList(domain, userName);
			LinkedList<Signature> signatureList = DB.getSignatureList(domain, userName);
			object = notificationInfo2JSON(notificationInfo, mailAccountList, signatureList);
				
			Utils.giveBack(req, resp, object, new JSONObject());
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("POST METHOD");
		
		String[] pathInfo = req.getPathInfo().split("/");
		String userName = pathInfo[1];
		String domainName = pathInfo[2]; 
		Domain domain = DB.getDomain(domainName, userName);

		String line = "";
		String s = "";
		while((line = req.getReader().readLine()) != null)
			s = s + " " + line;
		System.out.println(s);
		s = Utils.checkString(s);
		System.out.println(s);
		if(s == null || s.equals("")) s = "{}";
		JSONObject json = new JSONObject(s);
		
		if(pathInfo.length > 3){
			if(pathInfo[3].equals("configuration")){
				updateNotificationInfo(domain, userName, json);
			}
		} else {
			Integer number = json.getInt("number");
			NotificationType notificationType = NotificationType.values()[json.getInt("notification_type")];
			Task task = DB.getTaskWithNumber(domain, userName, number);
			NotificationInfo ni = buildNotificationInfo(domain, userName, task, notificationType);
			LinkedList<NotificationInfo> list = buildNotificationInfoList(domain, userName, task, notificationType);
			sendNotification(domain, userName, ni, list, notificationType,"", null);
		}
	}
	
	private static final long serialVersionUID = 7426471939221433842L;
	
	private static final String[] OPEN = new String[]{"NUEVA INCIDENCIA ","creada "};
	private static final String[] NEW_INFO = new String[]{"INFORMACION ADICIONAL ","incluida "};
	private static final String[] CLOSE = new String[]{"INCIDENCIA CERRADA ",""};
	private static final String[] REOPEN = new String[]{"INCIDENCIA REABIERTA ",""};
	private static final String[] DESCRIPTION = new String[]{"INFORMACION ","descrita"};

	
	private String msg; 
	public void sendNotification(Domain domain, String login, NotificationInfo notificationInfo,
			LinkedList<NotificationInfo> list, NotificationType type, String closeUrl, Integer assignee){
		NotificationInfo ni = getNotificationInfo(domain, login);
		Boolean isManual = type.equals(NotificationType.MANUAL);
		String typeTitle = "";
		String typeDescription = "";
		if(type.equals(NotificationType.OPEN)){
			if(!ni.getNotifyOpen()) return;
			typeTitle = OPEN[0];
			typeDescription = OPEN[1];
		} else if(type.equals(NotificationType.NEW_INFO)){
			if(!ni.getNotifyComment()) return;
			typeTitle = NEW_INFO[0];
			typeDescription = NEW_INFO[1];
		}else if(type.equals(NotificationType.ASSIGNEE)){
			if(!ni.getNotifyAssignee()) return;
			typeTitle = NEW_INFO[0];
			typeDescription = NEW_INFO[1];
		} else if(type.equals(NotificationType.REOPEN)){
			if(!ni.getNotifyReopen()) return;
			typeTitle = REOPEN[0];
			typeDescription = REOPEN[1];
		} else if(type.equals(NotificationType.CLOSE)){
			if(!ni.getNotifyClose()) return;
			typeTitle = CLOSE[0];
			typeDescription = CLOSE[1];
		}

		if((!ni.getMode().equals(0)) || isManual){
			String title = notificationInfo.getTitle();		
			Domain dom = DB.getDomain(domain, login);

			msg = 	"<div style='margin-left: -30px;'>"
					+"<div style='margin: 7px 15px 14px 30px;line-height: 18px;font-size: 13px;box-shadow: 0px 1px 2px rgba(0, 0, 0, 0.075);'>"
					;
				//logo cabecera
			if(ni.getIsLogo())	msg = msg +"<div><img src=\"http://"+dom.getName() + "/aonDocuments/company.logo\" width=\""+ni.getLogoPercentage()+"%\"></div>";

			SimpleDateFormat format= new SimpleDateFormat("dd/MM/yyyy");
			
			NotificationInfo st = list.stream().sorted((n1,n2)-> n2.getDate().compareTo(n1.getDate()))
			.filter(n -> !n.getNotificationType().equals(NotificationType.NEW_INFO))
			.findFirst().orElse(new NotificationInfo());

			
			String status = "<b>ABIERTA</b>";
			if((st.getNotificationType() != null && st.getNotificationType().equals(NotificationType.CLOSE))
					|| type.equals(NotificationType.CLOSE)) 
				status = "<b>CERRADA</b> el <b>"+ format.format(notificationInfo.getDate()) +"</b>" ;
			if((st.getNotificationType() != null && st.getNotificationType().equals(NotificationType.REOPEN)) 
					|| type.equals(NotificationType.REOPEN))
				status = "<b>REABIERTA</b> el <b>"+ format.format(notificationInfo.getDate()) +"</b>" ;
				// title
			msg = msg +"<p></p><table style='border: 1px solid #E5E5E5;table-layout: fixed;width: 100%;min-width: 625px;border-collapse: collapse;' cellpadding='0'><tbody>"
					+"<tr><td style=\"background-color: #F6F6F6;color: #222;border: 1px solid #CCC;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
						+ "<span style='color: #222;font-size: 140%;margin-bottom: 2px;font-weight: bold;'>"+title+"</span>"
						+ "<span> con referencia <b>#" + notificationInfo.getNoticeId() + "</b></span>"
						+ "<div>"
							+ status + " , registrada el <b>" + format.format(notificationInfo.getCreateDate()) +"</b>"//+ "por XXX"
						+ "</div>"
							
						+ "<div>"
							+ "Empresa: <b>" + notificationInfo.getCompanyName() +"</b>"
						+ "</div>"
						+ "</tr></tbody></table>";
			
			if(!isManual)
				msg = msg + getMessage(notificationInfo, typeTitle, typeDescription);	
			
			if(ni.getCommentsHistory() || ni.getStatusHistory()){
				list.stream().sorted((n1,n2)-> n2.getDate().compareTo(n1.getDate())).forEach(n ->{
					String t ="";String d = "";
					if(n.getNotificationType().equals(NotificationType.OPEN)){
						t = OPEN[0];
						d = OPEN[1];
					} else if(n.getNotificationType().equals(NotificationType.NEW_INFO)){
						t = NEW_INFO[0];
						d = NEW_INFO[1];
					} else if(n.getNotificationType().equals(NotificationType.REOPEN)){
						t = REOPEN[0];
						d = REOPEN[1];
					} else if(n.getNotificationType().equals(NotificationType.CLOSE)){
						t = CLOSE[0];
						d = CLOSE[1];
					}
					if(!n.getNotificationType().equals(NotificationType.OPEN)){
						if(ni.getStatusHistory() && (n.getNotificationType().equals(NotificationType.CLOSE) 
								||n.getNotificationType().equals(NotificationType.REOPEN)))
							msg = msg + getMessage(n, t, d);
						if(ni.getCommentsHistory() && n.getNotificationType().equals(NotificationType.NEW_INFO))
							msg = msg + getMessage(n, t, d);
					}
				});
				if(notificationInfo.getDescription() != null && !notificationInfo.getDescription().equals("")
						&& ni.getCommentsHistory()){
					notificationInfo.setDate(notificationInfo.getCreateDate());
					msg = msg + getMessage(notificationInfo, DESCRIPTION[0], DESCRIPTION[1]);
				}
			}else if(isManual){
				NotificationInfo n = list.stream().sorted((n1,n2) -> n2.getDate().compareTo(n1.getDate())).findFirst().orElse(new NotificationInfo());
				String t ="";String d = "";
				if(n.getNotificationType().equals(NotificationType.OPEN)){
					t = OPEN[0];
					d = OPEN[1];
				} else if(n.getNotificationType().equals(NotificationType.NEW_INFO)){
					t = NEW_INFO[0];
					d = NEW_INFO[1];
				} else if(n.getNotificationType().equals(NotificationType.REOPEN)){
					t = REOPEN[0];
					d = REOPEN[1];
				} else if(n.getNotificationType().equals(NotificationType.CLOSE)){
					t = CLOSE[0];
					d = CLOSE[1];
				}
				msg = msg + getMessage(n, t, d);
			}
			
			if(type.equals(NotificationType.ASSIGNEE)){
				msg = msg + "<br>"
						+ "<a href=\""+ closeUrl +"\" style=\"text-decoration: none;color:#fff;\">"
							+ "<div style=\"color:#fff;background-color:#4d90fe;padding: 15px;font-weight: bold;width: 90px;\">"
								+ "Cerrar Tarea"
							+ "</div>"
						+ "</a>";
			}
		
			if(ni.getSignature() != null && ni.getSignature().getSignature() != null
					&& ni.getSignature().getSignature() != "")
				msg = msg+ "<p></p>"+ ni.getSignature().getSignature();
			else msg = msg +"<p></p><table><tbody><tr><td>"	
				+"<p style=\"color: #222;\">Para cualquier aclaración o información adicional, no dude en contactar con nosotros.</p>"
				+"<p style=\"color: #222;\">"
				+ "<div>"+getCompanyName(domain, login)+"</div>"
				+"<div>Dpto. de Atención al cliente | <b>Gracias por confiar en nosotros</b></div>"
				+ "</p>"
				+ "</td></tr></tbody></table>"			
				+ "</div></div>";
		
			Boolean bool = ni.getMode().equals(1);
			sendEmail(domain, login, ni.getMailAccount().getId(), getToEmails(domain, login, notificationInfo.getCompanyName(),bool, assignee), ni.getBcc(), title + " #" + notificationInfo.getNoticeId(), msg);
		}
	}
	
	public void sendEmail(Domain domain, String login, Integer mailAccountId, String to, String bcc, String issue, String message){
		try {
			JSONObject json = new JSONObject();
			json.put("mailAccountId", mailAccountId)
				.put("recipientsTo", to)
				.put("content", message)
				.put("subject", issue)
				.put("login", login)
				.put("domainName", domain.getName())
				.put("domainId", domain.getId())
				.put("md5", "")
				.put("bcc", bcc);
			
			sendPostHttpClient(domain.getName(),json);			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	protected void sendPostHttpClient(String domainName, JSONObject json) {
		try{
			String url = "http://"+domainName+ "/send_email/";
			System.out.println(url);
			HttpClientBuilder base = HttpClientBuilder.create();
			HttpClient client = base.build();
			HttpPost post = new HttpPost(url);
			List<NameValuePair> urlParameters =  new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("details", json.toString()));
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			HttpResponse resp = client.execute(post);
			System.out.println(resp);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private String getMessage(NotificationInfo n, String action, String typeDescription) {
		SimpleDateFormat format= new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat timeFomat = new SimpleDateFormat("HH:mm");
		String desc = typeDescription+ " el <b>" + format.format(n.getDate()) +"</b> a las <b>"+timeFomat.format(n.getDate())+"</b> ";	

		String msg = "<p></p><table style='border: 1px solid #E5E5E5;table-layout: fixed;width: 100%;min-width: 625px;border-collapse: collapse;' cellpadding='0'>"
				+"<tbody><tr>"
				+"<td style='padding: 10px;vertical-align: top;'>"
					+"<span style='color: #222;;margin-bottom: 2px;font-weight: bold;'>"+action+"</span>"
					+"<span style='color: #222;margin-bottom: 14px;'> "+desc+"</span>"
					+"<span style='color: #999;margin-bottom: 14px;'> por "+ n.getUserName()+ "</span>"
					+ "<table style='table-layout: fixed;border-collapse: collapse;'><tbody>";
		
		if(n.getBody() != null && !n.getBody().equals("")) // comentario.
			msg = msg + "<tr><td style='padding-right: 10px;min-width: 48px;white-space: nowrap;padding-bottom: 6px;color: #999;padding-left: 0px;padding-top: 2px;vertical-align: top;'>Comentario</td>"
					+ "<td style='padding-left: 0px;padding-top: 2px;vertical-align: top;'>"+n.getBody()+"</td></tr>";
		if(n.getDescription() != null && !n.getDescription().equals("")
				&& action.equals(DESCRIPTION[0]))  // DESCRIPTION.
			msg = msg + "<tr><td style='padding-right: 10px;min-width: 48px;white-space: nowrap;padding-bottom: 6px;color: #999;padding-left: 0px;padding-top: 2px;vertical-align: top;'>Descripcion</td>"
					+ "<td style='padding-left: 0px;padding-top: 2px;vertical-align: top;'>"+n.getDescription()+"</td></tr>";
		
		msg = msg + "</tbody></table></td>"
				+"</tr></tbody></table>";
		
		return msg;
	}
	
	public NotificationInfo buildNotificationInfo(Domain domain, String login, Task task, NotificationType notificationType){
		Registry enterprise = AON.getRegistry(domain.getName(), domain.getId(), login, task.getRegistry());
		NotificationInfo notificationInfo = new NotificationInfo().setTitle(task.getDescription())
				.setNoticeId(task.getNumber())
				.setCompanyName(enterprise.getName())
				.setCreateDate(task.getStartDate())
				.setDate(task.getStartDate())
				.setUserName(task.getCreationUser())
				.setDescription(task.getComments());
		if(notificationType.equals(NotificationType.REOPEN) || notificationType.equals(NotificationType.CLOSE)){
			TaskEvent taskEvent = DB.getLastTaskEvent(domain, login, task.getId());
			notificationInfo.setUserName(taskEvent.getCreationUser())
				.setDate(taskEvent.getCreationDate());
		} else if(notificationType.equals(NotificationType.NEW_INFO)){
			TaskComment taskComment = DB.getLastTaskComment(domain, login, task.getId());
			notificationInfo.setUserName(taskComment.getCreationUser())
				.setDate(taskComment.getCreationDate())
				.setBody(taskComment.getComment());			
		}
		return notificationInfo;
	}
	
	public LinkedList<NotificationInfo> buildNotificationInfoList(Domain domain, String login, Task task, NotificationType notificationType){
		LinkedList<NotificationInfo> list1 = DB.getTaskEventStream(domain, login, task.getId()).map(new TaskEvent2NotificationInfoFiller()).collect(Collectors.toCollection(LinkedList::new));
		LinkedList<NotificationInfo> list2 = DB.getTaskCommentStream(domain, login, task.getId()).map(new TaskComment2NotificationInfoFiller()).collect(Collectors.toCollection(LinkedList::new));
	
		if(notificationType.equals(NotificationType.REOPEN) || notificationType.equals(NotificationType.CLOSE)){
			if(list1 != null && !list1.isEmpty()) list1.remove(0);
		} else if(notificationType.equals(NotificationType.NEW_INFO)){
			if(list2 != null && !list2.isEmpty()) list2.remove(0);
		}
		
		list1.addAll(list2);		
		NotificationInfo open = new NotificationInfo().setDate(task.getStartDate()).setUserName(task.getCreationUser())
				.setNotificationType(NotificationType.OPEN);
		list1.add(open);
		return list1;
	}
	
	public NotificationInfo getNotificationInfo(Domain domain, String login){
		return AON.getNotificationInfo(domain.getName(), domain.getId(), login)
				.setMailAccountList(getMailAccountList(domain, login))
				.setSignatureList(getSignatureList(domain, login));
	}
	
	
	public void insertNotificationInfo(Domain domain, String login, NotificationInfo notificationInfo){
		AON.insertNotificationInfo(domain.getName(), domain.getId(), login, notificationInfo);
	}
	
	public LinkedList<MailAccount> getMailAccountList(Domain domain, String login) {		
		return AON.getMailAccountList(domain.getName(), domain.getId(), login, 
				f -> (f.getUserIdProperty().isNull())
				.and(f.getDomainProperty().eq(domain.getId())));
	}
	
	public MailAccount getMailAccount(Domain domain, String login, Integer mailAccountId) {
		return AON.getMailAccount(domain.getName(), domain.getId(), login, 
				f -> f.getIdProperty().eq(mailAccountId));
	}
	
	
	public  String getSignature(Domain domain, String login, Integer signatureId){
		return AON.getSignature(domain.getName(), domain.getId(), login, signatureId)
				.getSignature();
	}
	
	public  LinkedList<Signature> getSignatureList(Domain domain, String login){
		
		return AON.getSignatureList(domain.getName(), domain.getId(), login,
				f -> f.getUserIdProperty().isNull().and(f.getDomainProperty().eq(domain.getId())));
	}
	
	private String getCompanyName(Domain domain, String login) {
		return AON.getCompanyForDomain(domain.getName(), domain.getId(), login).getName();
	}
	
	private String getToEmails(Domain domain, String login, String name, Boolean send, Integer assignee){
		if(assignee != null){
			RegistryMedia rm = AON.getRMedia(domain.getName(), domain.getId(), login,
				f -> f.getRegistryProperty().eq(assignee).and(f.getMediaProperty().eq((byte)4)));
			return rm.getValue() != null ? rm.getValue() : "";
		} 
		
		if(send) return ""; 
		
		Registry r = AON.getRegistry(domain.getName(), domain.getId(), login, name);
		LinkedList<RegistryMedia> l = AON.getRMediaList(domain.getName(), domain.getId(), login,
				f -> f.getRegistryProperty().eq(r.getId()).and(f.getMediaProperty().eq((byte)4)));
		
		String emails = "";
		for(RegistryMedia rm : l){
			emails = emails + rm.getValue() +",";
		}
		
		System.out.println(emails);
		return emails;
	}

	private void updateNotificationInfo(Domain domain, String login, JSONObject json) {
		if(json.opt("email") != null){
			MailAccount ma = AON.getMailAccount(domain.getName(), domain.getId(), login, f -> (f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId()))).and(f.getNameProperty().eq(json.getString("email"))));
			DB.insertNotificationInfo(domain, login, ma.getId().toString(), AppParam.NOTICE_NOTIFICATION_MAIL);
		} else if(json.opt("sign") != null){
			Signature sign = AON.getSignature(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()).and(f.getNameProperty().eq(json.getString("sign"))));
			DB.insertNotificationInfo(domain, login, sign.getId().toString(), AppParam.NOTICE_NOTIFICATION_SIGNATURE);
		} else if(json.opt("mode") != null){
			String mode = "0";
			if(json.getString("mode").equals("Estado de pruebas")) mode = "1";
			else if(json.getString("mode").equals("Entorno de producci�n REAL")) mode = "2";
			DB.insertNotificationInfo(domain, login, mode, AppParam.NOTICE_NOTIFICATION_MODE);
		} else if(json.opt("logo") != null && json.opt("logo_percentage") != null){
			String logo = json.getString("logo").equals("true")? "1": "0";
			logo = logo + json.getString("logo_percentage");
			DB.insertNotificationInfo(domain, login, logo, AppParam.NOTICE_NOTIFICATION_LOGO);
		} else if(json.opt("commentHistory") != null && json.opt("statusHistory") != null){
			String comment = json.getString("commentHistory").equals("true")? "1": "0";
			String status = json.getString("statusHistory").equals("true")? "1": "0";
			DB.insertNotificationInfo(domain, login, comment+status, AppParam.NOTICE_NOTIFICATION_HISTORY);
		} else if(json.opt("notifyOpen") != null && json.opt("notifyClose") != null 
				&& json.opt("notifyReopen") != null && json.opt("notifyComment") != null
				&& json.opt("notifyAssign") != null){
			String open = json.getString("notifyOpen").equals("true")? "1": "0";
			String close = json.getString("notifyClose").equals("true")? "1": "0";
			String reopen = json.getString("notifyReopen").equals("true")? "1": "0";
			String comment = json.getString("notifyComment").equals("true")? "1": "0";
			String assign = json.getString("notifyAssign").equals("true")? "1": "0";
			DB.insertNotificationInfo(domain, login, open+close+reopen+comment+assign, AppParam.NOTICE_NOTIFICATION_AUTO);
		} else if(json.opt("bcc") != null){
			DB.insertNotificationInfo(domain, login, json.getString("bcc"), AppParam.NOTICE_NOTIFICATION_BCC);
		}
	}
	
	private static class TaskEvent2NotificationInfoFiller implements Function<TaskEvent, NotificationInfo> {
	
		@Override
		public NotificationInfo apply(TaskEvent r) {
			NotificationType nt = r.getEvent().equals("reopened") ? NotificationType.REOPEN : NotificationType.CLOSE;
			return new NotificationInfo()
					.setNotificationType(nt)
					.setDate(r.getCreationDate())
					.setUserName(r.getCreationUser());
		}
	}
	
	private static class TaskComment2NotificationInfoFiller implements Function<TaskComment, NotificationInfo> {
		
		@Override
		public NotificationInfo apply(TaskComment r) {
			return new NotificationInfo()
					.setNotificationType(NotificationType.NEW_INFO)
					.setDate(r.getCreationDate())
					.setUserName(r.getCreationUser())
					.setBody(r.getComment());
		}
	}

	private JSONArray notificationInfo2JSON(NotificationInfo notificationInfo, LinkedList<MailAccount> mailAccountList, LinkedList<Signature> signatureList){
		JSONObject json = new JSONObject();
		
		json.put("mail", notificationInfo.getMailAccount() != null && notificationInfo.getMailAccount().getName() != null ? notificationInfo.getMailAccount().getName() : "");
		json.put("sign", notificationInfo.getSignature() != null && notificationInfo.getSignature().getName() != null ? notificationInfo.getSignature().getName() : "");
		json.put("logo", notificationInfo.getIsLogo() ? "1" : "0");
		json.put("logo_percentage", notificationInfo.getLogoPercentage() != null ? notificationInfo.getLogoPercentage() : 20);
		json.put("open", notificationInfo.getNotifyOpen() ? "1" : "0");
		json.put("close", notificationInfo.getNotifyClose() ? "1" : "0");
		json.put("reopen", notificationInfo.getNotifyReopen() ? "1" : "0");
		json.put("comment", notificationInfo.getNotifyComment() ? "1" : "0");
		json.put("assign", notificationInfo.getNotifyAssignee() ? "1" : "0");
		json.put("comment_history", notificationInfo.getCommentsHistory() ? "1" : "0");
		json.put("status_history", notificationInfo.getStatusHistory() ? "1" : "0");
		json.put("bcc", notificationInfo.getBcc() != null ? notificationInfo.getBcc() : "");
		json.put("mode", NotificationMode.values()[notificationInfo.getMode()].getName());
		json.put("mail_account_list", mailAccountList2JSON(mailAccountList));
		json.put("signature_list", signatureList2JSON(signatureList));
		json.put("mode_list", modeList2JSON());
		JSONArray array = new JSONArray();
		array.put(json);
		return array;
	}
	
	private JSONArray mailAccountList2JSON(LinkedList<MailAccount> mailAccountList){
		JSONArray array = new JSONArray();
		mailAccountList.stream().forEach(ma -> {
			JSONObject json = new JSONObject();
			json.put("id",ma.getId());
			json.put("name", ma.getName());
			array.put(json);
		});
		return array;		
	}	
	
	private JSONArray signatureList2JSON(LinkedList<Signature> signatureList){
		JSONArray array = new JSONArray();
		signatureList.stream().forEach(s -> {
			JSONObject json = new JSONObject();
			json.put("id",s.getId());
			json.put("name", s.getName());
			array.put(json);
		});
		return array;		
	}	
	
	private JSONArray modeList2JSON(){
		JSONArray array = new JSONArray();
		for (NotificationMode mode : NotificationMode.values()) {
			JSONObject json = new JSONObject();
			json.put("id", mode.value());
			json.put("name", mode.getName());
			array.put(json);			
		}
		return array;		
	}	
	
}
