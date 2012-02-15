package com.esferalia.aon.gwt.employee.server;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
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
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DefaultDocumentFormatRegistry;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.gwt.employee.shared.Salary;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.RattachColumns;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class OpenDocument2ImageServlet extends HttpServlet {

	private static final float DEFAULT_ZOOM = 1.3f;
	private static final String DEFAULT_FORMAT = "png";
	private static final int DEFAULT_OFFICE_PORT = 2002;

	public static final String ZOOM_PARAM = "zoom";
	public static final String PAGE_PARAM = "page";
	public static final String FORMAT_PARAM = "format";


	protected static Connection getConnection() {
		String sessionFactory = HibernateUtil
				.getSessionFactoryName(Salary.class.getName());
		return HibernateUtil.getSQLConnection(sessionFactory);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Connection connection = getConnection();

		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

			stmt = connection.prepareStatement("SELECT *" 
					+ " FROM " + SQLConstants.RATTACH + " WHERE "
					+ RattachColumns.ID + "= ? ");

			String requestURI = req.getRequestURI();
			String ext = OpenDocument2ImageServlet.getExtn(requestURI);
			String rattach = OpenDocument2ImageServlet.getWithoutExtn(requestURI);

			int rattachId = Integer.parseInt(rattach);
			String format = ext != null ? ext : DEFAULT_FORMAT;

			stmt.setInt(1, rattachId);
			
			rs = stmt.executeQuery();
			
			if (!rs.next()) {
				return;
			}
			
			MimeType mimeType = 
					mimeTypeOf(rs.getInt(RattachColumns.MIMETYPE));
			Blob blob = rs.getBlob(RattachColumns.DATA);
			byte bytes[] = blob.getBytes(1, (int) blob.length());
			
			ByteBuffer buf = null;
			if ( mimeType == MimeType.MIME_PDF) {
				buf = ByteBuffer.wrap(bytes);
			}else {
				InputStream is = rs.getBinaryStream(RattachColumns.DATA);
				buf = getPdfByeBuffer(rattachId, mimeType, bytes);
			}
			
			OutputStream os = resp.getOutputStream();
			Map<String, String> params = req.getParameterMap();
			int page = params.containsKey(PAGE_PARAM) ? Integer.parseInt(params
					.get(PAGE_PARAM)) : 0;
			float zoom = params.containsKey(ZOOM_PARAM) ? Integer
					.parseInt(params.get(ZOOM_PARAM)) : DEFAULT_ZOOM;

			resp.setContentType(String.format("image/%s", format));
			pdf2Image(buf, os, page, format, zoom);
			os.flush();

		} catch (SQLException e) {
			throw new ServletException(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					throw new ServletException(e);
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					throw new ServletException(e);
				}
			}
		}
	}

	private static void pdf2Image(ByteBuffer buf, OutputStream os, int page,
			String format, float zoom) throws IOException {

		PDFFile pdffile = new PDFFile(buf);

		PDFPage pdfPage = pdffile.getPage(page);

		// get the width and height for the doc at the default zoom
		int width = (int) pdfPage.getBBox().getWidth();
		int height = (int) pdfPage.getBBox().getHeight();

		Rectangle rect = new Rectangle(0, 0, width, height);
		int rotation = pdfPage.getRotation();
		Rectangle rect1 = rect;
		if (rotation == 90 || rotation == 270) {
			rect1 = new Rectangle(0, 0, rect.height, rect.width);
		}

		// generate the image
		BufferedImage img = (BufferedImage) pdfPage.getImage(
				(int) (rect.width * zoom), (int) (rect.height * zoom), // width
																		// &
																		// height
				rect1, // clip rect
				null, // null for the ImageObserver
				true, // fill background with white
				true // block until drawing is done
				);

		ImageIO.write(img, format, os);

	}
	
	private MimeType mimeTypeOf(int value) {
		if ( value < 0 ) { 
			return null;
		}
		MimeType values [] = MimeType.values();
		if ( value >= values.length ) { 
			return null;
		}
		return values [value];
	}
	
	private static String getExtn(String path) {
		return path.substring(path.lastIndexOf('.') + 1);
	}

	private static String getWithoutExtn(String path) {
		String fileName = path.substring(path.lastIndexOf('/') + 1);
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}
	
	private static ByteBuffer getPdfByeBuffer ( Integer id, MimeType mimeType, byte [] bytes ) 
			throws IOException {
		String tmpDir = System.getProperty("java.io.tmpdir");

		File inputFile = 
				new File(tmpDir, id + "." + mimeType.getExtension() ); 
		
		FileOutputStream inputFileOs = 
				new FileOutputStream(inputFile);
		inputFileOs.write(bytes);
		inputFileOs.close();
		
		File outputFile = 
				new File(tmpDir, id + "." + MimeType.MIME_PDF.getExtension() ); 
		
		convert( inputFile , outputFile );
		
		RandomAccessFile randomAccessFile = 
				new RandomAccessFile(outputFile, "r");
		
		FileChannel fileChannel = 
				randomAccessFile.getChannel();
		
		return fileChannel.map(MapMode.READ_ONLY, 0, randomAccessFile.length());
	}

	private static void convert(File inputFile, File outputFile) 
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
