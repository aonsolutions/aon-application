package com.code.aon.webservice.documental;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.code.aon.webservice.common.Utils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.aonsolutions.NotificationSource;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.AuthDAO;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.notification.NotificationRequest;

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

		resp.setContentType("application/json;charset=UTF-8");
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, uploadFile(domain, userName, json), new JSONObject());	
	}
	
	private JSONObject uploadFile(Domain domain, String login, JSONObject json) {
		String base64 = json.optString("content");
		String contentType = json.optString("contentType");
		String name = json.optString("contentName");
		Long size = json.optLong("contenSize");
		byte[] fileData = Base64.getDecoder().decode(base64);
		Company cp = AON.getCompany(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()));
		Integer scopeId = json.opt(IJsonNames.SCOPE) != null && !AonStringUtils.isEmpty(json.optString(IJsonNames.SCOPE))? json.optInt(IJsonNames.SCOPE) : null;
		Integer categoryId = json.opt(IJsonNames.CATEGORY) != null && !AonStringUtils.isEmpty(json.optString(IJsonNames.CATEGORY))? json.optInt(IJsonNames.CATEGORY) : null;

		Attach attach = new Attach()
    			.setAttachModule(cp.getId())
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
    	
    	LinkedList<String> uuids = new LinkedList<>();
    	AON.getDomainUserStream(domain.getName(), domain.getId(), login, f -> f.getAuthProperty().isNotNull()).forEach(user -> {
    		DomainUserRoles dur = SECURITY.getDomainUserRoles(domain, login, user.getId());
    		if(!user.getAuth().isEmpty() &&
    			((dur.isDocumentalManager() && RegistryAttachmentType.DOCUMENTAL_ASESOR.value() == attach.getType().byteValue())
    				|| (dur.isDocumentalPortal() && RegistryAttachmentType.CORPORATE_IDENTITY.value() == attach.getType().byteValue())
    				|| (dur.isDocumental() && RegistryAttachmentType.DOCUMENTAL_EMPLOYEE.value() == attach.getType().byteValue()))) {
    		    uuids.add(user.getAuth());
   			}
    	});
    	
    	List<Auth> auths = AON_SOLUTIONS.getAuths(uuids);
    	
  
    	NotificationRequest notification= new NotificationRequest();
    	notification.setUser(new User().setLogin(login));
    	notification.setSource(NotificationSource.DOCUMENTAL);
    	notification.setSourceId(attachId);
    	notification.setTitle("Nuevo Documento");
    	notification.setBody("Se ha subido un nuevo documento a la empresa " + domain.getDescription());
	    String str = "domain="+ attach.getDomain().getId() + "&id=" + attach.getId() + "&attach_type=registry";
    	String result = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
 	    String url = "http://" + domain.getName() + "/ms/download_attachment/"  + attach.getDomain().getName() + "/" + attach.getCreationUser() + "/" +  result;
    	notification.setUrl(url);
    	notification.setAuths(auths);
    	notification.setDomain(domain);
    	notification.send();

    	JSONObject resp = new JSONObject();
    	resp.put("id", attachId);
    	return resp;
	}
}
