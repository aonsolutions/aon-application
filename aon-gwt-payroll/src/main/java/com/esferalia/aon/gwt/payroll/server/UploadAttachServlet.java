package com.esferalia.aon.gwt.payroll.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Date;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.JSONObject;

import com.esferalia.aon.gwt.payroll.jooq.JooqCRA;
import com.esferalia.aon.gwt.payroll.jooq.JooqContractAttach;
import com.esferalia.aon.gwt.payroll.jooq.JooqSaltra;
import com.esferalia.aon.gwt.payroll.shared.SaltraService.Parameter;

import solutions.aon.saltra.api.Saltra; 

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "ATTACH", urlPatterns = { "/aon_gwt_payroll/attach/*" })
public class UploadAttachServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		//Get Request Parametrers
		String attachIdStr = req.getParameter("attachId");
		Integer attachId = Integer.parseInt(attachIdStr);
		
		//Get domain Name
		String domainName = req.getServerName();
		
		String extension = req.getParameter("extension");
		 
		String fileName = "";
         String fileType = getFileType(extension);
         // Find this file id in database to get file name, and file type

         // You must tell the browser the file type you are going to send
         // for example application/pdf, text/plain, text/html, image/jpg
         res.setContentType(fileType);

         // Make sure to show the download dialog
         res.setHeader("Content-disposition","attachment; filename=PRUEBA."+extension);
		
		try {
			ServletOutputStream output = res.getOutputStream();
			
			byte[] data = JooqContractAttach.getContractAttachAttachment(domainName, attachId);
			
			output.write(data);
			
			res.flushBuffer();
			
		} catch (Exception e) {
			// TODO: handle exception
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

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		//Get Request Parametrers
		String attachIdStr = req.getParameter("attachId");
		Integer attachId = Integer.parseInt(attachIdStr);
		
		//Get domain Name
		String domainName = req.getServerName();
		
		String extension = req.getParameter("extension");
		byte mimeType = getMimeType(extension);
		
		Part filePart = req.getPart("uploader");
		
		try ( InputStream is = filePart.getInputStream() ){
				byte data [] = toByteArray(is);
				JooqContractAttach.setContractAttachAttachment(domainName, attachId, data, mimeType);
				System.out.println(data);
		}
		
//		byte data [] = toByteArray(req.getInputStream());
//		
//		JooqContractAttach.setContractAttachAttachment(domainName, attachId, data);
//		
//		System.out.println(data);
		
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
//	    byte[] buffer = new byte[0x0FFF];
	    byte[] buffer = new byte[1024];
	    for (int len = is.read(buffer); len != -1; len = is.read(buffer)) { 
	        os.write(buffer, 0, len);
	    }
	    return os.toByteArray();
	}

}
