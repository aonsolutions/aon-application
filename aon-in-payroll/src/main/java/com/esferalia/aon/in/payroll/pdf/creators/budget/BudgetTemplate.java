package com.esferalia.aon.in.payroll.pdf.creators.budget;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.beans.PdfPage;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.PAGE_TYPE;
import com.esferalia.aon.in.payroll.pdf.creators.budget.beans.Budget;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;

public class BudgetTemplate {
	
	static float fontSize = 9f;
	static String filename = "./budget.pdf";
	static float limit = 100;
	
	PDPageContentStream contents;
	PDDocument doc;
	Budget budget;
	Locale lang;
	
	float x;
	float y;
	
	ResourceBundle words;
	
	public static void print(String out,Budget budget,Optional<Locale> language) throws CanNotCreatePdfException {
		try (PDDocument doc = new PDDocument()) {
			PdfPage page = new PdfPage(PAGE_TYPE.HORIZONTAL);
			PDPageContentStream contents = page.stream(doc);			
			
		}catch (Exception e) {throw new CanNotCreatePdfException(e);}
	}

	private void draw_client_info() {
		
	}
	
	private void draw_products() {
		
	}
	
	private void draw_conditions() {
		
	}
}


