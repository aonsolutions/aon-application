package com.esferalia.aon.in.payroll.pdf.formParsers;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.in.payroll.pdf.creators.PDFToolkit;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.form.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class PdfFieldNameParser {

	//RENAME PDF BASE
	public static void rename_pdf(InputStream is, String filename, String default_prefix, Map<String, String> names) throws IOException {
		try (PDDocument doc = PDDocument.load(is)) {
			doc.setAllSecurityToBeRemoved(true);

			PDDocumentCatalog pdCatalog = doc.getDocumentCatalog();
			PDAcroForm pdAcroForm = pdCatalog.getAcroForm();

			PDFieldTree fieldTree = pdAcroForm.getFieldTree();
			Iterator<PDField> fieldTreeIterator = fieldTree.iterator();

			int i = 0;
			while (fieldTreeIterator.hasNext()) {
				PDField field = fieldTreeIterator.next();
				if (names != null && names.containsKey(field.getPartialName()))
					field.setPartialName(names.get(field.getPartialName()));
				else {
					field.setPartialName(default_prefix + i);
					i++;
				}
			}
			doc.save(filename);
		}
	}

	//DISPLAY PDF INPUT NAMES
	public static void display_pdf_form_names(InputStream is, String new_pdf) throws UnknownPDFException {
		try (PDDocument doc = PDDocument.load(is)) {
			doc.setAllSecurityToBeRemoved(true);

			PDDocumentCatalog pdCatalog = doc.getDocumentCatalog();
			PDAcroForm pdAcroForm = pdCatalog.getAcroForm();
			Optional<PDDocument> new_doc_opt = copyFlat(Optional.of(doc));

			if (new_doc_opt.isEmpty()) throw new UnknownPDFException("");
			PDDocument new_doc = new_doc_opt.get();

			PDFieldTree fieldTree = pdAcroForm.getFieldTree();
			Iterator<PDField> fieldTreeIterator = fieldTree.iterator();

			doc.getDocumentCatalog().getAcroForm().flatten();

			//FOR EACH INPUT
			while (fieldTreeIterator.hasNext()) {
				PDField field = fieldTreeIterator.next();
				if (field.getFieldType().equals("Tx")) field.setValue("");
				String name = field.getFullyQualifiedName();

				//FOR EACH WIDGET
				for (int i = 0; i < field.getWidgets().size(); i++) {
					Optional<PDPage> page = PDFToolkit.get_field_page(Optional.of(field), i);
					Optional<PDRectangle> rectangle = PDFToolkit.get_field_rectangle(Optional.of(field), i);

					Optional<Integer> page_index = page_index(Optional.of(new_doc), page);
					if (page_index.isEmpty()) System.err.println("WARNING: Page not found");
					else {
						float x, y, w, h;
						x = rectangle.get().getLowerLeftX();
						y = rectangle.get().getLowerLeftY();
						w = rectangle.get().getWidth();
						h = rectangle.get().getHeight();

						y -= 0;
						if (y > page.get().getMediaBox().getHeight() - 50) y = page.get().getMediaBox().getHeight() - 50;
						if (w < 20) w = 20;
						if (w > 200) w = 200;

						PDPage current_page = new_doc.getPage(page_index.get());
						PDPageContentStream contents = PDFToolkit.open_in_append_mode(doc, current_page);

						PDFToolkit.drawBox(contents, x, y, w, h, PDFToolkit.WHITE);
						PDFToolkit.drawText(contents, name, x + 3, y + h / 2 - 1.5f, PDFToolkit.BLUE, PDFToolkit.HELVETICA_BOLD, 4f);

						contents.close();
					}
				}
			}

			new_doc.save(new_pdf);
		} catch (IOException e) {
			throw new UnknownPDFException("File not found");
		}
	}

	//DISPLAY PDF INPUT VALUES
	public static void display_pdf_form_values(InputStream is, String new_pdf) throws UnknownPDFException {
		try (PDDocument doc = PDDocument.load(is)) {
			doc.setAllSecurityToBeRemoved(true);

			PDDocumentCatalog pdCatalog = doc.getDocumentCatalog();
			PDAcroForm pdAcroForm = pdCatalog.getAcroForm();
			Optional<PDDocument> new_doc_opt = copyFlat(Optional.of(doc));

			if (new_doc_opt.isEmpty()) throw new UnknownPDFException("");
			PDDocument new_doc = new_doc_opt.get();

			PDFieldTree fieldTree = pdAcroForm.getFieldTree();
			Iterator<PDField> fieldTreeIterator = fieldTree.iterator();

			doc.getDocumentCatalog().getAcroForm().flatten();

			//FOR EACH INPUT
			while (fieldTreeIterator.hasNext()) {
				PDField field = fieldTreeIterator.next();
				if (field.getFieldType().equals("Tx")) field.setValue("");
				String value = field.getValueAsString();

				//FOR EACH WIDGET
				for (int i = 0; i < field.getWidgets().size(); i++) {
					Optional<PDPage> page = PDFToolkit.get_field_page(Optional.of(field), i);
					Optional<PDRectangle> rectangle = PDFToolkit.get_field_rectangle(Optional.of(field), i);

					Optional<Integer> page_index = page_index(Optional.of(new_doc), page);
					if (page_index.isEmpty()) System.err.println("WARNING: Page not found");
					else {
						float x, y, w, h;
						x = rectangle.get().getLowerLeftX();
						y = rectangle.get().getLowerLeftY();
						w = rectangle.get().getWidth();
						h = rectangle.get().getHeight();

						y -= 0;
						if (y > page.get().getMediaBox().getHeight() - 50) y = page.get().getMediaBox().getHeight() - 50;
						if (w < 20) w = 20;
						if (w > 200) w = 200;

						PDPage current_page = new_doc.getPage(page_index.get());
						PDPageContentStream contents = PDFToolkit.open_in_append_mode(doc, current_page);

						PDFToolkit.drawBox(contents, x, y, w, h, PDFToolkit.WHITE);
						PDFToolkit.drawText(contents, value, x + 3, y + h / 2 - 1.5f, PDFToolkit.BLUE, PDFToolkit.HELVETICA_BOLD, 4f);

						contents.close();
					}
				}
			}

			new_doc.save(new_pdf);
		} catch (IOException e) {
			throw new UnknownPDFException("File not found");
		}
	}


	//COPIES THE PDF WITHOUT FORM
	private static Optional<PDDocument> copyFlat(Optional<PDDocument> original_opt) throws IOException {
		if (original_opt.isEmpty()) return Optional.empty();

		PDDocument original = original_opt.get();
		PDDocument new_doc = new PDDocument();

		Iterator<PDPage> pageIterator = original.getPages().iterator();
		while (pageIterator.hasNext()) new_doc.addPage(pageIterator.next());

		return Optional.of(new_doc);
	}

	//RETURNS IF THE DOCUMENT CONTAINS A PAGE
	private static Optional<Integer> page_index(Optional<PDDocument> doc_opt, Optional<PDPage> page_opt) {
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

}
