package com.code.aon.webservice.documental;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.code.aon.webservice.common.Utils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

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

    	Attach attach = new Attach()
    			.setAttachModule(cp.getId()) // TODO
    			.setAttachType(AttachType.REGISTRY)
    			.setData(fileData)
    			.setDescription(name)
    			.setMimeType(MimeType.get(contentType))
    			.setDomain(domain)
    			.setType(RegistryAttachmentType.CORPORATE_IDENTITY.value())
    			.setDate(new Date())
    			.setScope(null)
    			.setConfidential(false)
    			.setDparentId(Long.toString(size));
    	Integer attachId = AON.insertAttach(domain.getName(), domain.getId(), login, attach);
   	
//    	attach.setId(attachId);
//		                   	
//    	DomainGserviceaccount d = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), login);
//    	Drive drive = AonDrive.getInstace().serviceInitialize(d);
//    	User user = AON.getUser(domain.getName(), domain.getId(), login);
//    	AonDrive.getInstace().sync(drive, user, attach, false);
//    	SendNotification.sendGmail(domain, user, attach, true);

	}
}
