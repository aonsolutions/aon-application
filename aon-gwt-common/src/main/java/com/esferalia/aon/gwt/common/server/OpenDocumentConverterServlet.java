package com.esferalia.aon.gwt.common.server;

import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;

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
import org.jooq.Condition;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.type.MimeType;


public class OpenDocumentConverterServlet extends HttpServlet {


	
	protected  static class NoSuchDocumentException extends IOException {
		
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
			String extension = AonServletUtils.getExtn(requestURI);
			String md5 = AonServletUtils.getFileName(requestURI);
			//int rattachId = Integer.parseInt(rattachIdStr);
			Map<String, String[]> params = req.getParameterMap();
			Integer id = null;
			String domainName = "";
			Integer domainId = null;
			if(params.containsKey("id")) 
				id = Integer.parseInt(req.getParameter("id"));

			if(params.containsKey("domainName")) 
				domainName = req.getParameter("domainName");

			if(params.containsKey("id")) 
				domainId = Integer.parseInt(req.getParameter("id"));

			MimeType mimeType = MimeType.getByExtension(extension);
						
			String login = AonServletUtils.getLoggedUser();
			Condition condition = RATTACH.ID.eq(id);
			Attach rattach = AON.getAttach(domainName, domainId, login, condition, AttachType.REGISTRY);
			
			resp.setContentType(mimeType.getName());
			OutputStream os = resp.getOutputStream();

			if ( rattach.getMimeType() == mimeType ) {
				os.write(rattach.getData());
			} else { 
				String tmpDir = System.getProperty("java.io.tmpdir");
				File inputFile = 
						new File(tmpDir, md5 + "." + rattach.getMimeType().getExtension() ); 
				FileOutputStream inputFileOs = 
						new FileOutputStream(inputFile);
				inputFileOs.write(rattach.getData());
				inputFileOs.close();
				
				File outputFile = 
						new File(tmpDir, md5 + "." + mimeType.getExtension() ); 
				convert(inputFile, outputFile);
				InputStream outputFileIs = 
						new FileInputStream(outputFile);
				byte buff [] = new byte [1024] ;
				int read = -1;
				while ( ( read = outputFileIs.read(buff, 0 , buff.length) ) != -1 ) {
					os.write(buff, 0, read );
				}
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
		// TODO Servlet params ???
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
	


}
