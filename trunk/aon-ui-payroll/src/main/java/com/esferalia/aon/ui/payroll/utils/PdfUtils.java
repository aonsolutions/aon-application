package com.esferalia.aon.ui.payroll.utils;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IAttachment;
import com.code.aon.ui.util.AonUtil;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class PdfUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PdfUtils.class.getName());
	
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

	public static byte[] mergePdf(ArrayList<IAttachment> attachList) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			List<PdfReader> pdfReaderList = new ArrayList<PdfReader>();
			for(IAttachment attach: attachList){
				pdfReaderList.add(new PdfReader(attach.getData()));
			}

			PdfCopyFields copy = new PdfCopyFields(outputStream);
			copy.open();

			if (null != pdfReaderList && !pdfReaderList.isEmpty()) {
				Iterator<PdfReader> iter = pdfReaderList.iterator();
				while (iter.hasNext()) {
					String pageNOs = "";
					PdfReader pdfReader = (PdfReader) iter.next();
					int noOfPages = pdfReader.getNumberOfPages();
					if (noOfPages > 0) {
						pageNOs = getNumderOfPages(noOfPages);
					}
					copy.addDocument(pdfReader, pageNOs);
				}
			}
			copy.close();
			return outputStream.toByteArray();
		} catch (DocumentException e) {
			String msg = "Se ha producido un error al generar un documento a partir de la lista de documentos dada.";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg + "["+e+"]");
		} catch (IOException e) {
			String msg = "Se ha producido un error al generar un documento a partir de la lista de documentos dada.";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg + "["+e+"]");
		}
		return null;
	}

	/**
	 * Function to get page numbers in string with comma separated
	 * 
	 * @param noOfPages
	 * @return
	 */
	private static String getNumderOfPages(int noOfPages) {
		String pageNOs = "";
		boolean flag = false;
		for (int i = 0; i < noOfPages; i++) {

			if (flag == true) {
				Integer c = (Integer) i;
				pageNOs = pageNOs.concat("," + c.toString());
			}
			if (flag == false) {
				Integer c = (Integer) i;
				pageNOs = c.toString();
				flag = true;
			}
		}
		return pageNOs;
	}
}