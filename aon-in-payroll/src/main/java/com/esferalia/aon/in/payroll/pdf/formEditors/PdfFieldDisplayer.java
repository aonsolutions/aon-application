package com.esferalia.aon.in.payroll.pdf.formEditors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDFieldTree;
import org.apache.pdfbox.pdmodel.interactive.form.PDRadioButton;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.in.payroll.pdf.toolkits.PDFToolkit;

public class PdfFieldDisplayer {

	//DISPLAY PDF INPUT NAMES
	static void display_pdf_form_names(InputStream is, String new_pdf) throws UnknownPDFException {
		try (PDDocument doc = PDDocument.load(is)) {
			doc.setAllSecurityToBeRemoved(true);

			PDDocumentCatalog pdCatalog = doc.getDocumentCatalog();
			PDAcroForm pdAcroForm = pdCatalog.getAcroForm();
			Optional<PDDocument> new_doc_opt = PdfFormEditor.copyFlat(Optional.of(doc));

			if (new_doc_opt.isEmpty()) throw new UnknownPDFException("");
			PDDocument new_doc = new_doc_opt.get();

			PDFieldTree fieldTree = pdAcroForm.getFieldTree();
			Iterator<PDField> fieldTreeIterator = fieldTree.iterator();

			doc.getDocumentCatalog().getAcroForm().flatten();
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(new_pdf + "default_values.txt")));
			
			//FOR EACH INPUT
			while (fieldTreeIterator.hasNext()) {
				PDField field = fieldTreeIterator.next();
				if (field.getFieldType().equals("Tx")) field.setValue("");
				String name = field.getFullyQualifiedName();
				
				try {
					Set<String> values = ((PDRadioButton) field).getOnValues();
					bw.write(field.getPartialName() + "\n" + values.toString() + "\n\n");
				}catch(Exception notRadius) {}

				//FOR EACH WIDGET
				for (int i = 0; i < field.getWidgets().size(); i++) {
					Optional<PDPage> page = PDFToolkit.get_field_page(Optional.of(field), i);
					Optional<PDRectangle> rectangle = PDFToolkit.get_field_rectangle(Optional.of(field), i);

					Optional<Integer> page_index = PdfFormEditor.page_index(Optional.of(new_doc), page);
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

			bw.close();
			new_doc.save(new_pdf);
		} catch (IOException e) {throw new UnknownPDFException("File not found");}
	}

}
