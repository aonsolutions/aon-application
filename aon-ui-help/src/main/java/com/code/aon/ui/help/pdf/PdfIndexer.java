package com.code.aon.ui.help.pdf;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDestinationNameTreeNode;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import com.code.aon.ui.help.pdf.items.TextType;

public class PdfIndexer {

	/**
	 * <p>
	 * <b>Description:</b> <i>Open a PDF file in append mode.</i>
	 * </p>
	 * 
	 * @return PDPageContentStream (the pdf file stream)
	 */
	public static PDPageContentStream openInAppendMode(PDDocument doc, PDPage page) {
		try {
			return new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Add file to external index 
	 * @param stream The PDF to add 
	 * @param index 
	 */
	public static PDDocument index(InputStream stream, String name, PDDocument index) {

		try {
			System.out.println("[Loading][PDF] " + (stream.available()/1024)  + "KB");
			
			// Get PDF lines
			
			OutlineTextStripper stripper = new OutlineTextStripper();			
			PDDocument document =  Loader.loadPDF(stream);
			stripper.getText(document);
			
			PDDocumentOutline outline = index.getDocumentCatalog().getDocumentOutline();
			
			//  Generate PDF outline and add Document destination
			
			PDOutlineItem root = new PDOutlineItem();	
			root.setTitle(name);
			outline.addLast(root);
	
			
			// Prepare items for hierarchy
			PDOutlineItem parent = null;

			
			// For each text create outline items
			
			ArrayList<PdfText> lines = (ArrayList<PdfText>) stripper.getLines();
			
			System.out.println("LINES FOUND: " + lines.size());
			
			 
			Map<String, PDPageDestination> namesMap = new HashMap<>(); 
			
			for(PdfText l: lines) {
				
				if(l.getType() == TextType.TEXT || l.getType() == null)
					continue;

				// Create outline item
				
				PDOutlineItem item = new PDOutlineItem();			
				PDPageXYZDestination dest = new PDPageXYZDestination();
				dest.setTop(0 * (int)l.getY());
				dest.setLeft((int) l.getX());
				dest.setPageNumber(l.getPage());
				dest.setPage(document.getPage(l.getPage()));

				// Add a destination
				
				item.setDestination(dest);
				item.setTitle(l.getText());		
				item.setTextColor(Color.blue);
				
				// Add an action
				PDPageXYZDestination pageXYZDestination = new PDPageXYZDestination();
				pageXYZDestination.setLeft((int)l.getX());
				pageXYZDestination.setTop((int)l.getY());
				pageXYZDestination.setPageNumber(l.getPage());
				//String md5 = DigestUtils.md5Hex(l.getText());
				String id = String.format("aon%d", namesMap.size());
				namesMap.put(id, pageXYZDestination );
				
				PDActionURI action = new PDActionURI();
				//action.setURI("help/"+ name + ".pdf" + "#page=" + l.getPage() );
				action.setURI("help/"+ name + ".pdf" + "#" + id );
				System.out.println( action.getURI() );
				item.setAction(action);
				
				if(l.getType() == TextType.TITLE_1) {
					root.addLast(item);
					parent = item;
				} else {
					parent.addLast(item);
				}
				
				
			}
			
			PDDocumentNameDictionary names = document.getDocumentCatalog().getNames();
			if ( names == null ) {
				names = new PDDocumentNameDictionary(document.getDocumentCatalog());
			}
			PDDestinationNameTreeNode dests = names.getDests();
			if ( dests == null ) {
				dests = new PDDestinationNameTreeNode();
				names.setDests(dests);
			}
			dests.setNames(namesMap);
			return document;
			//document.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	
//	public static List<PDOutlineItem> search( String name) throws IOException {
//		try  ( InputStream is = PdfIndexer.class.getResourceAsStream("index.pdf");
//			   PDDocument document = Loader.loadPDF(is) ) {
//			return search(document, name );
//		}
//	}
//
//	public static List<PDOutlineItem> search() throws IOException {
//		try  ( InputStream is = PdfIndexer.class.getResourceAsStream("index.pdf");
//			   PDDocument document = Loader.loadPDF(is) ) {
//			return search(document, "" );
//		}
//	}

	/**
	 * Search all entries for given name
	 * @param document The index 
	 * @param name The name to search for
	 * @return The found entries
	 */
	public static ArrayList<PDOutlineItem> search(PDDocument document, String name) {
		
		ArrayList<PDOutlineItem> results = new ArrayList<PDOutlineItem>(); 
				
		
			PDDocumentCatalog catalogue = document.getDocumentCatalog();
			
			if(catalogue == null) {
				return results;
			}
			
			PDDocumentOutline outline = catalogue.getDocumentOutline();
			
			if(outline == null) {
				return results;
			}
			
			// For each outline item search sub-items
			
			for(PDOutlineItem item = outline.getFirstChild(); item != null; item = item.getNextSibling()) {
				if(item.getTitle().toUpperCase().contains(name.toUpperCase())) {
					results.add(item);
				}
				searchInside(item, name , results);
			}
				
		return results;
	}
	
	/**
	 * Search inside the given item 
	 * @param parent The item to search in
	 * @param name The name to search
	 * @param results The results
	 */
	private static void searchInside(PDOutlineItem parent, String name, ArrayList<PDOutlineItem> results) {
		
		for(PDOutlineItem item = parent.getFirstChild(); item != null; item = item.getNextSibling()) {
			if(item.getTitle().toUpperCase().contains(name.toUpperCase())) {
				results.add(item);
			}
			searchInside(item,name,results);
		}
				
	}
	
	/**
	 * Main for testing
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		System.out.println("-----------------------------------------------------------");
		System.out.println(" PDF INDEX GENERATION ");
		System.out.println("-----------------------------------------------------------");
		
		InputStream pdfOneStream = PdfIndexer.class.getResourceAsStream("payroll.pdf");
		//InputStream pdfTwoStream = PdfIndexer.class.getResourceAsStream("payroll_new.pdf");
		
		PDDocument index = new PDDocument();		
		PDDocumentOutline outline = new PDDocumentOutline();
		index.getDocumentCatalog().setDocumentOutline(outline);	
		
		PDPage blankPage = new PDPage();
		index.addPage( blankPage );	
	
		PDDocument payrollNamed = index(pdfOneStream, "LABORAL Manual de USUARIO", index );
		
		
		//index(pdfTwoStream, "payroll_new.pdf", document);
		
		try {
			
			payrollNamed.save("target/classes/com/code/aon/ui/help/pdf/payroll_names.pdf");
			payrollNamed.save("src/main/resources/com/code/aon/ui/help/pdf/payroll_names.pdf");
			
			//document.save("src/main/resources/com/code/aon/web/help/pdf/index.pdf");
			//new File("target/generated-sources/com/code/aon/web/help/pdf").mkdirs();
			index.save("target/classes/com/code/aon/ui/help/pdf/index.pdf");
			index.save("src/main/resources/com/code/aon/ui/help/pdf/index.pdf");
			index.close();
			System.out.println("[DONE] Index generated.");
			
			try  ( InputStream is = PdfIndexer.class.getResourceAsStream("index.pdf");
					PDDocument pddoc = Loader.loadPDF(is) ) {
				search(index, "" ).forEach( i -> System.out.println( i.getTitle()));
		}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				index.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/*
		try {
			InputStream indexStream = PdfIndexer.class.getResourceAsStream("index.pdf");		
			PDDocument indexDoc =  Loader.loadPDF(indexStream);		
		
			System.out.println("------------------------------------------");
			System.out.println(" Searching for \"Trabajo\"");
			System.out.println("------------------------------------------");
			
			ArrayList<PDOutlineItem> results = search(indexDoc,"Trabajo");
			results.forEach(r -> {
				
				PDAction action = r.getAction();
				if(action instanceof PDActionURI) {
					System.out.println("[NAME] " + r.getTitle());
					System.out.println("[URL] " + ((PDActionURI) action).getURI() + "\n");
					
				} else {
					System.out.println(r.getTitle());
				}
					
			});
			indexDoc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		*/
	}
	
}
