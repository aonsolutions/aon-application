package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.gwt.payroll.server.PayrollServletUtils.getRAttach;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.server.PayrollServletUtils.RAttach;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class OpenDocument2ImageServlet extends OpenDocumentConverterServlet {

	static final int DEFAULT_ZOOM = 130;
	static final String DEFAULT_FORMAT = "png";

	public static final String ZOOM_PARAM = "zoom";
	public static final String PAGE_PARAM = "page";
	public static final String FORMAT_PARAM = "format";

	private static Map<Integer, PDFFile> PDFS = new HashMap<Integer, PDFFile>();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {

			String requestURI = req.getRequestURI();
			String ext = AonServletUtils.getExtn(requestURI);
			String rattach = AonServletUtils.getFileName(requestURI);

			int rattachId = Integer.parseInt(rattach);
			String format = ext != null ? ext : DEFAULT_FORMAT;

			OutputStream os = resp.getOutputStream();

			Map<String, String[]> params = req.getParameterMap();
			int page = params.containsKey(PAGE_PARAM) ? Integer.parseInt(params
					.get(PAGE_PARAM)[0]) : 1;
			int zoom = params.containsKey(ZOOM_PARAM) ? Integer.parseInt(params
					.get(ZOOM_PARAM)[0]) : DEFAULT_ZOOM;

			resp.setContentType(String.format("image/%s", format));

			PDFFile pdfFile = getPDFFile(rattachId);
			pdf2Image(pdfFile, os, page, format, zoom);
			os.flush();

		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}

	protected static PDFFile getPDFFile(int id) throws SQLException,
			IOException {
		PDFFile pdfFile = PDFS.get(id);
		if (pdfFile == null) {
			ByteBuffer byteBuffer = getPdfByeBuffer(id);
			pdfFile = new PDFFile(byteBuffer);
			PDFS.put(id, pdfFile);
		}
		return pdfFile;
	}

	private static ByteBuffer getPdfByeBuffer(Integer id) throws SQLException,
			IOException {

		RAttach rattach = getRAttach(id);

		return getPdfByeBuffer(id, rattach.mimeType, rattach.bytes);
	}

	private static ByteBuffer getPdfByeBuffer(Integer id, MimeType mimeType,
			byte[] bytes) throws IOException {

		if (mimeType == MimeType.MIME_PDF) {
			return ByteBuffer.wrap(bytes);
		}

		String tmpDir = System.getProperty("java.io.tmpdir");

		File inputFile = new File(tmpDir, id + "." + mimeType.getExtension());

		FileOutputStream inputFileOs = new FileOutputStream(inputFile);
		inputFileOs.write(bytes);
		inputFileOs.close();

		File outputFile = new File(tmpDir, id + "."
				+ MimeType.MIME_PDF.getExtension());

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
}
