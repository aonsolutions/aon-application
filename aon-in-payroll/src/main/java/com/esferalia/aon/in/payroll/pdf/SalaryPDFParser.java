package com.esferalia.aon.in.payroll.pdf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import com.esferalia.aon.in.payroll.pdf.templates.A3PDFTemplate;
import com.esferalia.aon.in.payroll.pdf.templates.AltaiPDFTemplate;
import com.esferalia.aon.in.payroll.pdf.templates.DSIPDFTemplate;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SalaryPDFParser {
	
	private static final SalaryPDFTemplate PDF_TEMPLATES [] = {AltaiPDFTemplate.ALTAI_PDF_TEMPLATE, A3PDFTemplate.A3_PDF_TEMPLATE};
	
	
	public static void parse( File file , ISalaryBuilder<?> salaryBuilder) throws IOException, UnknownPDFException {
		try (PDDocument doc = PDDocument.load(file))
		{
			parser(doc, salaryBuilder);
		}
	}

	public static void parse( InputStream is , ISalaryBuilder<?> salaryBuilder) throws IOException , UnknownPDFException {
		try (PDDocument doc = PDDocument.load(is))
		{
			parser(doc, salaryBuilder);
		}
	}
	
	private static void parser(PDDocument doc, ISalaryBuilder<?> salaryBuilder) throws IOException, UnknownPDFException {
        AccessPermission ap = doc.getCurrentAccessPermission();
		if (!ap.canExtractContent())
		{
			throw new IOException("You do not have permission to extract text");
		}
		
		PDFTextStripper stripper= new PDFTextStripper();
		
		stripper.setSortByPosition(true);
		
		SalaryPDFTemplate template = null;
		
		for (int p = 1; p <= doc.getNumberOfPages(); p++) {
            // Set the page interval to extract. 
			// If we don't, then all pages would be extracted.
			stripper.setStartPage(p);
			stripper.setEndPage(p);
			
			String text = stripper.getText(doc);
			if ( AonStringUtils.isBlank(text) ) 
				continue;
			
//			System.out.println(text);
			
			template = parse(template, text, salaryBuilder);
			
		}
	}
	
	private static SalaryPDFTemplate parse ( SalaryPDFTemplate pdfTemplate, String text, ISalaryBuilder<?> salaryBuilder) throws UnknownPDFException, IOException {
		if ( pdfTemplate == null ) {
			for (SalaryPDFTemplate template : PDF_TEMPLATES ) {
				try {
					return template.parse(text, salaryBuilder);
				} catch ( SalaryPDFException e ) {
					return template;
				} catch ( Exception e ) {
				}
			}
			throw new UnknownPDFException("Formato de nómina desconocido");
		
		} else {
			try {
				return pdfTemplate.parse(text, salaryBuilder);
			} catch ( SalaryPDFException e ) {
				return pdfTemplate;
			}
		}
	}

}
