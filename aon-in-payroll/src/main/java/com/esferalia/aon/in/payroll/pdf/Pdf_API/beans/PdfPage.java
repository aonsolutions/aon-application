package com.esferalia.aon.in.payroll.pdf.Pdf_API.beans;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.PAGE_TYPE;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;

public class PdfPage{

	private PAGE_TYPE type;
	private PDPage page;
	
	public PdfPage(PAGE_TYPE type) {
		this.type = type;
		switch (type) {
		case HORIZONTAL:
			this.page = PDFToolkit.createHorizontalPage();
			break;
		default:
			this.page = PDFToolkit.createVerticalPage();
			break;
		}
	}
	
	
	public PDPageContentStream stream(PDDocument doc) {
		try {return new PDPageContentStream(doc, page);}
		catch (IOException e) {e.printStackTrace();}
		return null;
	}


	public PAGE_TYPE getType() {
		return type;
	}


	public void setType(PAGE_TYPE type) {
		this.type = type;
	}


	public PDPage getPage() {
		return page;
	}


	public void setPage(PDPage page) {
		this.page = page;
	}
	
	
	public float getWidth() {
		return page.getMediaBox().getWidth();
	}
	
	
	public float getHeight() {
		return page.getMediaBox().getHeight();
	}
	
	//HELP INFO
	public static String describe() {
		String info = "PdfPage:\t\t\t\t\tPage inside a pdf.";
		return info;
	}
}
