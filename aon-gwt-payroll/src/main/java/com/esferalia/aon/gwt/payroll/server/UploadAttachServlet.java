package com.esferalia.aon.gwt.payroll.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.jooq.JooqContractAttach; 

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "ATTACH", urlPatterns = { "/aon_gwt_payroll/attach/*" })
public class UploadAttachServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// Get Attach Id
		String attachIdStr = req.getParameter("attachId");
		Integer attachId = Integer.parseInt(attachIdStr);
		
		// Get Domain Name
		String domainName = req.getServerName();
		
		// Get extension
		String extension = req.getParameter("extension");
		
		// Get fileName
		String fileName = req.getParameter("filename");
		if(StringUtils.isBlank(fileName))
			fileName = "noname";
		else
			fileName = fileName.replaceAll("\\s+","");
		 
        // FileType : application/pdf, text/plain, text/html, image/jpg
		String fileType = getFileType(extension);
        res.setContentType(fileType);
        
        // Make sure to show the download dialog
        res.setHeader("Content-disposition","attachment; filename=" + fileName + "." + extension);
		
		try {
			ServletOutputStream output = res.getOutputStream();
			byte[] data = JooqContractAttach.getContractAttachAttachment(domainName, attachId);
			output.write(data);
			res.flushBuffer();
		} catch (Exception e) {}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// Get Attach Id
		String attachIdStr = req.getParameter("attachId");
		Integer attachId = Integer.parseInt(attachIdStr);
		
		// Get Domain Name
		String domainName = req.getServerName();
		
		// Get extension and parse to MimeType
		String extension = req.getParameter("extension");
		byte mimeType = getMimeType(extension);
		
		// Get extension and parse to MimeType
		String fileName = req.getParameter("filename");
		
		// Get FilePart
		Part filePart = req.getPart("uploader");
		
		try ( InputStream is = filePart.getInputStream() ){
				byte data [] = toByteArray(is);
				JooqContractAttach.setContractAttachAttachment(domainName, attachId, fileName, data, mimeType);
		}		
	}
	
	private String getFileType(String extension) {
		//application/pdf, text/plain, text/html, image/jpg
		switch (extension) {
		case "pdf":
			return "application/pdf";
		case "jpg":
			return "image/jpg";
		case "jpeg":
			return "image/jpeg";
		case "png":
			return "image/png";
		default:
			return "application/pdf";
		}
	}
	
	private byte getMimeType(String extension) {
		switch (extension) {
		case "pdf":
			return (byte)22;
		case "jpg":
			return (byte)0;
		case "jpeg":
			return (byte)0;
		case "png":
			return (byte)6;
		default:
			return (byte)22;
		}
	}

	private static byte[] toByteArray(InputStream is) throws IOException {
	    ByteArrayOutputStream os = new ByteArrayOutputStream(); 
	    byte[] buffer = new byte[0x0FFF];
//	    byte[] buffer = new byte[1024];
	    for (int len = is.read(buffer); len != -1; len = is.read(buffer)) { 
	        os.write(buffer, 0, len);
	    }
	    return os.toByteArray();
	}

}
