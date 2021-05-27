package com.esferalia.aon.in.payroll.pdf.form;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.interactive.form.*;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class PdfFieldRenamer {

	//RENAME PDF BASE
	static void rename_pdf_fields(InputStream is, String filename, String default_prefix, Map<String, String> names) throws IOException, UnknownPDFException {
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
		}catch (IOException e) {throw new UnknownPDFException("File not found");}
	}

}
