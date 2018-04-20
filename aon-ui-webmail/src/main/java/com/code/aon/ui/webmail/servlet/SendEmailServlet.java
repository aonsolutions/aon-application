package com.code.aon.ui.webmail.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.code.aon.common.util.AonFile;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.IMailAccount;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.bean.AonMessage;
import com.code.aon.webmail.bean.AonServer;
import com.code.aon.webmail.enumeration.ConnectionSecurity;
import com.code.aon.webmail.enumeration.MailAccountType;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;

@SuppressWarnings("serial")
@WebServlet(name = "SendEmail", urlPatterns = { "/send_email/*" })
public class SendEmailServlet extends HttpServlet{

	public static SendEmailServlet getInstance() {
		return new SendEmailServlet();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String jsonData = req.getParameter("details");  
			JSONObject jsonRequest = new JSONObject(jsonData);
			Integer mailAccountId = jsonRequest.getInt("mailAccountId");
			String recipientsTo = jsonRequest.getString("recipientsTo");
			String content = jsonRequest.getString("content");
			String subject = jsonRequest.getString("subject");
			String md5 = jsonRequest.getString("md5");
			String login = jsonRequest.getString("login");
			String domainName = jsonRequest.getString("domainName");
			String bcc = jsonRequest.getString("bcc");
			Integer domainId = jsonRequest.getInt("domainId");

			Domain domain = AON.getDomain(domainName, domainId, login);
			MailAccount ma = getMailAccount(domain, login, mailAccountId);		
			
			if(md5 != null && !md5.equals("")){
				String attachName = jsonRequest.getString("attachName");
				Integer mimetype = jsonRequest.getInt("mimetype");
				MimeType mimeType = MimeType.values()[mimetype];
				byte[] data = Base64.getDecoder().decode(md5.getBytes());//(byte[]) req.getSession().getAttribute(md5);		
				sendEmail(domain, ma, recipientsTo, bcc, content, subject, attachName, mimeType, md5, data);
			} else sendEmail(domain, ma,recipientsTo, bcc, content, subject); 
			resp.setContentType("application/json");
			JSONObject json = new JSONObject();
			json.put("response", "200. ok");

		} catch (JSONException e) {
			resp.setContentType("application/json");
			JSONObject json = new JSONObject();
			try {
				json.put("response", "999. fail");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	
	public void sendEmail(Domain domain, MailAccount ma,
			String recipientsTo,String bcc, String content, String subject , String attachName,
			MimeType mimetype, String md5, byte[] data) {
		//MailConfigController mcg = new MailConfigController();
		
				//(MailConfigController) AonUtil
					//	.getRegisteredBean(IWebMailConstants.BEAN_MAIL_CONFIG);
		//IMailAccount ima2 = (IMailAccount) ma;
		IMailAccount ima2 = (IMailAccount) getMa2(ma);
		/*IMailAccount ima2 = mcg.getDefaultMailAccount(true); 
		List<IMailAccount> imas = mcg.getIMailAccounts();
		for (IMailAccount iMailAccount : imas) {
			if (ma.getEmail().equals(iMailAccount.getEmail())) {
				ima2 = iMailAccount;
			}
		}*/
		System.out.println(ima2.getDisplayName());
		AonServer server = new AonServer(ima2);
		
		MessageController mc = new MessageController();
		mc.setRecipientsTo(recipientsTo);
		mc.setRecipientsBcc(bcc);
		mc.setContent(content);
		mc.setSenderMailAccount(ima2);
		mc.setSubject(subject);

				
		mc.initNewMsgFileList();

					
		AonFile aonFile = new AonFile();
		java.io.File file = new java.io.File("/tmp/"
				+ attachName);
		try {
			org.apache.commons.io.FileUtils.writeByteArrayToFile(file, data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		aonFile.setFile(file);
		aonFile.setData(data);
		aonFile.setFileName(attachName);
		aonFile.setMimeType(com.code.aon.common.enumeration.MimeType.get(mimetype.getName()));
		mc.addAttachment(aonFile);
	

		try {
			AonMessage sentMessage = mc.compoundMessage(server);// Utils.getAonMessage(server,ma);
			server.sendMessage(sentMessage);
		} catch (WebmailException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public void sendEmail(Domain domain, MailAccount ma, String recipientsTo, String bcc,String content, String subject) {
		IMailAccount ima2 = (IMailAccount) getMa2(ma);
		ima2.getEmail();
		System.out.println(ima2.getDisplayName());
		AonServer server = new AonServer(ima2);
		
		MessageController mc = new MessageController();
		mc.setRecipientsTo(recipientsTo);
		mc.setRecipientsBcc(bcc);
		mc.setContent(content);
		mc.setSenderMailAccount(ima2);
		mc.setSubject(subject);
				
		mc.initNewMsgFileList();

		try {
			AonMessage sentMessage = mc.compoundMessage(server);
			server.sendMessage(sentMessage);
		} catch (WebmailException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public MailAccount getMailAccount(Domain domain, String login, Integer mailAccountId) {
		return AON.getMailAccount(domain.getName(), domain.getId(), login, 
				f -> f.getIdProperty().eq(mailAccountId));
	}
	
	public String getSignature(Domain domain, User user, Integer signatureId){
		return AON.getSignature(domain.getName(), domain.getId(), user.getLogin(), signatureId)
				.getSignature();
	}
	
	public com.code.aon.webmail.db.MailAccount getMa2(MailAccount ma) {
		com.code.aon.webmail.db.MailAccount ma2 = new com.code.aon.webmail.db.MailAccount();
		ma2.setDefaultAccount(ma.getDefaultAccount() == 1);
		ma2.setDisplayName(ma.getDisplayName());
		ma2.setDomain(ma.getDomain());
		ma2.setDraftFolder(ma.getDraftFolder());
		ma2.setEmail(ma.getEmail());
		ma2.setId(ma.getId());
		ma2.setIncomingHost(ma.getIncomingHost());
		ma2.setIncomingPort(ma.getIncomingPort());
		ma2.setIncomingSecurity(ConnectionSecurity.values()[ma.getIncomingSecurity()]);
		ma2.setMailUsername(ma.getMailUsername());
		ma2.setName(ma.getName());
		ma2.setOutgoingHost(ma.getOutgoingHost());
		ma2.setOutgoingPort(ma.getOutgoingPort());
		ma2.setOutgoingVerification(ma.getOutgoingVerification() == 1);
		ma2.setOutgoingSecurity(ConnectionSecurity.values()[ma.getOutgoingSecurity()]);
		ma2.setPasswordString(ma.getPassword());
		ma2.setProtocol(ma.getProtocol());
		ma2.setReplyToMail(ma.getReplytoMail());
		ma2.setSentFolder(ma.getSentFolder());
		ma2.setSpamFolder(ma.getSpamFolder());
		ma2.setTrashFolder(ma.getTrashFolder());
		ma2.setType(MailAccountType.values()[ma.getType()]);
		
		return ma2;
	}
	
}
