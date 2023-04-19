package com.esferalia.aon.gwt.payroll.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Certificate.CertificateOwner;
import com.esferalia.aon.occam.api.model.Certificate.CertificateSecurity;
import com.esferalia.aon.occam.api.model.CertificateInfo;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet; 

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "CERTIFICATE", urlPatterns = { "/aon_gwt_payroll/certificate/*" })
public class CertificateServlet extends AonApiHttpServlet {
	
	private static final Logger LOGGER  = Logger.getLogger(CertificateServlet.class.getName());
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API CERTIFICATE SERVLET - GET METHOD");
		try {
			manage( req, resp );
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API CERTIFICATE SERVLET - POST METHOD");
		try {
			manage( req, resp );
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
		
	private void manage(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		switch (req.getPathInfo()) {
			case "/check/":
				response(req, resp, getCertificateInfo(req));
				break;
			case "/create/":
				response(req, resp, createCertificate(req));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
		}
	}

	private JSONObject getCertificateInfo(HttpServletRequest req) {
		// Get extension and parse to MimeType
		String password = req.getParameter("password");
		
		// Get FilePart
		try {
			Part filePart = req.getPart("uploader");
			byte[] data = null;
			InputStream is = filePart.getInputStream();
			data = readAllBytes(is);
			
			return toJSON(AON.getCertificateInfo(data, password));
			
		} catch (IOException | ServletException e) {
			throw new AonApiException("Contrase\u00F1a incorrecta");
		}
	}
	
	private JSONObject createCertificate(HttpServletRequest req) {
		// Get rattach Id
		String rattachIdStr = req.getParameter("rattachId");
		Integer rattachId = AonStringUtils.isBlank(rattachIdStr) ? null : Integer.parseInt(rattachIdStr);
		
		// Get raddinfo Id
		String raddinfoIdStr = req.getParameter("raddinfoId");
		Integer raddinfoId = (AonStringUtils.isBlank(raddinfoIdStr) || AonStringUtils.equals(raddinfoIdStr, "null")) ? null : Integer.parseInt(raddinfoIdStr);
		
		// Get extension and parse to MimeType
		String fileName = req.getParameter("filename");
		
		// Get password
		String password = req.getParameter("password");
		
		// Get currentUser
		String currentUser = req.getParameter("currentUser");
		
		// Get currentUser
		String domainName = req.getParameter("currentDomain");
		
		Domain domain = AON.getDomain(domainName, 0, "", f -> f.getNameProperty().eq(domainName));
		User user = AON.getUser(domainName, domain.getId(), currentUser);
		
		// Get Security
		String securityStr = req.getParameter("security");
		CertificateSecurity security = AonStringUtils.equalsIgnoreCase(securityStr, "public") ? CertificateSecurity.PUBLIC : CertificateSecurity.PRIVATE;
		
		// Get tags type
		List<CertificateType> tagTypes = new ArrayList<>();
		String tgss = req.getParameter("tgss");
		if(AonStringUtils.isNotBlank(tgss)) tagTypes.add(CertificateType.TGSS);
		String sepe = req.getParameter("sepe");
		if(AonStringUtils.isNotBlank(sepe)) tagTypes.add(CertificateType.SEPE);
		String aeat = req.getParameter("aeat");
		if(AonStringUtils.isNotBlank(aeat)) tagTypes.add(CertificateType.AEAT);
		
		// Get owner
		String ownerValue = req.getParameter("owner");
		CertificateOwner owner = AonStringUtils.equalsIgnoreCase(ownerValue, "USER") ? CertificateOwner.USER : CertificateOwner.ENTERPRISE;
		
		// Get FilePart
		try {
			Part filePart = req.getPart("uploader");
			byte[] data = null;
			if(null != filePart) {
				InputStream is = filePart.getInputStream();
				data = readAllBytes(is);
			}
			
			Certificate certificate = new Certificate()
					.setDescription(fileName)
					.setTags(tagTypes)
					.setOwner(owner)
					.setData(data)
					.setPassword(password)
					.setId(rattachId)
					.setConfidential(security);
			
			AON.saveCertificate(domainName, domain.getId(), currentUser, user.getId(), certificate);
			
			return new JSONObject().put("type", "create");
			
		} catch (IOException | ServletException e) {
			throw new AonApiException("No se ha podido analizar el archivo");
		}
	}

	private JSONObject toJSON(CertificateInfo certificateInfo) {
		JSONObject certificateInfoJson = new JSONObject();
		certificateInfoJson.put("enterprise", normalize(certificateInfo.getEnterprise()));
		certificateInfoJson.put("cif", certificateInfo.getCif());
		certificateInfoJson.put("name", normalize(certificateInfo.getName()));
		certificateInfoJson.put("surname", normalize(certificateInfo.getSurname()));
		certificateInfoJson.put("document", certificateInfo.getDocument());
		certificateInfoJson.put("typeCert", certificateInfo.getType());
		certificateInfoJson.put("ocupation", certificateInfo.getOcupation());
		certificateInfoJson.put("fromDate", dateFormat.format(certificateInfo.getFromDate()));
		certificateInfoJson.put("toDate", dateFormat.format(certificateInfo.getToDate()));
		return certificateInfoJson;
	}
	
	protected static String normalize(String input) {
		if(AonStringUtils.isBlank(input)) return input;
		 
		return Normalizer
	        .normalize(input, Normalizer.Form.NFD)
	        .replaceAll("[^\\p{ASCII}]", "");
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
