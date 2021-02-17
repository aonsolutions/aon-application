package com.esferalia.aon.in.payroll.pdf.creators.pdf_lab;

import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.PAGE_TYPE.HORIZONTAL;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.TEXT_ALIGNMENT.LEFT;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.TEXT_ALIGNMENT.RIGHT;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ResourceBundle;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.beans.PdfFile;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.beans.PdfTable;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.beans.PdfText;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.TEXT_ALIGNMENT;
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
			
			for (int i = 0; i < 10; i++) {
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
		temp.y = 550;
		PdfTable table = new PdfTable(temp.x, temp.y, temp.contents, 790, 20, 1, new float[] {10,30,10,20,10,10,5,5}, new String[]{"Nombre","Apellidos","edad","provincia","LOL"});
		table.fontsize = 10f;
		table.header_color = PdfColors.BLUE;
		table.set_alignment(new TEXT_ALIGNMENT[] {LEFT,LEFT,RIGHT,LEFT});
		
		table.draw_header();
		for (int i = 0; i < 20; i++) table.new_row(new String[] {temp.text("TEXT1"),temp.text("TEXT2"),temp.text("TEXT3"),temp.text("TEXT4")});
	
		temp.y = table.getY();
		
		PdfText text = new PdfText(temp.x, temp.y -50, 150, 50, temp.contents, temp.text("TEXT5"), BLACK, temp.font, temp.fontsize, LEFT);
		text.draw();
		text.border(BLACK);
		
	}

	public static void main(String[] args) {
		try {
			FileOutputStream fos = new FileOutputStream("Demo.pdf");
			print(fos);
		} catch (FileNotFoundException | CanNotCreatePdfException e) {e.printStackTrace();}
	}

}
