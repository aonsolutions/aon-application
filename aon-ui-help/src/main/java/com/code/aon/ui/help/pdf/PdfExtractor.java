package com.code.aon.ui.help.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;

public class PdfExtractor {

	/**
	 * Extract all pages as independent documents
	 * @param doc The document to extract
	 * @param url The output url 
	 */
	public static void extractAllPages(PDDocument doc, String url) {
		try {
			Splitter splitter = new Splitter();
			List<PDDocument> Pages = splitter.split(doc);
			
			int i = 0;
			for (PDDocument pd : Pages) {
				System.out.println(pd.getDocumentCatalog().getDests());
				pd.save(url + "/page_" + i + ".pdf");
				i++;
			}
		} catch (IOException e) {e.printStackTrace();}		
	}

	
	/**
	 * Extract a page region
	 * @param page The pge to extract from
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param height The height of the region
	 * @param width The width of the region
	 * @return The new document containing the region
	 */
	public static PDDocument extractPageRegion(PDPage page, float x, float y, float width, float height) {
		PDDocument doc = new PDDocument();
		page.setCropBox(new PDRectangle(x, y, width, height));		
		doc.addPage(page);
		return doc;
	}
	
	
	private static void extractDestinationRegions(PDDocument doc) throws IOException {
		Map<String, PDPageDestination> names = doc.getDocumentCatalog().getNames().getDests().getNames();
		
		// Order names by number
		Object[] nameArray = names.keySet().stream().sorted((Object t1, Object t2) -> {
			
			String n1 = (t1 + "");
			String n2 = (t2 + "");
			
			n1 = n1.substring(n1.indexOf("aon") + 3);
			n2 = n2.substring(n2.indexOf("aon") + 3);
			
			return Integer.parseInt(n1) - Integer.parseInt(n2);
		}).toArray();		
		
		
		// For each name 
		for (int i = 0; i < nameArray.length; i++) {
			
			String id = "" + nameArray[i];			
			PDPageXYZDestination dest = (PDPageXYZDestination) names.get(id);
			
			int index = doc.getPages().indexOf(dest.getPage());
			PDPage page = doc.getPages().get(index);
			
			float height = doc.getPage(1).getMediaBox().getHeight();
			float y =  dest.getTop() - (height);
			
			PDDocument region = extractPageRegion(page, 0, y, doc.getPage(1).getMediaBox().getWidth(), height);
			region.save("/tmp/aon/" + id + ".pdf");
		}
		
	}
	
	
	public static InputStream extractPage(InputStream stream, int page) {
		
		
		
		return stream;
	}
	
	
	public static InputStream fromDestinationName(InputStream stream, String name) {
		
		Instant before = Instant.now();
		
		try {
			
			PDDocument doc = Loader.loadPDF(stream);
			Map<String, PDPageDestination> names = doc.getDocumentCatalog().getNames().getDests().getNames();
						
			PDPageXYZDestination dest = (PDPageXYZDestination) names.get(name);
			
			int index = doc.getPages().indexOf(dest.getPage());
			PDPage page = doc.getPages().get(index);
			
			float height = doc.getPage(1).getMediaBox().getHeight();
			float y =  dest.getTop() - (height);
			
			PDDocument region = extractPageRegion(page, 0, y + 10, doc.getPage(1).getMediaBox().getWidth(), height);
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			region.save(out);
			
			Instant after = Instant.now();
			long delta = Duration.between(before, after).toMillis();
			
			System.out.println("Extracted pdf region " + name  + " in: " + delta + "ms");
			
			return new ByteArrayInputStream(out.toByteArray());
		} catch(Exception e) {
			return new ByteArrayInputStream(new byte[0]);
		}
	}
	
	
}
