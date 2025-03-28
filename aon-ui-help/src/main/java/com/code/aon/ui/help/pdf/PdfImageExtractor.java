package com.code.aon.ui.help.pdf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;

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
	 * @param height The height to crop
	 * @return The file 
	 */
	public static InputStream imageFromPdfPageRegion(PDDocument doc, int page, int x, int y, int height) {
		
			// DPI Scales
			final float xsc = 2483 / 596;
			final float ysc = 3508 / 842;
			
			x *= xsc;
			y *= ysc;
		
			PDFRenderer renderer = new PDFRenderer(doc);
			
			try {
				BufferedImage image = renderer.renderImageWithDPI(page, 300, ImageType.RGB);
				BufferedImage subImage = image.getSubimage(0, (int) y, image.getWidth(), (int)(height * ysc) );
				 
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				ImageIO.write(subImage, "jpg", output);
				InputStream is = new ByteArrayInputStream(output.toByteArray());
					
				return is;
			} catch (IOException e) {e.printStackTrace();}
		return null;		
	}
	
	/**
	 * Get an image from a given destination name 
	 * @param stream The pdf file stream
	 * @param name The name to search for
	 * @return The image of the pdf region containing the given destination name
	 */
	public static InputStream imageFromPdfDestinationName(InputStream stream, String name) {
		Instant before = Instant.now();
		PDDocument doc;
		try {
			doc = Loader.loadPDF(stream.readAllBytes());
			PDPageXYZDestination dest = (PDPageXYZDestination) doc.getDocumentCatalog().getNames().getDests().getValue(name);	
			
			if(dest == null) {
				return null ;
			}
			
			int page = doc.getPages().indexOf(dest.getPage());
			int y = (int) (doc.getPage(page + 1).getMediaBox().getHeight() - dest.getTop());
			
			InputStream res = imageFromPdfPageRegion(doc, page, 0, y, 150);
			
			Instant after = Instant.now();
			long delta = Duration.between(before, after).toMillis();
			
			System.out.println("Extracted Image region " + name + " in: " + delta + "ms");
			return res;
			
		} catch (IOException e) {e.printStackTrace();}	
		
		return null;		
	}
	
	/**
	 * Get an image from a given page
	 * @param stream The pdf file stream
	 * @param name The name to search for
	 * @param y The y coordinate
	 * @param height The height to crop
	 * @return The image of the pdf region containing the given destination name
	 */
	public static InputStream imageFromPdfPage(InputStream stream, int page, int y, int height) {
		
		PDDocument doc;
		try {
			doc = Loader.loadPDF(stream.readAllBytes());
			int x = 0;
			
			return imageFromPdfPageRegion(doc, page, x, y, height);
		} catch (IOException e) {e.printStackTrace();}
		
		return null;		
	}
	
}
