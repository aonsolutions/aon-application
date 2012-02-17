package com.esferalia.aon.gwt.employee.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.esferalia.aon.gwt.employee.server.OpenDocumentConverterServlet.NoSuchDocumentException;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.RattachColumns;

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
			String rattachIdStr = AonServletUtils.getWithoutExtn(requestURI);
			int rattachId = Integer.parseInt(rattachIdStr);

			MimeType mimeType = MimeType.getByExtension(extension);
			
			RAttach rattach = getRAttach(rattachId);
			
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

	protected static RAttach getRAttach ( Integer id ) 
			throws SQLException, IOException  {
		Connection connection = AonServletUtils.getConnection();

		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

			stmt = connection.prepareStatement("SELECT *" 
					+ " FROM " + SQLConstants.RATTACH + " WHERE "
					+ RattachColumns.ID + "= ? ");


			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if (!rs.next()) {
				throw new OpenDocumentConverterServlet.NoSuchDocumentException(id);
			}

			RAttach rattach  = new RAttach();
			Blob blob = rs.getBlob(RattachColumns.DATA);
			rattach.bytes = blob.getBytes(1, (int) blob.length());
			rattach.mimeType = mimeTypeOf(rs.getInt(RattachColumns.MIMETYPE));
			
			return rattach;
			
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}
	
	protected static class RAttach {
		
		byte [] bytes;
		MimeType mimeType;
	
	}
	
}
