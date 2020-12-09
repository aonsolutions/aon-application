package com.esferalia.aon.gwt.payroll.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.esferalia.aon.gwt.payroll.jooq.JooqDigitalCertificate;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.watson.util.AonStringUtils; 

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "CERTIFICATE", urlPatterns = { "/aon_gwt_payroll/certificate/*" })
public class CertificatesServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// Get extension and parse to MimeType
		String extension = req.getParameter("extension");
		byte mimeType = getMimeType(extension);
		
		// Get extension and parse to MimeType
		String fileName = req.getParameter("filename");
		
		// Get extension and parse to MimeType
		String certificateTypeStr = req.getParameter("certificatetype");
		Byte certificateType = Byte.parseByte(certificateTypeStr);
		
		// Get currentUser
		String currentUser = req.getParameter("currentUser");
		
		// Get currentUser
		String domainName = req.getParameter("currentDomain");
		

		if(AonStringUtils.isEmpty(currentUser)) {
			String token = req.getParameter("token");
			Domain domain = AON.getDomain(domainName, 0, "", f -> f.getNameProperty().eq(domainName));
			currentUser = AON_SOLUTIONS.getUser(domain, token).getLogin();
		}
		
		// Get FilePart
		Part filePart = req.getPart("uploader");
		
		try ( InputStream is = filePart.getInputStream() ){
				byte data [] = toByteArray(is);
				JooqDigitalCertificate.setDigitalCertificateData(domainName, currentUser, mimeType, fileName, certificateType, data);
		}		
	}
	
	private byte getMimeType(String extension) {
		switch (extension) {
		case "p12":
			return (byte)36;
		default:
			return (byte)36;
		}
	}

	private static byte[] toByteArray(InputStream is) throws IOException {
	    ByteArrayOutputStream os = new ByteArrayOutputStream(); 
	    byte[] buffer = new byte[0x0FFF];
	    for (int len = is.read(buffer); len != -1; len = is.read(buffer)) { 
	        os.write(buffer, 0, len);
	    }
	    return os.toByteArray();
	}

}
