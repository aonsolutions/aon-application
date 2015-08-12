package com.esferalia.aon.gwt.common.server;


import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;

import com.esferalia.aon.gwt.common.shared.FileInfo;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.attachment.Rattach;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.codec.AonDigestUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class OpenDocument2ImageServlet extends OpenDocumentConverterServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final int DEFAULT_ZOOM = 130;
	static final String DEFAULT_FORMAT = "png";

	public static final String ZOOM_PARAM = "zoom";
	public static final String PAGE_PARAM = "page";
	public static final String FORMAT_PARAM = "format";

	private static Map<String, PDFFile> PDFS = new HashMap<String, PDFFile>();
	
	public static class CheckSum {
		/***
		 * Convierte un arreglo de bytes a String usando valores hexadecimales
		 * 
		 * @param digest
		 *            arreglo de bytes a convertir
		 * @return String creado a partir de <code>digest</code>
		 */
		private static String toHexadecimal(byte[] digest) {
			String hash = "";
			for (byte aux : digest) {
				int b = aux & 0xff;
				if (Integer.toHexString(b).length() == 1)
					hash += "0";
				hash += Integer.toHexString(b);
			}
			return hash;
		}

		/***
		 * Realiza la suma de verificación de un archivo mediante MD5
		 * 
		 * @param archivo
		 *            archivo a que se le aplicara la suma de verificación
		 * @return valor de la suma de verificación.
		 */
		public static String getMD5Checksum(InputStream is) {
			String md5 = null;
			try {
				byte[] data = AonIOUtils.toByteArray(is);
				md5 = AonDigestUtils.md5Hex(ArrayUtils.nullToEmpty(data));
			} catch (IOException e) {
			}
			
			return md5;
		}
		public static String getMD5Checksum(byte[] data) {
			String md5 = null;
			md5 = AonDigestUtils.md5Hex(ArrayUtils.nullToEmpty(data));
			return md5;
		}

	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			
			/*ClassLoader loader = _getResourceLoader(req);
			String resourcePath = getResourcePath(req);

			URL url = loader.getResource(resourcePath);
			
			// Make sure the resource is available
			if (url == null) {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			// Stream the resource contents to the servlet response
			URLConnection connection = url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(false);

			_setHeaders(connection, resp);
			*/
			
			String requestURI = req.getRequestURI();
			String ext = AonServletUtils.getExtn(requestURI);
			String rattach = AonServletUtils.getWithoutExtn(requestURI);

			//int rattachId = Integer.parseInt(rattach);
			String format = ext != null ? ext : DEFAULT_FORMAT;

			OutputStream os = resp.getOutputStream();

			Map<String, String[]> params = req.getParameterMap();
			int page = params.containsKey(PAGE_PARAM) ? Integer.parseInt(params
					.get(PAGE_PARAM)[0]) : 1;
			int zoom = params.containsKey(ZOOM_PARAM) ? Integer.parseInt(params
					.get(ZOOM_PARAM)[0]) : DEFAULT_ZOOM;
			Integer id = null;
			if(params.containsKey("id"))
				 id = Integer.parseInt(req.getParameter("id"));
			
			resp.setContentType(String.format("image/%s", format));
			FileInfo fi = new FileInfo();
			fi.setFileId(id);
			fi.setMd5(rattach);
			PDFFile pdfFile = getPDFFile(fi);
			
			pdf2Image(pdfFile, os, page, format, zoom);
			os.flush();

		} catch (SQLException e) {
			throw new ServletException(e);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
	}

	public static PDFFile getPDFFile(FileInfo doc) throws SQLException,
			IOException, KeyStoreException, GeneralSecurityException {

		PDFFile pdfFile = PDFS.get(doc.getMd5());
		if (pdfFile == null) {
			ByteBuffer byteBuffer = getPdfByeBuffer(doc);
			pdfFile = new PDFFile(byteBuffer);
			PDFS.put(doc.getMd5(), pdfFile);
		}
		return pdfFile;
	}

	private static ByteBuffer getPdfByeBuffer(FileInfo doc) throws SQLException,
			IOException, KeyStoreException, GeneralSecurityException {
		byte[] b = null;
		MimeType mimetype = MimeType.values()[doc.getMimetype()];
		
		//ViewerUtils.RAttach rattach1 = ViewerUtils.getRAttach(doc.getFileId());
		
		Rattach rattach = AON.getRattach(doc.getDomain(), doc.getDomainId(), doc.getFileId());
		
		b = rattach.getData();
		if (mimetype ==null) mimetype = rattach.getMimeType();
	

		return getPdfByeBuffer(doc.getMd5(), mimetype, b);
	}

	private static ByteBuffer getPdfByeBuffer(String md5, MimeType mimeType,
			byte[] bytes) throws IOException {

		if (mimeType == MimeType.PDF) {
			return ByteBuffer.wrap(bytes);
		}

		String tmpDir = System.getProperty("java.io.tmpdir");

		File inputFile = new File(tmpDir, md5 + "." + mimeType.getExtension());

		FileOutputStream inputFileOs = new FileOutputStream(inputFile);
		inputFileOs.write(bytes);
		inputFileOs.close();

		File outputFile = new File(tmpDir, md5 + "."
				+ MimeType.PDF.getExtension());

		convert(inputFile, outputFile);

		RandomAccessFile randomAccessFile = new RandomAccessFile(outputFile,
				"r");

		FileChannel fileChannel = randomAccessFile.getChannel();

		return fileChannel.map(MapMode.READ_ONLY, 0, randomAccessFile.length());
	}

	private static void pdf2Image(PDFFile pdffile, OutputStream os, int page,
			String format, int zoom) throws IOException {

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

		int zoomWidth = rect.width * zoom / 100;
		int zoomHeight = rect.height * zoom / 100;

		BufferedImage bufferedImage = new BufferedImage(
				zoomWidth,
				zoomHeight, 
				BufferedImage.TYPE_INT_RGB);

		// generate the image
		Image image = pdfPage.getImage(
				zoomWidth,  // width
				zoomHeight, // height
				rect1, 		// clip rect
				null, 		// null for the ImageObserver
				true, 		// fill background with white
				true 		// block until drawing is done
				);
		
		Graphics2D bufImageGraphics = 
				bufferedImage.createGraphics();
		bufImageGraphics.drawImage(image, 0, 0, null);
		
		ImageIO.write(bufferedImage, format, os);

	}
	
	

	/**
	 * Sets HTTP headers on the response which tell the browser to cache the
	 * resource indefinitely.
	 */
	private void _setHeaders(URLConnection connection, HttpServletResponse resp) {
		int contentLength = connection.getContentLength();
		if (contentLength >= 0)
			resp.setContentLength(contentLength);

		long lastModified = connection.getLastModified();
		if (lastModified > 0)
			resp.setDateHeader("Last-Modified", lastModified);

		resp.setHeader("Cache-Control", "Public");
		
		long currentTime = System.currentTimeMillis();

		resp.setDateHeader("Expires", currentTime + ONE_YEAR_MILLIS);
	}

	
	public static final long ONE_YEAR_MILLIS = 31363200000L;
	

	protected String getResourcePath(HttpServletRequest req) {
		String path = req.getServletPath();
		String info = req.getPathInfo();
		if ( path.startsWith("/")) {
			path = path.substring(1);
		}
		return path + info;
	}

	private ClassLoader _getResourceLoader(HttpServletRequest req) {
		return Thread.currentThread().getContextClassLoader();
	}
}
