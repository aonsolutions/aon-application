package com.esferalia.aon.in.payroll.ivl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class IvlParserMain {

	public static void main(String[] args) throws IOException, UnknownPDFException {

//		IvlParserListener psl = new IvlParserListener() {
//		};
//
//		File file = new File(
//				"/home/jmortega/eclipse-workspace/aon.parent/aon-in-payroll/src/test/resources/com/esferalia/aon/in/payroll/tgss/ivl/ivl-aon.pdf");
//		PDDocument doc = Loader.loadPDF(file);
//		PDFTextStripper stripper = new PDFTextStripper();
//		String text = stripper.getText(doc);
//		System.out.println(text);
//
//		IvlCccParser.parse(text, psl);
		 
		//document-31.pdf
		IvlParserListener psl = new IvlParserListener() {
		};
		try (InputStream is = new FileInputStream("/home/jmortega/eclipse-workspace/aon.parent/aon-in-payroll/src/test/resources/com/esferalia/aon/in/payroll/tgss/ivl/ivl-aon.pdf");
				
				PDDocument doc = Loader.loadPDF(is)) {
		        PDFTextStripper stripper = new PDFTextStripper();
		        stripper.setSortByPosition(true);
		        for (int p = 0; p <= doc.getNumberOfPages(); p++) {
		        // Set the page interval to extract.
		        // If we don't, then all pages would be extracted.
		        stripper.setStartPage(p);
		        stripper.setEndPage(p);
		        String text = stripper.getText(doc);
		        if (AonStringUtils.isBlank(text))
		            continue;
		        
//		        System.out.println(text);
				IvlCccParser.parse(text, psl);

		        }
		    }

	}

}
