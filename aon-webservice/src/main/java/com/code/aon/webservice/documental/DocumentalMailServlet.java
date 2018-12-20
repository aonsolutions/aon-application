package com.code.aon.webservice.documental;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.common.Utils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.security.User;

@WebServlet(name = "DocumentalNotification", urlPatterns = { "/documental_notification/*",
															  "/aon_gwt_aio/documental_notification/*"})
public class DocumentalMailServlet extends HttpServlet{
	private static final long serialVersionUID = 7426471939221433842L;

	private static final Logger LOGGER  = Logger.getLogger(DocumentalMailServlet.class.getName());
	private String msg; 
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)  {
		LOGGER.info("Notification Servlet - GET METHOD");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Notification Servlet - POST METHOD");
		
		String[] pathInfo = req.getPathInfo().split("/");
		String login = pathInfo[2];
		String domainName = pathInfo[1]; 
		Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));
		JSONObject json = Utils.getRequestJSON(req);

		Integer mail_account = json.getInt(MSG.MAIL_ACCOUNT);
		Integer signature_id = json.getInt(MSG.SIGNATURE);
		String to = json.getString("to");
		JSONArray array = json.getJSONArray("id");
		Integer[] ids = new Integer[array.length()];
		for(Integer i = 0; i < array.length(); i++) {
			Integer id = array.getInt(i);
			ids[i] = id;
		}
		LinkedList<Attach> attachStream = AON.getAttachStream(domain.getName(), domain.getId(), login, f -> f.getIdProperty().in(ids), AttachType.REGISTRY, false)
				.collect(Collectors.toCollection(LinkedList::new));
		
		Signature signature = null;
		if(!signature_id.equals(-1)){
			signature = AON.getSignature(domain.getName(), domain.getId(), login, signature_id);
		}
		String scheme = req.getParameter("scheme");
		MailAccount ma = AON.getMailAccount(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(mail_account));
		User user =AON.getUser(domain.getName(), domain.getId(), login);
		sendNotification(domain, user, attachStream, signature, ma, to, scheme);		
	}
		
	public void sendNotification(Domain domain, User user, LinkedList<Attach> attachList, Signature signature, MailAccount mailAccount, String to, String scheme){
		String content = getContent(domain, user, attachList, signature, scheme);
		sendEmail(domain, user.getLogin(), mailAccount.getId(), to, "Notificaci�n Documental", content, scheme);
	}
	
	private String getUrl(Domain domain, String login, Integer id, String scheme) {
		String str ="domain="+ domain.getId() + "&id=" + id + "&attach_type=registry";
		String base64 = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
		return scheme + "://" + domain.getName() + "/aon_gwt_aio/download_attachment/"+ domain.getName() + "/" + login + "/" + base64;
	}	
	
	
	private String getContent(Domain domain, User user, LinkedList<Attach> attachList, Signature signature, String scheme) {
		msg = "<div> Estimado Colaborador, </div><div><p></p></div>";
		Domain userDomain = AON.getDomain(domain.getName(), user.getDomain(), user.getLogin());
		msg = msg + "<div style='margin-left: -30px;'>"
				+"<div style='margin: 7px 15px 14px 30px;line-height: 18px;font-size: 13px;box-shadow: 0px 1px 2px rgba(0, 0, 0, 0.075);'>";
		msg = msg + "<p> El usuario <b>"+ (user.getName() != null ? user.getName() : user.getLogin()) +"</b> de la empresa <b>" + userDomain.getDescription() +
			"</b> ha compartido "+ " los siguientes archivos: </p>";
		msg = msg + "<br>";
		
		for(Attach attach : attachList) {
			msg = msg + "<a href=\""+ getUrl(domain, user.getLogin(), attach.getId(), scheme) +"\" style=\"text-decoration: none;color:#fff;\">"
				+ "<div style=\"color:#fff;background-color:#4d90fe;padding: 15px;font-weight: bold;width: 120px;margin-bottom:10px;\">"
					+ attach.getDescription()
				+ "</div>"
			+ "</a>";
		}
		
		printSignature(domain, user.getLogin(), signature);

		msg = msg + "</div> </div>";
		return msg;
	} 
	
	private void printSignature(Domain domain, String login, Signature signature){
		if(signature != null && signature.getSignature() != null
			&& signature.getSignature() != "")
			msg = msg+ "<p></p>"+ signature.getSignature();
		else msg = msg +"<p></p><table><tbody><tr><td>"	
				+"<p style=\"color: #222;\">Para cualquier aclaración o información adicional, no dude en contactar con nosotros.</p>"
				+"<p style=\"color: #222;\">"
				+ "<div>"+getCompanyName(domain, login)+"</div>"
				+"<div><b>Gracias por confiar en nosotros</b></div>"
				+ "</p>"
				+ "</td></tr></tbody></table>"			
				+ "</div></div>";
	}
	
	public void sendEmail(Domain domain, String login, Integer mailAccountId, String to, String issue, String message, String scheme){
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
				.put("bcc", "");
			
			sendPostHttpClient(domain, login, json, scheme);			
		} catch (JSONException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}
	
	protected void sendPostHttpClient(Domain domain, String login, JSONObject json, String scheme) {
		try{
			String url = scheme + "://"+domain.getName()+ "/send_email/";
			System.out.println(url);
			HttpClientBuilder base = HttpClientBuilder.create();
			HttpClient client = base.build();
			HttpPost post = new HttpPost(url);
			List<NameValuePair> urlParameters =  new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("details", json.toString()));
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			client.execute(post);
		} catch (IOException e){
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}
	
	private String getCompanyName(Domain domain, String login) {
		return AON.getCompanyForDomain(domain.getName(), domain.getId(), login).getName();
	}
}
