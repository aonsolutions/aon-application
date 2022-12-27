package com.code.aon.webservice.documental;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.GmailUtils;
import com.code.aon.google.apis.UrlShortenerUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.urlshortener.Urlshortener;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

public class SendNotification {

	
	public static void sendGmail(Domain domain, User user, Attach attach, Boolean isNew) {
		LinkedList<String> to = new LinkedList<>();
		
		List<User> users = AON.getUsers(attach.getDomain().getId(), attach.getDomain().getName(), user.getLogin());
		
		if(user.getDomain().equals(attach.getDomain().getParentId())) {
			Company c = AON.getCompany(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDomainProperty().eq(attach.getDomain().getId()));
			RegistryMedia rm = AON.getRMedia(domain.getName(), domain.getId(), user.getLogin(), f -> f.getRegistryProperty().eq(c.getId()).and(f.getMediaProperty().eq((byte) 4)));
			if(rm.getValue() != null) {
				to.add(rm.getValue());
			}
		} else if(attach.getDomain().getParentId() != null) {
			Company c = AON.getCompany(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDomainProperty().eq(attach.getDomain().getParentId()));
			RegistryMedia rm = AON.getRMedia(domain.getName(), domain.getId(), user.getLogin(), f -> f.getRegistryProperty().eq(c.getId()).and(f.getMediaProperty().eq((byte) 4)));
			if(rm.getValue() != null) {
				to.add(rm.getValue());
			}
		}
		
		users.stream().forEach(r -> {
			if(!user.getId().equals(r.getId())) {
				if(attach.getScope() != null ) {
					Integer[] scpArr = null;
					try {
						scpArr = AON.getUserScopes(attach.getDomain().getName(), attach.getDomain().getId(), user.getLogin(), r.getId());
					} catch (Exception e) {}
					LinkedList<Integer> l = new LinkedList<>();
					if(scpArr != null) {
						l = new LinkedList<>(Arrays.asList(scpArr));
					} 
					if(l.contains(attach.getScope())) {
						MailAccount ma = AON.getMailAccount(attach.getDomain().getName(), attach.getDomain().getId(), user.getLogin(), f -> f.getUserIdProperty().eq(r.getId())
								.and(f.getDomainProperty().eq(attach.getDomain().getId()).or(f.getDomainProperty().eq(attach.getDomain().getParentId()))));
						if(ma.getEmail() != null) {
							to.add(ma.getEmail());
						}
					}
				} else {
					MailAccount ma = AON.getMailAccount(domain.getName(), domain.getId(), user.getLogin(), f -> f.getUserIdProperty().eq(r.getId())
							.and(f.getDomainProperty().eq(attach.getDomain().getId()).or(f.getDomainProperty().eq(attach.getDomain().getParentId()))));
					if(ma.getEmail() != null) {
						to.add(ma.getEmail());
					}
				}
			}
		});
		
		
		try {
			DomainGserviceaccount g = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), user.getLogin());		
			Gmail gmail = GmailUtils.serviceInitialize(g);
			MimeMessage email = createEmail(to , g.getGoogleAccount(), isNew ? "Nuevo Documento" : "Documento Editado", getContent(domain, user, attach, to, isNew), "DOCUMENTAL | " + domain.getDescription());
			GmailUtils.sendMessage(gmail, "me", email); 
		} catch (MessagingException | IOException | GeneralSecurityException e) {
			e.printStackTrace();
		}
	}
	
	
	private static String getContent(Domain domain, User user, Attach attach, LinkedList<String> to, Boolean isNew) {
		Domain userDomain = AON.getDomain(domain.getName(), user.getDomain().getId(), user.getLogin());
		String msg = "<div style='margin-left: -30px;'>"
				+"<div style='margin: 7px 15px 14px 30px;line-height: 18px;font-size: 13px;box-shadow: 0px 1px 2px rgba(0, 0, 0, 0.075);'>";
		msg = msg + "<p> El usuario <b>"+ (user.getName() != null ? user.getName() : user.getLogin()) +"</b> de la empresa <b>" + userDomain.getDescription() +
			(isNew ? "</b> ha compartido "+ (userDomain.getId().equals(attach.getDomain().getId()) ? "" : "en la empresa <b>" + attach.getDomain().getDescription()+ "</b>") + " el archivo: </p> <p> <b>" : "</b> ha editado el archivo: </p><p><b>") + attach.getDescription() + "." + attach.getMimeType().getExtension() + "</b></p>";
		msg = msg + "<br>"
				+ "<a href=\""+ getUrl(domain, user, attach, to) +"\" style=\"text-decoration: none;color:#fff;\">"
					+ "<div style=\"color:#fff;background-color:#4d90fe;padding: 15px;font-weight: bold;width: 120px;\">"
						+ "Ver Documento"
					+ "</div>"
				+ "</a>";
		msg = msg + "</div> </div>";
		return msg;
	}
	
	public static MimeMessage createEmail(LinkedList<String> to, String from, String subject,
			  String bodyText, String fromName) throws MessagingException {
	    Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);

	    MimeMessage email = new MimeMessage(session);
	    try {
			email.setFrom(new 	InternetAddress(from, fromName));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	    for(String e : to) {
	    	email.addRecipient(javax.mail.Message.RecipientType.BCC,
                    new InternetAddress(e));	
	    }	
	    email.setSubject(subject);
	    email.setContent(bodyText, "text/html");
	    return email;
	  }
	
	public static String getUrl(Domain domain, User user, Attach attach, LinkedList<String> mails){		
		String link = "";
		try {
			DomainGserviceaccount g = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), user.getLogin());
			Urlshortener u = UrlShortenerUtils.serviceInitialize(g);
		
			if(attach.getDriveId() != null){
				Drive drive = DriveUtils.serviceInitialize(g);
				File file = DriveUtils.getFile(drive, attach.getDriveId());
				link = file.getWebViewLink();
				AonDrive.getInstace().setPermissions(drive, file.getId(), mails);
				return file.getWebViewLink();	
			}
			else{
				String md5 = md5(domain, user, attach);
				link = domain.getName() +"/aonDocuments/"+ attach.getId() +"-"+ md5;
				return UrlShortenerUtils.getShortUrl(u, link);
			}
		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
		}
		return link;
	}
	
	private static String md5(Domain domain, User user, Attach attach) {
		if(attach.getDriveId() != null) {
			DomainGserviceaccount g = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), user.getLogin());
			Drive drive = DriveUtils.serviceInitialize(g);
			File file = DriveUtils.getFile(drive, attach.getDriveId());
			return file.getMd5Checksum();
		} else if(attach.getData() != null){
			return AonFileUtils.getMD5Checksum(attach.getData());
		}
		return null;
	}
}
