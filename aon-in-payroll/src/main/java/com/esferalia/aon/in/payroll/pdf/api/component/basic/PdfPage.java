package com.esferalia.aon.in.payroll.pdf.api.component.basic;

import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.createHorizontalPage;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.createVerticalPage;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.PAGE_TYPE;

/**
 * <p>
 * <b>Description:</b><i> This class represents a page.</i>
 * </p>
 * 
 * @version 0.2-AK
 * @author akrck02
 */
public class PdfPage {

	private PAGE_TYPE type;
	private PDPage page;

	/**
	 * <p>
	 * <b>Description:</b> <i>The constructor. </i>
	 * </p>
	 * 
	 * @param type
	 */
	public PdfPage(PAGE_TYPE type) {
		this.type = type;
		switch (type) {
		case HORIZONTAL:
			this.page = createHorizontalPage();
			break;
		default:
			this.page = createVerticalPage();
			break;
		}
	}

	/**
	 * <p>
	 * <b>Description:</b> <i>Open a stream of a document. </i>
	 * </p>
	 * 
	 * @param doc
	 */
	public PDPageContentStream stream(PDDocument doc) {
		try {
			return new PDPageContentStream(doc, page);
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	// HELP INFO
	public static String describe() {
		return "PdfPage:\t\t\t\t\tPage inside a pdf.";
	}
}
