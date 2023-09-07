package com.esferalia.aon.gwt.payroll.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DefaultDocumentFormatRegistry;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.gwt.common.server.AonServletUtils;

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
			String rattachIdStr = AonServletUtils.getFileName(requestURI);
			int rattachId = Integer.parseInt(rattachIdStr);

			MimeType mimeType = MimeType.getByExtension(extension);
			
			PayrollServletUtils.RAttach rattach = PayrollServletUtils.getRAttach(rattachId);
			
			resp.setContentType(mimeType.getName());
			OutputStream os = resp.getOutputStream();

			if ( rattach.mimeType == mimeType ) {
				os.write(rattach.bytes);
			} else { 
				String tmpDir = System.getProperty("java.io.tmpdir");
				File inputFile = 
						new File(tmpDir, rattachId + "." + rattach.mimeType.getExtension() ); 
				FileOutputStream inputFileOs = 
						new FileOutputStream(inputFile);
				inputFileOs.write(rattach.bytes);
				inputFileOs.close();
				
				File outputFile = 
						new File(tmpDir, rattachId + "." + mimeType.getExtension() ); 
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
