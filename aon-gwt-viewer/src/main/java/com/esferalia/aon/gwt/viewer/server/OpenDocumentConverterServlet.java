package com.esferalia.aon.gwt.viewer.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DefaultDocumentFormatRegistry;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;
import com.esferalia.aon.occam.api.model.type.MimeType;

@WebServlet(name = "openDocumentConverter", urlPatterns = {"/openDocumentConverter/*"})
public class OpenDocumentConverterServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected  static class NoSuchDocumentException extends IOException {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private int id;
		
		public NoSuchDocumentException(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
	}

	private static final int DEFAULT_OFFICE_PORT = 2002;
	public static HashMap<String, byte[]> IMGS = new HashMap<String, byte[]>();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String requestURI = req.getRequestURI();
		String extension = getExtn(requestURI);
		String md5 = getWithoutExtn(requestURI);
		MimeType mimeType = MimeType.getByExtension(extension);
		resp.setContentType(mimeType.getName());
		OutputStream os = resp.getOutputStream();
		
		if(IMGS.containsKey(md5)){
			os.write(IMGS.get(md5));
			IMGS.remove(md5);
		}
		os.flush();
	}
	

	
	protected static MimeType mimeTypeOf(int value) {
		if ( value < 0 ) { 
			return null;
		}
		MimeType values [] = MimeType.values();
		if ( value >= values.length ) { 
			return null;
		}
		return values [value];
	}
	
	protected static void convert(File inputFile, File outputFile) 
			throws IOException {
		
		DocumentFormatRegistry formatRegistry = 
				new DefaultDocumentFormatRegistry();
		
		DefaultOfficeManagerConfiguration configuration = 
				new DefaultOfficeManagerConfiguration();

		configuration.setPortNumber(DEFAULT_OFFICE_PORT);
		
		OfficeManager officeManager = configuration.buildOfficeManager();
		officeManager.start();
		OfficeDocumentConverter converter = 
				new OfficeDocumentConverter(officeManager, formatRegistry);
		try {
			 converter.convert(inputFile, outputFile);
		}finally {
			officeManager.stop();
		}
	}
	
	public static AuthPrincipal getRequestPrincipal(HttpServletRequest request) {
		return (AuthPrincipal) request.getUserPrincipal();
	}
	
	public static String getRequestUser(HttpServletRequest request) {		
		return getRequestPrincipal(request).getShortName();
	}
	
	public static String getLoggedUser() {
		HttpServletRequest request = HttpServletRequestValve.getHttpServletRequest();
		return getRequestUser(request);
	}
	
	public static String getFileName(String path) {
		String fileName = path.substring(path.lastIndexOf('/') + 1);
		int startExt = fileName.lastIndexOf('.');
		if ( startExt == -1 )
			return fileName;
		return fileName.substring(0, startExt);
	}
	
	public static String getExtn(String path) {
		return path.substring(path.lastIndexOf('.') + 1);
	}
	
	public static String getWithoutExtn(String path) {
		return getFileName(path);
	}
}
