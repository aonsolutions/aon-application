package com.code.aon.ui.help.pdf;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import com.esferalia.aon.watson.util.AonStringUtils;

public class PdfSearcher {

	private static final List<PDOutlineItem> INDEX = getIndex();

	public static List<PDOutlineItem> search(Predicate<PDOutlineItem> filter) {
		return INDEX.stream().filter(filter)
				.collect(Collectors.toList());
	}

	public static List<PDOutlineItem> search(String name) {
		return search(outline -> AonStringUtils.containsIgnoreCase(outline.getTitle(), name));
	}

	/**
	 * Search index entries for given name
	 * 
	 * @param document The index
	 * @param name     The name to search for
	 * @return The found entries
	 */
	public static ArrayList<PDOutlineItem> search(PDDocument document, String name) {

		ArrayList<PDOutlineItem> results = new ArrayList<PDOutlineItem>();

		PDDocumentCatalog catalogue = document.getDocumentCatalog();

		if (catalogue == null) {
			return results;
		}

		PDDocumentOutline outline = catalogue.getDocumentOutline();

		if (outline == null) {
			return results;
		}

		// For each outline item search sub-items

		for (PDOutlineItem item = outline.getFirstChild(); item != null; item = item.getNextSibling()) {
			if (item.getTitle().toUpperCase().contains(name.toUpperCase())) {
				results.add(item);
			}
			searchInside(item, name, results);
		}

		return results;
	}

	/**
	 * Search inside the given item
	 * 
	 * @param parent  The item to search in
	 * @param name    The name to search
	 * @param results The results
	 */
	private static void searchInside(PDOutlineItem parent, String name, ArrayList<PDOutlineItem> results) {

		for (PDOutlineItem item = parent.getFirstChild(); item != null; item = item.getNextSibling()) {
			if (item.getTitle().toUpperCase().contains(name.toUpperCase())) {
				results.add(item);
			}
			searchInside(item, name, results);
		}

	}

	private static List<PDOutlineItem> getIndex() {
		try (InputStream is = PdfSearcher.class.getResourceAsStream("index.pdf");
				PDDocument doc = Loader.loadPDF(is)) {
			return search(doc, "");
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

}
