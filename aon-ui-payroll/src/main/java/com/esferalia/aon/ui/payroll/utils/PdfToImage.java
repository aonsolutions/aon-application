package com.esferalia.aon.ui.payroll.utils;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esferalia.aon.ui.payroll.controller.ContractGenerationWizard;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class PdfToImage {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractGenerationWizard.class.getName());
	
	public static BufferedImage create(URL url, int pageNumber, int width, int height){
		BufferedImage img = null;;
		try {
			ByteBuffer buf = getAsByteArray(url);
			PDFFile pdffile = new PDFFile(buf);
			PDFPage page = pdffile.getPage(pageNumber);
			Rectangle2D r2d = page.getBBox ();
			img = (BufferedImage) page.getImage ((int) width, (int) height, r2d, null, true, true);
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return img;
	}
	
	public static ByteBuffer getAsByteArray(URL url) throws IOException {
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
