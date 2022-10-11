package com.esferalia.aon.gwt.payroll.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.doc.DOC;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet; 

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "ATTACH", urlPatterns = { "/aon_gwt_payroll/attach/*" })
public class UploadAttachServlet extends AonApiHttpServlet {
	
	private static final Logger LOGGER  = Logger.getLogger(UploadAttachServlet.class.getName());
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API ATTACH SERVLET - GET METHOD");
		try {
			manage( req, resp );
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API ATTACH SERVLET - POST METHOD");
		try {
			if(AonStringUtils.equalsIgnoreCase(req.getPathInfo(), "/download/"))
				doGet(req, resp);
			else
				manage( req, resp );
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void manage(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		switch (req.getPathInfo()) {
			case "/update/":
				response(req, resp, updateAttach(req));
				break;
			case "/create/":
				response(req, resp, createAttach(req));
				break;
			case "/download/":
				downloadAttach(req, resp);
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
		}
	}

	private JSONObject updateAttach(HttpServletRequest req) {
		// Get currentUser
		String login = req.getParameter("login");
		
		// Get domain
		String domainName = req.getParameter("domain");
		Domain domain = AON.getDomain(domainName, 0, "", f -> f.getNameProperty().eq(domainName));
		
		// Get contractId
		Integer contractId = Integer.parseInt(req.getParameter("contractId"));
		
		// Get attachId
		Integer attachId = Integer.parseInt(req.getParameter("attachId"));
		
		// Get description
		String description = req.getParameter("description");
		
		// Get type
		Byte type = AonStringUtils.isNotBlank(req.getParameter("type")) && !AonStringUtils.equals(req.getParameter("type"), "-1") ? Byte.parseByte(req.getParameter("type")) : null;
		
		// Get security
		Boolean confidential = Boolean.parseBoolean(req.getParameter("security"));
		
		// Get date
		Date date = null;
		try {
			date = AonStringUtils.isNotBlank(req.getParameter("date")) ? dateFormat.parse(req.getParameter("date")) : new Date();
		} catch (ParseException e) {
			throw new AonApiException(e.getMessage());
		}
		
		// Get scope
		Integer scope = AonStringUtils.isNotBlank(req.getParameter("scope")) && !AonStringUtils.equals(req.getParameter("scope"), "null") ? Integer.parseInt(req.getParameter("scope")) : null;
		
		// Get scope
		MimeType mimeType = AonStringUtils.isNotBlank(req.getParameter("mimeType")) ? MimeType.valueOf(req.getParameter("mimeType").toUpperCase()) : null;
		
		Attach attach = new Attach()
				.setAttachType(AttachType.CONTRACT)
				.setDomain(domain)
				.setId(attachId)
				.setAttachModule(contractId)
				.setDescription(description)
				.setType(type)
				.setConfidential(confidential)
				.setDate(date)
				.setScope(scope)
				.setMimeType(mimeType);
		
		AON.updateAttach(domainName, domain.getId(), login, attach);
				
		return new JSONObject().put("type", "update");
	}

	private JSONObject createAttach(HttpServletRequest req) {
		// Get currentUser
		String login = req.getParameter("login");
		
		// Get domain
		String domainName = req.getParameter("domain");
		Domain domain = AON.getDomain(domainName, 0, "", f -> f.getNameProperty().eq(domainName));
		
		// Get contractId
		Integer contractId = Integer.parseInt(req.getParameter("contractId"));
		
		// Get description
		String description = req.getParameter("description");
		
		// Get type
		Byte type = AonStringUtils.isNotBlank(req.getParameter("type")) && !AonStringUtils.equals(req.getParameter("type"), "-1") ? Byte.parseByte(req.getParameter("type")) : null;
		
		// Get security
		Boolean confidential = Boolean.parseBoolean(req.getParameter("security"));
		
		// Get date
		Date date = null;
		try {
			date = AonStringUtils.isNotBlank(req.getParameter("date")) ? dateFormat.parse(req.getParameter("date")) : new Date();
		} catch (ParseException e) {
			throw new AonApiException(e.getMessage());
		}
		
		// Get scope
		Integer scope = AonStringUtils.isNotBlank(req.getParameter("scope")) && !AonStringUtils.equals(req.getParameter("scope"), "undefined") ? Integer.parseInt(req.getParameter("scope")) : null;
		
		// Get scope
		MimeType mimeType = AonStringUtils.isNotBlank(req.getParameter("mimeType")) ? MimeType.valueOf(req.getParameter("mimeType").toUpperCase()) : null;
		
		// Get FilePart
		try {
			Part filePart = req.getPart("uploader");
			byte[] data = null;
			if(null != filePart) {
				InputStream is = filePart.getInputStream();
				data = readAllBytes(is);
			}
			
			Attach attach = new Attach()
					.setAttachType(AttachType.CONTRACT)
					.setDomain(domain)
					.setAttachModule(contractId)
					.setDescription(description)
					.setData(data)
					.setType(type)
					.setConfidential(confidential)
					.setDate(date)
					.setScope(scope)
					.setMimeType(mimeType);
			
			AON.insertAttach(domainName, domain.getId(), login, attach);
			
			return new JSONObject().put("type", "create");
			
		} catch (IOException | ServletException e) {
			throw new AonApiException("No se ha podido analizar el archivo");
		}
	}

	private void downloadAttach(HttpServletRequest req, HttpServletResponse resp) {
		// Get currentUser
		String login = req.getParameter("login");
		
		// Get currentUser
		String domainName = req.getParameter("domain");
		Domain domain = AON.getDomain(domainName, 0, "", f -> f.getNameProperty().eq(domainName));
		
		// Get currentUser
		Integer attachId = Integer.parseInt(req.getParameter("attachId"));
		
		try ( CloseableAONContext aonCtx = AONContext.getAONContext(domainName, login) ) {
			DOC.getContratDoc(aonCtx.getDslContext(), attachId)
			.ifPresent( doc -> {
				try {
					resp.sendRedirect(doc.getDownloadURL( "attachment; filename=\"" + doc.getDescription() + "." + doc.getMimeType(MimeType.class).getExtension() +"\";").toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			if ( resp.isCommitted() ) {
				return;
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		
		try {
			Attach attach = AON.getAttach(domainName, domain.getId(), login, f -> f.getIdProperty().eq(attachId), AttachType.CONTRACT);
			responseFile(resp, attach);
		} catch (IOException e) {
			throw new AonApiException(e.getMessage());
		}
	}
	
	@Override
	public void responseFile(HttpServletResponse resp, Attach attach) throws IOException {
		resp.setContentType(attach.getMimeType().getName());
		resp.setHeader(IConstants.CONTENT_DISPOSITION, "attachment; filename=\"" + attach.getDescription() + "." + attach.getMimeType().getExtension() +"\";");
		ServletOutputStream output = resp.getOutputStream();
		resp.setStatus(HttpServletResponse.SC_OK);
		output.write(attach.getData());
		output.flush();
		output.close();
	}
	
	protected static byte [] readAllBytes ( InputStream is ) throws IOException {
		try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
		    int nRead;
		    byte[] data = new byte[1024];
		    while ((nRead = is.read(data, 0, data.length)) != -1) {
		        buffer.write(data, 0, nRead);
		    }
		 
		    buffer.flush();
		    return buffer.toByteArray();
		}
	}

}
