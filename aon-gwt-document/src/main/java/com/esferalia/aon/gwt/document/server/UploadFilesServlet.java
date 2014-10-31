package com.esferalia.aon.gwt.document.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


public class UploadFilesServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7293524876320817731L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		System.out.println("SERVLET SUBIDA");
		ServletFileUpload upload = new ServletFileUpload();
		
		 try {
			FileItemIterator iter = upload.getItemIterator(req);
			
			
			while(iter.hasNext()){
				 FileItemStream item = iter.next();
				 String name = item.getFieldName();
				 InputStream stream = item.openStream();
				 String mimetype = item.getContentType();
				 if(stream!=null){
					 DocumentsServlet.setFile(stream);
					 DocumentsServlet.setMimetype(mimetype);
				 }
				
				 
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                int len;
	                byte[] buffer = new byte[8192];
	                while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
	                    out.write(buffer, 0, len);
	                }

	                int maxFileSize = 10*(1024*1024); //10 megs max 
	                if (out.size() > maxFileSize) { 
	                    throw new RuntimeException("File is > than " + maxFileSize);
	                }
	                
					DocumentsServlet.setOut(out);
					
				 // TODO Subir archivo a Drive
			}
		} catch (FileUploadException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}
		//super.doPost(req, resp);
	}
	
	
}
