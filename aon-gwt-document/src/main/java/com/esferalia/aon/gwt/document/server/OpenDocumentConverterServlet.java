package com.esferalia.aon.gwt.document.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.Utils;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.google.apis.jooq.DomainGserviceaccount;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.RattachColumns;
import com.google.api.services.drive.Drive;

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

		try {

			String requestURI = req.getRequestURI();
			String extension = AonServletUtils.getExtn(requestURI);
			String md5 = AonServletUtils.getWithoutExtn(requestURI);
			//int rattachId = Integer.parseInt(rattachIdStr);
			Map<String, String[]> params = req.getParameterMap();
			Integer id = null;
			if(params.containsKey("id"))
				 id = Integer.parseInt(req.getParameter("id"));

			MimeType mimeType = MimeType.getByExtension(extension);
			
			ViewerUtils.RAttach rattach = ViewerUtils.getRAttach(id);
			
			resp.setContentType(mimeType.getName());
			OutputStream os = resp.getOutputStream();

			if ( rattach.mimeType == mimeType ) {
				byte[] b = null;
				if(rattach.driveId != null){
					String domain = AonUtil.getDomainName();
					DomainGserviceaccount g = DBConsults.getServiceAccount(domain, rattach.domainId);
					Drive d;
					try {
						d = DriveUtils.serviceInitialize(g);
						com.google.api.services.drive.model.File f = d.files().get(rattach.driveId).execute();
						InputStream in = DriveUtils.downloadFile(d, f);
						b = Utils.InputStreamToByte(in);
					} catch (KeyStoreException e) {
						e.printStackTrace();
					} catch (GeneralSecurityException e) {
						e.printStackTrace();
					}	
				}
				else b = rattach.bytes;
				os.write(b);
			} else { 
				String tmpDir = System.getProperty("java.io.tmpdir");
				File inputFile = 
						new File(tmpDir, md5 + "." + rattach.mimeType.getExtension() ); 
				FileOutputStream inputFileOs = 
						new FileOutputStream(inputFile);
				inputFileOs.write(rattach.bytes);
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
			
		} catch (SQLException e) {
			throw new ServletException(e);
		} 
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
