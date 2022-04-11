package com.code.aon.ui.help.pdf;

import java.text.Normalizer;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import com.esferalia.aon.watson.util.AonStringUtils;

public class PdfSearcher {
	
	/**
	 * Returns if a text contains a word with typo tolerance
	 * @param text The text to search in
	 * @param searcher The word to search for
	 * @param tolerance The tolerance to use 
	 * @return
	 */
	public static boolean containsMatching(String text, String searcher, int tolerance) {

		
		text = normalized(text);
		searcher = normalized(searcher);
		
		System.out.println(text);
		System.out.println(searcher);
	
				
		String[] words = text.split("\\s");
		
		for (String word : words) {
			
			int currentDistance = AonStringUtils.getLevenshteinDistance(searcher.toUpperCase(), word.toUpperCase());
			int realTolerance = tolerance;
			
			if(currentDistance < realTolerance) {
				return true;
			}
		
		}
		
		return false;
	}
	
	/**
	 * Get normalized text (no accents)
	 * @param text
	 * @return
	 */
	public static String normalized(String text) {

		text = Normalizer.normalize(text, Normalizer.Form.NFD);
		text = text.replaceAll("\\p{M}", "");
		return text;
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
	
	
}
