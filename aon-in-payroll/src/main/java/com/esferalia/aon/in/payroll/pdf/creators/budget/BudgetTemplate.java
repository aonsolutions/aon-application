package com.esferalia.aon.in.payroll.pdf.creators.budget;

import java.awt.Color;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.beans.PdfPage;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.beans.PdfText;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.PAGE_TYPE;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.TEXT_ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;
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
	
	public void print(String out,Budget budget,Optional<Locale> language) throws CanNotCreatePdfException {
		try (PDDocument doc = new PDDocument()) {
			
			PdfPage page = new PdfPage(PAGE_TYPE.VERTICAL);
			doc.addPage(page.getPage());
			contents = page.stream(doc);	
			
			draw_client_info();
			draw_conditions();
			draw_products();
			
			contents.close();
			doc.save(filename);
		}catch (Exception e) {throw new CanNotCreatePdfException(e);}
	}

	private void draw_client_info() {
		
		PdfText name = new PdfText(0, 20, 200, 30, 5, 10, contents, "My text is alive!", PdfColors.WHITE, PdfFonts.HELVETICA, fontSize, TEXT_ALIGNMENT.CENTER);
		
		name.square(Color.black);
		name.draw();
		
		
	}
	
	private void draw_products() {
		
		
		
		
	}
	
	private void draw_conditions() {
		
		
		
		
	}
}


