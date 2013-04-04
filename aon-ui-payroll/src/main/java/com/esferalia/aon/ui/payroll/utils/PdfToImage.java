package com.esferalia.aon.ui.payroll.utils;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.util.AonUtil;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;


public class PdfToImage {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PdfToImage.class.getName());

	private static BufferedImage pdfWallpaperImage;
	
	public static BufferedImage getPdfWallpaperImage(){
		return pdfWallpaperImage;
	}
	
	/**
	 * Creates a wallpaper image of the pdf file passed in url argument
	 * PDF version of the file must not be greater than v1.4
	 * 
	 * @param url
	 * @param pageNumber
	 * @param width
	 * @param height
	 */
	public static void createPdfWallpaper(URL url, int pageNumber, int width, int height){
		pdfWallpaperImage = null;
		try {
			ByteBuffer buf = getAsByteArray(url);
			PDFFile pdffile = new PDFFile(buf);
			PDFPage page = pdffile.getPage(pageNumber);
			Rectangle2D r2d = page.getBBox ();
			pdfWallpaperImage = (BufferedImage) page.getImage ((int) width, (int) height, r2d, null, true, true);
		} catch (FileNotFoundException e) {
			String msg = "Se ha producido un error al obtener la pagina del contrato.";
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage(msg);
		} catch (IOException e) {
			String msg = "Se ha producido un error al obtener la pagina del contrato.";
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage(msg);
		}
	}
	
	
//	public static void createPdfWallpaper(URL url, int pageNumber, int width, int height){
//		PDDocument document = null;
//		BufferedImage image = null;
//		try {
//			document = PDDocument.load(url);
//			PDPage page = (PDPage) document.getPrintable(pageNumber-1);
////			Double resolution = width/8.1;
//			Double resolution = height/11.66;
////			image = page.convertToImage();
//			image = page.convertToImage(BufferedImage.TYPE_INT_RGB, resolution.intValue() );
////			image = page.convertToImage(BufferedImage.TYPE_BYTE_GRAY, resolution.intValue() );
//			document.close();
//		} catch (IOException e) {
//			String msg = "Se ha producido un error al obtener la pagina del contrato.";
//			LOGGER.error(msg);
//			AonUtil.addErrorMessage(msg);
//		} 
//		pdfWallpaperImage = image;
//	}
	
	private static ByteBuffer getAsByteArray(URL url) throws IOException {
        ByteArrayOutputStream tmpOut = new ByteArrayOutputStream();
        InputStream in = url.openStream();
        byte[] buf = new byte[512];
        int len;
        while (true) {
            len = in.read(buf);
            if (len == -1) {
                break;
            }
            tmpOut.write(buf, 0, len);
        }
        tmpOut.close();
        ByteBuffer bb = ByteBuffer.wrap(tmpOut.toByteArray(), 0, tmpOut.size());
        return bb;
	}

}
