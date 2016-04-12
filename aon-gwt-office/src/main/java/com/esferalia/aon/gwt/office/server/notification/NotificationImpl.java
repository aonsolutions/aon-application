package com.esferalia.aon.gwt.office.server.notification;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.office.client.notification.INotification;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.NotificationInfo;
import com.esferalia.aon.occam.api.model.office.NotificationType;
import com.esferalia.aon.occam.api.model.security.User;

@WebServlet(name = "NotificationGwtServlet", urlPatterns = { "/aon_gwt_office/gwt_office_notification/*" })
public class NotificationImpl extends AonRemoteServiceServlet implements INotification{

	private static final long serialVersionUID = 7426471939221433842L;

	public void sendNotification(Domain domain, Notice notice,NotificationType type, String to){
		// get CONFIGURATION  
		// 		MAIL ACCOUNT
		//		FIRMA 
		// 		Notificaction automatica
		// 		historial
		NotificationInfo ni = getNotificationInfo(domain);
		
	//	LinkedList<MailAccount> mailAccountList = getMailAccountList(domain);
	//	MailAccount mailAccount = mailAccountList.get(0);
	//	to = "ander.ibz@gmail.com, aibanez@aonsolutions.es";
		String title = notice.getTitle();		
		
		String msg = 
			"<div style='margin-left: -30px;'>"
			+"<div style='margin: 7px 15px 14px 30px;line-height: 18px;font-size: 13px;box-shadow: 0px 1px 2px rgba(0, 0, 0, 0.075);'>"
		
			//logo cabecera
			+"<div><img src=\"http://"+domain.getName() + "/aonDocuments/company.logo\" width=\"25%\"></div>"
			
			// title
			+"<p></p><table style='border: 1px solid #E5E5E5;table-layout: fixed;width: 100%;min-width: 625px;border-collapse: collapse;' cellpadding='0'><tbody>"
				+"<tr><td style=\"background-color: #F6F6F6;color: #222;border: 1px solid #CCC;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;font-weight: bold; \">"
					+ "<div style='color: #222;font-size: 140%;margin-bottom: 2px;'>"+title+"</div>"
			+ "</tr></tbody></table>";
		
		if(ni.getHistory()){
			for(Notice n : notice.getComments()){
				String action = type.getTitle(); 
				SimpleDateFormat format= new SimpleDateFormat("dd/MM/yyyy");
				String desc = type.getDescription() + "<b>#"+ n.getId() +"</b> el <b>" + format.format(n.getStartDate()) +"</b> por <b>"+ n.getSender().getName() +"</b>"; 

				msg = msg +"<p></p><table style='border: 1px solid #E5E5E5;table-layout: fixed;width: 100%;min-width: 625px;border-collapse: collapse;' cellpadding='0'>"
					+"<tbody><tr>"
					+"<td style='padding: 10px;vertical-align: top;'>"
						+"<span style='color: #222;;margin-bottom: 2px;font-weight: bold;'>"+action+"</span>"
						+"<span style='color: #222;margin-bottom: 14px;'> "+desc+"</span>"
						
						+ "<table style='table-layout: fixed;border-collapse: collapse;'><tbody>";
						
				if(n.getBody() != null && !n.getBody().equals("")) // comentario.
					msg = msg + "<tr><td style='padding-right: 10px;min-width: 48px;white-space: nowrap;padding-bottom: 6px;color: #999;padding-left: 0px;padding-top: 2px;vertical-align: top;'>Comentario</td>"
								+ "<td style='padding-left: 0px;padding-top: 2px;vertical-align: top;'>"+n.getBody()+"</td></tr>";

				msg = msg + "</tbody></table></td>"
					+"</tr></tbody></table>";
			}
		} else{
			String action = type.getTitle(); 
			SimpleDateFormat format= new SimpleDateFormat("dd/MM/yyyy");
			String desc = type.getDescription() + "<b>#"+ notice.getId() +"</b> el <b>" + format.format(notice.getStartDate()) +"</b> por <b>"+ notice.getSender().getName() +"</b>"; 

			msg = msg +"<p></p><table style='border: 1px solid #E5E5E5;table-layout: fixed;width: 100%;min-width: 625px;border-collapse: collapse;' cellpadding='0'>"
				+"<tbody><tr>"
				+"<td style='padding: 10px;vertical-align: top;'>"
					+"<span style='color: #222;;margin-bottom: 2px;font-weight: bold;'>"+action+"</span>"
					+"<span style='color: #222;margin-bottom: 14px;'> "+desc+"</span>"
					
					+ "<table style='table-layout: fixed;border-collapse: collapse;'><tbody>";
					
			if(notice.getBody() != null && !notice.getBody().equals("")) // comentario.
				msg = msg + "<tr><td style='padding-right: 10px;min-width: 48px;white-space: nowrap;padding-bottom: 6px;color: #999;padding-left: 0px;padding-top: 2px;vertical-align: top;'>Comentario</td>"
							+ "<td style='padding-left: 0px;padding-top: 2px;vertical-align: top;'>"+notice.getBody()+"</td></tr>";

			msg = msg + "</tbody></table></td>"
				+"</tr></tbody></table>";
		}
		msg = msg +"<p></p><table><tbody><tr><td>"	
			+"<p style=\"color: #222;\">Para cualquier aclaración o información adicional, no dude en contactar con nosotros.</p>"
			+"<p style=\"color: #222;\">"
			+ "<div>COMPANY NAME</div>"
			+"<div>Dpto. de Atención al cliente | <b>Gracias por confiar en nosotros</b></div>"
			+ "</p>"
			+ "</td></tr></tbody></table>"
								
			+ "</div></div>"
			;
		
		sendEmail(domain, ni.getMailAccount().getId(), ni.getBcc(), title, msg);
	}
	
	public void sendEmail(Domain domain, Integer mailAccountId, String to, String issue, String message){
		try {
			JSONObject json = new JSONObject();
			json.put("mailAccountId", mailAccountId)
				.put("recipientsTo", to)
				.put("content", message)
				.put("subject", issue)
				.put("login", getUser().getLogin())
				.put("domainName", domain.getName())
				.put("domainId", domain.getId())
				.put("md5", "");
			
			sendPostHttpClient(domain.getName(),json);			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	protected void sendPostHttpClient(String domainName, JSONObject json) {
		try{
			String url = "http://"+domainName+"/aon-aio/send_email/";
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
	
	public NotificationInfo getNotificationInfo(Domain domain){
		return AON.getNotificationInfo(domain.getName(), domain.getId(), getUserLogin())
				.setMailAccountList(getMailAccountList(domain))
				.setSignatureList(getSignatureList(domain));
	}
	
	
	public void insertNotificationInfo(Domain domain, NotificationInfo notificationInfo){
		AON.insertNotificationInfo(domain.getName(), domain.getId(), getUserLogin(), notificationInfo);
	}
	
	public LinkedList<MailAccount> getMailAccountList(Domain domain) {
		User user = getUser();
		return AON.getMailAccountList(domain.getName(), domain.getId(), user.getLogin(), 
				f -> (f.getUserIdProperty().isNull().or(f.getUserIdProperty().eq(user.getId()))).and(f.getDomainProperty().eq(user.getDomain())));
	}
	
	public MailAccount getMailAccount(Domain domain, Integer mailAccountId) {
		User user = getUser();
		return AON.getMailAccount(domain.getName(), domain.getId(), user.getLogin(), 
				f -> f.getIdProperty().eq(mailAccountId));
	}
	
	
	public  String getSignature(Domain domain, User user, Integer signatureId){
		return AON.getSignature(domain.getName(), domain.getId(), user.getLogin(), signatureId)
				.getSignature();
	}
	
	public  LinkedList<Signature> getSignatureList(Domain domain){
		return AON.getSignatureList(domain.getName(), domain.getId(), getUser().getLogin(),
				f -> f.getUserIdProperty().eq(getUser().getId()));
	}
	
	public User getUser(){
		return new User()
				.setId(getUserID())
				.setLogin(getUserLogin())
				.setDomain(getUserDomainID());
	}
}
