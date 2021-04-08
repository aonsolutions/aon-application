package com.esferalia.aon.in.payroll.pdf.form;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDRadioButton;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;

public class PdfFormFiller {

	//FILL FORM FIELDS WITH ASSOCIATED VALUES
	static void fill_form_fields(InputStream pdf, Map<String,String> values, String new_pdf) throws UnknownPDFException {
		try (PDDocument doc = PDDocument.load(pdf)) {
			doc.setAllSecurityToBeRemoved(true);
	
			PDDocumentCatalog pdCatalog = doc.getDocumentCatalog();
			PDAcroForm form = pdCatalog.getAcroForm();
			
			Set<String> field_names = values.keySet();
			for (String field_name : field_names) {
				
				String value = values.get(field_name);
				PDField field = form.getField(field_name);
				
				if(field == null) continue;
				try {
					field.setValue(value);
				}catch(Exception e) {
					System.out.println("bad value at " + field.getFullyQualifiedName() + ". " + value + " is not a valid value.");
					
					try {
						System.out.println(
								"\n --------- VALID VALUES ----------" +
								"\n " + ((PDRadioButton) field).getOnValues() + "\n"				
						);
					}catch(Exception ex) {}
				}
				doc.save(new_pdf);
			}
		} catch (IOException e) {throw new UnknownPDFException();}		
	}	
}
