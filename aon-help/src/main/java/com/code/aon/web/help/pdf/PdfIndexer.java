package com.code.aon.web.help.pdf;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import com.code.aon.web.help.pdf.items.TextType;

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
	public static void index(InputStream stream, String name, PDDocument index) {

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
			
			PDOutlineItem grandParent = null;
			TextType grandParentType = null; 
			
			PDOutlineItem parent = null;
			TextType parentType = null;

			
			// For each text create outline items
			
			ArrayList<PdfText> lines = (ArrayList<PdfText>) stripper.getLines();
			
			System.out.println("LINES FOUND: " + lines.size());
			
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
				
				PDActionURI action = new PDActionURI();
				action.setURI("http://akrck02.github.io#/file=" + name + "&page=" + l.getPage() + "&type=" + l.getType());
				item.setAction(action);
				
				if(l.getType() == TextType.TITLE_1) {
					root.addLast(item);
					parent = item;
				} else {
					parent.addLast(item);
				}
				
				
				/**
				// If is root
				if(parent == null && grandParent == null)
				{
					outline.addLast(item);
					parent = item;
					parentType = l.getType();
				} 
								
				// if not root
				else {
					
					//if parent type > item type attach item to parent
					if(parentType.greaterThan(l.getType())) {
						parent.addLast(item);
					}
					//else attach item to grandpa
					else {
						//if no grandpa attach to root
						if(grandParent == null) {
							outline.addLast(item);
						} 
						
						// else attach to grandpa
						else {
							grandParent.addLast(item);
							parent = item;
							parentType = l.getType();
						}
						
						
					}
				}
				
				**/
			}
			//document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	
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
		
		InputStream pdfOneStream = PdfIndexer.class.getResourceAsStream("payroll_v3.pdf");
		InputStream pdfTwoStream = PdfIndexer.class.getResourceAsStream("payroll_new.pdf");
		
		PDDocument document = new PDDocument();		
		PDDocumentOutline outline = new PDDocumentOutline();
		document.getDocumentCatalog().setDocumentOutline(outline);	
		
		PDPage blankPage = new PDPage();
		document.addPage( blankPage );	
	
		index(pdfOneStream, "payroll_v3.pdf", document);
		index(pdfTwoStream, "payroll_new.pdf", document);
		
		try {
			document.save("src/main/resources/com/code/aon/web/help/pdf/index.pdf");
			document.close();
			System.out.println("[DONE] Index generated.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				document.close();
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
