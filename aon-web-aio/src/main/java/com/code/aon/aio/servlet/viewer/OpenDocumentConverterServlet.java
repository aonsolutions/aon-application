package com.code.aon.aio.servlet.viewer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DefaultDocumentFormatRegistry;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

import com.code.aon.google.apis.DriveUtils;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;

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

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String requestURI = req.getRequestURI();
		String extension = getExtn(requestURI);
		String md5 = getWithoutExtn(requestURI);
		Map<String, String[]> params = req.getParameterMap();
		Integer id = null;
		String domainName = "";
		Integer domainId = null;
		if(params.containsKey("id")) 
			id = Integer.parseInt(req.getParameter("id"));
		if(params.containsKey("domainName")) 
			domainName = req.getParameter("domainName");
		if(params.containsKey("domainId")) 
			domainId = Integer.parseInt(req.getParameter("domainId"));
		MimeType mimeType = MimeType.getByExtension(extension);
		Domain domain = new Domain().setName(domainName).setId(domainId);
		User user = new User().setLogin(getLoggedUser());
		final Integer attachId = id;
		Attach rattach = AON.getAttach(domainName, domainId, user.getLogin(), 
				filter -> filter.getIdProperty().eq(attachId)
				, AttachType.REGISTRY);
			
		resp.setContentType(mimeType.getName());
		OutputStream os = resp.getOutputStream();

		if (rattach.getMimeType() == mimeType ) {
			byte[] b = null;
			if(rattach.getDriveId() != null){
				b = DriveUtils.getByteFile(domain, user, rattach.getDriveId(), rattach.getId());
			}
			else b = rattach.getData();
			os.write(b);
		} else { 
			String tmpDir = System.getProperty("java.io.tmpdir");
			File inputFile = new File(tmpDir, md5 + "." + rattach.getMimeType().getExtension() ); 
			FileOutputStream inputFileOs = new FileOutputStream(inputFile);
			inputFileOs.write(rattach.getData());
			inputFileOs.close();
				
			File outputFile = new File(tmpDir, md5 + "." + mimeType.getExtension() ); 
			convert(inputFile, outputFile);
			InputStream outputFileIs = new FileInputStream(outputFile);
			byte buff [] = new byte [1024] ;
			int read = -1;
			while ( ( read = outputFileIs.read(buff, 0 , buff.length) ) != -1 ) {
				os.write(buff, 0, read );
			}
			outputFileIs.close();
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
