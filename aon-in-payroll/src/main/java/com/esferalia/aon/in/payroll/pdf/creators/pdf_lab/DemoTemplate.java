package com.esferalia.aon.in.payroll.pdf.creators.pdf_lab;

import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.BLUE;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.PAGE_TYPE.HORIZONTAL;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ResourceBundle;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic.PdfBox;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic.PdfFile;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic.PdfImage;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;

public class DemoTemplate extends PdfFile {

	public DemoTemplate(float x, float y, PDDocument doc, ResourceBundle words, OutputStream out, int limit) {
		super(x, y, doc, words, out, limit);
	}

	public static void print(OutputStream out) throws CanNotCreatePdfException {
		
		DemoTemplate temp = null;
		try{
			ResourceBundle words = ResourceBundle.getBundle("com.esferalia.aon.in.payroll.pdf.creators.pdf_lab.bundles.Demo_bundle");
			temp = new DemoTemplate(20, 550,new PDDocument(), words, out,100);
			temp.set_defaults(HELVETICA, 10f, BLACK, PdfColors.GRAY);
			
			for (int i = 0; i < 1; i++) {
				temp.new_page(HORIZONTAL);
				draw_text_bundle(temp);	
			}
			
			temp.print();			
		} catch (Exception e) {
			if(temp != null) try {temp.close();} catch (IOException e1) {}
			throw new CanNotCreatePdfException(e);
		}

	}

	
	private static void draw_text_bundle(DemoTemplate temp) throws IOException {
		temp.y(20);
		
		FileInputStream logo = new FileInputStream("/home/akrck02/Pictures/7285.jpg");
		PdfImage img = new PdfImage( 20f, temp.y(), 150f, 50f, ALIGNMENT.CENTER, temp.contents, temp.doc, logo.readAllBytes());
		
		img.draw();
		logo.close();
		
		PdfBox b = new PdfBox(20f, temp.y(), 150f, 50f, BLUE, temp.contents);
		b.border();
	}

	public static void main(String[] args) {
		try {
			FileOutputStream fos = new FileOutputStream("Demo.pdf");
			print(fos);
		} catch (FileNotFoundException | CanNotCreatePdfException e) {e.printStackTrace();}
	}

}
