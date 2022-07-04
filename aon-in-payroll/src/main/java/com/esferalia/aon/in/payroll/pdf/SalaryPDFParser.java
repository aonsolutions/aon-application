package com.esferalia.aon.in.payroll.pdf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import com.esferalia.aon.in.payroll.pdf.template.A3PDFTemplate;
import com.esferalia.aon.in.payroll.pdf.template.AltaiPDFTemplate;
import com.esferalia.aon.in.payroll.pdf.template.AplifisaPDFTemplate;
import com.esferalia.aon.in.payroll.pdf.template.DSIPDFTemplate;
import com.esferalia.aon.in.payroll.pdf.template.OmegaPDFTemplate;
//import com.esferalia.aon.in.payroll.pdf.util.PDFTextStripper;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SalaryPDFParser {
	
	private static final SalaryPDFTemplate PDF_TEMPLATES [] = {
			AltaiPDFTemplate.ALTAI_PDF_TEMPLATE, 
			A3PDFTemplate.A3_PDF_TEMPLATE, 
			DSIPDFTemplate.DSI_PDF_TEMPLATE, 
			AplifisaPDFTemplate.APLIFISA_PDF_TEMPLATE, 
			OmegaPDFTemplate.OMEGA_PDF_TEMPLATE
			};
	
	
	public static void parse( File file , ISalaryBuilder<?> salaryBuilder) throws IOException, UnknownPDFException {
		try (PDDocument doc = Loader.loadPDF(file))
		{
			parser(doc, salaryBuilder);
		}
	}
	public static void parseOmega( File file , ISalaryBuilder<?> salaryBuilder) throws IOException, UnknownPDFException {
		try (PDDocument doc = Loader.loadPDF(file))
		{
			parserOmega(doc, salaryBuilder);
		}
	}

	public static void parse( InputStream is , ISalaryBuilder<?> salaryBuilder) throws IOException , UnknownPDFException {
		try (PDDocument doc = Loader.loadPDF(is))
		{
			parser(doc, salaryBuilder);
		}
	}
	public static void parseOmega( InputStream is , ISalaryBuilder<?> salaryBuilder) throws IOException , UnknownPDFException {
		try (PDDocument doc = Loader.loadPDF(is))
		{
			parserOmega(doc, salaryBuilder);
		}
	}
	
	private static void parser(PDDocument doc, ISalaryBuilder<?> salaryBuilder) throws IOException, UnknownPDFException {
        AccessPermission ap = doc.getCurrentAccessPermission();
		if (!ap.canExtractContent())
		{
			throw new IOException("You do not have permission to extract text");
		}
		
		PDFTextStripper stripper= new PDFTextStripper();
		stripper.setWordSeparator("    ");
		stripper.setSortByPosition(true);
		
		SalaryPDFTemplate template = null;
		
		String[] partTimeSheetIdentifiers = {"Registro diario de jornada en trabajadores a tiempo parcial"};
		
		for (int p = 1; p <= doc.getNumberOfPages(); p++) {
            // Set the page interval to extract. 
			// If we don't, then all pages would be extracted.
			stripper.setStartPage(p);
			stripper.setEndPage(p);
			
			String text = stripper.getText(doc);
			if ( AonStringUtils.isBlank(text) || Arrays.stream(partTimeSheetIdentifiers).anyMatch(str -> AonStringUtils.containsIgnoreCase(text, str))) 
				continue;
			
//			System.out.println(text);
			
			template = parse(template, text, salaryBuilder);
			
		}
	}
	
	private static void parserOmega(PDDocument doc, ISalaryBuilder<?> salaryBuilder) throws IOException, UnknownPDFException {
        AccessPermission ap = doc.getCurrentAccessPermission();
		if (!ap.canExtractContent())
		{
			throw new IOException("You do not have permission to extract text");
		}
		
		org.apache.pdfbox.text.PDFTextStripper stripper= new org.apache.pdfbox.text.PDFTextStripper();
		stripper.setSortByPosition(true);
		stripper.setWordSeparator("    ");
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
			//TODO
//			System.out.println(text);
				
			try {
				template = parseOmega(template, text, salaryBuilder);
			} catch (UnknownPDFException e) {
				System.err.println(text);
				continue;
			}
			
		}
	}
	
	private static SalaryPDFTemplate parse ( SalaryPDFTemplate pdfTemplate, String text, ISalaryBuilder<?> salaryBuilder) throws UnknownPDFException, IOException {
		if ( pdfTemplate == null ) {
			for (SalaryPDFTemplate template : PDF_TEMPLATES ) {
				try {
					return template.parse(text, salaryBuilder);
				} catch ( SalaryPDFException e ) {
//					System.err.println(e.getMessage());
//					System.out.println(text);
					return template;
				} catch ( UnknownPDFException e ) {
//					System.err.println(e.getMessage());
//					System.err.println(text);
				}
			}
			throw new UnknownPDFException("Formato de nómina desconocido");
		
		} else {
			try {
				return pdfTemplate.parse(text, salaryBuilder);
			} catch ( SalaryPDFException e ) {
				//System.err.println(text);
				return pdfTemplate;
			}
		}
	}
	
	private static SalaryPDFTemplate parseOmega ( SalaryPDFTemplate pdfTemplate, String text, ISalaryBuilder<?> salaryBuilder) throws UnknownPDFException, IOException {
		if ( pdfTemplate == null ) {
			OmegaPDFTemplate template = OmegaPDFTemplate.OMEGA_PDF_TEMPLATE;
				try {
					return template.parse(text, salaryBuilder);
				} catch ( SalaryPDFException e ) {
//					System.err.println(e.getMessage());
//					System.out.println(text);
					return template;
				} catch ( UnknownPDFException e ) {
//					System.err.println(e.getMessage());
//					System.err.println(text);
				}
			throw new UnknownPDFException("Formato de nómina desconocido");
			
		} else {
			try {
				return pdfTemplate.parse(text, salaryBuilder);
			} catch ( SalaryPDFException e ) {
				//System.err.println(text);
				return pdfTemplate;
			}
		}
	}

}
