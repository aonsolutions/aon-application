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

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Certificate.CertificateOwner;
import com.esferalia.aon.occam.api.model.Certificate.CertificateSecurity;
import com.esferalia.aon.occam.api.model.Certificate.CertificateType;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;
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
			InputStream is = filePart.getInputStream();
			data = readAllBytes(is);
			
			Certificate certificate = new Certificate()
					.setDescription(fileName)
					.setTags(tagTypes)
					.setOwner(owner)
					.setData(data)
					.setPassword(password)
					.setId(rattachId)
					.setPasswordId(raddinfoId)
					.setConfidential(security);
			
			AON.saveCertificate(domainName, domain.getId(), currentUser, user.getId(), certificate);
			
		} catch (IOException | ServletException e) {
			try {
				res.sendError(123, "Algo ha fallado");
			} catch (IOException ex) {
				System.out.println("Algo ha fallado");
			}
		}
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
