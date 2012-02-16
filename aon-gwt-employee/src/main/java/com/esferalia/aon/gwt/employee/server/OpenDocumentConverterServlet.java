package com.esferalia.aon.gwt.employee.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

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

public class OpenDocumentConverterServlet extends HttpServlet {

	private static final int DEFAULT_OFFICE_PORT = 2002;

	
	
	
	/*
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {

			String requestURI = req.getRequestURI();
			String extension = OpenDocumentConverterServlet.getExtn(requestURI);
			String rattach = OpenDocumentConverterServlet.getWithoutExtn(requestURI);

			int rattachId = Integer.parseInt(rattach);

			OutputStream os = resp.getOutputStream();
			MimeType mimeType = MimeType.getByExtension(extension);
			
			//resp.setContentType(String.format("image/%s", format));
			
		} catch (SQLException e) {
			throw new ServletException(e);
		} 
	}*/
	

	
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
