package com.code.aon.aio.servlet.viewer;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.code.aon.aio.servlet.viewer.html2image.Html2Image;
import com.code.aon.aio.servlet.viewer.html2image.ImageRenderer;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.Utils;
import com.code.aon.ui.google.apis.controller.GoogleDriveController;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;
import com.google.api.services.drive.Drive;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
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
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String jsonData = req.getParameter("details");  
			JSONObject jsonRequest = new JSONObject(jsonData);
			
			Attach attach = new Attach()
					.setMd5(jsonRequest.getString("md5"))
					.setDomain(AON.getDomain(jsonRequest.getString("domainName"),
							jsonRequest.getInt("domainId"),""))
					.setMimeType(MimeType.values()[(byte) jsonRequest.getInt("mimetype")])
					.setId(jsonRequest.getInt("fileId"))
					.setIsDrive(jsonRequest.getBoolean("isDrive"));
			if(!jsonRequest.getString("driveId").equals("null"))
				attach.setDriveId(jsonRequest.getString("driveId"));
			
			PDFFile pdfFile = getPDFFile(attach);
			Integer page = pdfFile.getNumPages();
		
			resp.setContentType("application/json");
			PrintWriter out = resp.getWriter();
			JSONObject json = new JSONObject();
		
			json.put("page", page);
			for(Integer i = 1; i<= page ; i++){
				PDFPage pdfPage = pdfFile.getPage(i);
				json.put("width"+i, pdfPage.getBBox().getWidth());
				json.put("height"+i, pdfPage.getBBox().getHeight());
			}
			out.print(json.toString());
			out.flush();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String requestURI = req.getRequestURI();
		String ext = getExtn(requestURI);
		String rattach = getWithoutExtn(requestURI);
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
		Attach attach = new Attach();
		attach.setId(id);
		attach.setMd5(rattach);
		PDFFile pdfFile;
		try {
			pdfFile = getPDFFile(attach);
			pdf2Image(pdfFile, os, page, format, zoom);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		os.flush();
		
	}


	protected static PDFFile getPDFFile(Attach attach) throws IOException, GeneralSecurityException{
		PDFFile pdfFile = PDFS.get(attach.getMd5());
		if (pdfFile == null) {
			ByteBuffer byteBuffer = getPdfByeBuffer(attach);
			pdfFile = new PDFFile(byteBuffer);
			PDFS.put(attach.getMd5(), pdfFile);
		}
		return pdfFile;
	}

	private static ByteBuffer getPdfByeBuffer(Attach attach) throws IOException, GeneralSecurityException {
		Domain domain = attach.getDomain();
		String login = ""; //AonServletUtils.getLoggedUser();
		User user = new User().setLogin(login);
		
		byte[] b = null;
		MimeType mimetype = attach.getMimeType();
		if(attach.getDriveId()!= null){
			if(attach.getIsDrive()){ // Si entra con una cuenta de Google a la aplicación
        		Drive drive = GoogleDriveController.dconnection;
        		com.google.api.services.drive.model.File file = 
        				DriveUtils.getFile(drive, attach.getDriveId());
        		InputStream in = DriveUtils.downloadFile(drive, file);
    			b = Utils.InputStreamToByte(in);
			}
			else b = DriveUtils.getByteFile(domain, user, attach.getDriveId(), attach.getId());
		}
		else {
			Attach rattach =  AON.getAttach(domain.getName(), domain.getId(), login, 
					filter -> filter.getIdProperty().eq(attach.getId())
					,AttachType.REGISTRY);
			b = rattach.getData();
			if (mimetype ==null) mimetype = rattach.getMimeType();
		}		
		return getPdfByeBuffer(attach.getMd5(), mimetype, b);
	}

	private static ByteBuffer getPdfByeBuffer(String md5, MimeType mimeType,
			byte[] bytes) throws IOException {
		RandomAccessFile randomAccessFile  = null;
		try{
			if (mimeType == MimeType.PDF) {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				PdfReader reader = new PdfReader(bytes);
				PdfStamper stamper;
				try {
					stamper = new PdfStamper(reader, os, '4');
					stamper.close();
				} catch (DocumentException e) {
					e.printStackTrace();
				}
				return ByteBuffer.wrap(os.toByteArray());
			}
			String tmpDir = System.getProperty("java.io.tmpdir");
			File inputFile = new File(tmpDir, md5 + "." + mimeType.getExtension());

			FileOutputStream inputFileOs = new FileOutputStream(inputFile);
			inputFileOs.write(bytes);
			inputFileOs.close();

			File outputFile = new File(tmpDir, md5 + "."
				+ MimeType.PDF.getExtension());

			convert(inputFile, outputFile);

			randomAccessFile = new RandomAccessFile(outputFile,
				"r");

			FileChannel fileChannel = randomAccessFile.getChannel();

			return fileChannel.map(MapMode.READ_ONLY, 0, randomAccessFile.length());
		}finally{
			if(randomAccessFile != null) randomAccessFile.close();
		}
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
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
  		ImageIO.write(bufferedImage, format, baos);
		byte[] data = baos.toByteArray();
		String md5 = AonFileUtils.getMD5Checksum(data);
		System.out.println(md5);
		if(md5.equals("968634550561b68ca4675b1ffe77fd6f")) error2Image(os);
		else ImageIO.write(bufferedImage, format, os);
		
	}
	
	private static void error2Image(OutputStream os) throws IOException {

	    String html = "<html>" +
	            "<h1> ERROR DE VISUALIZACIÓN</h1>" +
	    		"<span> El Documento no se puede visualizar, pulse en Descargar.</span>" +
	            "</html>";
	    ImageRenderer imageRenderer = Html2Image.fromHtml(html).getImageRenderer();
	    BufferedImage bufferedImage = imageRenderer.getBufferedImage();
	     ImageIO.write(bufferedImage, "png", os);
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
}
