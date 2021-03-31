package com.code.aon.webservice.documental;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.code.aon.webservice.common.Utils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.security.AuthDevice;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.notification.Notification;

@SuppressWarnings("serial")
@WebServlet(name = "UploadDocumentalServlet", urlPatterns = { "/ms/api/attachment_upload/*",
														"/aon_gwt_aio/ms/attachment_upload/*",
														"/aon_gwt_fiscal/ms/attachment_upload/*"})
public class UploadDocumentalServlet extends HttpServlet{
	
	private static final Logger LOGGER  = Logger.getLogger(UploadDocumentalServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Documental Servlet - POST METHOD");
		
		JSONObject json = Utils.getRequestJSON(req);
		
		String[] pathInfo = req.getPathInfo().split("/");
		String userName = pathInfo[2];
		String domainName = pathInfo[1]; 
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));

		uploadFile(domain, userName, json);
		
		resp.setContentType("application/json;charset=UTF-8");
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, new JSONObject(), new JSONObject());	
	}
	
	private void uploadFile(Domain domain, String login, JSONObject json) {
		String base64 = json.optString("content");
		String contentType = json.optString("contentType");
		String name = json.optString("contentName");
		Long size = json.optLong("contenSize");
		byte[] fileData = Base64.getDecoder().decode(base64);
		Company cp = AON.getCompany(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()));
		Integer scopeId = json.opt("scope") != null && !AonStringUtils.isEmpty(json.optString("scope"))? json.optInt("scope") : null;
		Integer categoryId = json.opt("category") != null && !AonStringUtils.isEmpty(json.optString("category"))? json.optInt("category") : null;

		Attach attach = new Attach()
    			.setAttachModule(cp.getId()) // TODO
    			.setAttachType(AttachType.REGISTRY)
    			.setData(fileData)
    			.setDescription(name)
    			.setMimeType(MimeType.get(contentType))
    			.setDomain(domain)
    			.setType(RegistryAttachmentType.CORPORATE_IDENTITY.value())
    			.setDate(new Date())
    			.setScope(scopeId)
    			.setCategory(categoryId)
    			.setConfidential(false)
    			.setDparentId(Long.toString(size));

		if(json.opt("type") != null) { 
			String type = json.optString("type");
			if(type.equalsIgnoreCase("asesor")) {
				attach.setType(RegistryAttachmentType.DOCUMENTAL_ASESOR.value());
			} else if(type.equalsIgnoreCase("employee")) {
				attach.setType(RegistryAttachmentType.DOCUMENTAL_EMPLOYEE.value());
			} else attach.setType(RegistryAttachmentType.CORPORATE_IDENTITY.value());
		}
		
		Integer attachId = AON.insertAttach(domain.getName(), domain.getId(), login, attach);
    	
    	if(json.opt("tag") != null && !AonStringUtils.isEmpty(json.optString("tag"))) {
        	AON.insertRegistryAttachTag(domain.getName(), domain.getId(), login, attachId, json.optInt("tag"));
    	}
    	
    	LinkedList<AuthDevice> auths = new LinkedList<>();
    	AON.getDomainUserStream(domain.getName(), domain.getId(), login, f -> f.getAuthProperty().isNotNull()).forEach(user -> {
    		DomainUserRoles dur = SECURITY.getDomainUserRoles(domain, login, user.getId());
    		if(RegistryAttachmentType.DOCUMENTAL_ASESOR.value() == attach.getType().byteValue()) {
    			if(dur.isDocumentalManager()) {
    				AuthDevice ad = SECURITY.getAuthDevice(domain, login, f-> f.getAuthProperty().eq(user.getAuth()));
    				if(ad != null) auths.add(ad);
    			}
    		} else if(RegistryAttachmentType.CORPORATE_IDENTITY.value() == attach.getType().byteValue()) {
    			if(dur.isDocumentalPortal()) {
    				AuthDevice ad = SECURITY.getAuthDevice(domain, login, f-> f.getAuthProperty().eq(user.getAuth()));
    				if(ad != null) auths.add(ad);
    			}
    		} else if(RegistryAttachmentType.DOCUMENTAL_EMPLOYEE.value() == attach.getType().byteValue()) {
    			if(dur.isDocumental()) {
    				AuthDevice ad = SECURITY.getAuthDevice(domain, login, f-> f.getAuthProperty().eq(user.getAuth()));
    				if(ad != null) auths.add(ad);
    			}
    		}
    	});

    	Notification notification= new Notification();
    	notification.setTitle("Nuevo Documento");
    	notification.setBody("Se ha subido un nuevo documento a la empresa " + domain.getDescription());
	    String str = "domain="+ attach.getDomain().getId() + "&id=" + attach.getId() + "&attach_type=registry";
    	String result = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
 	    String url = "http://" + domain.getName() + "/ms/download_attachment/"  + attach.getDomain().getName() + "/" + attach.getCreationUser() + "/" +  result;
    	notification.setUrl(url);
    	notification.setDeviceTokens(auths.stream().map(ad -> ad.getDeviceToken()).toArray(String[]::new));
    	notification.send();

//    	attach.setId(attachId);
//		                   	
//    	DomainGserviceaccount d = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), login);
//    	Drive drive = AonDrive.getInstace().serviceInitialize(d);
//    	User user = AON.getUser(domain.getName(), domain.getId(), login);
//    	AonDrive.getInstace().sync(drive, user, attach, false);
//    	SendNotification.sendGmail(domain, user, attach, true);

	}
}
