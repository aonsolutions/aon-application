package com.esferalia.aon.gwt.employee.server;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.naming.spi.DirStateFactory.Result;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.esferalia.aon.gwt.employee.shared.Salary;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.RattachColumns;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class PDF2ImageServlet extends HttpServlet {

	private static float DEFAULT_ZOOM = 1.3f;
	private static String DEFAULT_FORMAT = "png";

	public static String ZOOM_PARAM = "zoom";
	public static String PAGE_PARAM = "page";
	public static String FORMAT_PARAM = "format";

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

			stmt = connection.prepareStatement("SELECT " + RattachColumns.DATA
					+ " FROM " + SQLConstants.RATTACH + " WHERE "
					+ RattachColumns.ID + "= ? ");

			String requestURI = req.getRequestURI();
			String ext = PDF2ImageServlet.getExtn(requestURI);
			String rattach = PDF2ImageServlet.getWithoutExtn(requestURI);

			int rattachId = Integer.parseInt(rattach);
			String format = ext != null ? ext : DEFAULT_FORMAT;

			stmt.setInt(1, rattachId);
			
			rs = stmt.executeQuery();
			
			if (!rs.next()) {
				return;
			}

			Blob blob = rs.getBlob(RattachColumns.DATA);

			int lenght = (int) blob.length();
			//resp.setContentLength(lenght);

			byte bytes[] = blob.getBytes(1, (int) blob.length());

			ByteBuffer buf = ByteBuffer.wrap(bytes);

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

	private void pdf2Image(ByteBuffer buf, OutputStream os, int page,
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

	private static String getExtn(String path) {
		return path.substring(path.lastIndexOf('.') + 1);
	}

	private static String getWithoutExtn(String path) {
		String fileName = path.substring(path.lastIndexOf('/') + 1);
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}

}
