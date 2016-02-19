package com.esferalia.aon.gwt.viewer.server;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.GeneralSecurityException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.ImageIOUtil;

import com.esferalia.aon.gwt.viewer.pdfbox.AonPDPage;
import com.esferalia.aon.gwt.viewer.server.html2Image.Html2Image;
import com.esferalia.aon.gwt.viewer.server.html2Image.ImageRenderer;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

@WebServlet(name = "openDocument2Image", urlPatterns = {"/openDocument2Image/*"})
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

	private static ViewerCache<String, PDFFile> PDFS = new ViewerCache<String, PDFFile>(30);
	private static ViewerCache<String, byte[]> datas = new ViewerCache<String, byte[]>(30);
	
	private static ViewerCache<String, Boolean> library = new ViewerCache<>(10);
	
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
		
		Boolean ok = false;
		Boolean pdfBox = true;
		if(library.containsKey(attach.getMd5())){
			pdfBox = library.get(attach.getMd5());
		}
		
		if( pdfBox && datas.containsKey(attach.getMd5())){
			ok = pdf2ImagePDFBox(datas.get(attach.getMd5()), os, page, format, zoom);
			library.put(attach.getMd5(), ok);
		}
		if(!ok){
			PDFFile pdfFile;
			try {
				pdfFile = getPDFFile(attach);
				ok = pdf2Image(pdfFile, os, page, format, zoom);
				if(!ok) {
					ok = pdf2ImagePDFBox(datas.get(attach.getMd5()), os, page, format, zoom);
					library.put(attach.getMd5(), ok);
				}
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			}
		}
		os.flush();
		os.close();
	}
	
	protected static Integer getPDFFile2(Attach attach) throws IOException, GeneralSecurityException{
		if(!PDFS.containsKey(attach.getMd5())){
			ByteBuffer byteBuffer = getPdfByeBuffer(attach);
			PDFS.put(attach.getMd5(), new PDFFile(byteBuffer));
			if(attach.getMimeType().equals(MimeType.PDF))
				datas.put(attach.getMd5(), byteBuffer.array());
			byteBuffer.clear();
		}
		return PDFS.get(attach.getMd5()).getNumPages();
	}
	
	
	protected static PDFFile getPDFFile(Attach attach) throws IOException, GeneralSecurityException{
		if(!PDFS.containsKey(attach.getMd5())){
			ByteBuffer byteBuffer = getPdfByeBuffer(attach);
			PDFS.put(attach.getMd5(), new PDFFile(byteBuffer));
			if(attach.getMimeType().equals(MimeType.PDF))
				datas.put(attach.getMd5(), byteBuffer.array());
			byteBuffer.clear();
		}
		return PDFS.get(attach.getMd5());
	}
	
	
	protected static PDDocument getPDFFile3(Attach attach) throws IOException, GeneralSecurityException{
		if(!datas.containsKey(attach.getMd5())){
			ByteBuffer byteBuffer = getPdfByeBuffer(attach);
			datas.put(attach.getMd5(), byteBuffer.array());
			byteBuffer.clear();
		}
		return PDDocument.load(new ByteArrayInputStream(datas.get(attach.getMd5())));
	}

	private static ByteBuffer getPdfByeBuffer(Attach attach) throws IOException, GeneralSecurityException {
		Domain domain = AON.getDomain(attach.getDomain().getName(), attach.getDomain().getId(), "");
		String login = ""; //AonServletUtils.getLoggedUser();
		User user = new User().setLogin(login);
		
		byte[] b = null;
		MimeType mimetype = attach.getMimeType();
		if(attach.getData() != null) 
			b = attach.getData();
		else if(attach.getDriveId()!= null)
			b = DriveUtils.getByteFile(domain, user, attach.getDriveId(), attach.getId(), attach.getAttachType());
		
		return getPdfByeBuffer(attach.getMd5(), mimetype, b);
	}

	private static ByteBuffer getPdfByeBuffer(String md5, MimeType mimeType,
			byte[] bytes) throws IOException {
		RandomAccessFile randomAccessFile  = null;
		try{
			if (mimeType == MimeType.PDF) {
				return ByteBuffer.wrap(bytes);
				/*
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				PdfReader reader = new PdfReader(bytes);
				PdfStamper stamper;
				try {
					stamper = new PdfStamper(reader, os, '4');
					stamper.close();
				} catch (DocumentException e) {
					e.printStackTrace();
				}
				ByteBuffer bb = ByteBuffer.wrap(os.toByteArray());
				os.flush();
				os.close();
				reader.close();
				return bb;*/
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

	private static Boolean pdf2Image(PDFFile pdffile, OutputStream os, int page,
			String format, int zoom) throws IOException {
		try{
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
		if(md5.equals("968634550561b68ca4675b1ffe77fd6f"))
			//error2Image(os);
			return false;
		else ImageIO.write(bufferedImage, format, os);
		image.flush();
		bufferedImage.flush();
		return true;
		}catch (Throwable e){
			return false;
		}
	}
	
	public static Boolean fontError = false;
	  
	private static Boolean pdf2ImagePDFBox(byte[] bb, OutputStream os, int page,
			String format, int zoom) throws IOException {
		PDDocument document = PDDocument.load(new ByteArrayInputStream(bb));
		BufferedImage bim = null;
		try{
			PDPage pdPage = (PDPage) document.getDocumentCatalog().getAllPages().get(page-1);
			AonPDPage aonPdPage = new AonPDPage(pdPage); 
			bim = aonPdPage.convertToImage(BufferedImage.TYPE_INT_RGB, (zoom * 72) /100 );
			if(fontError){
				return false;
			}
			ImageIOUtil.writeImage(bim, format, os);
			return true; 
		} catch (OutOfMemoryError e){
			System.out.println("error -> " +  e);
			return true;
		} catch (Throwable e){ 
			e.printStackTrace();
			return false;
		} finally {
			document.close();
			if(bim != null) bim.flush();
		}
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
