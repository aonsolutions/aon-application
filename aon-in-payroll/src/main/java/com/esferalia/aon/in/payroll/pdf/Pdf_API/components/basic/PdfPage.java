package com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.PAGE_TYPE;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;

/**
 * <p><b>Description:</b><i> This class represents a page.</i></p>
 * @version 0.2-AK
 * @author akrck02
 */
public class PdfPage{

	private PAGE_TYPE type;
	private PDPage page;
	
	/**
	 * <p><b>Description:</b> <i>The constructor. </i></p>
	 * @param type 
	 */
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
	
	/**
	 * <p><b>Description:</b> <i>Open a stream of a document. </i></p>
	 * @param doc 
	 */
	public PDPageContentStream stream(PDDocument doc) {
		try {return new PDPageContentStream(doc, page);}
		catch (IOException e) {e.printStackTrace();}
		return null;
	}


	public PAGE_TYPE getType() {return type;}
	public void setType(PAGE_TYPE type) {this.type = type;}

	public PDPage getPage() {return page;}
	public void setPage(PDPage page) {this.page = page;}
	
	public float getWidth() {return page.getMediaBox().getWidth();}	
	public float getHeight() {return page.getMediaBox().getHeight();}
	
	//HELP INFO
	public static String describe() {return "PdfPage:\t\t\t\t\tPage inside a pdf.";}
}
