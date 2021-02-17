package com.esferalia.aon.in.payroll.pdf.Pdf_API.beans;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;

public class PdfImage extends PdfComponent{

	private PDDocument doc;
	private byte[] img;

	public PdfImage(float x, float y, float width, float height, float margin_x, float margin_y,
			PDPageContentStream stream, PDDocument doc, byte[] img) {
		super(x, y, width, height, margin_x, margin_y, stream);
		this.doc = doc;
		this.img = img;
	}

	@Override
	public void draw() {
		try {PDFToolkit.drawImage(doc, stream, img, margin_x, margin_y);} 
		catch (IOException e) {e.printStackTrace();}
	}
	
	public PdfImage scale(float max_width, float max_height) {
		float[] sizes = PDFToolkit.reescale(width, height, max_width, max_height);		
		
		this.width = sizes[0];
		this.height = sizes[1];
		
		return this;
	}

	public PDDocument getDoc() {
		return doc;
	}

	public void setDoc(PDDocument doc) {
		this.doc = doc;
	}

	public byte[] getImg() {
		return img;
	}

	public void setImg(byte[] img) {
		this.img = img;
	}
	
	
}
