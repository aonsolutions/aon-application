package com.esferalia.aon.in.payroll.pdf.Pdf_API.beans;

import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.GRAY;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts.HELVETICA;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.PAGE_TYPE;

public class PdfFile {
	
	public float x; 
	public float y;
	public int page;
	
	public PDDocument doc;
	public PDPageContentStream contents;
	public ResourceBundle words;
	public OutputStream out;
	public Locale lang;  
	
	public float y_limit;
	
	public PDFont font;
	public float fontsize;
	public Color primary;
	public Color secondary;
	
	public PdfFile(float x, float y, PDDocument doc, ResourceBundle words, OutputStream out) {
		this.x = x;
		this.y = y;
		this.page = 0;
		this.doc = doc;
		this.words = words;
		this.y_limit = 0;
		
		this.font = HELVETICA;
		this.fontsize = 10f;
		this.primary = BLACK;
		this.secondary = GRAY;
		this.out = out;
	}
	
	public PdfFile(float x, float y, PDDocument doc, ResourceBundle words, OutputStream out,
			float y_limit) {
		this.x = x;
		this.y = y;
		this.page = 0;
		this.doc = doc;
		this.words = words;
		this.y_limit = y_limit;
		
		this.font = HELVETICA;
		this.fontsize = 10f;
		this.primary = BLACK;
		this.secondary = GRAY;
		this.out = out;
	}

	public void set_defaults(PDFont font, float fontsize, Color primary, Color secondary) {
		this.font = font;
		this.fontsize = fontsize;
		this.primary = primary;
		this.secondary = secondary;
	}
	

	public void new_page(PAGE_TYPE type) throws IOException {
		
		if(contents != null) contents.close();
		PdfPage page = new PdfPage(type);
		doc.addPage(page.getPage());
		contents = new PDPageContentStream(this.doc, page.getPage());
		this.page++;
	}
	
	
	public boolean jump() {
		return y < y_limit;
	}
	
	public void close() throws IOException {
		if(contents != null) contents.close();
		if(doc != null) doc.close();
	}
	
	
	public void print() throws IOException {
		if(contents != null)  contents.close();
		if(doc != null) doc.save(out);
	}

	public String text(String name) {
		if(words == null) return "";
		return words.getString(name);
	}
	
	//------------HELP INFO------------
		public static String describe() {
			String info =
			"PdfFile: \t\t\t\t\tRepresents the basic pdf file template." + 
			"";
			return info;
		}	
}
