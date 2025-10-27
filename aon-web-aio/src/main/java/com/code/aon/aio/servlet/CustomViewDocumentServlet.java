package com.code.aon.aio.servlet;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.IConstants;

@SuppressWarnings("serial")
@WebServlet(name = "CustomViewDocumentServlet", urlPatterns = {"/cvDocument/*"})
public class CustomViewDocumentServlet extends HttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(CustomViewDocumentServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			get(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		try {
			get(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());

		String domainStr = req.getServerName();
		String path = req.getPathInfo()!= null || IConstants.EMPTY.equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo() : "/";
		Domain domain = AON.getDomain(domainStr, 1, "", f -> f.getNameProperty().eq(domainStr));
		Integer customViewdomainId = domain.getParentId() != null ? domain.getParentId() : domain.getId();

		String idStr = AON.getApplicationParameter(domain.getName(), domain.getId(), "", AppParam.AON_CUSTOMIZE_ID).getValue();
		String heritableIdStr = AON.getApplicationParameter(domain.getName(), domain.getId(), "", AppParam.AON_CUSTOMIZE_HERITABLE_ID).getValue();
		Integer id = AonNumberUtils.toInteger(idStr);
		Integer heritableId = AonNumberUtils.toInteger(heritableIdStr);
		
		switch (path) {
		case "/logoCustom":
			Attach headerLogoAttach = AON.getAttach(domainStr, customViewdomainId, "", 
				p -> (p.getAttachModuleProperty().eq(id).or(p.getAttachModuleProperty().eq(heritableId)))
					.and(p.getTypeProperty().eq((byte) 2))
				    .and(p.getDescriptionProperty().eq("aon-header-logo")), AttachType.REGISTRY);
			responseFile(resp, headerLogoAttach);
			break;
		case "/loginLogoCustom":
			Attach loginLogoAttach = AON.getAttach(domainStr, customViewdomainId, "", 
			    p -> (p.getAttachModuleProperty().eq(id).or(p.getAttachModuleProperty().eq(heritableId)))
		         .and(p.getTypeProperty().eq((byte) 2))
		         .and(p.getDescriptionProperty().eq("aon-login-logo")), AttachType.REGISTRY);
			responseFile(resp, loginLogoAttach);
			break;
		case "/faviconCustom":
			Attach faviconAttach = AON.getAttach(domainStr, customViewdomainId, "", 
				p -> (p.getAttachModuleProperty().eq(id).or(p.getAttachModuleProperty().eq(heritableId)))
		         .and(p.getTypeProperty().eq((byte) 2))
		         .and(p.getDescriptionProperty().eq("favicon.svg")), AttachType.REGISTRY);
			responseFile(resp, faviconAttach);
			break;
		default:
			throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
		}
	}
	
	public void responseFile(HttpServletResponse resp, Attach attach) throws IOException {
		if (attach != null && attach.getData() != null) {
			ByteArrayInputStream is =  new ByteArrayInputStream(attach.getData());
			responseFile(resp, attach.getDescription(), is, attach.getMimeType());
		}
	}
	
	public void responseFile(HttpServletResponse resp, String filename, InputStream is, MimeType mimetype) throws IOException {
		responseFile(resp, filename, is, mimetype, "inline");
	}
	
	public void responseFile(HttpServletResponse resp, String filename, InputStream is, MimeType mimetype, String contentDisposition) throws IOException {
		addCorsHeader(resp);
        resp.setContentType(mimetype.getName());
		resp.setHeader(IConstants.CONTENT_DISPOSITION, contentDisposition + "; filename=\"" + filename + "." + mimetype.getExtension() +"\";");
		AonIOUtils.copy(is, resp.getOutputStream());
		resp.flushBuffer();
		is.close();
	}
	
	 protected void addCorsHeader(HttpServletResponse response) {
	    	response.addHeader(IConstants.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
	        response.addHeader(IConstants.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, OPTIONS, PUT, DELETE, HEAD");
	        response.addHeader(IConstants.ACCESS_CONTROL_ALLOW_HEADERS, "*");
	        response.addHeader(IConstants.ACCESS_CONTROL_MAX_AGE, "1728000");
	    }
}
