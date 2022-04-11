package com.code.aon.ui.help.pdf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PdfImageExtractor {
	
	/**
	 * Creat an image from an specific area of a PDF file
	 * @param doc The pdf document
	 * @param page The page to take the screenshot from
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @return The file 
	 * 
	 * TODO, FIX y 
	 */
	public static InputStream imageFromPdfPageRegion(PDDocument doc, int page, int x, int y) {
		
			// DPI Scales
			final float xsc = 2483 / 596;
			final float ysc = 3508 / 842;
			
			x *= xsc;
			y *= ysc;
		
			PDFRenderer renderer = new PDFRenderer(doc);

			
			try {
				BufferedImage image = renderer.renderImageWithDPI(page, 300, ImageType.RGB);
				BufferedImage subImage = image.getSubimage(0, (int) y, image.getWidth(), (int)(100 * ysc) );
				 
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				ImageIO.write(subImage, "jpg", output);
				InputStream is = new ByteArrayInputStream(output.toByteArray());
					
				return is;
			} catch (IOException e) {}
		return null;		
	}
	
	/**
	 * Get an image from a given destination name 
	 * @param stream The pdf file stream
	 * @param name The name to search for
	 * @return The image of the pdf region containing the given destination name
	 */
	public static InputStream imageFromPdfDestinationName(InputStream stream, String name) {
		
		PDDocument doc;
		try {
			doc = Loader.loadPDF(stream);
			PDPageXYZDestination dest = (PDPageXYZDestination) doc.getDocumentCatalog().getNames().getDests().getValue(name);	
			
			if(dest == null) {
				return null ;
			}
			
			int page = doc.getPages().indexOf(dest.getPage());
			int y =(int) (doc.getPage(page + 1).getMediaBox().getHeight() - dest.getTop());
			
			return imageFromPdfPageRegion(doc, page, 0, y);
			
		} catch (IOException e) {e.printStackTrace();}	
		
		return null;		
	}
	
	/**
	 * Get an image from a given page
	 * @param stream The pdf file stream
	 * @param name The name to search for
	 * @return The image of the pdf region containing the given destination name
	 */
	public static InputStream imageFromPdfPage(InputStream stream, int page) {
		
		PDDocument doc;
		try {
			doc = Loader.loadPDF(stream);
			
			// DPI Scales
			final float xsc = 2483 / 596;
			final float ysc = 3508 / 842;
			
			int x = 0;
			int y = 0;
			
			PDFRenderer renderer = new PDFRenderer(doc);

			BufferedImage image = renderer.renderImageWithDPI(page, 300, ImageType.RGB);
			BufferedImage subImage = image.getSubimage(0, (int) y, image.getWidth(), (int)(400 * ysc) );
			 
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ImageIO.write(subImage, "jpg", output);
			InputStream is = new ByteArrayInputStream(output.toByteArray());
				
			return is;
		} catch (IOException e) {}
		
		return null;		
	}
	
}
