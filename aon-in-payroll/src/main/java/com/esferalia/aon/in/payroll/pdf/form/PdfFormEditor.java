package com.esferalia.aon.in.payroll.pdf.form;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;

public class PdfFormEditor {
	
	//COPIES THE PDF WITHOUT FORM
	static Optional<PDDocument> copyFlat(Optional<PDDocument> original_opt) throws IOException {
		if (original_opt.isEmpty()) return Optional.empty();

		PDDocument original = original_opt.get();
		PDDocument new_doc = new PDDocument();

		Iterator<PDPage> pageIterator = original.getPages().iterator();
		while (pageIterator.hasNext()) new_doc.addPage(pageIterator.next());

		return Optional.of(new_doc);
	}

	//RETURNS IF THE DOCUMENT CONTAINS A PAGE
	static Optional<Integer> page_index(Optional<PDDocument> doc_opt, Optional<PDPage> page_opt) {
		if (doc_opt.isEmpty() || page_opt.isEmpty()) return Optional.empty();
		PDDocument doc = doc_opt.get();
		PDPage page = page_opt.get();

		int i = 0;
		Iterator<PDPage> pageIterator = doc.getPages().iterator();
		while (pageIterator.hasNext()) {
			if (pageIterator.next().equals(page)) return Optional.of(i);
			i++;
		}

		return Optional.empty();
	}

	//DISPLAY FORM VALUES
	public static void display_pdf_form_values(InputStream is, String new_pdf) throws UnknownPDFException {
		PdfFieldDisplayer.display_pdf_form_names(is, new_pdf);
	}
	
	//RENAME PDF FORM FIELDS
	public static void rename_pdf_fields(InputStream is, String filename, String default_prefix, Map<String, String> names) throws IOException, UnknownPDFException {
		PdfFieldRenamer.rename_pdf_fields(is, filename, default_prefix, names);
	}
	
	//FILL FORM FIELDS WITH ASSOCIATED VALUES
	public static void fill_form_fields(InputStream pdf, Map<String,String> values, String new_pdf) throws UnknownPDFException {
		PdfFormFiller.fill_form_fields(pdf, values, new_pdf);		
	}	
}
