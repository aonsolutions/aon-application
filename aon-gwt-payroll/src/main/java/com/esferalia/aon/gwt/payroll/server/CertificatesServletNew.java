package com.esferalia.aon.gwt.payroll.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.esferalia.aon.gwt.payroll.jooq.JooqDigitalCertificate;
import com.esferalia.aon.gwt.payroll.jooq.JooqDigitalCertificateNew;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificateNew.CertificateOwner;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificateNew.CertificateType;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.watson.util.AonStringUtils; 

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "CERTIFICATE-NEW", urlPatterns = { "/aon_gwt_payroll/certificate_new/*" })
public class CertificatesServletNew extends HttpServlet {
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// Get rattach Id
		String rattachIdStr = req.getParameter("rattachId");
		Integer rattachId = null;
		try {
			rattachId = AonStringUtils.isBlank(rattachIdStr) ? null : Integer.parseInt(rattachIdStr);
		} catch (Exception e) {
			// Not use in this case
		}
		
		
		// Get raddinfo Id
		String raddinfoIdStr = req.getParameter("raddinfoId");
		Integer raddinfoId = null;
		try{
			raddinfoId = (AonStringUtils.isBlank(raddinfoIdStr) || AonStringUtils.equals(raddinfoIdStr, "null")) ? null : Integer.parseInt(raddinfoIdStr);
		} catch (Exception e) {
			// Not use in this case
		}
		
		// Get extension and parse to MimeType
		String fileName = req.getParameter("filename");
		
		// Get extension and parse to MimeType
		String password = req.getParameter("password");
		
		// Get currentUser
		String currentUser = req.getParameter("currentUser");
		
		// Get currentUser
		String domainName = req.getParameter("currentDomain");
		
		if(AonStringUtils.isEmpty(currentUser)) {
			String token = req.getParameter("token");
			Domain domain = AON.getDomain(domainName, 0, "", f -> f.getNameProperty().eq(domainName));
			currentUser = AON_SOLUTIONS.getUser(domain, token).getLogin();
		}
		
		// Get Security
		String securityStr = req.getParameter("security");
		byte security = AonStringUtils.equalsIgnoreCase(securityStr, "public") ? (byte)0 : (byte)1;
		
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
		Part filePart = req.getPart("uploader");
		byte[] data = null;
		
		try (InputStream is = filePart.getInputStream()) {
			data = readAllBytes(is);
		}
			
		JooqDigitalCertificateNew.setDigitalCertificateData(domainName, currentUser, fileName, tagTypes, owner, data, password, security, rattachId, raddinfoId);
		
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
